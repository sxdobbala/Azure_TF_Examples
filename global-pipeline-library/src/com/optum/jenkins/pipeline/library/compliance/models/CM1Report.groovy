package com.optum.jenkins.pipeline.library.compliance.models

class CM1Report implements Serializable {
  String userStoryApprovers = null
  List<CM1UserStory> userStories = []
  List<String> errors = []
}
