package com.optum.jenkins.pipeline.library.compliance.models

class CM2Report implements Serializable {
  CM2ManualTestResults manualTestResults
  List<CM2AutomatedTestSummary> automatedTestSummaries
  List<CM2SecuritySummary> securitySummaries
  List<String> errors = []
}
