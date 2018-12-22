package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.compliance.services.LDAP
import com.optum.jenkins.pipeline.library.compliance.services.Rally
import groovy.json.JsonSlurperClassic
import spock.lang.Specification

class CM1Spec extends Specification {
  def "getCM1 returns valid user stories with full valid JSONs"() {
    given: 'a CM1 object with mocked valid JSON responses'
    def cm1 = getMockedCm1(validMilestoneJson, validUserStoriesJson, validRevisionHistory, validUser)

    when: 'I call fetchCM1'
    def report = cm1.fetchReport()
    def userStories = report.userStories
    def errors = report.errors

    then: 'I should have a list of fetched user stories and no errors'
    userStories.size() == 1
    errors.size() == 0
    userStories.get(0).formattedId == "US123456789"
    userStories.get(0).sourceUrl == "https://rally1.rallydev.com/#/detail/userstory/123456789"
    userStories.get(0).name == "user story name"
    userStories.get(0).description == "user story description"
    userStories.get(0).acceptanceCriteria == "user story acceptance criteria"
    userStories.get(0).accepted.timestamp == '2018-09-11T21:03:16.145Z'
    userStories.get(0).accepted.acceptorName == "User Name"
    userStories.get(0).accepted.acceptorEmail == "user@email.com"
  }

  def "getCM1 returns valid user stories with valid null user story fields"() {
    given: 'a CM1 object with mocked valid JSON responses'
    def cm1 = getMockedCm1(validMilestoneJson, validNulledUserStoriesJson, validRevisionHistory, validUser)

    when: 'I call fetchCM1'
    def report = cm1.fetchReport()
    def userStories = report.userStories
    def errors = report.errors

    then: 'I should have a list of fetched user stories and no errors'
    userStories.size() == 1
    errors.size() == 0
    userStories.get(0).formattedId == "US123456789"
    userStories.get(0).sourceUrl == "https://rally1.rallydev.com/#/detail/userstory/123456789"
    userStories.get(0).name == "user story name"
    userStories.get(0).description == ""
    userStories.get(0).acceptanceCriteria == ""
    userStories.get(0).accepted.timestamp == "2018-09-11T21:03:16.145Z"
    userStories.get(0).accepted.acceptorName == "User Name"
    userStories.get(0).accepted.acceptorEmail == "user@email.com"
  }

  def "getCM1 returns valid user stories and an error if user story is not accepted"() {
    given: 'a CM1 object with mocked valid JSON responses'
    def cm1 = getMockedCm1(validMilestoneJson, validUserStoriesJson, validRevisionHistoryUnaccepted, null)

    when: 'I call fetchCM1'
    def report = cm1.fetchReport()
    def userStories = report.userStories
    def errors = report.errors

    then: 'I should have a list of fetched user stories as well as any found errors'
    userStories.size() == 1
    errors.size() == 1
    userStories.get(0).formattedId == "US123456789"
    userStories.get(0).sourceUrl == "https://rally1.rallydev.com/#/detail/userstory/123456789"
    userStories.get(0).name == "user story name"
    userStories.get(0).description == "user story description"
    userStories.get(0).acceptanceCriteria == "user story acceptance criteria"
    userStories.get(0).accepted == null
    errors.get(0).contains("not")
    errors.get(0).contains("accepted")
  }

  def "getCM1 returns valid user stories and an error if user story is no longer accepted"() {
    given: 'a CM1 object with mocked valid JSON responses'
    def cm1 = getMockedCm1(validMilestoneJson, validUserStoriesJson, validRevisionHistoryNoLongerAccepted, null)

    when: 'I call fetchCM1'
    def report = cm1.fetchReport()
    def userStories = report.userStories
    def errors = report.errors

    then: 'I should have a list of fetched user stories as well as any found errors'
    userStories.size() == 1
    errors.size() == 1
    userStories.get(0).formattedId == "US123456789"
    userStories.get(0).sourceUrl == "https://rally1.rallydev.com/#/detail/userstory/123456789"
    userStories.get(0).name == "user story name"
    userStories.get(0).description == "user story description"
    userStories.get(0).acceptanceCriteria == "user story acceptance criteria"
    userStories.get(0).accepted == null
    errors.get(0).contains("not")
    errors.get(0).contains("accepted")
  }

  def "getCM1 returns valid user stories and an error if user story is accepted by the wrong person"() {
    given: 'a CM1 object with mocked valid JSON responses'
    def cm1 = getMockedCm1(validMilestoneJson, validUserStoriesJson, validRevisionHistory, validUserUnapproved)

    when: 'I call fetchCM1'
    def report = cm1.fetchReport()
    def userStories = report.userStories
    def errors = report.errors

    then: 'I should have a list of user stories with the same content as the JSON'
    userStories.size() == 1
    errors.size() == 1
    userStories.get(0).formattedId == "US123456789"
    userStories.get(0).sourceUrl == "https://rally1.rallydev.com/#/detail/userstory/123456789"
    userStories.get(0).name == "user story name"
    userStories.get(0).description == "user story description"
    userStories.get(0).acceptanceCriteria == "user story acceptance criteria"
    userStories.get(0).accepted.timestamp == "2018-09-11T21:03:16.145Z"
    userStories.get(0).accepted.acceptorName == "Bad User"
    userStories.get(0).accepted.acceptorEmail == "bad.user@email.com"
    errors.get(0).contains("accepted")
    errors.get(0).contains("inappropriate")
  }

  /*
  def "playground"() {
    when: 'I do the thing'
    def (user, email) = (new LDAP()).fetchSecureMemberDetails("jchoi106")
    then: 'I should get the thing'
    println(user)
    println(email)
  }
  */

  def getMockedCm1(String milestoneJson, String userStoriesJson, String revisionsJson, String userJson) {
    String mockMilestoneFormattedId = "MI123456789"
    String mockApiWorkspace = "123456789"
    String mockApiUrl = "fake.url"
    String mockApiToken = "mockedToken"
    String userStoryApprovers = "approversGroup"
    CM1 cm1 = new CM1(mockApiUrl, mockApiToken, mockApiWorkspace, mockMilestoneFormattedId, userStoryApprovers)
    Rally mockedRally = new Rally(mockApiUrl, mockApiToken)
    mockedRally.metaClass.fetchMilestoneMap = { String milestoneFormattedId, String apiWorkspace ->
      return new JsonSlurperClassic().parseText(milestoneJson)
    }
    mockedRally.metaClass.fetchUserStoriesMap = { String milestoneObjectId, String apiWorkspace ->
      return new JsonSlurperClassic().parseText(userStoriesJson)
    }
    mockedRally.metaClass.fetchRevisionHistoryMap = { String revisionHistoryObjectId, String apiWorkspace ->
      return new JsonSlurperClassic().parseText(revisionsJson)
    }
    mockedRally.metaClass.fetchUserMap = { String userObjectId, String apiWorkspace ->
      return new JsonSlurperClassic().parseText(userJson)
    }
    LDAP mockedLDAP = new LDAP()
    mockedLDAP.metaClass.fetchSecureGroupMemberEmails = { String group ->
      return ["user@email.com"]
    }
    cm1.rallyService = mockedRally
    cm1.ldapService = mockedLDAP
    return cm1
  }

  def validMilestoneJson = """
{
  "ObjectID": 123456789,
  "Description": "milestone description\n[config]approvers_group=example_group[config]",
  "Name": "milestone name"
}
"""

  def validMilestoneNoConfig = """
{
  "ObjectID": 123456789,
  "Description": "milestone description, but no configuration to be found.",
  "Name": "milestone name"
}
"""

  def validUserStoriesJson = """
[
  {
    "FormattedID": "US123456789",
    "ObjectID": "123456789",
    "Name": "user story name",
    "Description": "user story description",
    "c_AcceptanceCriteria": "user story acceptance criteria",
    "RevisionHistory": {
      "_ref": "https://rally1.rallydev.com/slm/webservice/v2.0/revisionhistory/123456789"
    }
  }
]
"""

  def validNulledUserStoriesJson = """
[
  {
    "FormattedID": "US123456789",
    "ObjectID": "123456789",
    "Name": "user story name",
    "RevisionHistory": {
      "_ref": "https://rally1.rallydev.com/slm/webservice/v2.0/revisionhistory/123456789"
    }
  }
]
"""

  def validRevisionHistory = """
[
  {
    "CreationDate": "2018-09-11T21:03:16.145Z",
    "Description": "SCHEDULE STATE changed from [In-Progress] to [Accepted], FLOW STATE CHANGED DATE changed from [Tue Sep 11 15:03:13 MDT 2018] to [Tue Sep 11 15:03:16 MDT 2018], FLOW STATE changed from [In-Progress] to [Accepted], ACCEPTED DATE added [Tue Sep 11 15:03:16 MDT 2018]",
    "User": {
      "_ref": "https://rally1.rallydev.com/slm/webservice/v2.0/user/123456789"
    }
  },
  {
    "CreationDate": "2018-09-11T20:58:02.920Z",
    "Description": "Original revision",
    "User": {
      "_ref": "https://rally1.rallydev.com/slm/webservice/v2.0/user/123456789"
    }
  }
]
"""

  def validRevisionHistoryUnaccepted = """
[
  {
    "CreationDate": "2018-09-11T20:58:02.920Z",
    "Description": "Original revision",
    "User": {
      "_ref": "https://rally1.rallydev.com/slm/webservice/v2.0/user/123456789"
    }
  }
]
"""

  def validRevisionHistoryNoLongerAccepted = """
[
  {
    "CreationDate": "2018-09-11T21:03:50.485Z",
    "Description": "SCHEDULE STATE changed from [Accepted] to [Defined], FLOW STATE CHANGED DATE changed from [Tue Sep 11 15:03:16 MDT 2018] to [Tue Sep 11 15:03:50 MDT 2018], FLOW STATE changed from [Accepted] to [Defined], ACCEPTED DATE removed [Tue Sep 11 15:03:16 MDT 2018], IN PROGRESS DATE removed [Tue Sep 11 14:58:01 MDT 2018]",
    "User": {
      "_ref": "https://rally1.rallydev.com/slm/webservice/v2.0/user/123456789"
    }
  },
  {
    "CreationDate": "2018-09-11T21:03:16.145Z",
    "Description": "SCHEDULE STATE changed from [In-Progress] to [Accepted], FLOW STATE CHANGED DATE changed from [Tue Sep 11 15:03:13 MDT 2018] to [Tue Sep 11 15:03:16 MDT 2018], FLOW STATE changed from [In-Progress] to [Accepted], ACCEPTED DATE added [Tue Sep 11 15:03:16 MDT 2018]",
    "User": {
      "_ref": "https://rally1.rallydev.com/slm/webservice/v2.0/user/123456789"
    }
  },
  {
    "CreationDate": "2018-09-11T20:58:02.920Z",
    "Description": "Original revision",
    "User": {
      "_ref": "https://rally1.rallydev.com/slm/webservice/v2.0/user/00000000"
    }
  }
]
"""
  def validUser = """
{
  "DisplayName": "User Name",
  "EmailAddress": "user@email.com"
}
"""
  def validUserUnapproved = """
{
  "DisplayName": "Bad User",
  "EmailAddress": "bad.user@email.com"
}
"""

}

