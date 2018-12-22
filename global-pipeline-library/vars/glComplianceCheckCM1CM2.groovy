import com.optum.jenkins.pipeline.library.compliance.ComplianceCheck

/**
 * Runs the automatic validation portion of the compliance report, and creates a partial
 * compliance document.
 * @param rallyApiUrl The base URL of the CA Agile Central API. Optional.
 * @param rallyApiToken The API token for CA Agile Central
 * @param rallyWorkspaceId The CA Agile Central workspace which contains your project and milestone.
 * @param rallyMilestoneId The milestone representing your current build and release.
 * @param failureThresholds Map containing the failure threshold for different type of tests.
 * @param fortifyProjectVersionId The fortify Project VersionId in scar.
 * @param fortifyUserCredentialsId The fortify  User CredentialsId in jenkins.
 * @param userStoryApprovers The secure group containing the members who has ability to accept the user stories.
 */

def call(Map<String, Object> config){
    ComplianceCheck complianceCheck = new ComplianceCheck(this)
    complianceCheck.validateCM1CM2Compliance(config)
}
