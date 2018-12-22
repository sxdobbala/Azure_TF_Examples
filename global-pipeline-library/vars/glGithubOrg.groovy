import com.optum.jenkins.pipeline.library.scm.Github

/**
 * Get the organization name
 */
def call(){
  Github github = new Github(this)
  return github.getOrg()
}
