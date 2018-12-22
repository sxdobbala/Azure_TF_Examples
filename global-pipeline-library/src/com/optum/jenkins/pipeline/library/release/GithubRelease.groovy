package com.optum.jenkins.pipeline.library.release

import com.optum.jenkins.pipeline.library.utils.Constants
import com.optum.jenkins.pipeline.library.utils.JenkinsPlugins.WorkflowBasicSteps
import org.kohsuke.github.GHIssueState
import org.kohsuke.github.GitHub
import org.kohsuke.github.GHRelease
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GHRepository


class GithubRelease implements Serializable {

  def jenkins

  GithubRelease() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
            'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  GithubRelease(jenkins) {
    this.jenkins = jenkins
  }

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
  def releaseGitHubRepo(Map<String, Object> params) {
    def defaults = [
            draft              : false,     // true if the release is draft, default false
            preRelease         : false,     // true if the release is a pre-release, default false
            commitish          : "master",  // branch or sha of a commit to cut the release from
    ]
    def config = defaults + params
    jenkins.echo ("Config Parameters : " + config.toString())
    validateParameters(config)
    GitHub github = GitHub.connectToEnterpriseWithOAuth(Constants.GITHUB_API_URL, config.githubUsername, config.token)

    if (github == null || !github.isCredentialValid()) {
      throw new ReleaseInvalidParameterException("ERROR 404: Unauthorized exception, please pass the correct github username and github token.")
    }

    GHRepository repository
    try {
      repository = github.getRepository(config.repositoryName)
      if (repository == null) {
        throw new ReleaseInvalidParameterException("ERROR: Repository name "+ config.repositoryName +" is not valid. Please pass the repository name in this format \'{owner/reponame}\', ex: \'devops-engineering/demo-maven-app\' ")
      }
    } catch (Exception ex) {
      jenkins.echo(ex.getMessage())
      throw new ReleaseInvalidParameterException("ERROR: Unexpected exception when fetching the repository. Please check the repository name you are passing")
    }

    for ( tag in repository.listTags()) {
      if (tag.getName() == config.tagName) {
        throw new ReleaseInvalidParameterException("ERROR: tagName already exists.")
      }
    }

    if (config.containsKey('descriptionFile') && config.descriptionFile != "" && WorkflowBasicSteps.fileExists(jenkins, [file: config.descriptionFile])) {
        config.description = WorkflowBasicSteps.readFile(jenkins, [file: config.descriptionFile])
    }

    StringBuffer description = buildDescription(config, repository)
    GHReleaseBuilder releaseBuilder = repository.createRelease(config.tagName)
            .name(config.containsKey("releaseName")? config.releaseName : config.tagName)
            .prerelease(config.preRelease)
            .body(description.toString())
            .draft(config.draft)
            .commitish(config.commitish)

    final GHRelease release = releaseBuilder.create()
    jenkins.echo ("Created release " + release.getHtmlUrl())
  }

  StringBuffer buildDescription(Map<String, Serializable> config, GHRepository repository) {
    Date pastReleasePublishDate
    def description = new StringBuffer()
    if (config.containsKey('description') && config.description != "") {
      description.append(config.description)
    } else {
      if (repository.getLatestRelease() == null) {
        pastReleasePublishDate = repository.getCreatedAt()
      } else {
        pastReleasePublishDate = repository.getLatestRelease().getPublished_at()
      }
      def hasMergedPullRequests = false
      description.append("This release contains \n")
      for (pullRequest in repository.getPullRequests(GHIssueState.CLOSED)) {
        if (pullRequest.getMergedAt() > pastReleasePublishDate) {
          hasMergedPullRequests = true
            description.append("#" + pullRequest.getNumber())
            description.append(":")
            description.append(pullRequest.getTitle())
            description.append("\n")
        }
      }
      if (!hasMergedPullRequests)  {
        throw new ReleaseInvalidParameterException ("ERROR: No merged pull requests to add to the release.")
      }
    }
    description
  }

  def validateParameters(config) {
    if (!config.containsKey('githubUsername') || config.githubUsername == "")  {
      throw new ReleaseInvalidParameterException("ERROR: githubUsername cannot be blank. Please pass the githubUsername in config.githubUsername parameter.")
    }
    if (!config.containsKey('token') || config.token == "")  {
      throw new ReleaseInvalidParameterException("ERROR: token cannot be blank. Please pass the token in config.token parameter.")
    }
    if (!config.containsKey('repositoryName') || config.repositoryName == "")  {
      throw new ReleaseInvalidParameterException("ERROR: repositoryName cannot be blank. Please pass the RepositoryName in config.repositoryName parameter.")
    }
    if (!config.containsKey('tagName') || config.tagName == "")  {
      throw new ReleaseInvalidParameterException("ERROR: tagName cannot be blank. Please pass the tagName in config.tagName parameter.")
    }
  }
}
