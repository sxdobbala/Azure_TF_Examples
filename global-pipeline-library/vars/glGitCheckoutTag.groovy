import com.optum.jenkins.pipeline.library.scm.Git

/**
 * Checkout to a branch or tag.
 *
 * @params credentialsID
 * @params tag
 * @params url
 *
 * Examples:
 *    checkoutTag( credentialsId: '123', tag: 'refs/tags/v1.2.3')
 *    checkoutTag( credentialsId: '123', tag: 'master')
 *    checkoutTag( credentialsId: '123', tag: 'refs/tags/v1.2.3',
 *        url: 'https://github.optum.com/org/repo')
 */
def call(Map<String, Object> config){
  Git git = new Git(this)
  git.checkoutTag(config)
}