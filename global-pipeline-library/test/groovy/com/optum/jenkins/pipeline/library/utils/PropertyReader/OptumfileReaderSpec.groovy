package com.optum.jenkins.pipeline.library.utils.PropertyReader

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

class OptumfileReaderSpec extends Specification {

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Successfull reading api version from Optumfile'() {
    def jenkins = [env: [:]]
    given: 'I read the Optumfile (configuration file containing project meta data)'
    OptumFileReader.metaClass.static.getOptumFile = {
      return new Yaml().load(getClass().getResource(OptumFileReader.OPTUMFILE_NAME).getText())
    }
    when: 'I retrieve the api version property'
      def version = OptumFileReader.getApiVersion(jenkins)
    then: 'I see value v1'
    version == 'v1'
  }

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Error when missing api version'() {
    def jenkins = [error: { msg -> throw new JenkinsErrorException(msg) },  env: [:]]
    given: 'I read an empty Optumfile'
    OptumFileReader.metaClass.static.getOptumFile = { [:] }
    when: 'I retrieve the api version property'
    OptumFileReader.getApiVersion(jenkins)
    then: 'I get an error'
    JenkinsErrorException e = thrown()
    e.message.contains('apiVersion')
  }

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Read CAAgile id from Optumfile'() {
    def jenkins = [env: [:]]
    given:
    OptumFileReader.metaClass.static.getOptumFile = {
      return new Yaml().load(getClass().getResource(OptumFileReader.OPTUMFILE_NAME).getText())
    }
    when:
    def cid = OptumFileReader.getCAAgileId(jenkins)
    then:
    cid == 'poc'
  }

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Read ASK id from Optumfile'() {
    def jenkins = [env: [:]]
    given:
    OptumFileReader.metaClass.static.getOptumFile = {
      return new Yaml().load(getClass().getResource(OptumFileReader.OPTUMFILE_NAME).getText())
    }
    when:
      def aid = OptumFileReader.getAskId(jenkins)
    then:
    aid == 'poc'
  }

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Error when missing ASK id'() {
    def jenkins = [error: { msg -> throw new JenkinsErrorException(msg) }, env: [: ]]
    given: 'I read an empty Optumfile'
    OptumFileReader.metaClass.static.getOptumFile = { [:] }
    when: 'I retrieve the ASK id property'
    OptumFileReader.getAskId(jenkins)
    then: 'I get an error'
    JenkinsErrorException e = thrown()
    e.message.contains('askId')
  }

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Read projectKey from Optumfile'() {
    def jenkins = [env: [:]]
    given:
    OptumFileReader.metaClass.static.getOptumFile = {
      return new Yaml().load(getClass().getResource(OptumFileReader.OPTUMFILE_NAME).getText())
    }
    when:
      def projectKey = OptumFileReader.getProjectKey(jenkins)
    then:
    projectKey == 'com.optum.oa.devopseng:Bodgeit'
  }

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Read projectFriendlyName from Optumfile'() {
    def jenkins = [env: [:]]
    given:
    OptumFileReader.metaClass.static.getOptumFile = {
      return new Yaml().load(getClass().getResource(OptumFileReader.OPTUMFILE_NAME).getText())
    }
    when:
    def projectFriendlyName = OptumFileReader.getProjectFriendlyName(jenkins)
    then:
    projectFriendlyName == 'DevOpsEng-Bodgeit'
  }

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Error when missing projectKey'() {
    def jenkins = [error: { msg -> throw new JenkinsErrorException(msg) }, env : [:]]
    given: 'I read an empty Optumfile'
    OptumFileReader.metaClass.static.getOptumFile = { [:] }
    when: 'I retrieve the projectKey property'
    OptumFileReader.getProjectKey(jenkins)
    then: 'I get an error'
    JenkinsErrorException e = thrown()
    e.message.contains('projectKey')
  }

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Read component type from Optumfile'() {
    def jenkins = [env: [ : ]]
    given:
    OptumFileReader.metaClass.static.getOptumFile = {
      return new Yaml().load(getClass().getResource(OptumFileReader.OPTUMFILE_NAME).getText())
    }
    when:
      def type = OptumFileReader.getComponentType(jenkins)
    then:
    type == OptumFileReader.ComponentType.CODE
  }

  @ConfineMetaClassChanges([OptumFileReader])
  def 'Error when missing component type'() {
    def jenkins = [error: { msg -> throw new JenkinsErrorException(msg) }, env : [:]]
    given: 'I read an empty Optumfile'
    OptumFileReader.metaClass.static.getOptumFile = { [:] }
    when: 'I retrieve the component type property'
    OptumFileReader.getComponentType(jenkins)
    then: 'I get an error'
    JenkinsErrorException e = thrown()
    e.message.contains('componentType')
  }

  def 'Reading optumfile successfully'() {
    given: 'I read the test resource Optumfile'
      def jenkins = [
        readYaml: { Map props -> new Yaml().load(getClass().getResource(props.file).getText()) },
        echo: {},
        env: [:]
      ]
    when: 'I create a yaml object from the Optumfile'
    def fileContent = OptumFileReader.getOptumFile(jenkins)
    then: 'It cointains the api version v1'
    fileContent.apiVersion == 'v1'
  }

  def 'No Optumfile, read Optumfile environment config instead'() {
    given: 'I read the test resource Optumfile'
    def jenkins = [
      readYaml: { null },
      echo    : {},
      env     : [
        apiVersion: 'v1'
      ]
    ]
    when: 'I have the optumfile as environment config'
    def apiVersion = OptumFileReader.getApiVersion(jenkins)
    then: 'It contains the api version v1'
    apiVersion == 'v1'
  }

  def 'Reading non existing Optumfile results in error message'() {
    given: 'I read a non existing Optumfile'
    def jenkins = [
      readYaml: { Map props ->
        if (props.file == 'Optumfile.yml') {
          return null
        } else if (props.text != null) {
          return new Yaml().load(props.text)
        } else {
          throw new RuntimeException("unexpected readYaml call")
        }
      },
      libraryResource: { path ->
        if (path.contains('Template')) {
          getClass().getResource('OptumfileTemplate_v1').getText()
        } else if (path.contains('Config')) {
          getClass().getResource('OptumFileReaderConfig.groovy').getText()
        }
      },
      error: { msg -> throw new JenkinsErrorException(msg) },
      echo: {},
      env: [:]
    ]
    when: 'I create a yaml object from the Optumfile'
    OptumFileReader.getOptumFile(jenkins)
    then: 'I get an error message'
    JenkinsErrorException e = thrown()
    e.message.contains("----------- SNIPPET Optumfile.yml -----------\napiVersion: v1")
  }

  /**
   * The package location of resource files for OptumFileReader is hard coded in OptumFileReader.OPTUMFILEREADER_PATH
   * If the packages changes, then the hardcoded value needs to be changed too
   *
   */
  def 'Package of OptumFileReader matches package configured in OptumFileReader.OPTUMFILEREADER_PATH'(){
    when: 'Reading OptumFileReader configuration'
    then: 'Hardcoded OptumFileReader.OPTUMFILEREADER_PATH matches package of OptumFileReader'
    getClass().getPackage().toString().replace('.', '/') + '/'.equals(OptumFileReader.OPTUMFILEREADER_PATH)
  }

}
