package com.optum.jenkins.pipeline.library.utils

//Marker Exception to throw in jenkins mocks instead of jenkins.error
class JenkinsMockErrorException extends GroovyRuntimeException{

  def JenkinsMockErrorException() {
    super()
  }

 def JenkinsMockErrorException(String message) {
    super(message)
  }

}
