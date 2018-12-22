import com.optum.jenkins.pipeline.library.versions.Versions

/**
 * Get latest semantic version from git tags
 * @param gitCredentials String required if one of these condition is fulfiledd:
 *        jenkins is configured to use ssh checkout, credential type is expected to be ssh key
 *        jenkins is configured to use https checkout to private repo, credential type is expected to be username/password
 * @param gitTagPrefix String optional, default empty. if you're using github recommended prefix, provide 'v'
 * @param defaultInitialVersion String optional, default to '0.0.0'
 *
 * <pre>
 * example usage:
 * NOTE: AN AGENT IS REQUIRED FOR THIS GLOBAL FUNCTION
 * pipeline {
 *   agent { label 'docker-slave' }
 *   environment {
 *     GIT_TAG_PREFIX = 'v'
 *     LAST_RELEASE_VERSION = glVersionsGetLatestSemanticVersionFromTag gitTagPrefix : "${env.GIT_TAG_PREFIX}"
 *     NEXT_VERSION = glVersionsBump version : "${env.LAST_RELEASE_VERSION}", patchLevel: 'minor'
 *   }
 *
 *   stages {
 *     stage ('version info') {
 *       steps {
 *         echo "LAST_RELEASE_VERSION : ${env.LAST_RELEASE_VERSION}"
 *         echo "NEXT_VERSION : ${env.NEXT_VERSION}"
 *       }
 *     }
 *   }
 * }
 * </pre>
 */
def call(Map<String, Object> config){
  Versions version = new Versions(this)
  this.echo "config : $config"
  version.deriveLatestSemanticVersionFromGitTag(config)
}
