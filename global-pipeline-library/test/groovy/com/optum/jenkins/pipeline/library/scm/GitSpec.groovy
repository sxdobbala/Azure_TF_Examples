package com.optum.jenkins.pipeline.library.scm

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import spock.lang.Specification

class GitSpec extends Specification {

  def "Test removal of 'origin/ branch prefix"() {
    given: 'Jenkins mocked to have /origin prefixed branch name variable'
    def jenkins = [
      env  : [GIT_BRANCH : 'origin/feature'],
      echo : {},
    ]
    when: "I try to get the git branch name"
      def git = new Git(jenkins)
      def branch = git.getBranch()

    then: "the branchname doesn't contain the prefix"
      branch == 'feature'
  }

  def "Test return of branch name from GIT_BRANCH variable"() {
    given: 'Jenkins mocked to have the GIT_BRANCH branch name variable'
      def jenkins = [
        env  : [GIT_BRANCH : 'feature'],
        echo : {},
      ]
    when: "I try to get the git branch name"
      def git = new Git(jenkins)
      def branch = git.getBranch()

    then: "the branchname 'feature' is returned"
      branch == 'feature'
  }

  def "Test git tagging with malformed URL"() {
    given: 'Jenkins mocked to have a git password, username'
      def calledJenkinsCommand
      def withCredentialsClosure
      def jenkins = [
        env             : [GIT_USERNAME : 'testUser', GIT_PASSWORD : 'testPass'],
        echo            : {},
        command         : { String cmd -> calledJenkinsCommand = cmd },
        withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
      ]
    when: 'I try to tag the branch'
      def git = new Git(jenkins)
      git.tagAndPush([credentialsId: 'test_cred', repoUrl: 'https://github.optum.com/testOrg/testRepo', tag: 'testTag', userName: 'testUser', userEmail: 'testMail'])
      withCredentialsClosure.call()

    then: 'tag command structured correctly'
      calledJenkinsCommand == "\n          git config user.email \"testMail\"\n          git config user.name \"testUser\"\n          git tag -a testTag -m Tagged by Jenkins\n          git push https://testUser:testPass@github.optum.com/testOrg/testRepo testTag\n        "
  }

  def "Test get git information command is structured correctly"() {
    given:
      def calledJenkinsCommand
      def jenkins = [
        command : { String cmd, Boolean output = true -> calledJenkinsCommand = [cmd,output] }
      ]
      
    when: "I run getRevision"
      def git = new Git(jenkins)
      def output = git.getRevision()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git rev-parse HEAD', true]
      
    when: "I run getRevisionShort"
      output = git.getRevisionShort()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git rev-parse --short HEAD', true]
      
    when: "I run getGitAuthorName"
      output = git.getGitAuthorName()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git --no-pager show -s --format=\'%an\'', true]
      
    when: "I run getGitAuthorEmail"
      output = git.getGitAuthorEmail()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git --no-pager show -s --format=\'%ae\'', true]
      
    when: "I run getGitAuthorDate"
      output = git.getGitAuthorDate()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git --no-pager show -s --format=\'%ai\'', true]
      
    when: "I run getGitCommitterName"
      output = git.getGitCommitterName()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git --no-pager show -s --format=\'%cn\'', true]
      
    when: "I run getGitCommitterEmail"
      output = git.getGitCommitterEmail()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git --no-pager show -s --format=\'%ce\'', true]
      
    when: "I run getGitCommitterDate"
      output = git.getGitCommitterDate()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git --no-pager show -s --format=\'%ci\'', true]
      
    when: "I run getGitSubject"
      output = git.getGitSubject()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git --no-pager show -s --format=\'%s\'', true]
      
    when: "I run getGitSanitizedSubject"
      output = git.getGitSanitizedSubject()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git --no-pager show -s --format=\'%f\'', true]
      
    when: "I run getRemoteUrl"
      output = git.getRemoteUrl()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == ['git config remote.origin.url', true]
  }

  def "Test get getSha command is structured correctly"() {
    given:
      def gitCommit = 'longhashforgitcommit'
      def calledJenkinsCommand
      def calledJenkinsEcho
      def jenkins = [
        env  : [GIT_COMMIT : gitCommit],
        echo : {String echoOut -> calledJenkinsEcho = ["fyi, this information is already available in GIT_COMMIT environment variable",
                                                       "env.GIT_COMMIT : ${gitCommit}"] },
        sh   : { def scriptParam = ['script':'git rev-parse HEAD', 'returnStdout':true] -> calledJenkinsCommand = scriptParam }
      ]
    when: "I run getSha"
      def git = new Git(jenkins)
      def output = git.getSha()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == [script:'git rev-parse HEAD', returnStdout:true]
  }

  def "Test get getGitTagNames command is structured correctly with SSH"() {
    given:
      def config = [gitCredentials: 'creds']
      def gitRemoteTagCmd = "git ls-remote --tags origin | awk '{print \$2}' | grep -v '\\^{}\$' | sort -r -V | sed 's@refs/tags/@@' "
      def calledJenkinsCommand
      def sshagentClosure
      def jenkins = [
        command : { String cmd, Boolean output = true -> calledJenkinsCommand = [cmd,output] },
        sshagent : { def a = [:], Closure c -> sshagentClosure = c }
      ]
      def gitSpy = GroovySpy(Git, global: true, useObjenesis: true)
      gitSpy.getRemoteUrl() >> 'git@github.optum.com:jenkins-pipelines/global-pipeline-library.git'
      
    when: "I run getGitTagNames"
      def git = new Git(jenkins)
      git.getGitTagNames(config)
      sshagentClosure.call()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == [gitRemoteTagCmd, true]
  }

  def "Test get getGitTagNames command failed without credential for SSH"() {
    given:
      def config = [gitCredentials: null]
      def errorMessage = 'If you\'re using ssh checkout, please provide credential id when calling this method. your credential is expected to be ssh key'
      def jenkins = [
        error       : { msg -> throw new JenkinsErrorException(msg) }
      ]
      def gitSpy = GroovySpy(Git, global: true, useObjenesis: true)
      gitSpy.getRemoteUrl() >> 'git@github.optum.com:jenkins-pipelines/global-pipeline-library.git'
      
    when: "I run getGitTagNames"
      def git = new Git(jenkins)
      git.getGitTagNames(config)
      
    then: "Exception is thrown"
      JenkinsErrorException e = thrown()
      e.message.contains(errorMessage)
  }

  def "Test get getGitTagNames command is structured correctly with credentialed HTTPS"() {
    given:
      def config = [gitCredentials: 'creds']
      def gitRemoteTagCmd = "git ls-remote --tags https://GIT_USERNAME:GIT_PASSWORD@github.optum.com/jenkins-pipelines/global-pipeline-library.git | awk '{print \$2}' | grep -v '\\^{}\$' | sort -r -V | sed 's@refs/tags/@@' "
      def calledJenkinsCommand
      def withCredentialsClosure
      def jenkins = [
        env             : [GIT_USERNAME:'GIT_USERNAME', GIT_PASSWORD:'GIT_PASSWORD'],
        command         : { String cmd, Boolean output = true -> calledJenkinsCommand = [cmd,output] },
        withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
      ]
      def expectedMap = "[[\$class:UsernamePasswordMultiBinding, credentialsId:credentials, usernameVariable:MAVEN_USER, passwordVariable:MAVEN_PASS]]"
      def gitSpy = GroovySpy(Git, global: true, useObjenesis: true)
      gitSpy.getRemoteUrl() >> 'https://github.optum.com/jenkins-pipelines/global-pipeline-library.git'
      
    when: "I run getGitTagNames"
      def git = new Git(jenkins)
      git.getGitTagNames(config)
      withCredentialsClosure.call()
      
    then: "The command is structured correctly"
      calledJenkinsCommand == [gitRemoteTagCmd, true]
  }
}
