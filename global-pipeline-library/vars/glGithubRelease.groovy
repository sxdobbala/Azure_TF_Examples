import com.optum.jenkins.pipeline.library.release.GithubRelease

/**
 * Creates a Github Release
 * @param token String to authenticate github, this token should have permissions to create a release in the github project
 * @param repositoryName String repository to create the release for
 * @param tagName String name of the tag to be created
 * @param preRelease boolean true if the release is preRelease and will not be published, default is false
 * @param draft boolean true if the release is draft, default is false
 * @param commitish String specifies the commitish value where the git tag is created from, default is master
 * @param descriptionFile String file contents will be used as the description
 * @param description String description for the release
 * @param releaseName string name for the release, if this is not passed, tagName will be used for releaseName
 *
 */

def call(Map<String, Object> config){
  GithubRelease release = new GithubRelease(this)
  release.releaseGitHubRepo(config)
}