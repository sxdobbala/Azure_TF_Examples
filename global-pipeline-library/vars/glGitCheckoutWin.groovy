import com.optum.jenkins.pipeline.library.scm.Git

/**
 * Checkout to a branch or tag.
 *
 * @deprecated use {glGitCheckOutTag instead}
 **/

@Deprecated
def call(Map<String, Object> config){
	Git git = new Git(this)
	git.checkoutTag(config)	
}
