import com.optum.jenkins.pipeline.library.scm.Github

/**
 * Send or update a Git status on the current revision.
 *
 * Usage: use on the same node/stage as where git checkout occurred. Have the option to pass
 *  one of four statuses to Github. These status are then able to serve as required checks for code merges to branches.
 *
 * @param gitHubUrl The base url for accessing Github APIs.
 * @param credentialsId Required jenkins credential reference id of SecretText type, for github. Containing github api token with write permission to repository.
 * @param context The title of the status to display on Github. (optional)
 * @param description The description associated with the status.
 * @param status The status display on Github. (success, pending, failure, error)
 * @param org The organization the repository belongs to on Github. (optional)
 * @param repo The repository the status will belong to. (optional)
 * @param sha The commit sha the status will belong to. (optional)
 * @param target_url The target url for the status to be associated with. (optional, default: jenkins.env.BUILD_URL)
 *
 *
 * <pre>
 * example usage:
 *
 * pipeline {
 *   agent { label 'docker-slave' }
 *
 *   stages {
 *     stage ('update github status to pending') {
 *       steps {
 *         glGithubStatus credentialsId: "my_github_credential_reference_id",
 *                        description: "my custom description ",
 *                        context: 'jenkins/customStatus',
 *                        status: "pending",
 *                        org: "my_github_org",
 *                        repo: "my_github_repo",
 *                        sha: "${env.GIT_COMMIT}",
 *                        target_url: "${env.BUILD_URL}"
 *       }
 *     }
 *     stage ('update github status to jenkins build result') {
 *       steps {
 *         glGithubStatus credentialsId: "my_github_credential_reference_id",
 *                        description: "my custom description ",
 *                        context: 'jenkins/customStatus',
 *                        status: "${currentBuild.currentResult}",
 *                        org: "my_github_org",
 *                        repo: "my_github_repo",
 *                        sha: "${env.GIT_COMMIT}",
 *                        target_url: "${env.BUILD_URL}"
 *       }
 *     }
 *   }
 * }
 * </pre>
 */

def call(Map<String, Object> config){
  Github github = new Github(this)
  github.sendGitStatus(config)
}
