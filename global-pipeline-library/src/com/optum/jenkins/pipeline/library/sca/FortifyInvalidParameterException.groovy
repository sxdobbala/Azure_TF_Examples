package com.optum.jenkins.pipeline.library.sca

class FortifyInvalidParameterException extends IllegalArgumentException {

  FortifyInvalidParameterException(String message) {
    super(message)
  }

}
