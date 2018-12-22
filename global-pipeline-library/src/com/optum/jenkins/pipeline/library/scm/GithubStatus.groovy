package com.optum.jenkins.pipeline.library.scm;

/**
 * Valid status values to pass to Github.
 */ 
enum GithubStatus {
  SUCCESS, PENDING, ERROR, FAILURE;

  static GithubStatus validateStatus(String inStatus) {
    return GithubStatus.valueOf(inStatus.toUpperCase());
  }
}