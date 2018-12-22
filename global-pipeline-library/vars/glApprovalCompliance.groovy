import com.optum.jenkins.pipeline.library.compliance.CM3
import com.optum.jenkins.pipeline.library.compliance.ComplianceInvalidParameterException
import com.optum.jenkins.pipeline.library.compliance.models.CM1Report
import com.optum.jenkins.pipeline.library.compliance.models.CM3Approver
import com.optum.jenkins.pipeline.library.compliance.models.CM3Report
import com.optum.jenkins.pipeline.library.event.EventStatus
import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

/**
 * Approval makes use of the input step to allow users to manually allow the job to proceed or abort.
 * It also has options to display/enter a change ticket and check for duplicate approvers
 * @param time integer Time limit. If the time limit is reached an exception is thrown which leads to aborting the build
 * @param unit The time limit can be in one of these values: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS
 * @param message String This top level message will be displayed to the user
 * @param description String This is a description of the approval parameter
 * @param submitter String User IDs or windows global groups of people permitted to respond to the input, separated by ','. No whitespace.
 * @param displayTicket boolean When true, add a parameter to display a ticket if it exists and allow it to be changed
 * @param milestoneId String Rally milestoneId or releaseId associated with this build
 * @param noOfApprovers int Number of approvers needed for this release to go
 * @param itApprover secure group containing the IT approvers
 * @param bizApprover secure group containing the Business approvers
 * @param noOfITApprovers no., of approvals required from the secure group containing the IT approvers
 * @param noOfBizApprovers np., of approvals required from the secure group containing the IT approvers

 */

def call(Map<String, Object> params){
  CM3 cm3 = new CM3(this)
  cm3.validateParameters(params)

  Map iTDefaults = [
    message : 'Please review CM1/CM2 details and approve the release',
    submitter: params.itApprover,
    noOfApprovers:params.noOfITApprovers,
    approverType:'IT'
  ]
  Map bizDefaults = [
    message : 'Please review CM1/CM2 details and approve the release',
    submitter: params.bizApprover,
    noOfApprovers:params.noOfBizApprovers,
    approverType:'Business'
  ]
  def startTime = new Date()

  // Get IT Approvals
  List<CM3Approver> cm3Report1 = cm3.getCM3Approvals(iTDefaults+params)
  this.env.APPROVERS = ''
  // Get Business Approvals
  List<CM3Approver> cm3Report2 = cm3.getCM3Approvals(bizDefaults+params)
  CM3Report cm3Report = new CM3Report()
  cm3Report.itApprovers = params.itApprover
  cm3Report.itApproversThreshold = params.noOfITApprovers
  cm3Report.itApprovals = cm3Report1
  cm3Report.businessApprovers = params.bizApprover
  cm3Report.businessApproversThreshold = params.noOfBizApprovers
  cm3Report.businessApprovals = cm3Report2
  this.env.cm3Report = JsonOutput.toJson(cm3Report)
}
