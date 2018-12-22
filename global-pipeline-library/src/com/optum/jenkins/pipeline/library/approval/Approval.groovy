#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.approval

import com.optum.jenkins.pipeline.library.servicenow.Ticket

class Approval implements Serializable {
  def jenkins

  Approval() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Approval(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Approval makes use of the input step to allow users to manually allow the job to proceed or abort.
 * It also has options to display/enter a servicenow ticket and check for duplicate approvers
 * @param time integer Time limit. If the time limit is reached an exception is thrown which leads to aborting the build
 * @param unit The time limit can be in one of these values: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS
 * @param message String This top level message will be displayed to the user
 * @param defaultValue String This is the default text value for the approval parameter which is displayed to the user. The user can manually overwrite this.
 * @param description String This is a description of the approval parameter
 * @param submitter String User IDs or windows global groups of people permitted to respond to the input, separated by ','. No whitespace.
 * @param duplicateApproverCheck boolean When true, verify that the current approver has not already approved using this input step during this build. For example,
 *                               there may be a requirement to have two separate approvers
 * @param displayTicket boolean When true, add a parameter to display a servicenow ticket if it exists and allow it to be changed
 */
  def approval(Map<String, Object> params) {
    def defaults = [
      time                   : 1,
      unit                   : 'DAYS',
      message                : 'Approve?',
      defaultValue           : "Comment on ${jenkins.env.BUILD_NUMBER} Approval",
      description            : "Approve build ${jenkins.env.BUILD_NUMBER}",
      submitter              : '', //optional, leave blank for anonymous approval, can be a secure group, set up with "Configure Global Security" in a Jenkins instance
      duplicateApproverCheck : false,
      displayTicket          : false,
    ]
    def config = defaults + params
    def userInput
    def inputParams = []
    def approvalCommentParam = jenkins.string(
      [
        defaultValue : config.defaultValue,
        description  : config.description,
        name         : 'APPROVAL_COMMENTS',
      ]
    )
    jenkins.echo "Approval arguments: $config"

    try {
      Ticket ticket = new Ticket(jenkins)

      if (config.duplicateApproverCheck && jenkins.env.APPROVERS != null ) {
        config.message = config.message + "\n Already approved by ${jenkins.env.APPROVERS}. Requires another approver."
      }
      inputParams.add( approvalCommentParam )

      if (config.displayTicket) {
        def inputDescription
        if (jenkins.env.SN_TICKET_DETAILS != "") {
          Map ticketDetails = jenkins.readJSON text: jenkins.env.SN_TICKET_DETAILS
          if (ticketDetails.number.toUpperCase().startsWith("CHG")) {
            inputDescription = "Ticket details retrieved from ServiceNow at: " + ticketDetails.date_of_this_info +
              "\n\nNumber : " + ticketDetails.number +
              "\nStart Date : " + ticketDetails.start_date +
              "\nEnd Date : " + ticketDetails.end_date +
              "\nApproval : " + ticketDetails.approval +
              "\nDescription : " + ticketDetails.short_description +
              "\nActive : " + ticketDetails.active +
              "\nState : " + ticketDetails.state +
              "\nType : " + ticketDetails.type +
              "\nhttps://optum.service-now.com/change_request.do?sys_id=" + ticketDetails.sys_id
          } else {
            inputDescription = "Ticket details retrieved from ServiceNow at: " + ticketDetails.date_of_this_info +
              "\n\nNumber : " + ticketDetails.number +
              "\nPriority : " + ticketDetails.priority +
              "\nApproval : " + ticketDetails.approval +
              "\nDescription : " + ticketDetails.short_description +
               "\nActive : " + ticketDetails.active +
              "\nState : " + ticketDetails.state +
              "\nParent Incident : " + ticketDetails.parent_incident +
              "\nhttps://optum.service-now.com/incident.do?sys_id=" + ticketDetails.sys_id
          }
        } else {
          inputDescription = "Ticket"
        }

        def ticketParam = jenkins.string([
              defaultValue : jenkins.env.SN_TICKET,
              description  : inputDescription,
              name         : 'TICKET',
            ])
          inputParams.add(ticketParam)
      }

      if (config.submitter != '') {
        jenkins.echo("Approval required from " +config.submitter)
      }
      jenkins.timeout(time: config.time, unit: config.unit) {
        userInput = jenkins.input([
          message            : config.message,
          parameters         : inputParams,
          submitter          : config.submitter,
          submitterParameter : 'APPROVER',
        ])
      }

      if (config.duplicateApproverCheck) {
        checkApproverDuplicate time: config.time,
                               unit        : config.unit,
                               message     : config.message,
                               submitter   : config.submitter,
                               approver    : "${userInput['APPROVER']}",
                               parameters  : inputParams
      }
      jenkins.echo "Approved by ${userInput['APPROVER']}, with comments: ${userInput['APPROVAL_COMMENTS']}"

      if (config.displayTicket) {
        jenkins.env.SN_TICKET = "${userInput['TICKET']}"
        while (!(ticket.verifyTicketFormat (ticket : jenkins.env.SN_TICKET))) {
          ticket.inputTicket time      : config.time,
                             unit      : config.unit,
                             submitter : config.submitter
        }
      }
    } catch(Exception ex) {
      jenkins.error("Approval failed: " + ex.getMessage())
    }
  }

/**
 * checkApproverDuplicate is a private helper which checks if the current responder to the input step has already responded during this build.
 * @param time integer Time limit. If the time limit is reached an exception is thrown which leads to aborting the build
 * @param unit The time limit can be in one of these values: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS
 * @param message String This top level message will be displayed to the user
 * @param defaultValue String This is the default text value for the approval parameter which is displayed to the user. The user can manually overwrite this.
 * @param description String This is a description of the approval parameter
 * @param submitter String User IDs or windows global groups of people permitted to respond to the input, separated by ','. No whitespace.
 */
  def checkApproverDuplicate(Map<String, Object> params) {
    def defaults = [
      time                   : 1,  // optional
      unit                   : 'DAYS',  // optional
      message                : 'Approve?', //optional
      defaultValue           : "Comment on ${jenkins.env.BUILD_NUMBER} Approval", //optional
      description            : "Approve build ${jenkins.env.BUILD_NUMBER}", //optional
      submitter              : '' //optional, leave blank for anonymous approval, can be a secure group, set up with "Configure Global Security" in a Jenkins instance
    ]
    def config = defaults + params
    def userInput

    try {
      if (jenkins.env.APPROVERS == null) {
        jenkins.env.APPROVERS = config.approver
      } else {
        if (!jenkins.env.APPROVERS.contains(config.approver)) {
          jenkins.env.APPROVERS = jenkins.env.APPROVERS + ' ' + config.approver
        } else {
          while (userInput == null || jenkins.env.APPROVERS.contains("${userInput['APPROVER']}")) {
            jenkins.echo jenkins.env.APPROVERS + " has already approved. Require a different approver. " +
              "This check is made when duplicateApproverCheck is set to true"
            jenkins.timeout(time: config.time, unit: config.unit) {
              userInput = jenkins.input([
                message            : config.message,
                parameters         : config.inputParams,
                submitter          : config.submitter,
                submitterParameter : 'APPROVER',
              ])
            }
          }
        }
      }
    } catch(Exception ex) {
      jenkins.error("checkApproverDuplicate failed: " + ex.getMessage())
    }
  }

/**
 * approvalReset Resets the APPROVERS environment variable. For example, it could be used in a flow where an approver approves stage
 * and the same approver is authorized to approve production
 */
  def approvalReset(Map<String, Object> params) {
    jenkins.env.APPROVERS = ''
    jenkins.echo "APPROVERS env var has been reset"
  }
}
