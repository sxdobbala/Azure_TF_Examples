import com.optum.jenkins.pipeline.library.scm.Github

/**
 * Get the repository name
 */
def call(){
  Github github = new Github(this)
  return github.getRepo()
}
