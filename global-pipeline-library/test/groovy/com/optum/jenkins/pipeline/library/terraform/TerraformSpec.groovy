package com.optum.jenkins.pipeline.library.terraform

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.event.TerraformEvent
import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import spock.lang.Specification
import spock.lang.Unroll
import com.optum.jenkins.pipeline.library.utils.Constants

class TerraformSpec extends Specification {

  def "jenkins context is available"() {
    given: "Default jenkins context"
    def jenkins = [echo: 'hello']
    when: 'Creating class with jenkins context'
    def terraform = new Terraform(jenkins)
    then: "Jenkins context is available"
    terraform.getJenkins() == jenkins
  }

  def "error for missing jenkins context"() {
    when: 'Creating class without jenkins context'
    def terraform = new Terraform()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  @Unroll
  def "terraformInit command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> calledJenkinsCommand = cmd },
      withEnv: { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    when: "I run terraformInit"
    def terraform = new Terraform(jenkins)
    terraform.terraformInit(config)
    withEnvClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName          | config                             | expectedCmd
    'default'         | [:]                                | 'terraform init '
    'additionalFlags' | [additionalFlags: ['input': true]] | 'terraform init -input="true" '
    'reconfigure'     | [reconfigure: true]                | 'terraform init -reconfigure'
  }

  def "terraformInit TERRAFORM_VERSION set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> 'nop' },
      withEnv: { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def expectedMap = '[TERRAFORM_VERSION=' +Constants.TERRAFORM_VERSION +']'
    when: "I run terraformInit"
    def terraform = new Terraform(jenkins)
    def config = [:]
    terraform.terraformInit(config)
    then: "TERRAFORM_VERSION set"
    withEnvMap.toString() == expectedMap
  }

  @Unroll
  def "terraformPlan command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> calledJenkinsCommand = cmd },
      withEnv: { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    when: "I run terraformPlan"
    def terraform = new Terraform(jenkins)
    terraform.terraformPlan(config)
    withEnvClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName          | config                             | expectedCmd
    'default'         | [:]                                | 'terraform plan '
    'additionalFlags' | [additionalFlags: ['input': true]] | 'terraform plan -input="true" '
  }

  def "terraformPlan TERRAFORM_VERSION set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> 'nop' },
      withEnv: { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def expectedMap = '[TERRAFORM_VERSION=' +Constants.TERRAFORM_VERSION +']'
    when: "I run terraformPlan"
    def terraform = new Terraform(jenkins)
    def config = [:]
    terraform.terraformPlan(config)
    then: "TERRAFORM_VERSION set"
    withEnvMap.toString() == expectedMap
  }

  @Unroll
  def "terraformApply command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> calledJenkinsCommand = cmd },
      withEnv: { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def terraformEvent = GroovySpy(TerraformEvent, global: true, useObjenesis: true)
    terraformEvent.send() >> 'nop'
    when: "I run terraformApply"
    def terraform = new Terraform(jenkins)
    terraform.terraformApply(config)
    withEnvClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName          | config                             | expectedCmd
    'default'         | [:]                                | 'terraform apply -auto-approve'
    'additionalFlags' | [additionalFlags: ['input': true]] | 'terraform apply -input="true" -auto-approve'
    'autoApprove'     | [autoApprove: false]               | 'terraform apply '
  }

  def "terraformApply TERRAFORM_VERSION set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> 'nop' },
      withEnv: { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def terraformEvent = GroovySpy(TerraformEvent, global: true, useObjenesis: true)
    terraformEvent.send() >> 'nop'
    def expectedMap = '[TERRAFORM_VERSION=' +Constants.TERRAFORM_VERSION +']'
    when: "I run terraformApply"
    def terraform = new Terraform(jenkins)
    def config = [:]
    terraform.terraformApply(config)
    then: "TERRAFORM_VERSION set"
    withEnvMap.toString() == expectedMap
  }

  @Unroll
  def "terraformDestroy command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def withEnvClosure
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> calledJenkinsCommand = cmd },
      withEnv: { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    when: "I run terraformDestroy"
    def terraform = new Terraform(jenkins)
    terraform.terraformDestroy(config)
    withEnvClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName          | config                             | expectedCmd
    'default'         | [:]                                | 'terraform destroy -force'
    'additionalFlags' | [additionalFlags: ['input': true]] | 'terraform destroy -input="true" -force'
    'force'           | [force: false]                     | 'terraform destroy '
  }

  def "terraformDestroy TERRAFORM_VERSION set"() {
    given:
    def withEnvMap
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> 'nop' },
      withEnv: { java.util.ArrayList a, Closure c -> withEnvMap = a }
    ]
    def terraformEvent = GroovySpy(TerraformEvent, global: true, useObjenesis: true)
    terraformEvent.send() >> 'nop'
    def expectedMap = '[TERRAFORM_VERSION=' +Constants.TERRAFORM_VERSION +']'
    when: "I run terraformDestroy"
    def terraform = new Terraform(jenkins)
    def config = [:]
    terraform.terraformDestroy(config)
    then: "TERRAFORM_VERSION set"
    withEnvMap.toString() == expectedMap
  }

  @Unroll
  def "cloudConfig aws command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def withCredentialsClosure
    def jenkins = [
      echo   : {},
      env    : [CLOUD_USER: 'user', CLOUD_PASS: 'pw'],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> calledJenkinsCommand = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    when: "I run cloudConfig for aws"
    def terraform = new Terraform(jenkins)
    terraform.cloudConfig(config)
    withCredentialsClosure.call()
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName       | config                                                          | expectedCmd
    'aws default'  | [cloudProvider:'aws', region:'region', credentialsId:'credId']  | '\n              aws configure set aws_access_key_id user\n' +
                                                                                         '              aws configure set aws_secret_access_key pw\n' +
                                                                                         '              aws configure set default.region region\n            '
  }

  @Unroll
  def "cloudConfig azure command is structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def jenkins = [
      echo   : {},
      env    : [CLOUD_USER: 'user', CLOUD_PASS: 'pw'],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> calledJenkinsCommand = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    when: "I run cloudConfig for azure"
    def terraform = new Terraform(jenkins)
    terraform.cloudConfig(config)
    then: "The command is structured correctly"
    calledJenkinsCommand == expectedCmd
    where:
    testName         | config                   | expectedCmd
    'azure default'  | [cloudProvider:'azure']  | 'az login'
  }

  @Unroll
  def "cloudConfig error for missing required parameter '#testName'"() {
    given:
    def calledJenkinsCommand
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> calledJenkinsCommand = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    when: "I run cloudConfig with missing required paramaters"
    def terraform = new Terraform(jenkins)
    terraform.cloudConfig(config)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    testName           | config                                         | errorMessage
    'cloudProvider'    | [:]                                            | 'Cloud provider flag is required'
    'aws credentials'  | [cloudProvider:'aws', region:'region']         | 'Credentials id required'
    'aws region'       | [cloudProvider:'aws', credentialsId:'credId']  | 'Region required'
  }

  def "cloudConfig credentials for aws set"() {
    given:
    def withCredentialsMap
    def jenkins = [
      echo   : {},
      env    : [],
      error  : { msg -> throw new JenkinsErrorException(msg) },
      command: { String cmd -> 'nop' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a }
    ]
    def expectedMap = "[[\$class:UsernamePasswordMultiBinding, credentialsId:credId, usernameVariable:CLOUD_USER, passwordVariable:CLOUD_PASS]]"
    when: "I run cloudConfig for aws"
    def terraform = new Terraform(jenkins)
    def config = [cloudProvider:'aws', region:'region', credentialsId:'credId']
    terraform.cloudConfig(config)
    then: "credentials are set"
    withCredentialsMap.toString() == expectedMap
  }

  def "sendCloudEvent no error"() {
    given:
    def jenkins =[
      echo : {}
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: 'sendCloudEvent is called'
    def terraform = new Terraform(jenkins)
    terraform.sendCloudEvent(jenkins, new Date(),'dev','maven', EventStatus.SUCCESS )
    then: 'No exception is thrown'
    noExceptionThrown()
  }
}