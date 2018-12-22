package com.optum.jenkins.pipeline.library.security

// IMPORTANT
//      ** TODO: Determine if the Contrast SDK compile dependency can be
//               satisfied for the Jenkins runtime environment without
//               using @Grab (see notes below for context)
//
// ADDITIONAL CONTEXT @Grab issue
//      (1) Even though this library reference is included in build.gradle,
//          it appears that @Grab is needed for Jenkins to pull in the
//          library during compile time in the Jenkins runtime environment.
//          Without the @Grab, the exception is thrown when Jenkins compiles
//          this code. The stack trace is long and can be seen here: https://jenkins.optum.com/pedstest/job/contrast-jpac-smoketest/32/console
//      (2) However, the presence of @Grab When building w/ gradlew results in
//          a compile failure due to:
//          java.lang.NoClassDefFoundError: org/apache/ivy/core/report/ResolveReport
//          See: https://stackoverflow.com/questions/18173908/error-compiling-a-groovy-project-using-grab-annotation
//
//          The adjustment noted above was attempted, but resulted in this exception
//          during unit test execution: General error during conversion: No suitable ClassLoader found for grab.
//          For now, @Grab will be ignored via gradle compile options
//
@Grab('com.contrastsecurity:contrast-sdk-java:2.7')

import jenkins.model.Jenkins
import com.cloudbees.groovy.cps.NonCPS
import org.jenkinsci.plugins.credentialsbinding.impl.CredentialNotFoundException
import groovy.json.JsonBuilder
import com.contrastsecurity.exceptions.UnauthorizedException
import com.contrastsecurity.sdk.ContrastSDK
import com.contrastsecurity.models.Application
import com.contrastsecurity.models.Applications
import com.contrastsecurity.models.TraceListing
import com.contrastsecurity.http.TraceFilterForm
import com.contrastsecurity.http.FilterForm
import com.contrastsecurity.models.Traces
import com.contrastsecurity.models.Trace
import com.contrastsecurity.models.Libraries
import com.contrastsecurity.models.Library
import com.contrastsecurity.models.AgentType
import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.utils.Constants
import com.optum.jenkins.pipeline.library.utils.Utils
import com.optum.jenkins.pipeline.library.event.ContrastEvent

class Contrast implements Serializable {
  def jenkins
  def build

  Contrast() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Contrast(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Contrast metrics / attribute retriever for JPAC
 * <p>
 * Pulls metrics / attributes from Contrast TeamServer that are associated with this build.
 * NOTE : Teams can use contrastVerification to determine if a build should fail
 *        given defined critieria (e.g., critical > 3).
 *        For additional information - see: https://jenkins.io/doc/pipeline/steps/contrast-continuous-application-security/
 *
 * Requirements:
 * (1) Application has been configured and setup in Contrast TeamServer
 * (2) The Contrast Agent is running inconcert with the application (per
 *     Contrast instructions) when automated tests are run.
 * </p>
 * <p>
 *
 * Pipeline code example:
 * stage('Pull contrast metrics') {
 *    steps {
 *      glContrastPullMetrics credentialIdForApiKeys:'your-api-credential-id-here'
 *    }
 * }
 * </p>
 * Pulls metrics from the Constrast TeamServer for a given build identifier
 * Parameter list:
 * @param apiUrl [optional] Contrast TeamServer API URL
 * @param credentialIdForApiKeys [required] Stored in Jenkins as type "SecretText".
 *        Credential elements are entered as a single value with a space delimiter between
 *        Information on Contrast API Keys can be found here:
 *        https://optum.contrastsecurity.com/Contrast/static-secure/docs/restapi/index.html#accessingTheApi
 * @param orgUuid [optional] Organization UUID as identified in TeamServer;
 *                           if not provided it will defaulto to the user's default orgUuid
 * @param appUuid [optional] UUID for the application as setup/configured in Contrast TeamServer.
 *                           If not provided, it will be inferred by the by the Jenkins job name;
 *                           which is how the 
 * @param appVersionTag [optional] Reserved for unit-testing methods; during production the number is always determined by JOB_BASE_NAME-BUILD_NUMBER
 * @param listVulnStatusSummary [optional] defaults to true; true will include a list of counts; grouped by issue status+sub_stats
 * @param listLibraryGradeSummary [optional] defaults to true; true will include a list of counts group by library letter grade
 * @param listVulnLibraryDetail [optional] defaults to false; true will include a list of libraries w/ detail whose letter grade != A and vulerabilities > 0
 */
  def pullMetrics(Map<String, Object> params) {
    def startTime = new Date()
    def finalJsonResult = null

    def defaults = [
      apiUrl          : Constants.CONTRAST_TEAMSERVER_API_URL,
      credentialIdForApiKeys: null, // required
      orgUuid         : null, 
      appUuid         : null,  // If provided used in place of the env.JOB_BASE_NAME to infer the application appUuid.
      appVersionTag   : null,  // Typically will be null; can be populated for unit tests wherein a build # is forced for testing
      buildId         : null,  // defaults to jenkins build_number
      jobName         : null,  // defaults to jenkins job_base_name
      listVulnLibraryDetail: false,   // Some environments like NODE can result in 100s of libraries so leave this option to developer discretion
      listLibraryGradeSummary: true, // Show summary count - grouped by library grade assigned by Contrast
      listVulnStatusSummary: true    // Show summary count - grouped by vuln.status+vuln.sub_status
    ]

    // The development teams must configure their applications -- that will
    // be under observation by Contrast -- to start up with the Contrast
    // Agent with the build identifer specified.
    // For example, for Java applications this is provided as part of
    // the commandline: -Dcontrast.override.appversion=[build-id-here]
    // NOTE from contrast documentation: The plugin uses the unique identifier ${JOB_BASE_NAME}-${BUILD_NUMBER} to 
    // filter vulnerabilities by "appVersionTags" vulnerability attribute. JOB_BASE_NAME and BUILD_NUMBER are available
    // as Jenkins environment properties
    def defaultAppVersionTag = "${jenkins.env.JOB_BASE_NAME}-${jenkins.env.BUILD_NUMBER}"

    try {
      def config = defaults + params
      if (config.credentialIdForApiKeys == null) {
        throw new Exception("Missing argument(s): credentialIdForApiKeys must be provided.")
      }

      config.jobName = "${jenkins.env.JOB_BASE_NAME}"
      config.buildId = "${jenkins.env.BUILD_NUMBER}"

      // appVersionTag to pull metrics overriden by caller (typical in the case of unit testing this code)
      if (config.appVersionTag != null) {
        jenkins.echo "Using appVersionTag: ${config.appVersionTag} to pull metrics instead of default: $defaultAppVersionTag"
      } else {
        config.appVersionTag = defaultAppVersionTag
        jenkins.echo "appVersionTag used by Contrast to filter issues to this build: $defaultAppVersionTag"
      }

      // These two keys will be retrieved from the Jenkins Credential provider.
      // NOTE: (1) Code expects API username & keys to be of Jenkins type "SecretText"
      //       (2) Stored as apiUsername [space] apiKey [space] serviceKey
      //           Information on Contrast API Keys can be found here:
      //           https://optum.contrastsecurity.com/Contrast/static-secure/docs/restapi/index.html#accessingTheApi
      def (apiUsername, apiKey, serviceKey) = getApiKeys(config.credentialIdForApiKeys)
      if (apiUsername == null || apiKey == null || serviceKey == null) {
        throw new Exception("Missing API keys: apiUsername, apiKey and serviceKey must exist.")
      }

      jenkins.echo "Contrast.pullMetrics arguments: $config"
      def jsonResult = summarizeCounts(config, apiUsername, apiKey, serviceKey)
      if (jsonResult == null) {
        throw new Exception("Unexpected failure during summarizeCounts")
      }

      // Output to console log for now.
      // TODO: Instead of sending to console, output an artifact
      //       that can remain with other build artifacts
      finalJsonResult = jsonResult.toString()
      jenkins.echo finalJsonResult

    } catch (Exception ex) {
      jenkins.echo ex.getMessage()

      sendEvent(jenkins, startTime, EventStatus.FAILURE, finalJsonResult)
      jenkins.error "Failed to pull contrast metrics from TeamServer."
    }

    sendEvent(jenkins, startTime, EventStatus.SUCCESS, finalJsonResult)
  }

  def getApiKeys(String credentialIdForApiKeys) {
      def apiUsername = null
      def apiKey = null
      def apiServiceKey = null

      // Credentials are expected to be of type "SecretText" (Global store)
      // TODO: Confirm w/ EIS just in case there's another vault that's preferred
      jenkins.withCredentials([jenkins.string(credentialsId: credentialIdForApiKeys, variable: 'CONTRAST_API_CREDENTIALS')]) {
        (apiUsername, apiKey, apiServiceKey) = jenkins.env.CONTRAST_API_CREDENTIALS?.tokenize(' ')
      }

      return [apiUsername, apiKey, apiServiceKey]
  }

  def sendEvent(jenkins, Date processStart, EventStatus status, String contrastPayload) {
    new ContrastEvent(jenkins, [duration: new Utils(jenkins).getDuration(processStart).toString(), status: status, contrastPayload: contrastPayload]).send()
  }

  // NonCPS - portions of the ContrastSDK are not serializable;
  // Jenkins will not be able to pause/resume the job while method
  // is running
  @NonCPS
  def summarizeCounts(Map<String, Object> config, String apiUsername, String apiKey, String serviceKey) {
    // Total number of vulnerabilities that have been tagged with the
    // specified appVersionTag
    def vulnTotalForBuild = 0
    def vulnTotalForBuildOpen = 0
    def totalCritical = 0 // Critical vuln count
    def totalHigh = 0     // High vuln count
    def totalMedium = 0   // Medium vuln count
    def totalNote = 0     // Note only vuln count

    ContrastSDK contrastSDK = new ContrastSDK(apiUsername, serviceKey, apiKey, config.apiUrl)

    // Pull in default orgUuid if not provided via parameters
    if (config.orgUuid == null) {
      config.orgUuid = contrastSDK.getProfileDefaultOrganizations().getOrganization().getOrgUuid()
    }

    // Determine appUuid using the job_name as a look-key.
    // NOTE: This is how the contrastVerification pipeline method (provided by Contrast)
    //       determines the appUuid as well. In short, the job_name suffix should
    //       be identical to the application name as shown in the Contrast TeamServer
    // TODO: Document as part of the Contrast onboarding procedures for Jenkins.
    if (config.appUuid == null) {
      def applicationName = config.jobName.tokenize('/').last()
      config.appUuid = getApplicationId(contrastSDK, config.orgUuid, applicationName)

      if (config.appUuid.equals('')) {
        throw new Exception("Failure to determine appUuid from job-name: $applicationName")
      }
    }

    // This will tell the SDK to also fetch the SCORES for the application
    EnumSet<FilterForm.ApplicationExpandValues> expandAppValues = EnumSet.of(FilterForm.ApplicationExpandValues.SCORES)
    // Get application information that can be included in the summary output
    def applicationDetails = contrastSDK.getApplication(config.orgUuid, config.appUuid, expandAppValues).getApplication()

    TraceFilterForm filterForm = new TraceFilterForm()

    // Filter issues / vuln that have been tagged with given appVersionTag
    filterForm.appVersionTags = Arrays.asList(config.appVersionTag)

    // TODO: Support additional calls should more than 1000 issues exist
    filterForm.limit = 1000 // Allow up to 1000 traces in a single call
    
    Traces fullTraceForBuild = contrastSDK.getTraces(config.orgUuid, config.appUuid, filterForm)
    vulnTotalForBuild = fullTraceForBuild.getCount()

    // Iterate through issues for the given appVersionTag and tally counts
    if (vulnTotalForBuild > 0) {
      for (Trace singleTrace in fullTraceForBuild.getTraces()) {
        def status = singleTrace.getStatus().toUpperCase()

        // If the issue has been handled, exclude from metrics
        if (status.equals("NOT A PROBLEM") || status.equals("REMEDIATED")) continue

        vulnTotalForBuildOpen++;

        def severity = singleTrace.getSeverity().toUpperCase()

        switch(severity) {
          case 'CRITICAL': totalCritical++; break
          case 'HIGH': totalHigh++; break
          case 'MEDIUM': totalMedium++; break
          case 'NOTE': totalNote++; break
        }
      }
    }

    def appLibraries = contrastSDK.getLibraries(config.orgUuid,config.appUuid)
    def jsonBuilder = new JsonBuilder()

    // Tally now so it can be included in the total, but save list
    // so it can be iterated & output with JSON details
    def appVulnLibraryList = appLibraries.getLibraries().findAll {
      // Example filter: libraries that have known vulnerabilties
      it.getTotalVulnerabilities() > 0
    }

    // Helpful summary; groups and counts the libraries based on the
    // letter grade given by Contrast
    def appLibraryGradeCountsList = appLibraries.getLibraries().countBy {
      it.getGrade()
    }

    // Helpful summary; groups & counts by vulnerability status
    // Helps identify if high % of issues are marked "NOT A PROBLEM"
    def vulnStatusCountList = fullTraceForBuild.getTraces().countBy {
      if (it.getSubStatus() != null && !it.getSubStatus().equals('')) {
        it.getStatus() + '-' + it.getSubStatus()
      } else {
        it.getStatus()
      }
    }

    // Just an example of output. The final measures / information required to be
    // saved as an artifact for a build will be determined in concert w/ EIS
    jsonBuilder {
      buildId config.buildId
      orgUuid config.orgUuid
      appUuid config.appUuid
      appVersionTag config.appVersionTag
      appName applicationDetails.getName()
      appPath applicationDetails.getPath()
      appLetterGrade applicationDetails.getScores().getLetterGrade()
      appLanguage applicationDetails.getLanguage()
      appVulnTotalLibrary appVulnLibraryList.size()


      // Totals summarized by on issues tagged with the appVersionTag (i.e., application version/id)
      buildTotals {
        vulnTotal vulnTotalForBuild
        vulnTotalOpen vulnTotalForBuildOpen
        critical totalCritical
        high totalHigh
        medium totalMedium
        note totalNote
      }

      // Total by vulnerability status
      if (config.listVulnStatusSummary) {
        vulnStatusSummary vulnStatusCountList.collect {
          [
            status: it.key,
            count: it.value
          ]
        }
      }

      // Library count - grouped by library grade given by Contrast
      if (config.listLibraryGradeSummary) {
        libraryGradeSummary appLibraryGradeCountsList.collect {
          [
            grade: it.key,
            count: it.value
          ]
        }
      }

      // Vulnerable library list
      if (config.listVulnLibraryDetail) {
        vulnerableLibraries appVulnLibraryList.collect {
          [
            fileName: it.getFileName(),
            fileVersion: it.getFileVersion(),
            fileHash: it.getHash(),
            grade: it.getGrade(),
            vulnTotal: it.getTotalVulnerabilities()
          ]
        }
      }
    } //JsonBuilder

    return jsonBuilder
  }

  @NonCPS
  private String getApplicationId(ContrastSDK sdk, String orgUuid, String applicationName) {
    Applications applications;

    if (orgUuid == null || applicationName == null || applicationName.equals('')) {
      return ""
    }

    try {
      applications = sdk.getApplications(orgUuid)
    } catch (IOException | UnauthorizedException e) {
      return ""
    }

    // TODO: Check w/ contrast vendor to see if name found via a direct
    //       search v.s. pulling all applications then looping through
    //       the entire list.
    for (Application application : applications.getApplications()) {
      if (applicationName.equals(application.getName())) {
        return application.getId()
      }
    }

    return ""
  }

  /**
  * Wrapper for backwards compatibility.
  */
  def pullMetrics(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    pullMetrics(config)
  }
}
