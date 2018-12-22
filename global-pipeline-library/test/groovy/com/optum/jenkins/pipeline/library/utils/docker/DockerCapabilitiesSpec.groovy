package com.optum.jenkins.pipeline.library.utils.docker

import spock.lang.Specification
import spock.lang.Unroll

class DockerCapabilitiesSpec extends Specification {

  @Unroll
  def "--password-stdin not supported '#testName'"() {
    given:
    when: "I know docker doesn't support --password-stdin"
    then: "--password-stdin not supported"
    DockerCapabilities.supportsPasswordStdin(dockerVersion) == expectedResult
    where:
    testName          | dockerVersion     | expectedResult
    'empty version'   | ''                | false
    'docker=null'     | null              | false
    'docker=null'     | 'null'            | false
    'docker vAB.cd'   | 'AB.cd'           | false
    'docker v1.12'    | '1.12'            | false
    'docker v16'      | '16'              | false
    'docker v16.a'    | '16.a'            | false
    'docker v17'      | '17'              | false
    'docker v17.08'   | '17.08'           | false
    'docker v17.08.01'| '17.08.01'        | false
  }

  @Unroll
  def "--password-stdin supported '#testName'"() {
    given:
    when: "I know docker supports --password-stdin"
    then: "--password-stdin is supported"
    DockerCapabilities.supportsPasswordStdin(dockerVersion) == expectedResult
    where:
    testName          | dockerVersion     | expectedResult
    'docker v17.09'   | '17.09'           | true
    'docker v17.10'   | '17.10'           | true
    'docker v18'      | '18'              | true
    'docker v18.0'    | '18.0'            | true
    'docker v18.01'   | '18.01'           | true
    'docker v18.01.0' | '18.01.0'         | true
  }
}
