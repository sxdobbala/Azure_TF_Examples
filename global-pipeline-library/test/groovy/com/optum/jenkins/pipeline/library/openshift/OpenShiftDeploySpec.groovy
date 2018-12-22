package com.optum.jenkins.pipeline.library.openshift

import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.sonar.Sonar
import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import spock.lang.Specification

class OpenShiftDeploySpec extends Specification {

  def "jenkins context is available"(){
    given: "Default jenkins context"
    def jenkins = [echo : 'hello']
    when: 'Creating class with jenkins context'
    def openshift = new OpenShift(jenkins)
    then: "Jenkins context is available"
    openshift.getJenkins() == jenkins
  }

  def "error for missing jenkins context"(){
    when: 'Creating class without jenkins context'
    def openshift = new OpenShift()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  def "targets project"(){
    given:
    def calledJenkinsCommand
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED: 'false'],
      echo    : { String s -> println(s) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() }
    ]
    def project = 'projectname'
    def params = [
      credentials: 'credentials',
      dockerImage: 'docker.optum.com/org/repo:latest',
      project: project,
      ocpUrl: 'https://ocp-ctc-core-nonprod.optum.com',
      port: '8080',
      serviceName: 'serviceName',
      tag: 'latest',
      wait: false,
    ]
    when: "Deploy is called"
    def openshift = new OpenShift(jenkins)
    openshift.deploy(params)
    then: "Script targets the correct project"
    noExceptionThrown()
    calledJenkinsCommand.toString().contains('oc project ' + project)
  }

  def "identifies service"(){
    given:
    def calledJenkinsCommand
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED: 'false'],
      echo    : { String s -> println(s) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() }
    ]
    def serviceName = 'longservice_NameWithCapsAndTooManyChars'
    def correctedServiceName = serviceName.toLowerCase().substring(0, 23).replace('_','-')
    def params = [
      credentials: 'credentials',
      dockerImage: 'docker.optum.com/org/repo:latest',
      project: 'projectname',
      ocpUrl: 'https://ocp-ctc-core-nonprod.optum.com',
      port: '8080',
      serviceName: serviceName,
      wait: false,
    ]
    when: "Deploy is called"
    def openshift = new OpenShift(jenkins)
    openshift.deploy(params)
    //print calledJenkinsCommand.toString()
    then: "Script targets the correct service"
    noExceptionThrown()
    calledJenkinsCommand.toString().contains('oc get rc | grep ' + correctedServiceName)
    calledJenkinsCommand.toString().contains('oc expose svc ' + correctedServiceName)
  }

  def "overwrites tag of image with tag parameter, 3.6"(){
    given:
    def calledJenkinsCommand
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED: 'false'],
      echo    : { String s -> println(s) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() }
    ]
    def dockerImage = 'docker.optum.com/org/repo:somethingelse'
    def correctedDockerImage = dockerImage.split(':')[0]
    def tag = 'latest'
    def params = [
      credentials: 'credentials',
      dockerImage: dockerImage,
      project: 'projectname',
      ocpUrl: 'https://ocp-ctc-core-nonprod.optum.com',
      port: '8080',
      serviceName: 'serviceName',
      tag: tag,
      wait: false,
    ]
    when: "Deploy is called"
    def openshift = new OpenShift(jenkins)
    openshift.deploy(params)
    then: "Script pulls in the correct image"
    noExceptionThrown()
    calledJenkinsCommand.toString().contains('oc new-app --docker-image=' + correctedDockerImage + ':' + tag)
    calledJenkinsCommand.toString().contains('oc import-image ' + 'servicename:' + tag)
  }

  def "identifies image without tag, 3.6"(){
    given: "Project"
    def calledJenkinsCommand
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED : 'false'],
      echo    : { String s -> println(s) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() }
    ]
    def dockerImage = 'docker.optum.com/aps/eap64:2.0'
    def serviceName = 'servicename2'
    def tag = 'tag2'
    def dockerImageWithTag = dockerImage.split(':')[0] + ':' + tag
    def params = [
      credentials: 'credentials',
      dockerImage: dockerImage,
      project: 'projectname',
      ocpUrl: 'https://ocp-ctc-core-nonprod.optum.com',
      port: '8080',
      serviceName: serviceName,
      wait: false,
      tag: tag
    ]
    when: "Deploy is called"
    def openshift = new OpenShift(jenkins)
    openshift.deploy(params)
    then: "Script targets the correct project"
    noExceptionThrown()
    calledJenkinsCommand.toString().contains('oc new-app --docker-image=' + dockerImageWithTag)
    calledJenkinsCommand.toString().contains('oc import-image ' + serviceName + ':' + tag)
  }

  def "overwrites tag of image with tag parameter, 3.2"(){
    given:
    def calledJenkinsCommand
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED : 'false'],
      echo    : { String s -> println(s) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() }
    ]
    def dockerImage = 'docker.optum.com/org/repo:somethingelse'
    def correctedDockerImage = dockerImage.split(':')[0]
    def tag = 'something'
    def params = [
      credentials: 'credentials',
      dockerImage: dockerImage,
      project: 'projectname',
      ocpUrl: 'https://ose-ctc-core.optum.com',
      port: '8080',
      serviceName: 'serviceName',
      tag: tag,
      wait: false,
    ]
    when: "Deploy is called"
    def openshift = new OpenShift(jenkins)
    openshift.deploy(params)
    then: "Script pulls in the correct image"
    noExceptionThrown()
    calledJenkinsCommand.toString().contains('oc new-app --docker-image=' + correctedDockerImage + ':' + tag)
  }

  def "identifies image without tag, 3.2"(){
    given: "Project"
    def calledJenkinsCommand
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED: 'false'],
      echo    : { String s -> println(s) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() }
    ]
    def dockerImage = 'docker.optum.com/org/repo:latest'
    def params = [
      credentials: 'credentials',
      dockerImage: dockerImage,
      project: 'projectname',
      ocpUrl: 'https://ose-ctc-core.optum.com',
      port: '8080',
      serviceName: 'serviceName',
      wait: false,
    ]
    when: "Deploy is called"
    def openshift = new OpenShift(jenkins)
    openshift.deploy(params)
    then: "Script targets the correct project"
    noExceptionThrown()
    calledJenkinsCommand.toString().contains('oc new-app --docker-image=' + dockerImage)
  }

  def "template deploy with no templateFile"(){
    given: "no templateFile"
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED: 'false']
    ]
    def params = [
      credentials: 'credentials',
      project: 'projectname',
      ocpUrl: 'https://ose-ctc-core.optum.com'
    ]
    when: "processTemplate() is called"
    def openshift = new OpenShift(jenkins)
    openshift.processTemplate(params)
    then:
    thrown(java.lang.Exception)
  }

  def "template initial deploy"(){
    given: "template given and blank return of the jenkins.sh"
    def calledJenkinsSh
    def template = 'file.yml'
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED: 'false'],
      echo            : { String s -> println(s) },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
      command         : { String a, Boolean b -> calledJenkinsSh += a },
      withEnv         : { java.util.ArrayList a, Closure c -> c.call() },
    ]
    def params = [
      credentials: 'credentials',
      project: 'projectname',
      ocpUrl: 'https://ose-ctc-core.optum.com',
      templateFile: template
    ]
    when: "processTemplate() is called"
    def openshift = new OpenShift(jenkins)
    openshift.processTemplate(params)
    then:
    noExceptionThrown()
    calledJenkinsSh.contains('oc process -f ' + template)
  }

  def "template continued deploy"(){
    given: "template given and non-blank return of the jenkins.sh"
    def calledJenkinsSh
    def template = 'file.yml'
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED: 'false'],
      echo            : { String s -> println(s) },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
      command         : { String a, Boolean b -> calledJenkinsSh = a + calledJenkinsSh},
      withEnv         : { java.util.ArrayList a, Closure c -> c.call() },
    ]
    def params = [
      credentials: 'credentials',
      project: 'projectname',
      ocpUrl: 'https://ose-ctc-core.optum.com',
      templateFile: template,
      production: false
    ]
    when: "processTemplate() is called"
    def openshift = new OpenShift(jenkins)
    openshift.processTemplate(params)
    then:
    noExceptionThrown()
    calledJenkinsSh.contains('oc replace --force')
    calledJenkinsSh.contains('oc process -f ' + template)
  }

  def "template deploy no force"(){
    given: "template given and non-blank return of the jenkins.sh"
    def calledJenkinsSh
    def template = 'file.yml'
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED: 'false'],
      echo            : { String s -> println(s) },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
      command         : { String a, Boolean b -> calledJenkinsSh = a + calledJenkinsSh},
      withEnv         : { java.util.ArrayList a, Closure c -> c.call() },
    ]
    def params = [
      credentials: 'credentials',
      project: 'projectname',
      ocpUrl: 'https://ose-ctc-core.optum.com',
      templateFile: template,
      force: false,
      production: false
    ]
    when: "processTemplate() is called"
    def openshift = new OpenShift(jenkins)
    openshift.processTemplate(params)
    then:
    noExceptionThrown()
    !calledJenkinsSh.contains('--force')
    calledJenkinsSh.contains('oc process -f ' + template)
  }

  def "template deploy no force and annotate"(){
    given: "template given and non-blank return of the jenkins.sh"
    def calledJenkinsSh
    def template = 'file.yml'
    def jenkins = [
      env     : [DEVOPS_METRICS_ENABLED: 'false'],
      echo            : { String s -> println(s) },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
      command         : { String a, Boolean b -> calledJenkinsSh = a + calledJenkinsSh},
      withEnv         : { java.util.ArrayList a, Closure c -> c.call() },
    ]
    def params = [
      credentials: 'credentials',
      project: 'projectname',
      ocpUrl: 'https://ose-ctc-core.optum.com',
      templateFile: template,
      force: false,
      annotations: ['test-annotation-key':'test-annotation-value'],
      production: false
    ]

    def ocAnnotations = ""
    params?.annotations?.each{ annotationKey, annotationValue ->
      ocAnnotations += " ${annotationKey}=${annotationValue} "
    }

    when: "processTemplate() is called"
    def openshift = new OpenShift(jenkins)
    openshift.processTemplate(params)
    then:
    noExceptionThrown()
    !calledJenkinsSh.contains('--force')
    calledJenkinsSh.contains('oc process -f ' + template)
    calledJenkinsSh.contains('oc annotate --overwrite ' + ocAnnotations + ' -f -')
  }

  def "apply annotations for openshift object"(){
    given: "template given and non-blank return of the jenkins.sh"
    def calledJenkinsSh
    def template = 'file.yml'
    def jenkins = [
      env             : [],
      echo            : { String s -> println(s) },
      withCredentials : { java.util.ArrayList a, Closure c -> c.call() },
      command         : { String a, Boolean b -> calledJenkinsSh = a + calledJenkinsSh},
      withEnv         : { java.util.ArrayList a, Closure c -> c.call() },
    ]
    def params = [
      credentials: 'credentials',
      project: 'projectname',
      ocpUrl: 'https://ose-ctc-core.optum.com',
      templateFile: template,
      force: false,
      annotations: ['test-annotation-key':'test-annotation-value']
    ]

    def ocAnnotations = ""
    params?.annotations?.each{ annotationKey, annotationValue ->
      ocAnnotations += " ${annotationKey}=${annotationValue} "
    }

    when: "processTemplate() is called"
    def openshift = new OpenShift(jenkins)
    openshift.applyAnnotations(params)
    then:
    noExceptionThrown()
    calledJenkinsSh.contains('oc process -f ' + template)
    calledJenkinsSh.contains('oc annotate --overwrite ' + ocAnnotations + ' -f -')
  }

  def "sendDeployEvent no error"() {
    given:
    def jenkins =[
      echo : {},
      env  : []
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: 'sendDeployEvent is called'
    def openshift = new OpenShift(jenkins)
    openshift.sendDeployEvent(jenkins, new Date(), EventStatus.SUCCESS,'jenkins','dev',false, null)
    then: 'No exception is thrown'
    noExceptionThrown()
  }
}
