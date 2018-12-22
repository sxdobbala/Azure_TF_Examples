package com.optum.jenkins.pipeline.library.compliance.models

class CM2ManualTestCase implements Serializable {
  String formattedId
  String userStoryName
  String sourceUrl
  String userStoryFormattedId
  CM2ManualTestCaseExecution executed = null
}
