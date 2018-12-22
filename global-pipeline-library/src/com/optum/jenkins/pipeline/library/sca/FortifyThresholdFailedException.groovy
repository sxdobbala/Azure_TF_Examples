package com.optum.jenkins.pipeline.library.sca

class FortifyThresholdFailedException extends Exception {

  FortifyThresholdFailedException(String message) {
    super(message)
  }

}
