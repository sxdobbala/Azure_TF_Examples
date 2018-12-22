package com.optum.jenkins.pipeline.library.compliance.models

class CM2AutomatedTestSummary implements Serializable {
  String projectName
  String type
  BigDecimal failThreshold
  BigDecimal passedPercent
  int totalScenarios
  int executedCases
  int passedCases
  int failedCases
  String timestamp

}
