import com.optum.jenkins.pipeline.library.compliance.CM3
import com.optum.jenkins.pipeline.library.compliance.ComplianceInvalidParameterException
/**
 * This call validates the current build for compliance.
 * @param rallyApiUrl The base URL of the CA Agile Central API. Optional.
 * @param rallyApiToken The API token for CA Agile Central
 * @param rallyWorkspaceId The CA Agile Central workspace which contains your project and milestone.
 * @param rallyMilestoneId The milestone representing your current build and release.
 */

def call(Map<String, Object> config){
    CM3 cm3 = new CM3(this)
    if (config.containsKey('submitter') && config.submitter != "")
      cm3.sendNotification(config.submitter)
    else {
      throw new ComplianceInvalidParameterException("Please pass submitter in the parameter, config.submitter")
    }
}
