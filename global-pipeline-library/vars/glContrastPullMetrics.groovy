import com.optum.jenkins.pipeline.library.security.Contrast

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
 * @param appVersionTag [optional] Reserved for unit-testing methods; during production the number is always determined by JOB_NAME-BUILD_NUMBER
 * @param listVulnStatusSummary [optional] defaults to true; true will include a list of counts; grouped by issue status+sub_stats
 * @param listLibraryGradeSummary [optional] defaults to true; true will include a list of counts group by library letter grade
 * @param listVulnLibraryDetail [optional] defaults to false; true will include a list of libraries w/ detail whose letter grade != A and vulerabilities > 0
 */

def call(Map<String, Object> config){
  Contrast contrast = new Contrast(this)
  contrast.pullMetrics(config)
}