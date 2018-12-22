package com.optum.jenkins.pipeline.library.utils.PropertyReader

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import spock.lang.Specification

class JenkinsfileReaderSpec extends Specification{
  def "Extract used libraries from jenkins env"(){
    given: 'Jenkins mocked to return test resource Jenkinsfile'
      def jenkins = [
        env: [ a : []],
        echo : {},
        fileExists: {Map map -> return true},
        readFile: { Map map -> return [] },
      ]
    when: "extracting libraries and calling the libraries again"
      def libraries = JenkinsfileReader.getLibraries(jenkins)
    then: "the libraries match from env values"
      libraries == []
  }

  def "Extract used libraries from root Jenkinsfile"(){
    given: 'Jenkins mocked to return test resource Jenkinsfile'
      def jenkins = [
        env: [ a : []],
        echo : {},
        fileExists: {Map map -> return true},
        readFile: { Map map -> return getClass().getResource(map.file).getText() },
      ]
    when: "extracting libraries"
      def libraries = JenkinsfileReader.getLibraries(jenkins)
    then: "the libraries match the ones declared in the Jenkinsfile"
      libraries.size() == 4
      with (libraries[0]){
        id == 'com.optum.jenkins.pipeline.library-DEV'
        version =='docs4DevopsEvents'
      }
      with (libraries[1]){
        id == 'my.special.custom.lib.abc'
        version =='v0.0.1'
      }
      with (libraries[2]){
        id == 'com.optum.jenkins.pipeline.library'
        version =='master'
      }
      with (libraries[3]){
        id == 'my.special.custom.lib'
        version =='unknown'
      }
      def librariesFromEnv = JenkinsfileReader.getLibraries(jenkins)
    then: "then the jenkins libraries are extracted from the jenkins env "
      librariesFromEnv.size() == 4
      with (librariesFromEnv[0]){
        id == 'com.optum.jenkins.pipeline.library-DEV'
        version =='docs4DevopsEvents'
      }
      with (librariesFromEnv[1]){
        id == 'my.special.custom.lib.abc'
        version =='v0.0.1'
      }
      with (librariesFromEnv[2]){
        id == 'com.optum.jenkins.pipeline.library'
        version =='master'
      }
      with (librariesFromEnv[3]){
        id == 'my.special.custom.lib'
        version =='unknown'
      }
  }


  def "Extract used libraries from Jenkinsfile in custom location"(){
    given: 'Jenkins mocked to return test resource Jenkinsfile with custom name/location'
      def jenkins = [
        env: [
          JENKINSFILE_LOCATION: 'JenkinsfileCustom'
        ],
        fileExists: {Map map -> return true},
        readFile: { Map map -> return getClass().getResource(map.file).getText() },
      ]
    when: "extracting libraries"
      def libraries = JenkinsfileReader.getLibraries(jenkins)
    then: "the libraries match the ones declared in the Jenkinsfile"
      libraries.size() == 8
      with (libraries[0]){
        id == 'com.optum.jenkins.123'
        version =='123'
      }
      with (libraries[1]){
        id == 'com.optum.jenkins'
        version =='v1.0.0'
      }
      with (libraries[2]){
        id == 'testing'
        version =='12'
      }
      with (libraries[3]){
        id == 'com.optum.jenkins.pipeline.library'
        version =='Feature'
      }
      with (libraries[4]){
        id == 'GlobalOptumPipeline123'
        version =='unknown'
      }
      with (libraries[5]){
        id == 'testing'
        version =='unknown'
      }
      with (libraries[6]){
        id == 'GlobalOptumPipeline456'
        version =='unknown'
      }
      with (libraries[7]){
        id == 'Global-Optum.Pipe_line'
        version =='unknown'
      }
  }

  def "Non existing custom Jenkinsfile location throws error"(){
    given: 'Jenkins mocked to return non existing Jenkinsfile location'
      def jenkins = [
        env: [
          JENKINSFILE_LOCATION: 'notHere'
        ],
        fileExists: {params -> return false},
        error: {msg -> throw new JenkinsErrorException(msg)}
      ]
    when: "extracting libraries"
      JenkinsfileReader.getLibraries(jenkins)
    then: "Error thrown because Jenkinsfile doesnt exist at custom location"
      JenkinsErrorException e = thrown()
      e.message.startsWith('Could not find Jenkinsfile at location')
  }

  def "No custom Jenkinsfile location configured and no Jenkinsfile in root raises error"(){
    given: 'Jenkins mocked with no custom Jenkinsfile location and no Jenkinsfile exists in project root'
      def jenkins = [
        env: [:],
        fileExists: {params -> return false},
        error: {msg -> throw new JenkinsErrorException(msg)}
      ]
    when: "extracting libraries"
      JenkinsfileReader.getLibraries(jenkins)
    then: "Error is raised because Jenkinsfile doesnt in project root"
      JenkinsErrorException e = thrown()
      e.message.startsWith('Jenkinsfile not found in project root directory.')
  }
}
