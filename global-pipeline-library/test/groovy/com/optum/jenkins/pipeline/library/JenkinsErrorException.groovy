package com.optum.jenkins.pipeline.library

//Marker Exception to throw in jenkins mocks instead of jenkins.error
class JenkinsErrorException extends GroovyRuntimeException{

  def JenkinsErrorException() {
    super()
  }

 def JenkinsErrorException(String message) {
    super(message)
  }

}
