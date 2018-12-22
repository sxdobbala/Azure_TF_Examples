package com.optum.jenkins.pipeline.library.sca

import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.event.FortifyLocalEvent
import com.optum.jenkins.pipeline.library.utils.Utils

class Fortify implements Serializable {
  Object jenkins

  Fortify() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Fortify(jenkins) {
    this.jenkins = jenkins
  }

/**
 *
 * This class allows you to create a Fortify Scan
 *
 * In Jenkins -> Manage Jenkins -> Configure System
 * 1 - Look for the "Global properties" section.
 *     You must add Environment Variable FORTIFY_VERSION=HP_Fortify_SCA_and_Apps_16.11
 * 2 - Look in the section Cloud -> BuildSwarm -> find the "docker-maven-slave" and click on "Container Settings".
 *     Find the "Volumes From" section and add "fortify".  This will make the fortify mixin available to your "docker-maven-slave".
 *     See: https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins
 * 3 - the env.FORTIFY_HOME is being set from the FORTIFY_VERSION in the Constants
 *
 * @param scarProjectVersion Project version in Scar. This is required to upload the fpr file to scar and can be found within the URL
 *  for your SCAR project and desired version. (e.g. https://scar.uhc.com/ssc/html/ssc/version/99999/fix?issueGrouping=ISSUE_FOLDER. 99999 is the version ID.)
 * @param fortifyBuildName The name of the fortify build.
 * @param scarUploadToken Token when uploading the fpr file to scar. This is for authentication. This can be used, or
 *  scarCredentialsId can be provided and a token will be retrieved.
 * @param scarCredentialsId ID to SCAR credentials for upload and/or download. This is required if downloading from SCAR.
 *  If uploading to SCAR, this can be used or scarUploadToken may be used.
 * @param fortifyTranslateExclusions To pass the list of exclusions to fortify so that can be excluded from the scan
 * @param fortifyMaxSizeMemory defaults to 1G
 * @param uploadToScar If true, uploads the fpr scar, defaults to false. Requires scarUploadToken or scarCredentialsId.
 * @param downloadFromScar If true, downloads previous scan FPR from SCAR, defaults to false. Requires scarCredentialsId.
 * @param criticalThreshold, fortify fails if the critical scan errors are greater than this threshold, defaults to 0
 * @param highThreshold, fortify fails if the high scan errors are greater than this threshold, defaults to 0
 * @param mediumThreshold, fortify fails if the medium scan errors are greater than this threshold
 * @param lowThreshold, fortify fails if the low scan errors are greater than this threshold
 * @param isDotNetCore If true, translates the project using .NET Core 1.10
 * @param javascript.additionalTranslateOptions Additional options to be tagged onto the translate command
 * @param javascript.source Source file or directory of the javascript files
 * @param isGenerateDevWorkbook Set to true to generate the developer workbook report.
 * @param onlyNewIssues Set to true to report only on new issues. This applies to the thresholds and the Developer
 *  Workbook (PDF Scan Summary will also report on all issues.) This is useful in branch builds, for example. Defaults
 *  to false.
 * @param additionalIssueFilters Additional filters to apply to the issue count. This applies to the thresholds and the
 *  Developer Workbook (PDF Scan Summary will also report on all issues.) See the Audit Workbench documentation
 *  for details. (Hint, use the search box in Audit Workbench below the issue list build and try out filters.)
 * @param source specify the source directory for non-javascript scans (see javascript.source for javascript scans.)
 *  Defaults to **\/*.
 */

  def fortifyScan(Map<String, Object> params) {
    def defaults = [
      fortifyHome               : "/tools/fortify/$jenkins.env.FORTIFY_VERSION",
      fortifyBuildName          : null,
      fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
      fortifyMaxSizeMemory      : "-Xmx1G",
      fortifyJdkVersion         : "1.7",
      fortifyClassPath          : "",
      fortifyScarUrl            : "https://scar.uhc.com/ssc",
      uploadToScar              : false,  // Not to upload by default,
      downloadFromScar          : false,
      criticalThreshold         : 0,
      highThreshold             : 0,
      isDotNetCore              : false,   // Set this to true if the project is built on .NET Core
      javascript                : [
        additionalTranslateOptions: "",  // Additional options to be tagged onto the translate command
        source                    : ""   // Source file or directory of the javascript files
      ],
      isGenerateDevWorkbook     : false,   // Set to true to generate the developer workbook report.
      onlyNewIssues             : false,
      additionalIssueFilters    : '',
      source                    : '**/*'
    ]
    def config = defaults + params
    def startTime = new Date()
    jenkins.echo "fortifyScan arguments: $config"

    validateBasicParameters(jenkins, config)
    if (config.uploadToScar) {
      validateFortifyUploadToScarParameters(config)
    }
    if (config.downloadFromScar) {
      validateFortifyDownloadParameters(config)
    }

    // do we even need the 64 which refers to the 64 bit scan versus a 32 bit scan
    String fortifyVersionCmd = "${config.fortifyHome}/bin/sourceanalyzer -version"
    String fortifyCleanCmd = "${config.fortifyHome}/bin/sourceanalyzer -verbose -64 -b ${config.fortifyBuildName} -clean -logfile fortify_results/fortify_clean.log"
    String fortifyTranslateCmd = "${config.fortifyHome}/bin/sourceanalyzer -verbose -64 ${config.fortifyMaxSizeMemory} -source ${config.fortifyJdkVersion} -b ${config.fortifyBuildName} -cp '${config.fortifyClassPath}' ${config.fortifyTranslateExclusions} '$config.source' -logfile fortify_results/fortify_translation.log"
    String fortifyLocalScanCmd = "${config.fortifyHome}/bin/sourceanalyzer -verbose -64 ${config.fortifyMaxSizeMemory} -b ${config.fortifyBuildName} -scan -f fortify_results/${config.fortifyBuildName}.fpr -logfile fortify_results/fortify_scan.log"
    String fortifyDownloadTokenCmd = "${config.fortifyHome}/bin/fortifyclient -url ${config.fortifyScarUrl} -user \$SCAR_USER -password \$SCAR_PASS token -gettoken AnalysisDownloadToken"
    def fortifyDownloadFprCmd = { token -> "${config.fortifyHome}/bin/fortifyclient -url ${config.fortifyScarUrl} -authtoken ${token} downloadFPR -file fortify_results/${config.fortifyBuildName}.fpr -projectVersionID ${config.scarProjectVersion}"}
    String fortifyUploadTokenCmd = "${config.fortifyHome}/bin/fortifyclient -url ${config.fortifyScarUrl} -user \$SCAR_USER -password \$SCAR_PASS token -gettoken AnalysisUploadToken"
    def fortifyUploadFpr = { token -> "${config.fortifyHome}/bin/fortifyclient uploadFPR -f fortify_results/${config.fortifyBuildName}.fpr -applicationVersionID ${config.scarProjectVersion} -url ${config.fortifyScarUrl} -authtoken ${token}"}
    String fortifyShowFiles = "${config.fortifyHome}/bin/sourceanalyzer -verbose -b ${config.fortifyBuildName} -show-files"
    String fortifyShowBuildWarnings = "${config.fortifyHome}/bin/sourceanalyzer -verbose -b ${config.fortifyBuildName} -show-build-warnings"
    String fortifyResultGeneratorPdf = "${config.fortifyHome}/bin/ReportGenerator ${config.fortifyMaxSizeMemory} -source fortify_results/${config.fortifyBuildName}.fpr -template ScanReport.xml -f fortify_results/${config.fortifyBuildName}.pdf -format pdf -verbose"
    String birtReportsQuery = createSearchQuery(config.onlyNewIssues, config.additionalIssueFilters)
    String fortifyResultGeneratorDevWorkbookPdf = "${config.fortifyHome}/bin/BIRTReportGenerator ${config.fortifyMaxSizeMemory} -source fortify_results/${config.fortifyBuildName}.fpr -template 'Developer Workbook' -output fortify_results/${config.fortifyBuildName}-developer-workbook.pdf -format PDF -verbose -searchQuery '$birtReportsQuery'"
    String fortifyMakeMobile = "${config.fortifyHome}/bin/sourceanalyzer -b ${config.fortifyBuildName} -make-mobile"

    jenkins.command(fortifyVersionCmd)
    jenkins.command(fortifyCleanCmd)
    if (config.isDotNetCore == true) {
      String dotnetRestoreCmd = "dotnet restore"
      fortifyTranslateCmd = "${config.fortifyHome}/bin/sourceanalyzer -dotnet-core-version 1.1 -verbose -64 ${config.fortifyMaxSizeMemory} -source ${config.fortifyJdkVersion} -b ${config.fortifyBuildName} . -cp '${config.fortifyClassPath}' ${config.fortifyTranslateExclusions} '**/*' -logfile fortify_results/fortify_translation.log"
      jenkins.command(dotnetRestoreCmd)
    } else if (config.javascript?.source != "") {
      fortifyTranslateCmd = "${config.fortifyHome}/bin/sourceanalyzer -verbose -64 ${config.fortifyMaxSizeMemory} -source ${config.fortifyJdkVersion} -b ${config.fortifyBuildName} '${config.javascript.source}' ${config.fortifyTranslateExclusions} -logfile fortify_results/fortify_translation.log ${config.javascript?.additionalTranslateOptions ? config.javascript?.additionalTranslateOptions : ""}"
    }

    if (config.downloadFromScar) {
      jenkins.echo 'Downloading from scar...'
      jenkins.withCredentials([jenkins.usernamePassword(credentialsId: config.scarCredentialsId, usernameVariable: 'SCAR_USER', passwordVariable: 'SCAR_PASS')]) {
        String token = jenkins.command(fortifyDownloadTokenCmd, true) - 'Authorization Token: '
        jenkins.command fortifyDownloadFprCmd(token)
      }
    }

    jenkins.command(fortifyTranslateCmd)
    jenkins.command(fortifyMakeMobile)

    try {
      jenkins.echo 'Local scan in progress...'
      jenkins.command(fortifyLocalScanCmd)
      if (config.uploadToScar == true) {
        jenkins.echo 'Uploading to scar...'
        String token
        if (config.scarUploadToken) {
          token = config.scarUploadToken
        } else {
          jenkins.withCredentials([jenkins.usernamePassword(credentialsId: config.scarCredentialsId, usernameVariable: 'SCAR_USER', passwordVariable: 'SCAR_PASS')]) {
            token = jenkins.command(fortifyUploadTokenCmd, true) - 'Authorization Token: '
          }
        }
        jenkins.command fortifyUploadFpr(token)
      }
    } catch (Exception ex) {
      sendFortifyLocalEvent(jenkins, startTime, EventStatus.FAILURE, config.fortifyBuildName, config.fortifyTranslateExclusions, issuesMap)
      jenkins.error("Local scan failed due to " + ex.getMessage())
    }
    jenkins.command(fortifyShowFiles)
    jenkins.command(fortifyShowBuildWarnings)
    Map issuesMap = [:]
    try {
      jenkins.command(fortifyResultGeneratorPdf)
      if (config.isGenerateDevWorkbook == true) {
        jenkins.command(fortifyResultGeneratorDevWorkbookPdf)
      }

      issuesMap = getIssueCount(config.fortifyBuildName, config.fortifyHome, config.onlyNewIssues, config.additionalIssueFilters)
      jenkins.echo("Fortify issue count: $issuesMap")
      checkIssues(jenkins, issuesMap, config.criticalThreshold, 'CriticalIssues')
      checkIssues(jenkins, issuesMap, config.highThreshold, 'HighIssues')
      if (config.containsKey('mediumThreshold')) {
        checkIssues(jenkins, issuesMap, config.mediumThreshold, 'MediumIssues')
      }
      if (config.containsKey('lowThreshold')) {
        checkIssues(jenkins, issuesMap, config.lowThreshold, 'LowIssues')
      }
      if (config.uploadToScar) {
        sendFortifyLocalEventWithScar(jenkins, startTime, EventStatus.SUCCESS, config.fortifyBuildName, config.fortifyTranslateExclusions, config.scarProjectVersion, issuesMap)
      } else {
        sendFortifyLocalEvent(jenkins, startTime, EventStatus.SUCCESS, config.fortifyBuildName, config.fortifyTranslateExclusions, issuesMap)
      }
    } catch (Exception ex) {
      sendFortifyLocalEvent(jenkins, startTime, EventStatus.FAILURE, config.fortifyBuildName, config.fortifyTranslateExclusions, issuesMap)
      jenkins.error("Fortify result generator failed due to " + ex.getMessage())
    } finally {
      jenkins.archiveArtifacts 'fortify_results/**/*'
    }

  }

  /**
   * Run a fortify scan for a Maven project. See fortifyScan for arguments.
   */
  def fortifyScanMaven(Map<String, Object> params) {
    def defaults = [
      source        : '**/src/main/**/*'
    ]
    def config = defaults + params
    def libPath = 'target/lib'
    if (config.fortifyClassPath) {
      config.fortifyClassPath += ":**/$libPath/*.jar"
    } else {
      config.fortifyClassPath = "**/$libPath/*.jar"
    }
    jenkins.echo 'Collecting Maven dependencies...'
    jenkins.command "mvn package dependency:copy-dependencies -Dmaven.javadoc.skip=true -Dmaven.test.skip=true -DoutputDirectory=$libPath"
    fortifyScan(config)
  }

  static checkIssues(Object jenkins, Map issuesMap, int issueThreshold, String issueType) {
    if (issuesMap.containsKey(issueType)) {
      def issueCount = issuesMap.get(issueType)
      int intValue = issueCount.isInteger() ? issueCount.toInteger() : 0
      if (intValue > issueThreshold) {
        throw new FortifyThresholdFailedException("ERROR: Fortify scan failed with $issueType : $intValue greater than the threshold : $issueThreshold")
      }
      jenkins.echo("No. of $issueType : $intValue")
    }
  }

  Map getIssueCount(fileName, fortifyHome, onlyNewIssues=false, additionalFilters=null) {
    String filePath = "fortify_results/${fileName}.fpr"
    def countIssues = { severity ->
      def query = createSearchQuery(onlyNewIssues, additionalFilters, severity)
      def count = jenkins.command(
        "$fortifyHome/bin/FPRUtility -information -project '$filePath' -search -query '$query'", true).split(' ')[0]
      return count == 'No' ? '0' : count
    }

    Map fortifyIssueCount = [:]
    fortifyIssueCount.CriticalIssues = countIssues('critical')
    fortifyIssueCount.HighIssues = countIssues('high')
    fortifyIssueCount.MediumIssues = countIssues('medium')
    fortifyIssueCount.LowIssues = countIssues('low')

    fortifyIssueCount
  }

  static String createSearchQuery(onlyNewIssues, additionalFilters, severity=null) {
    def filters = ''
    if (additionalFilters) {
      filters = " AND $additionalFilters"
    }
    if (onlyNewIssues) {
      filters += ' AND [issue age]:new'
    }
    if (severity) {
      filters += " AND [fortify priority order]:$severity"
    }
    "[analysis type]:SCA AND analysis:!Not an Issue$filters"
  }

  static sendFortifyLocalEvent(jenkins, Date processStart, EventStatus status, String fortifyBuildName, String fortifyTranslateExclusions, Map issuesMap=[:]) {
    new FortifyLocalEvent(
      jenkins,
      [
        duration: new Utils(jenkins).getDuration(processStart).toString(),
        status: status,
        fortifyBuildName: fortifyBuildName,
        translateExclusions: fortifyTranslateExclusions,
        issuesMap: issuesMap
      ]).send()
  }

  def sendFortifyLocalEventWithScar(jenkins, Date processStart, EventStatus status, String fortifyBuildName, String fortifyTranslateExclusions, String scarProjectVersion, Map issuesMap=[:]) {
    new FortifyLocalEvent(
      jenkins,
      [duration: new Utils(jenkins).getDuration(processStart).toString(),
       status: status,
       fortifyBuildName: fortifyBuildName,
       translateExclusions: fortifyTranslateExclusions,
       scarProjectVersion: scarProjectVersion,
       issuesMap: issuesMap
      ]).send()
  }

  boolean validateBasicParameters(Object jenkins, Map<String, Object> config) {
    if (!config.fortifyBuildName) {
      throw new FortifyInvalidParameterException('ERROR: Fortify Build Name is required in the input configuration, Please pass the build name in config.fortifyBuildName parameter.')
    }
    if (config.containsKey('cloudScanOrLocal') || config.containsKey('fortifyCloudScanEmailTo')) {
      throw new FortifyInvalidParameterException("ERROR: Cloud scan is no more supported in pipeline library, 'cloudScanOrLocal' and 'fortifyCloudScanEmailTo' are not supported so please remove those parameters from your config.")
    }
  }

  boolean validateFortifyDownloadParameters(Map<String, Object> config) {
    if (!config.scarCredentialsId) {
      throw new FortifyInvalidParameterException('ERROR: Scar credentials ID is required to download the scan ' +
        'results from scar, please pass the credential ID in config.scarCredentialsId parameter.')
    }
  }

  boolean validateFortifyUploadToScarParameters(Map<String, Object> config) {
    if (!config.scarUploadToken && !config.scarCredentialsId) {
      throw new FortifyInvalidParameterException('ERROR: Scar upload token(authentication token) or credentials ID ' +
        'is required to upload the scan results to scar, please pass the auth token in config.scarUploadToken ' +
        'parameter or the credential ID in config.scarCredentialsId parameter.')
    }
    if (!config.scarProjectVersion) {
      throw new FortifyInvalidParameterException('ERROR: Scar Project Version is required to upload the scan results ' +
        'to scar, please pass the project version in config.scarProjectVersion parameter.')
    }
  }

}
