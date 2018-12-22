package com.optum.jenkins.pipeline.library.compliance.models

class CM2ManualTestCaseExecution implements Serializable {
  String timestamp
  String testerName
  String testerEmail
  boolean passed
}
