import com.optum.jenkins.pipeline.library.approval.Approval

/**
 * Approval makes use of the input step to allow users to manually allow the job to proceed or abort.
 * It also has options to display/enter a change ticket and check for duplicate approvers
 * @param time integer Time limit. If the time limit is reached an exception is thrown which leads to aborting the build
 * @param unit The time limit can be in one of these values: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS
 * @param message String This top level message will be displayed to the user
 * @param defaultValue String This is the default text value for the approval parameter which is displayed to the user. The user can manually overwrite this.
 * @param description String This is a description of the approval parameter
 * @param submitter String User IDs or windows global groups of people permitted to respond to the input, separated by ','. No whitespace.
 * @param duplicateApproverCheck boolean When true, verify that the current approver has not already approved using this input step during this build. For example,
 *                               there may be a requirement to have two separate approvers
 * @param displayTicket boolean When true, add a parameter to display a ticket if it exists and allow it to be changed
 */

def call(Map<String, Object> config){
    Approval approval = new Approval(this)
    approval.approval(config)
}