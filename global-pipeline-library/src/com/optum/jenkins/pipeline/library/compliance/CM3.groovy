#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.approval.Approval
import com.optum.jenkins.pipeline.library.compliance.models.CM3Approver
import com.optum.jenkins.pipeline.library.compliance.models.CM3Report
import com.optum.jenkins.pipeline.library.compliance.services.LDAP
import groovy.json.JsonSlurperClassic

import java.text.DateFormat
import java.text.SimpleDateFormat

class CM3 implements Serializable {

  Object jenkins
  LDAP ldapService

  CM3() throws Exception {
    throw new ComplianceInvalidParameterException('`this` be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
            'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  CM3(jenkins) {
    this.jenkins = jenkins
    this.ldapService = new LDAP()
  }

  /**
   * Does the CM3 Approval
   * @param milestoneId String Rally milestoneId or releaseId associated with this build
   * @param submitter String secure group for release approval
   * @param noOfApprovers int Number of approvers needed for this release to go
   *
   */
  List<CM3Approver> getCM3Approvals(Map<String, Object> params) {
    def milestoneid = (new JsonSlurperClassic()).parseText(jenkins.env.releaseScope as String)['id']
    List<CM3Approver> approvers = []
    def config = params
    config.defaultValue = 'Approving build ' + jenkins.env.BUILD_NUMBER + ' to release the milestone: '+ milestoneid
    config.duplicateApproverCheck = true
    Approval approval = new Approval(jenkins)
    CM3Approver cm3Approver = new CM3Approver()
    TimeZone tz = TimeZone.getTimeZone('UTC')
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
    df.setTimeZone(tz)
    for (int i = 0; i < params.noOfApprovers; i++) {
      approval.approval(config)
      jenkins.echo 'Approvers So Far: ' + jenkins.env.APPROVERS + ':Approvers'
      cm3Approver.approverMSid = jenkins.env.APPROVERS.toString().split(' ').last()
      cm3Approver.approvedDate = df.format(new Date())
      def approverDetails = ldapService.fetchSecureMemberDetails(cm3Approver.approverMSid)
      cm3Approver.approverName = approverDetails[0].replaceAll('\\[', '').replaceAll('\\]', '')
      cm3Approver.approverEmail = approverDetails[1].replaceAll('\\[', '').replaceAll('\\]', '')
      approvers.add(cm3Approver)
    }
    return approvers
  }

  def validateParameters(config) {
    if (!config.containsKey('itApprover') || config.itApprover == '')  {
      throw new ComplianceInvalidParameterException('ERROR: itApprover cannot be blank. Please pass the itApprover in config.itApprover parameter.')
    }
    if (!config.containsKey('bizApprover') || config.bizApprover == '')  {
      throw new ComplianceInvalidParameterException('ERROR: bizApprover cannot be blank. Please pass the bizApprover in config.bizApprover parameter.')
    }
    if (!config.containsKey('noOfITApprovers') || config.noOfITApprovers == '')  {
      throw new ComplianceInvalidParameterException('ERROR: noOfITApprovers cannot be blank. Please pass the integer value for noOfITApprovers in config.noOfITApprovers parameter.')
    }
    if (!config.containsKey('noOfBizApprovers') || config.noOfBizApprovers == '')  {
      throw new ComplianceInvalidParameterException('ERROR: noOfBizApprovers cannot be blank. Please pass the integer value for noOfBizApprovers in config.noOfBizApprovers parameter.')
    }
    // Duplicate groups check
    if (jenkins.env.userStoryApprovers == config.itApprover
      || jenkins.env.userStoryApprovers == config.bizApprover
      || config.itApprover == config.bizApprover) {
      throw new ComplianceInvalidParameterException('Duplicate Groups. Please ensure that the user story approvers, it approvers, and business approvers are unique.')
    }

  }

  void sendNotification(String submitter) {
    def milestoneid = (new JsonSlurperClassic()).parseText(jenkins.env.releaseScope as String)['id']
    def emails = ldapService.fetchSecureGroupMemberEmails(submitter)
    jenkins.sh 'ls'
    jenkins.unstash 'cm1Cm2Report'
    jenkins.sh 'ls'
    emails.each {
        // TODO : Remove the below if condition after testing
        if (jenkins.env.cm1Validation == 'PASSED' && jenkins.env.cm2Validation == 'PASSED') {
          jenkins.emailext body: 'Please review the CM1 and CM2 data. Click the below link to approve \n' + jenkins.env.BUILD_URL + 'input/',
            subject: 'ACTION REQUIRED: Please approve the release ' + milestoneid,
            to: it,
            attachmentsPattern:'CM1CM2.xls'
        } else {
          jenkins.emailext body: 'Please review the CM1 and CM2 data. There are (CM1/CM2)validation failures and hence the build cannot proceed' ,
            subject: 'ACTION REQUIRED: Compliance Validation Failures ' + milestoneid,
            to: it,
            attachmentsPattern:'CM1CM2.xls'
        }
    }
    if (jenkins.env.cm1Validation == 'FAILED' || jenkins.env.cm2Validation == 'FAILED') {
      jenkins.error('CM1/CM2 Validation Errors, build cannot proceed.')
    }
  }

  void sendCM3Report() {
    def milestoneid = (new JsonSlurperClassic()).parseText(jenkins.env.releaseScope as String)['id']
    CM3Report cm3Report = (new JsonSlurperClassic()).parseText(jenkins.env.cm3Report as String) as CM3Report
    jenkins.env.cm3Approvers
    def itEmails = ldapService.fetchSecureGroupMemberEmails(cm3Report.itApprovers)
    def bizEmails = ldapService.fetchSecureGroupMemberEmails(cm3Report.businessApprovers)
    itEmails.each {
        jenkins.emailext body: 'Here is the final compliance report.\n\n Note: You are receiving this email because you are part of the IT approver group for the release',
          subject: 'Compliance Report for the release Id: ' + milestoneid,
          to: it,
          attachmentsPattern:'Compliance.xls'
    }
    bizEmails.each {
        jenkins.emailext body: 'Here is the final compliance report.\n\n Note: You are receiving this email because you are part of the business approver group for the release',
          subject: 'Compliance Report for the release Id: ' + milestoneid,
          to: it,
          attachmentsPattern:'Compliance.xls'
    }
  }
}
