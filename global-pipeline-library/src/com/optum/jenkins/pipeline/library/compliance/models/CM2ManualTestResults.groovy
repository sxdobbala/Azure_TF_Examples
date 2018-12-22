package com.optum.jenkins.pipeline.library.compliance.models

class CM2ManualTestResults implements Serializable {
  BigDecimal failThreshold
  List<CM2ManualTestCase> testCases
  BigDecimal passedPercent
}
