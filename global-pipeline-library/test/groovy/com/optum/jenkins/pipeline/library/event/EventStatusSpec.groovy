package com.optum.jenkins.pipeline.library.event

import spock.lang.Specification

class EventStatusSpec extends Specification {

  def 'Enum has all required values'() {
    when: 'I access all enum values'
      def values = EventStatus.enumConstants
    then: 'The values match SUCCESS, FAILURE, UNSTABLE, ABORTED, STARTED, APPROVED, TIMEOUT'
      values.toArrayString() == '[SUCCESS, FAILURE, UNSTABLE, ABORTED, STARTED, APPROVED, TIMEOUT]'
  }

  def 'Looking up an enum using the jenkins status'() {
    when: "I request the enum value based on the jenkins status 'SUCCESS'"
      def successValue = EventStatus.mapFromJenkinsStatus("SUCCESS")
    then: "I get back the EventStatus enum 'SUCCESS"
      successValue == EventStatus.SUCCESS
  }
}
