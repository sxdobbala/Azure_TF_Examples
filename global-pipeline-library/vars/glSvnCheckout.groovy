import com.optum.jenkins.pipeline.library.scm.Svn


/**
 * Wrapper for `checkout scm` for SVN
 */

def call(){
  Svn svn = new Svn(this)
  svn.checkout()
}
