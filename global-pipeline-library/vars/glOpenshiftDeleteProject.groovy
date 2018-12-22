import com.optum.jenkins.pipeline.library.openshift.Project

/**
 * deletes an OpenShift Project
 *
 * @param credentials of the owner of the project for deletion.
 * @param name String to set the name of the project being deleted.
 * @param platform String such as 'nonprod-origin'
 * @param datacenter String such as 'elr'
 * @param zone String such as 'core'
 */
def call(Map<String, Object> config){
    Project project = new Project(this)
    project.delete(config)
}

