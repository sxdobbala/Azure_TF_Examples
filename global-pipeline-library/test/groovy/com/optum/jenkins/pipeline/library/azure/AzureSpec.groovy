package com.optum.jenkins.pipeline.library.azure

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import spock.lang.Specification
import spock.lang.Unroll

class AzureSpec extends Specification{
  def "jenkins context is available"() {
    given: "Default jenkins context"
    def jenkins = [echo: 'hello']
    when: 'Creating class with jenkins context'
    def azure = new Azure(jenkins)
    then: "Jenkins context is available"
    azure.getJenkins() == jenkins
  }

  def "error for missing jenkins context"() {
    when: 'Creating class without jenkins context'
    def azure = new Azure()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  @Unroll
  def "buildDeployDocker exception if no '#testName'"() {
    given:
    def jenkins = [
      echo        : {},
      env         : [],
      error       : { msg -> throw new JenkinsErrorException(msg) },
      command     : { String cmd -> 'nop' },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    when: "I run buildDeployDocker with missing required parameters"
    def azure = new Azure(jenkins)
    azure.buildDeployDocker(config)
    then: "An exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(expectedMsg)
    where:
    testName         | config                                         | expectedMsg
    'loginServer'    | [credentialsId:'credId',image:'image']         | 'The login server is required'
    'credentialsId'  | [loginServer:'server',image:'image']           | 'The credentials id to push to azure container registry is required'
    'image'          | [credentialsId:'credId',loginServer:'server']  | 'The image name is required'
  }

  @Unroll
  def "buildDeployDocker build commands structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def withCredentialsClosure
    def jenkins = [
      env             : [BUILD_NUMBER: '1'],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    when: "I run buildDeployDocker"
    def azure = new Azure(jenkins)
    azure.buildDeployDocker(config)
    withCredentialsClosure.call()
    then: "The build commands are structured correctly"
    allCommandsCalled.toString().contains(expectedCmd)
    where:
    testName            | config                                                                                    | expectedCmd
    'default'           | [loginServer:'loginServer',credentialsId:'credId',image:'image']                          | 'docker build --pull  image:1 .'
    'requirePullFalse'  | [loginServer:'loginServer',credentialsId:'credId',image:'image',requirePull:false]        | 'docker build   image:1 .'
    'extraBuildOptions' | [loginServer:'loginServer',credentialsId:'credId',image:'image',extraBuildOptions:'opts'] | 'docker build --pull opts image:1 .'
    'baseDir'           | [loginServer:'loginServer',credentialsId:'credId',image:'image',baseDir:'base']           | 'docker build --pull  image:1 base'
  }

  @Unroll
  def "buildDeployDocker tag command structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def withCredentialsClosure
    def jenkins = [
      env             : [BUILD_NUMBER: '1'],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    when: "I run buildDeployDocker"
    def azure = new Azure(jenkins)
    azure.buildDeployDocker(config)
    withCredentialsClosure.call()
    then: "The tag command is structured correctly"
    allCommandsCalled.toString().contains(expectedCmd)
    where:
    testName  | config                                                            | expectedCmd
    'default' | [loginServer:'loginServer',credentialsId:'credId',image:'image']  | 'docker tag image:1 loginServer/image:1'
  }

  @Unroll
  def "buildDeployDocker login command structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def withCredentialsClosure
    def jenkins = [
      env             : [BUILD_NUMBER: '1', AZURE_USER: 'user', AZURE_PASS: 'pw'],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    when: "I run buildDeployDocker"
    def azure = new Azure(jenkins)
    azure.buildDeployDocker(config)
    withCredentialsClosure.call()
    then: "The login command is structured correctly"
    allCommandsCalled.toString().contains(expectedCmd)
    where:
    // Test for positive, negative and outlier scenarios
    testName        | config                                                                                 | expectedCmd
    'default'       | [loginServer:'loginServer',credentialsId:'credId',image:'image']                       | "docker login -u user -p 'pw' loginServer"
    'docker=blank'  | [loginServer:'loginServer',credentialsId:'credId',image:'image',dockerVersion:'']      | "docker login -u user -p 'pw' loginServer"    
    'docker=null'   | [loginServer:'loginServer',credentialsId:'credId',image:'image',dockerVersion:null]    | "docker login -u user -p 'pw' loginServer"    
    'docker=ab.cd'  | [loginServer:'loginServer',credentialsId:'credId',image:'image',dockerVersion:'ab.cd'] | "docker login -u user -p 'pw' loginServer"    
    'docker=16'     | [loginServer:'loginServer',credentialsId:'credId',image:'image',dockerVersion:'16']    | "docker login -u user -p 'pw' loginServer"
    'docker<17.09'  | [loginServer:'loginServer',credentialsId:'credId',image:'image',dockerVersion:'17.08'] | "docker login -u user -p 'pw' loginServer"
    'docker>=17.09' | [loginServer:'loginServer',credentialsId:'credId',image:'image',dockerVersion:'17.09'] | "echo -n 'pw' | docker login -u user --password-stdin loginServer"
  }

  @Unroll
  def "buildDeployDocker push command structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def withCredentialsClosure
    def jenkins = [
      env             : [BUILD_NUMBER: '1', AZURE_USER: 'user', AZURE_PASS: 'pw'],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    when: "I run buildDeployDocker"
    def azure = new Azure(jenkins)
    azure.buildDeployDocker(config)
    withCredentialsClosure.call()
    then: "The push command is structured correctly"
    allCommandsCalled.toString().contains(expectedCmd)
    where:
    testName           | config                                                                                   | expectedCmd
    'default'          | [loginServer:'loginServer',credentialsId:'credId',image:'image']                         | 'docker push  loginServer/image:1'
    'extraPushOptions' | [loginServer:'loginServer',credentialsId:'credId',image:'image',extraPushOptions:'opts'] | 'docker push opts loginServer/image:1'
  }

  def "buildDeployDocker credentials set"() {
    given:
    def withCredentialsMap
    def jenkins = [
      env             : [BUILD_NUMBER: '1'],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a }
    ]
    def expectedMap = "[[\$class:UsernamePasswordMultiBinding, credentialsId:credId, usernameVariable:AZURE_USER, passwordVariable:AZURE_PASS]]"
    when: "I run buildDeployDocker"
    def azure = new Azure(jenkins)
    def config = [loginServer:'loginServer',credentialsId:'credId',image:'image']
    azure.buildDeployDocker(config)
    then: "Credentials are set"
    withCredentialsMap.toString() == expectedMap
  }

  @Unroll
  def "deploy exception if no '#testName'"() {
    given:
    def jenkins = [
      echo        : {},
      env         : [],
      error       : { msg -> throw new JenkinsErrorException(msg) },
      command     : { String cmd -> 'nop' },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    when: "I run deploy with missing required parameters"
    def azure = new Azure(jenkins)
    azure.deploy(config)
    then: "An exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(expectedMsg)
    where:
    testName            | config                                                                                                                        | expectedMsg
    'resourceGroup'     | [clusterName:'c',deployConfig:'d',appName:'a',loginServer:'l',azureClientId:'a',azureClientSecret:'a',azureTenant:'a']        | 'The resource group is required'
    'clusterName'       | [resourceGroup:'r',deployConfig:'d',appName:'a',loginServer:'l',azureClientId:'a',azureClientSecret:'a',azureTenant:'a']      | 'The name of your Kubernetes cluster is required'
    'deployConfig'      | [resourceGroup:'r',clusterName:'c',appName:'a',loginServer:'l',azureClientId:'a',azureClientSecret:'a',azureTenant:'a']       | 'The path to your yaml deployment config must be specified'
    'appName'           | [resourceGroup:'r',clusterName:'c',deployConfig:'d',loginServer:'l',azureClientId:'a',azureClientSecret:'a',azureTenant:'a']  | 'The name of your app is required'
    'loginServer'       | [resourceGroup:'r',clusterName:'c',deployConfig:'d',appName:'a',azureClientId:'a',azureClientSecret:'a',azureTenant:'a']      | 'The login server is required'
    'azureClientId'     | [resourceGroup:'r',clusterName:'c',deployConfig:'d',appName:'a',loginServer:'l',azureClientSecret:'a',azureTenant:'a']        | 'Azure client id required'
    'azureClientSecret' | [resourceGroup:'r',clusterName:'c',deployConfig:'d',appName:'a',loginServer:'l',azureClientId:'a',azureTenant:'a']            | 'Azure client secret required'
    'azureTenant'       | [resourceGroup:'r',clusterName:'c',deployConfig:'d',appName:'a',loginServer:'l',azureClientId:'a',azureClientSecret:'a']      | 'Azure tenant id required'
  }

  @Unroll
  def "deploy az and kubectl commands structured correctly '#testName'"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def withCredentialsClosure
    def jenkins = [
      env             : [ARM_CLIENT_ID: 'client', ARM_CLIENT_SECRET: 'secret', ARM_TENANT_ID: 'tenant'],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    when: "I run deploy"
    def azure = new Azure(jenkins)
    azure.deploy(config)
    withCredentialsClosure.call()
    then: "The az and kubectl commands are structured correctly"
    allCommandsCalled.toString().contains(expectedCmd)
    where:
    testName            | config                                                                                                                                    | expectedCmd
    'login'             | [resourceGroup:'r',clusterName:'c',deployConfig:'d',appName:'a',loginServer:'l',azureClientId:'a',azureClientSecret:'a',azureTenant:'a']  | "az login --service-principal -u client -p 'secret' --tenant tenant"
    'az credentials'    | [resourceGroup:'r',clusterName:'c',deployConfig:'d',appName:'a',loginServer:'l',azureClientId:'a',azureClientSecret:'a',azureTenant:'a']  | 'az aks get-credentials --resource-group r --name c'
    'deployCmd'         | [resourceGroup:'r',clusterName:'c',deployConfig:'d',appName:'a',loginServer:'l',azureClientId:'a',azureClientSecret:'a',azureTenant:'a',containerName:'x',imageName:'y',imageTag:'z']  | 'if kubectl get deployment a | grep -w \'a\'; then\n' +
                                                                                                                                                                      '        echo "Deployment exists, updating image..."\n' +
                                                                                                                                                                      '        kubectl set image deployment/a x=l/y:z\n' +
                                                                                                                                                                      '      else\n' +
                                                                                                                                                                      '        echo "Deployment does not exist, applying config..."\n' +
                                                                                                                                                                      '        kubectl apply -f d\n' +
                                                                                                                                                                      '      fi\n' +
                                                                                                                                                                      '        echo "Deployment or redeployment complete"'
  }

  def "deploy credentials set"() {
    given:
    def withCredentialsMap
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a }
    ]
    when: "I run deploy"
    def azure = new Azure(jenkins)
    def config = [resourceGroup:'r',clusterName:'c',deployConfig:'d',appName:'a',loginServer:'l',azureClientId:'a',azureClientSecret:'a',azureTenant:'a']
    azure.deploy(config)
    then: "Credentials are set"
    withCredentialsMap.toString().contains('ARM_CLIENT_ID')
    withCredentialsMap.toString().contains('ARM_CLIENT_SECRET')
    withCredentialsMap.toString().contains('ARM_TENANT_ID')
  }
}
