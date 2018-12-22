import com.optum.jenkins.pipeline.library.versions.Versions

/**
 * Bump semantic version according to the version bump level MAJOR/MINOR/PATCH
 * @param patchLevel valid values "MAJOR" "MINOR" "PATCH"(default), patch level will bump the specific version field
 * @param version version in format of semantic versioning MAJOR.MINOR.PATCH
 * @see <a href="https://semver.org/">https://semver.org/</a>
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
  version.deriveNextSemanticVersion(config)
}
