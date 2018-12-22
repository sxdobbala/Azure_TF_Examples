package com.optum.jenkins.pipeline.library.release

import org.kohsuke.github.GHPullRequest
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import org.kohsuke.github.GHRelease
import spock.lang.Specification


class GithubReleaseSpec extends Specification {

  def "jenkins context is available"() {
    given: "Default jenkins context"
    def jenkins = [echo: {}]
    when: 'Creating class with jenkins context'
    def githubRelease = new GithubRelease(jenkins)
    then: "Jenkins context is available"
    githubRelease.getJenkins() == jenkins
  }

  def "when username is not passed, throws ReleaseInvalidParameterException"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            token         : "tokentoken",
            repositoryName: "owner/repo",
            tagName       : "v1.0",
            draft         : false,
            commitish     : "master",
            releaseName   : "This is a test release"
    ]
    when: 'validate parameters is called with incorrect parameters'
    def githubRelease = new GithubRelease(jenkins)
    GitHub github = GroovySpy(GitHub, global: true, useObjenesis: true)
    github.connectToEnterpriseWithOAuth(_) >> github
    githubRelease.validateParameters(config)
    then: 'Exception is thrown'
    def e = thrown(Exception)
    e.message.contains("githubUsername cannot be blank. Please pass the githubUsername in config.githubUsername parameter.")
  }


  def "when token is not passed, throws ReleaseInvalidParameterException"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            githubUsername: "uname",
            repositoryName: "owner/repo",
            tagName       : "v1.0",
            draft         : false,
            commitish     : "master",
            releaseName   : "This is a test release"
    ]
    when: 'validate parameters is called with incorrect parameters'
    def githubRelease = new GithubRelease(jenkins)
    GitHub github = GroovySpy(GitHub, global: true, useObjenesis: true)
    github.connectToEnterpriseWithOAuth(_) >> github
    githubRelease.validateParameters(config)
    then: 'Exception is thrown'
    def e = thrown(Exception)
    e.message.contains("ERROR: token cannot be blank. Please pass the token in config.token parameter.")
  }


  def "when repo name is not passed, throws ReleaseInvalidParameterException"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            token         : "tokentoken",
            githubUsername: "uname",
            tagName       : "v1.0",
            draft         : false,
            commitish     : "master",
            releaseName   : "This is a test release"
    ]
    when: 'validate parameters is called with incorrect parameters'
    def githubRelease = new GithubRelease(jenkins)
    GitHub github = GroovySpy(GitHub, global: true, useObjenesis: true)
    github.connectToEnterpriseWithOAuth(_) >> github
    githubRelease.validateParameters(config)
    then: 'Exception is thrown'
    def e = thrown(Exception)
    e.message.contains("ERROR: repositoryName cannot be blank. Please pass the RepositoryName in config.repositoryName parameter.")
  }

  def "when tag name is not passed, throws ReleaseInvalidParameterException"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            token         : "tokentoken",
            githubUsername: "uname",
            repositoryName: "owner/repo",
            draft         : false,
            commitish     : "master",
            releaseName   : "This is a test release"
    ]
    when: 'validate parameters is called with incorrect parameters'
    def githubRelease = new GithubRelease(jenkins)
    GitHub github = GroovySpy(GitHub, global: true, useObjenesis: true)
    GHRepository repo = GroovySpy(GHRepository, global: true, useObjenesis: true)
    github.connectToEnterpriseWithOAuth(_) >> github
    github.getRepository(_) >> repo
    githubRelease.validateParameters(config)
    then: 'Exception is thrown'
    def e = thrown(Exception)
    e.message.contains("ERROR: tagName cannot be blank. Please pass the tagName in config.tagName parameter.")
  }


  def "when invalid username and token is passed, throws ReleaseInvalidParameterException"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            token         : "tokentoken",
            githubUsername: "uname",
            repositoryName: "owner/repo",
            tagName       : "v1.0",
            draft         : false,
            commitish     : "master",
            releaseName   : "This is a test release"
    ]
    when: 'validate parameters is called with incorrect parameters'
    def githubRelease = new GithubRelease(jenkins)
    GitHub github = GroovySpy(GitHub, global: true, useObjenesis: true)
    github.connectToEnterpriseWithOAuth(_) >> github
    githubRelease.releaseGitHubRepo(config)
    then: 'Exception is thrown'
    def e = thrown(ReleaseInvalidParameterException)
    e.message.contains("ERROR 404: Unauthorized exception, please pass the correct github username and github token.")
  }



  def "when description is passed, description is used"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            description: "This is a test release."
    ]
    when: 'buildDescription is called'
    def githubRelease = new GithubRelease(jenkins)
    GHRepository gitRepo = GroovySpy(GHRepository, global: true, useObjenesis: true)
    def desc = githubRelease.buildDescription(config, gitRepo)
    then: "description is returned from the config"
    assert desc.toString() == config.description
  }

  def "when there are no pull requests are merged, throws ReleaseInvalidParameterException"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            a : ""
    ]
    when: 'buildDescription is called'
    def githubRelease = new GithubRelease(jenkins)
    GHRepository gitRepo = GroovySpy(GHRepository, global: true, useObjenesis: true)
    gitRepo.getLatestRelease() >> null
    Date date = GroovySpy(Date, global: true, useObjenesis: true)
    gitRepo.getCreatedAt() >> date
    gitRepo.getPullRequests(_) >> null
    githubRelease.buildDescription(config, gitRepo)
    then: "exception is thrown"
    def e = thrown(Exception)
    e.message.contains("ERROR: No merged pull requests to add to the release.")

  }

  def "when the description is not passed, it gets generated"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            a : ""
    ]
    when: 'buildDescription is called'
    def githubRelease = new GithubRelease(jenkins)
    GHRepository gitRepo = GroovySpy(GHRepository, global: true, useObjenesis: true)
    GHRelease gitRelease = GroovySpy(GHRelease, global: true, useObjenesis: true)
    Date date1 = new Date()
    Date date2 = date1.plus(30)
    gitRepo.getLatestRelease() >> gitRelease
    gitRelease.getPublished_at() >> date1
    GHPullRequest PR1 = GroovySpy(GHPullRequest, global: true, useObjenesis: true)
    def PRList = [PR1]
    PR1.getMergedAt() >> date2
    PR1.getNumber() >> 20
    PR1.getTitle() >> "Test PR"
    gitRepo.getPullRequests(_) >> PRList
    def desc = githubRelease.buildDescription(config, gitRepo)
    then: "description gets created from PRs"
    assert desc.toString() == "This release contains \n#20:Test PR\n"
  }
}