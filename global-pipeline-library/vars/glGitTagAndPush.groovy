import com.optum.jenkins.pipeline.library.scm.Git

/**
 * Creates a git tag and pushes it
 *
 * @param credentialsId String Required Credentials to git push
 * @param repoUrl String Required The repo url getting tagged
 *      Example: "github.optum.com/org/repo"
 * @param tag String Required Tag annotation
 *      Example: "app-name-1.0.0.54"
 * @param userName String Required User name with which to execute git tag
 * @param userEmail String Required Email with which to execute git tag
 * @param tagMessage String Optional Description for the tag
 *
 */
def call(Map<String, Object> config){
  Git git = new Git(this)
  git.tagAndPush(config)
}