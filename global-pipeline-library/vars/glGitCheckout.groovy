import com.optum.jenkins.pipeline.library.scm.Git

/**
 * Wrapper for `checkout scm` for GIT
 */

def call(){
  Git git = new Git(this)
  git.checkout()
}