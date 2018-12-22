package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.compliance.models.AgileReleaseScope
import com.optum.jenkins.pipeline.library.compliance.models.CM1Report
import com.optum.jenkins.pipeline.library.compliance.models.CM2Report
import com.optum.jenkins.pipeline.library.compliance.models.CM3Report
import com.optum.jenkins.pipeline.library.compliance.services.FileSaver
import com.optum.jenkins.pipeline.library.event.ComplianceCheckEvent
import com.optum.jenkins.pipeline.library.utils.Constants
import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

// Class responsible as the entry point for our library calls.
// Handles interaction with jenkins.
class ComplianceCheck implements Serializable {
  Object jenkins

  ComplianceCheck() throws Exception {
    throw new ComplianceInvalidParameterException('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  ComplianceCheck(jenkins) {
    this.jenkins = jenkins
  }

  /**
   * Runs the automatic validation portion of the compliance report, and creates a partial
   * compliance document.
   * @param rallyApiUrl The base URL of the CA Agile Central API. Optional.
   * @param rallyApiToken The API token for CA Agile Central
   * @param rallyWorkspaceId The CA Agile Central workspace which contains your project and milestone.
   * @param rallyMilestoneId The milestone representing your current build and release.
   * @param failureThresholds Map containing the failure threshold for different type of tests.
   * @param fortifyProjectVersionId The fortify Project VersionId in scar.
   * @param fortifyUserCredentialsId The fortify  User CredentialsId in jenkins.
   * @param userStoryApprovers The secure group containing the members who has ability to accept the user stories.
   */

  void validateCM1CM2Compliance(Map<String, Object> params) {
    Map defaults = [
      rallyApiUrl: Constants.RALLY_API_URL,
      rallyApiToken: null, //required
      rallyWorkspaceId: null,  //required
      rallyMilestoneId: null, //required
      failureThresholds: [Manual: 1.0] , //defaults to 1
      fortifyProjectVersionId: 0,
      fortifyUserCredentialsId: null,
      userStoryApprovers: null // required
    ]

    Map config = defaults + params
    jenkins.echo('Config Parameters : ' + config.toString())
    validateParameters(config)

    ReleaseScope releaseScope = new ReleaseScope(
      config['rallyApiUrl'] as String,
      config['rallyApiToken'] as String,
      config['rallyWorkspaceId'] as String,
      config['rallyMilestoneId'] as String
    )
    AgileReleaseScope releaseScopeReport = releaseScope.fetchReleaseScope()

    if (releaseScopeReport.deploymentDate != '' || releaseScopeReport.deploymentDate != null) {
      jenkins.env.DeploymentDate = releaseScopeReport.deploymentDate
    }

    if (releaseScopeReport.deploymentId != '' || releaseScopeReport.deploymentId != null) {
      jenkins.env.DeploymentId = releaseScopeReport.deploymentId
    }

    jenkins.env.userStoryApprovers = config.userStoryApprovers

    CM1 cm1 = new CM1(
      config['rallyApiUrl'] as String,
      config['rallyApiToken'] as String,
      config['rallyWorkspaceId'] as String,
      config['rallyMilestoneId'] as String,
      config['userStoryApprovers'] as String
    )
    CM1Report cm1Report = cm1.fetchReport()
    if (cm1Report.errors.size() > 0) {
      jenkins.env.cm1Validation = 'FAILED'
    } else {
      jenkins.env.cm1Validation = 'PASSED'
    }
    CM2 cm2 = new CM2(
      this.jenkins,
      config['rallyApiUrl'] as String,
      config['rallyApiToken'] as String,
      config['rallyWorkspaceId'] as String,
      config['rallyMilestoneId'] as String,
      config['failureThresholds'] as Map,
      config['fortifyProjectVersionId'] as int,
      config['fortifyUserCredentialsId'] as String
    )
    CM2Report cm2Report = cm2.fetchReport()
    if (cm2Report.errors.size()> 0) {
      jenkins.env.cm2Validation = 'FAILED'
    } else {
      jenkins.env.cm2Validation = 'PASSED'
    }
    // Store the current compliance results in local jenkins environment
    jenkins.env.releaseScope = JsonOutput.toJson(releaseScopeReport)
    jenkins.env.milestoneId = config.rallyMilestoneId
    jenkins.env.cm1Report = JsonOutput.toJson(cm1Report)
    jenkins.env.cm2Report = JsonOutput.toJson(cm2Report)
    createAndSaveCM1CM2excelReport()
  }

  void createAndSaveCM1CM2excelReport() {
    AgileReleaseScope deploymentInfo = ((new JsonSlurperClassic()).parseText(this.jenkins.env.releaseScope as String)) as AgileReleaseScope
    CM1Report cm1Report = ((new JsonSlurperClassic()).parseText(this.jenkins.env.cm1Report as String)) as CM1Report
    CM2Report cm2Report = ((new JsonSlurperClassic()).parseText(this.jenkins.env.cm2Report as String)) as CM2Report

    // Create cm1/cm2 spread sheet here
    Excel csv = new Excel()
    byte[] workbookContent = csv.createCM1CM2report(deploymentInfo, cm1Report, cm2Report)

    // Write it out
    FileSaver.saveFile(this.jenkins, 'CM1CM2.xls', workbookContent)
    this.jenkins.stash(includes: 'CM1CM2.xls', name: 'cm1Cm2Report')
    this.jenkins.archiveArtifacts('CM1CM2.xls')
  }

  /**
   *  Uses the stashed Scope/CM1/CM2 JSON data to assemble the final compliance report
   */
  void createFinalComplianceReport(Map<String, Object> params) {
    AgileReleaseScope deploymentInfo = ((new JsonSlurperClassic()).parseText(this.jenkins.env.releaseScope as String)) as AgileReleaseScope
    if (deploymentInfo.deploymentId == null || deploymentInfo.deploymentId == '') {
      this.jenkins.error ('Please enter Service Now Ticket Id in the `Production Deployment Id` field in the Milestone.')
    }
    if (deploymentInfo.deploymentDate == null || deploymentInfo.deploymentDate == '') {
      this.jenkins.error ('Please enter Production deployment date in the `Target Date` field in the Milestone.')
    }
    CM1Report cm1Report = ((new JsonSlurperClassic()).parseText(this.jenkins.env.cm1Report as String)) as CM1Report
    CM2Report cm2Report = ((new JsonSlurperClassic()).parseText(this.jenkins.env.cm2Report as String)) as CM2Report
    CM3Report cm3Report = ((new JsonSlurperClassic()).parseText(jenkins.env.cm3Report as String)) as CM3Report

    // Create final spread sheet here
    Excel csv = new Excel()
    byte[] workbookContent = csv.createComplianceReport(deploymentInfo, cm1Report, cm2Report, cm3Report)

    // Write it out
    FileSaver.saveFile(this.jenkins, 'Compliance.xls', workbookContent)
    this.jenkins.stash(includes: 'Compliance.xls', name: 'complianceReport')
    this.jenkins.archiveArtifacts('Compliance.xls')

    // To do fetch cm1 and cm2 from Elastic Search in case this call is made independently
    sendComplianceEvent(this.jenkins.env.releaseScope as String,
      jenkins.env.cm1Report as String,
      jenkins.env.cm2Report as String,
      jenkins.env.cm3Report as String)

    (new CM3(this.jenkins)).sendCM3Report()
  }

  /**
   * Validates the parameter map.
   */
  def validateParameters(config) {
    if (!config.rallyMilestoneId)  {
      throw new ComplianceInvalidParameterException('ERROR: rallyMilestoneId cannot be blank. Please pass the rallyMilestoneId in config.rallyMilestoneId parameter.')
    }
    if (!config.rallyApiToken)  {
      throw new ComplianceInvalidParameterException('ERROR: rallyApiToken cannot be blank. Please pass the rallyApiToken in config.rallyApiToken parameter.')
    }
    if (!config.rallyWorkspaceId)  {
      throw new ComplianceInvalidParameterException('ERROR: rallyWorkspaceId cannot be blank. Please pass the rallyWorkspaceId in config.rallyWorkspaceId parameter.')
    }
    if (!config.userStoryApprovers)  {
      throw new ComplianceInvalidParameterException('ERROR: userStoryApprovers cannot be blank. Please pass the userStoryApprovers in config.userStoryApprovers parameter.')
    }
  }

  /**
   * Creates a compliance event from the provided parameter JSON strings.
   */
  void sendComplianceEvent(String agileReleaseScopeJson, String CM1Json, String CM2Json, String CM3Json) {

    new ComplianceCheckEvent(jenkins, [status: getJenkins().currentBuild.currentResult, agileReleaseScope: agileReleaseScopeJson, deploymentId: jenkins.env.milestoneId, CM1Data: CM1Json, CM2Data: CM2Json, CM3Data: CM3Json]).send()

  }
}
