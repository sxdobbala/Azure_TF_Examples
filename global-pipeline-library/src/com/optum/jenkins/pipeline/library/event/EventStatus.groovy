package com.optum.jenkins.pipeline.library.event;

enum EventStatus {
  SUCCESS, FAILURE, UNSTABLE, ABORTED, STARTED, APPROVED, TIMEOUT;

  static EventStatus mapFromJenkinsStatus(String jenkinsStatus) {
    //identical right now
    return EventStatus.valueOf(jenkinsStatus);
  }
}
