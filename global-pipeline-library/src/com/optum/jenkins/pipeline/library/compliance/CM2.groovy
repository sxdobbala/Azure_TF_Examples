package com.optum.jenkins.pipeline.library.compliance

@GrabResolver(name='internal', root='https://repo1.uhc.com/artifactory/UHG-Releases/')
@Grab('com.optum.psas:securitymetrics-client:0.0.2')
import com.optum.jenkins.pipeline.library.compliance.models.CM2ManualTestCase
import com.optum.jenkins.pipeline.library.compliance.models.CM2ManualTestCaseExecution
import com.optum.jenkins.pipeline.library.compliance.models.CM2Report
import com.optum.jenkins.pipeline.library.compliance.models.CM2ManualTestResults
import com.optum.jenkins.pipeline.library.compliance.models.CM2SecuritySummary
import com.optum.jenkins.pipeline.library.compliance.models.CM2AutomatedTestSummary
import com.optum.jenkins.pipeline.library.compliance.services.Rally
import com.optum.jenkins.pipeline.library.utils.Constants
import com.optum.jenkins.pipeline.library.utils.CredentialValidator
import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import com.optum.psas.securitymetrics.fortify.FortifyApiClient
import com.optum.psas.securitymetrics.model.fortify.IssueScope
import com.optum.psas.securitymetrics.model.fortify.Scan
import groovy.json.JsonSlurperClassic

// Responsible for getting/making all data required for a CM2 report.
class CM2 implements Serializable {
  Rally rallyService
  String milestoneFormattedId
  String apiWorkspace
  Map<String, BigDecimal> failureThresholds
  Object jenkins
  int fortifyProjectVersionId
  String fortifyUserCredentialsId

  CM2(Object jenkins,
      String apiUrl,
      String apiToken,
      String apiWorkspace,
      String milestoneFormattedId,
      Map failureThresholds,
      int fortifyProjectVersionId,
      String fortifyUserCredentialsId) {
    this.rallyService = new Rally(apiUrl, apiToken)
    this.milestoneFormattedId = milestoneFormattedId
    this.apiWorkspace = apiWorkspace
    this.jenkins = jenkins
    this.failureThresholds = failureThresholds
    this.fortifyProjectVersionId = fortifyProjectVersionId
    this.fortifyUserCredentialsId = fortifyUserCredentialsId
  }

  CM2Report fetchReport() {
    def milestoneJsonMap = rallyService.fetchMilestoneMap(
      milestoneFormattedId,
      apiWorkspace)

    List<CM2ManualTestCase> manualTestCases = fetchTestCases((String)milestoneJsonMap['ObjectID'], apiWorkspace)
    def manualPassedPercent = 0
    BigDecimal manualTestsExecuted =  manualTestCases.findAll { it.executed }.size()
    BigDecimal manualTestsPassed = manualTestCases.findAll { it.executed && it.executed.passed }.size()
    if (manualTestsExecuted > 0) {
      manualPassedPercent = manualTestsPassed / manualTestsExecuted * 100
    }
    CM2ManualTestResults manualTestResults = new CM2ManualTestResults(
      failThreshold: (failureThresholds.Manual ?: 1.0),
      passedPercent:manualPassedPercent,
      testCases: manualTestCases)

    def automatedTestSummaries = fetchAutomatedTestSummaries(
      this.jenkins,
      failureThresholds)

    List<CM2SecuritySummary> securitySummaries = []
    if(fortifyProjectVersionId != 0 && fortifyUserCredentialsId != null) {
      securitySummaries.add(fetchSecuritySummary())
    }

    def errors = []
    errors.addAll(validateFailureAmount(manualTestResults, automatedTestSummaries))

    return new CM2Report(manualTestResults: manualTestResults,
      automatedTestSummaries: automatedTestSummaries,
      securitySummaries: securitySummaries,
      errors: errors)
  }

  private List<CM2ManualTestCase> fetchTestCases(String milestoneObjectId, String apiWorkspace) {
    List testCases = []
    List userStoriesJsonMap = rallyService.fetchUserStoriesMap(milestoneObjectId, apiWorkspace)
    userStoriesJsonMap.each { userStoryMap ->
      List testCasesMap = rallyService.fetchTestCasesMap((String)userStoryMap['ObjectID'], apiWorkspace)
      testCasesMap.each { testCaseMap ->
        String formattedId = testCaseMap['FormattedID']
        String objectId = testCaseMap['ObjectID']
        String sourceUrl = 'https://rally1.rallydev.com/#/detail/testcase/' + testCaseMap['ObjectID']
        String userStoryFormattedId = userStoryMap['FormattedID']
        String userStoryName = userStoryMap['Name']
        CM2ManualTestCaseExecution executed = null
        if (testCaseMap['Results']) {
          Map latestTestCaseExecutionMap = rallyService.fetchLatestTestCaseExecution(objectId, apiWorkspace)
          if (latestTestCaseExecutionMap) {
            String timestamp = (String)latestTestCaseExecutionMap['Date']
            String testerName = null
            String testerEmail = null
            boolean passed = ((String)latestTestCaseExecutionMap['Verdict']) == 'Pass'
            if (latestTestCaseExecutionMap['Tester']) {
              Map userMap = rallyService.fetchUserMap(
                Rally.fetchObjectIdFromRef((String)latestTestCaseExecutionMap['Tester']['_ref']), apiWorkspace)
              testerName = userMap['DisplayName']
              testerEmail = userMap['EmailAddress']
            }
            executed = new CM2ManualTestCaseExecution(
              timestamp: timestamp,
              testerName: testerName,
              testerEmail: testerEmail,
              passed: passed
            )
          }
        }
        testCases.add(new CM2ManualTestCase(
          formattedId: formattedId,
          sourceUrl: sourceUrl,
          userStoryName: userStoryName,
          userStoryFormattedId: userStoryFormattedId,
          executed: executed
        ))
      }
    }

    return testCases
  }

  private List<CM2AutomatedTestSummary> fetchAutomatedTestSummaries(jenkins,
                                                                    Map<String, BigDecimal> failureThresholds) {
    List<CM2AutomatedTestSummary> automatedTestSummaries = []

    ElasticSearchRest elasticSearchRest = new ElasticSearchRest()
    def buildUrl = BuildInfoReader.getBuildUrl(jenkins)
    JsonSlurperClassic slurper = new JsonSlurperClassic()
    //RestClient.RestResponse testResponse = elasticSearchRest.get(jenkins, ['index':Constants.TEST_EVENT_TOPIC, 'buildUrl':buildUrl])
    def testResponse = elasticSearchRest.get(jenkins, ['index':Constants.TEST_EVENT_TOPIC, 'buildUrl':buildUrl])
    Map testResultsMap = (Map)slurper.parseText((String)testResponse.getMessage())
    List testResults = testResultsMap?.hits?.hits ?: []
    BigDecimal passedPercent = 0
    testResults.each { testResultContainer ->
      if (testResultContainer['_source']) {
        Map testResultMap = (Map)testResultContainer['_source']
        if (testResultMap['testsExecuted'] != 0) {
          passedPercent = testResultMap['testsPassed'] / testResultMap['testsExecuted']
        }
        automatedTestSummaries.add(new CM2AutomatedTestSummary(
          projectName: testResultMap['projectKey'],
          failThreshold: (failureThresholds[(String)testResultMap['testType']] ?: 1.0),
          totalScenarios: testResultMap['totalTests'],
          executedCases: testResultMap['testsExecuted'],
          passedCases: testResultMap['testsPassed'],
          failedCases: testResultMap['testsFailed'],
          timestamp: testResultMap['timestamp'],
          type: testResultMap['testType'],
          passedPercent: passedPercent,
        ))
      }
    }
    return automatedTestSummaries
  }

  CM2SecuritySummary fetchSecuritySummary() {
    CredentialValidator.validate(this.jenkins, fortifyUserCredentialsId, 'UsernamePassword')
    try {
       jenkins.withCredentials( [jenkins.usernamePassword(credentialsId: fortifyUserCredentialsId, usernameVariable: 'fortifyUsername', passwordVariable: 'fortifyPassword')]) {
        FortifyApiClient apiClient = new FortifyApiClient('', jenkins.env.fortifyUsername, jenkins.env.fortifyPassword)
        Map issueCountMap = apiClient.getOpenIssuesByCategory(fortifyProjectVersionId, IssueScope.SCA)
        Scan fortifyScan = apiClient.getMostRecentScan(fortifyProjectVersionId, IssueScope.SCA, 20)
        def recentScanDate = fortifyScan.getUploadDate().toString()
        CM2SecuritySummary securitySummary = new CM2SecuritySummary(
          sourceType: 'Fortify',
          testType: 'Static Code Analysis',
          openLowIssues: issueCountMap.get('Low').openCount,
          openMediumIssues: issueCountMap.get('Medium').openCount,
          openHighIssues: issueCountMap.get('High').openCount,
          openCriticalIssues: issueCountMap.get('Critical').openCount,
          totalLowIssues: issueCountMap.get('Low').totalCount,
          totalMediumIssues: issueCountMap.get('Medium').totalCount,
          totalHighIssues: issueCountMap.get('High').totalCount,
          totalCriticalIssues: issueCountMap.get('Critical').totalCount,
          recentScanDate: recentScanDate)
        return securitySummary
      }
    } catch (ex) {
      jenkins.error('Error while getting Fortify data from scar ' + ex.message)
    }
  }

  private List validateFailureAmount(CM2ManualTestResults manualTestResults, List<CM2AutomatedTestSummary> fetchAutomatedTestSummaries) {
    def errors = []
    if (manualTestResults) {
      if (manualTestResults.passedPercent < manualTestResults.failThreshold) {
        errors.add("Manual test pass rate too low. Found ${manualTestResults.passedPercent}, and required ${manualTestResults.failThreshold}.")
      }
    }
    (fetchAutomatedTestSummaries ?: []).each { automatedTestSummary ->
      if (automatedTestSummary.passedPercent < automatedTestSummary.failThreshold) {
        errors.add("${automatedTestSummary.type} test pass rate too low. Found automatedPassedPercent, and required ${automatedTestSummary.failThreshold}.")
      }
    }
    return errors
  }
}
