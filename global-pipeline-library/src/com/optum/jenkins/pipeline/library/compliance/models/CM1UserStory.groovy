package com.optum.jenkins.pipeline.library.compliance.models

class CM1UserStory implements Serializable {
  String formattedId
  String sourceUrl
  String name
  String description = null
  String acceptanceCriteria = null
  // null if not accepted
  CM1UserStoryAcceptance accepted = null
}
