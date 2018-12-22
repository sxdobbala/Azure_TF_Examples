package com.optum.jenkins.pipeline.library.compliance

class ComplianceInvalidParameterException extends IllegalArgumentException implements Serializable {

  ComplianceInvalidParameterException(String message) {
    super(message)
  }

}
