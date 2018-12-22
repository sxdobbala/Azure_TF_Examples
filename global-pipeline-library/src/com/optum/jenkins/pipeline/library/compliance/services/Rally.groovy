package com.optum.jenkins.pipeline.library.compliance.services

import com.cloudbees.groovy.cps.NonCPS
import com.google.gson.Gson
import com.optum.jenkins.pipeline.library.compliance.ComplianceInvalidParameterException
@Grab('com.rallydev.rest:rally-rest-api:2.2.1')
import com.rallydev.rest.RallyRestApi
import com.rallydev.rest.request.QueryRequest
import com.rallydev.rest.util.QueryFilter
import groovy.json.JsonSlurperClassic
import org.acegisecurity.acls.NotFoundException

class Rally implements Serializable {
  int PAGE_SIZE = 2000
  String apiUrl
  String apiToken

  Rally(String apiUrl, String apiToken) {
    this.apiUrl = apiUrl
    this.apiToken = apiToken
  }

  @NonCPS
  Map fetchMilestoneMap(String milestoneFormattedId, String apiWorkspace) {

    def workspaceObjectRef = '/workspace/' + apiWorkspace

    def milestoneRequest = new QueryRequest('milestone')
    milestoneRequest.setWorkspace(workspaceObjectRef)
    milestoneRequest.setPageSize(PAGE_SIZE)
    milestoneRequest.setQueryFilter(new QueryFilter('FormattedID', '=', milestoneFormattedId))
    def milestoneResponse = new RallyRestApi(new URI(apiUrl), apiToken).query(milestoneRequest)
    if (!milestoneResponse.wasSuccessful()) {
      throw new ComplianceInvalidParameterException('Rally API milestone request unsuccessful: ' + milestoneResponse.getErrors())
    }
    if (milestoneResponse.getResults().size() == 0) {
      throw new ComplianceInvalidParameterException('Milestone not found.')
    }

    def gson = new Gson()
    def jsonSlurper = new JsonSlurperClassic()
    println(milestoneRequest.toUrl())
    return (Map)jsonSlurper.parseText(gson.toJson(milestoneResponse.getResults().get(0)))
  }

  @NonCPS
  List fetchUserStoriesMap(String milestoneObjectId, String apiWorkspace) {
    def milestoneObjectRef = '/milestone/' + milestoneObjectId
    def workspaceObjectRef = '/workspace/' + apiWorkspace

    def userStoriesRequest = new QueryRequest('HierarchicalRequirement')
    userStoriesRequest.setWorkspace(workspaceObjectRef)
    userStoriesRequest.setPageSize(PAGE_SIZE)
    def directMilestoneFilter = new QueryFilter('Milestones', 'contains', milestoneObjectRef)
    userStoriesRequest.setQueryFilter(directMilestoneFilter)

    // Removed feature filter
    /* def featureMilestoneFilter = new QueryFilter('Feature.Milestones', 'contains', milestoneObjectRef)
    userStoriesRequest.setQueryFilter(directMilestoneFilter.or(featureMilestoneFilter)) */

    def userStoriesResponse = new RallyRestApi(new URI(apiUrl), apiToken).query(userStoriesRequest)
    if (!userStoriesResponse.wasSuccessful()) {
      throw new NotFoundException('Rally API user story request unsuccessful: ' + userStoriesResponse.getErrors())
    }
    def gson = new Gson()
    def jsonSlurper = new JsonSlurperClassic()
    return (List)jsonSlurper.parseText(gson.toJson(userStoriesResponse.getResults()))
  }

  @NonCPS
  List fetchTestCasesMap(String userStoryObjectId, String apiWorkspace) {
    def userStoryObjectRef = '/hierarchicalrequirement/' + userStoryObjectId
    def workspaceObjectRef = '/workspace/' + apiWorkspace

    def testCasesRequest = new QueryRequest('testcase')
    testCasesRequest.setWorkspace(workspaceObjectRef)
    testCasesRequest.setPageSize(PAGE_SIZE)
    def userStoryFilter = new QueryFilter('WorkProduct', '=', userStoryObjectRef)
    testCasesRequest.setQueryFilter(userStoryFilter)
    def testCasesResponse = new RallyRestApi(new URI(apiUrl), apiToken).query(testCasesRequest)
    if (!testCasesResponse.wasSuccessful()) {
      throw new NotFoundException('Rally API user story request unsuccessful: ' + testCasesResponse.getErrors())
    }
    def gson = new Gson()
    def jsonSlurper = new JsonSlurperClassic()
    return (List)jsonSlurper.parseText(gson.toJson(testCasesResponse.getResults()))
  }

  @NonCPS
  Map fetchLatestTestCaseExecution(String testCaseObjectId, String apiWorkspace) {
    def testCaseRef = '/testcase/' + testCaseObjectId
    def workspaceObjectRef = '/workspace/' + apiWorkspace

    def testCaseExecutionHistoryRequest = new QueryRequest('testcaseresult')
    testCaseExecutionHistoryRequest.setWorkspace(workspaceObjectRef)
    testCaseExecutionHistoryRequest.setPageSize(PAGE_SIZE)
    def testCaseFilter = new QueryFilter('testcase', '=', testCaseRef)
    testCaseExecutionHistoryRequest.setQueryFilter(testCaseFilter)
    testCaseExecutionHistoryRequest.setOrder('Build DESC')
    def testCaseExecutionHistoryResponse = new RallyRestApi(new URI(apiUrl), apiToken).query(testCaseExecutionHistoryRequest)
    if (!testCaseExecutionHistoryResponse.wasSuccessful()) {
      throw new NotFoundException('Rally API revision history request unsuccessful: ' + testCaseExecutionHistoryResponse.getErrors())
    }
    if (testCaseExecutionHistoryResponse.results.size() > 0) {
      def gson = new Gson()
      def jsonSlurper = new JsonSlurperClassic()
      return (Map)jsonSlurper.parseText(gson.toJson(testCaseExecutionHistoryResponse.getResults().get(0)))
    }
    return null
  }

  @NonCPS
  List fetchRevisionHistoryMap(String revisionHistoryObjectId, String apiWorkspace) {
    def revisionHistoryObjectRef = '/revisionhistory/' + revisionHistoryObjectId
    def workspaceObjectRef = '/workspace/' + apiWorkspace

    def revisionHistoryRequest = new QueryRequest('Revision')
    revisionHistoryRequest.setWorkspace(workspaceObjectRef)
    revisionHistoryRequest.setPageSize(PAGE_SIZE)
    def revisionHistoryFilter = new QueryFilter('RevisionHistory', '=', revisionHistoryObjectRef)
    revisionHistoryRequest.setQueryFilter(revisionHistoryFilter)
    revisionHistoryRequest.setOrder('RevisionNumber DESC')
    def revisionHistoryResponse = new RallyRestApi(new URI(apiUrl), apiToken).query(revisionHistoryRequest)
    if (!revisionHistoryResponse.wasSuccessful() || revisionHistoryResponse.getResults().size() == 0) {
      throw new NotFoundException('Rally API revision history request unsuccessful: ' + revisionHistoryResponse.getErrors())
    }

    def gson = new Gson()
    def jsonSlurper = new JsonSlurperClassic()
    return (List)jsonSlurper.parseText(gson.toJson(revisionHistoryResponse.getResults()))
  }

  @NonCPS
  Map fetchUserMap(String userObjectId, String apiWorkspace) {
    def workspaceObjectRef = '/workspace/' + apiWorkspace

    def userRequest = new QueryRequest('user')
    userRequest.setWorkspace(workspaceObjectRef)
    userRequest.setPageSize(PAGE_SIZE)
    userRequest.setQueryFilter(new QueryFilter('ObjectID', '=', userObjectId))
    def userResponse = new RallyRestApi(new URI(apiUrl), apiToken).query(userRequest)
    if (!userResponse.wasSuccessful() || userResponse.getTotalResultCount() == 0) {
      throw new NotFoundException('Rally API user request unsuccessful.')
    }

    def gson = new Gson()
    def jsonSlurper = new JsonSlurperClassic()
    return (Map)jsonSlurper.parseText(gson.toJson(userResponse.getResults().get(0)))
  }

  static fetchObjectIdFromRef(String ref) {
    def splitRef = ref.split('/')
    return splitRef.last()
  }

  // Returns html-stripped string from description / acceptance criteria
  static stripHtml(String description) {
    if (description == null) {
      return ''
    }
    // <p>, <br>, nbsp
    String strippedDescription = description.replaceAll('<p>', ' ')
    strippedDescription = strippedDescription.replaceAll('</p>', ' ')
    strippedDescription = strippedDescription.replaceAll('<br />', ' ')
    strippedDescription = strippedDescription.replaceAll('&nbsp;', ' ')
    // if there are any lingering tags, just take them out
    strippedDescription = strippedDescription.replaceAll('(?s)<[^>]*>(\\s*<[^>]*>)*', '')
    return strippedDescription
  }
}
