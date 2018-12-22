package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.compliance.models.AgileReleaseScope
import com.optum.jenkins.pipeline.library.compliance.models.CM2Report
import com.optum.jenkins.pipeline.library.compliance.services.Rally
import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import com.optum.jenkins.pipeline.library.utils.http.RestClient
import groovy.json.JsonSlurperClassic
import spock.lang.Specification

class ComplianceCheckSpec extends Specification {

  def "Empty required config raises error when milestone is null"(){
    def jenkins = [echo : {}]
    given: 'a ComplianceCheck object is created'
    ComplianceCheck complianceCheck = new ComplianceCheck(jenkins)
    when: 'the validateCompliance() method is called'
    def cm1Json = complianceCheck.validateCM1CM2Compliance(['rallyApiToken':'1234'])
    then: "throws ComplianceInvalidParameterException"
    def ex = thrown(ComplianceInvalidParameterException)
    //assert ex.message.contains("Error...Project key is required")
    assert ex.message.contains("ERROR: rallyMilestoneId cannot be blank. Please pass the rallyMilestoneId in config.rallyMilestoneId parameter.")
  }

  def "Empty required config raises error when rallyApiKey is null"(){
    def jenkins = [echo : {}]
    given: 'a ComplianceCheck object is created'
    ComplianceCheck complianceCheck = new ComplianceCheck(jenkins)
    when: 'the validateCompliance() method is called'
    def cm1Json = complianceCheck.validateCM1CM2Compliance(['rallyMilestoneId':'MI001'])
    then: "throws ComplianceInvalidParameterException"
    def ex = thrown(ComplianceInvalidParameterException)
    //assert ex.message.contains("Error...Project key is required")
    assert ex.message.contains("ERROR: rallyApiToken cannot be blank. Please pass the rallyApiToken in config.rallyApiToken parameter.")
  }


}

