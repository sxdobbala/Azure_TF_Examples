package com.optum.jenkins.pipeline.library.compliance

import spock.lang.Specification

class ElasticSearchRestSpec extends Specification {
  def "invalid params throws an error"() {
    given: 'create a elastic search rest object '
    ElasticSearchRest rest = new ElasticSearchRest()
    when: 'I call getInvalidProperties()'
    def invalids = rest.getInvalidProperties([:])
    then: 'throws error'
    invalids == [index:null, buildUrl:null]
  }

  def "valid input params returns valid response"() {
    given: 'create a elastic search rest object '
    ElasticSearchRest rest = new ElasticSearchRest()
    rest.config = [index:'devops.test', buildUrl:"jenkins.optum.com/abcd"]
    when: 'I call getBody()'
    Map valid = rest.getBody()
    then: 'returns valid response'
    valid.records.value.index.contains("devops.test")
    valid.records.value.buildUrl.contains("jenkins.optum.com/abcd")
  }


}
