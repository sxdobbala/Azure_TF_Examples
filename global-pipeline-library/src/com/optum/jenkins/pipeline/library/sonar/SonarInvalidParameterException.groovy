package com.optum.jenkins.pipeline.library.sonar

class SonarInvalidParameterException extends IllegalArgumentException {

  SonarInvalidParameterException(String message) {
    super(message)
  }

}
