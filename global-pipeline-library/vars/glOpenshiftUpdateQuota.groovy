import com.optum.jenkins.pipeline.library.openshift.Project

/**
 * update the Quota for any or all resources related to your OpenShift Project
 *
 * @param credentials of the owner of the project for deletion.
 * @param name String to set the name of the project being deleted.
 * @param platform String such as 'nonprod-origin'
 * @param datacenter String such as 'elr'
 * @param zone String such as 'core'
 * @param cpu String
 * @param ram String
 * @param pods String
 * @param secrets String
 * @param volumes String
 * @param replicationControllers String
 * @param storage String
 */
def call(Map<String, Object> config){
    Project project = new Project(this)
    project.updateQuota(config)
}