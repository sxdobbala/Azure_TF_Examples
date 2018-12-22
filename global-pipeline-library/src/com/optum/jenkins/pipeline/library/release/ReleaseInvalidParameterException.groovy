package com.optum.jenkins.pipeline.library.release

class ReleaseInvalidParameterException extends IllegalArgumentException {

  ReleaseInvalidParameterException(String message) {
    super(message)
  }

}
