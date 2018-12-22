import com.optum.jenkins.pipeline.library.openshift.Project

/**
 * Creates an OpenShift Project
 *
 * @param credentials of the owner creating the project
 * @param name String to set the name of the project being created.
 *    @Pattern(
 *      regexp = "([a-z0-9]+-)*[a-z0-9]+$",
 *      message = "project name cannot start with hyphen and must contain only lowercase letters, numbers and dashes"
 *      )
 *    @Size
 *      min = 6,
 *      max = 63,
 *      message = "Project name must be between 6 and 63 charact
 * @param displayName String (Optional)
 *    @Pattern(
 *      regexp = "[A-Za-z0-9-]+"
 *      message = "Upper or lower case with hyphens, no spaces"
 *      )
 *    @Size
 *      min = 0,
 *      max = 63,
 *      message = "Project name must be between 0 and 63 characters.
 * @param description String (Optional)
 * @param cpu String (Optional)
 * @param ram String (Optional)
 * @param tmdbCode String (Required)
 * @param askId String (Required)
 * @param platform String such as 'nonprod-origin'
 * @param datacenter String such as 'elr'
 * @param zone String such as 'core'
 */

def call(Map<String, Object> config){
    Project project = new Project(this)
    project.create(config)
}
