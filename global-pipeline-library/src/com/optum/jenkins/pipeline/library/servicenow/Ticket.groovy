#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.servicenow

import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException
import com.optum.jenkins.pipeline.library.scm.Git
import com.optum.jenkins.pipeline.library.utils.Constants
import groovy.json.JsonOutput

class Ticket implements Serializable {
  def jenkins

  Ticket() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Ticket(jenkins) {
    this.jenkins = jenkins
  }

/**
 * verifyTicket validates change and incident tickets against ServiceNow
 * @param credentials String Required credentials to interact with ServiceNow
 * @param ticket String Ticket to be verified e.g. CHG1234567 or INC1234567
 * @param isChangeWindow boolean Verify that current time is within the Change Ticket change window for non incident Change Tickets
 */
  def verifyTicket(Map<String, Object> params) {
    def defaults = [
      credentials      : null, // required
      ticket           : jenkins.env.SN_TICKET,
      isChangeWindow   : true,
      serviceNowApiUrl : Constants.SERVICENOW_API_URL
    ]
    def config = defaults + params
    def isValid
    jenkins.echo "verifyTicket arguments: $config"

    try {
      if (!config.credentials) {
        jenkins.error "The credentials for ServiceNow api are required."
      }

      if (!verifyTicketFormat(ticket: config.ticket)) {
        jenkins.error "Invalid servicenow ticket format"
      }

      if (config.ticket.toUpperCase().startsWith("CHG")) {
        isValid = verifyChangeTicket(config)
      } else {
        isValid = verifyIncidentTicket(config)
      }

      jenkins.echo 'isValid: ' + isValid

      if (isValid == false) {
        jenkins.error "The servicenow ticket failed verification with ServiceNow"
      }
      return isValid
    } catch (Exception ex) {
      jenkins.error("verifyTicket failed: " + ex.getMessage())
    }
  }

/**
 * verifyChangeTicket validates that:
 * - non incident change tickets are approved
 * - incident change tickets exist
 * @param credentials String Required credentials to interact with ServiceNow
 * @param ticket String Change Ticket to be verified e.g. CHG1234567
 * @param isChangeWindow boolean Verify that current time is within the Change Ticket change window for non incident Change Tickets
 */
  def verifyChangeTicket(Map<String, Object> params) {
    def defaults = [
      credentials   : null, // required
      ticket        : jenkins.env.SN_TICKET,
      isChangeWindow: true,
      serviceNowApiUrl : Constants.SERVICENOW_API_URL
    ]
    def config = defaults + params
    boolean isValid = false
    String cmdReturn
    jenkins.echo "verifyChangeTicket arguments: $config"

    try {
      jenkins.withCredentials([jenkins.usernamePassword(credentialsId: "$config.credentials", usernameVariable: 'SN_USER',
        passwordVariable: 'SN_PASS')]) {
        String createCommand = """
          curl -X GET --header "Accept:application/json" --user '${jenkins.env.SN_USER}':'${jenkins.env.SN_PASS}' \\
          "${config.serviceNowApiUrl}/now/table/change_request?sysparm_query=number=$config.ticket^active=true&sysparm_fields=number,approval,active,start_date,end_date,short_description,state,type&sysparm_display_value=true&sysparm_exclude_reference_link=true&sysparm_limit=1"
        """
        cmdReturn = jenkins.command(createCommand, true)
      }

      Map ticketMap = parseApiResponse(cmdReturn)

      if (ticketMap.approval.equalsIgnoreCase('approved')) {
        if (config.isChangeWindow && !ticketMap.type.equalsIgnoreCase('incident')) {
          Date currentDate = new Date()
          Date start_date = Date.parse("MM/dd/yyyy HH:mm:ss", ticketMap.start_date)
          Date end_date = Date.parse("MM/dd/yyyy HH:mm:ss", ticketMap.end_date)
          if (currentDate.after(start_date) && currentDate.before(end_date)) {
            isValid = true
          } else {
            jenkins.echo currentDate.toString() + " is not between the servicenow start date " + start_date.toString() + " and end date " + end_date.toString()
          }
        } else {
          isValid = true
        }
      } else if (ticketMap.type.equalsIgnoreCase('incident')) {
        // Don't check approval or date on incident change since that may be entered retrospectively
        isValid = true
      }
      return isValid
    } catch (Exception ex) {
      jenkins.error("verifyChangeTicket failed: " + ex.getMessage())
    }
  }

/**
 * verifyIncidentTicket verifies that an incident is priority 1 or 2
 * @param credentials String Required credentials to interact with ServiceNow
 * @param ticket String Incident Ticket to be verified e.g. INC1234567
 */
  def verifyIncidentTicket(Map<String, Object> params) {
    def defaults = [
      credentials   : null, // required
      ticket        : jenkins.env.SN_TICKET,
      serviceNowApiUrl : Constants.SERVICENOW_API_URL
    ]
    def config = defaults + params
    boolean isValid = false
    String cmdReturn
    jenkins.echo "verifyIncidentTicket arguments: $config"

    try {
      jenkins.withCredentials([jenkins.usernamePassword(credentialsId: "$config.credentials", usernameVariable: 'SN_USER',
        passwordVariable: 'SN_PASS')]) {
        String createCommand = """
          curl -X GET --header "Accept:application/json" --user '${jenkins.env.SN_USER}':'${jenkins.env.SN_PASS}' \\
          "${config.serviceNowApiUrl}/now/table/incident?sysparm_query=number=$config.ticket^active=true&sysparm_fields=number,approval,active,priority,short_description,state&sysparm_display_value=true&sysparm_exclude_reference_link=true&sysparm_limit=1"
        """
        cmdReturn = jenkins.command(createCommand, true)
      }

      Map ticketMap = parseApiResponse(cmdReturn)

      if (ticketMap.priority.equals('1') || ticketMap.priority.equals('2')) {
        isValid = true
      } else {
        isValid = false
        jenkins.echo config.ticket + " priority is " + ticketMap.priority + ". Incident priority must be 1 or 2, otherwise an approved change ticket is required for production deployment"
      }
      return isValid
    } catch (Exception ex) {
      jenkins.error("verifyIncidentTicket failed: " + ex.getMessage())
    }
  }

/**
 * getTicket retrieves key fields for a servicenow ticket
 * @param credentials String Required credentials to interact with ServiceNow
 * @param ticket String Ticket to be verified e.g. CHG1234567 or INC1234567
 * @param serviceNowApiUrl String Base of the url for accessing the ServiceNow API
 */
  def getTicket(Map<String, Object> params) {
    def defaults = [
      credentials: null, // required
      ticket     : jenkins.env.SN_TICKET,
      serviceNowApiUrl : Constants.SERVICENOW_API_URL
    ]
    def config = defaults + params
    Map ticketDetails
    jenkins.echo "getTicket arguments: $config"

    try {
      if (!config.credentials) {
        jenkins.error "The credentials for ServiceNow api are required."
      }

      if (!verifyTicketFormat(ticket: config.ticket)) {
        jenkins.error "Invalid servicenow ticket format"
      }

      if (config.ticket.toUpperCase().startsWith("CHG")) {
        ticketDetails = getChangeTicket(config)
      } else {
        //calls placeholder
        ticketDetails = getIncidentTicket(config)
      }
      return ticketDetails
    } catch (Exception ex) {
      jenkins.error("getTicket failed: " + ex.getMessage())
    }
  }

/**
 * getChangeTicket retrieves key fields for a change
 * @param credentials String Required credentials to interact with ServiceNow
 * @param ticket String Ticket to be retrieved e.g. CHG1234567
 * @param serviceNowApiUrl String Base of the url for accessing the ServiceNow API
 */
  def getChangeTicket(Map<String, Object> params) {
    def defaults = [
      credentials      : null, // required
      ticket           : jenkins.env.SN_TICKET,
      serviceNowApiUrl : Constants.SERVICENOW_API_URL
    ]
    def config = defaults + params
    Map ticketMap
    String cmdReturn
    jenkins.echo "getChangeTicket arguments: $config"

    try {
      jenkins.withCredentials([jenkins.usernamePassword(credentialsId: "$config.credentials", usernameVariable: 'SN_USER',
        passwordVariable: 'SN_PASS')]) {
        String createCommand = """
          curl -X GET --header "Accept:application/json" --user '${jenkins.env.SN_USER}':'${jenkins.env.SN_PASS}' \\
          "${config.serviceNowApiUrl}/now/table/change_request?sysparm_query=number=$config.ticket^active=true&sysparm_fields=number,approval,active,start_date,end_date,short_description,state,type,sys_id&sysparm_display_value=true&sysparm_exclude_reference_link=true&sysparm_limit=1"
        """
        cmdReturn = jenkins.command(createCommand, true)
      }

      ticketMap = parseApiResponse(cmdReturn)

      jenkins.echo "##############################################################################################" +
        "\nTicket Number     : " + ticketMap.number +
        "\nStart Date        : " + ticketMap.start_date +
        "\nEnd Date          : " + ticketMap.end_date +
        "\nApproval          : " + ticketMap.approval +
        "\nDescription       : " + ticketMap.short_description +
        "\nActive            : " + ticketMap.active +
        "\nState             : " + ticketMap.state +
        "\nType              : " + ticketMap.type +
        "\nDate of this Info : " + ticketMap.date_of_this_info +
        "\n##############################################################################################"
      def jsonTicket = JsonOutput.toJson(ticketMap)
      jenkins.env.SN_TICKET_DETAILS = jsonTicket
      return ticketMap
    } catch (Exception ex) {
      jenkins.error("getChangeTicket failed: " + ex.getMessage())
    }
  }

/**
 * getIncidentTicket retrieves key fields for an incident
 * @param credentials String Required credentials to interact with ServiceNow
 * @param ticket String Ticket to be retrieved e.g. INC1234567
 * @param serviceNowApiUrl String Base of the url for accessing the ServiceNow API
 */
  def getIncidentTicket(Map<String, Object> params) {
    def defaults = [
      credentials      : null, // required
      ticket           : jenkins.env.SN_TICKET,
      serviceNowApiUrl : Constants.SERVICENOW_API_URL
    ]
    def config = defaults + params
    Map ticketMap
    String cmdReturn
    jenkins.echo "getIncidentTicket arguments: $config"

    try {
      jenkins.withCredentials([jenkins.usernamePassword(credentialsId: "$config.credentials", usernameVariable: 'SN_USER',
        passwordVariable: 'SN_PASS')]) {
        String createCommand = """
          curl -X GET --header "Accept:application/json" --user '${jenkins.env.SN_USER}':'${jenkins.env.SN_PASS}' \\
          "${config.serviceNowApiUrl}/now/table/incident?sysparm_query=number=$config.ticket^active=true&sysparm_fields=number,approval,active,priority,short_description,state,parent_incident,sys_id&sysparm_display_value=true&sysparm_exclude_reference_link=true&sysparm_limit=1"
        """
        cmdReturn = jenkins.command(createCommand, true)
      }

      ticketMap = parseApiResponse(cmdReturn)

      jenkins.echo "##############################################################################################" +
        "\nTicket Number     : " + ticketMap.number +
        "\nPriority          : " + ticketMap.priority +
        "\nApproval          : " + ticketMap.approval +
        "\nDescription       : " + ticketMap.short_description +
        "\nActive            : " + ticketMap.active +
        "\nState             : " + ticketMap.state +
        "\nParent Incident   : " + ticketMap.parent_incident +
        "\nDate of this Info : " + ticketMap.date_of_this_info +
        "\n##############################################################################################"
      def jsonTicket = JsonOutput.toJson(ticketMap)
      jenkins.env.SN_TICKET_DETAILS = jsonTicket
      return ticketMap
    } catch (Exception ex) {
      jenkins.error("getIncidentTicket failed: " + ex.getMessage())
    }
  }

/**
 * getTicketFromGitCommit reads the first word of the latest git commit subject message.
 * @param envVar boolean When true, set the environment variable for the servicenow
 */
  def getTicketFromGitCommit(Map<String, Object> params) {
    def defaults = [
      envVar: true // write the servicenow ticket to an environment variable
    ]
    def config = defaults + params
    String ticket
    def msgList
    jenkins.echo "getTicketFromGitCommit arguments: $config"

    try {
      Git gitInfo = new Git(jenkins)
      def gitCommitSubject = gitInfo.getGitSubject()
      jenkins.echo "git commit message: " + gitCommitSubject

      if (gitCommitSubject) {
        msgList = gitCommitSubject.split()
      }
      if (msgList) {
        ticket = msgList[0]
        if (!(verifyTicketFormat(ticket: ticket))) {
          jenkins.echo "No valid servicenow ticket format in first word of git commit message subject"
          ticket = null
        }
      }
      if (ticket && config.envVar) {
        jenkins.env.SN_TICKET = ticket
      }
      jenkins.echo "Ticket from git commit: " + ticket
      return ticket
    } catch (Exception ex) {
      jenkins.error("getTicketFromGitCommit failed: " + ex.getMessage())
    }
  }

/**
 * inputTicket makes use of the input step to allow users to manually enter a servicenow ticket
 * @param time integer Time limit. If the time limit is reached an exception is thrown which leads to aborting the build
 * @param unit The time limit can be in one of these values: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS
 * @param message String This top level message will be displayed to the user
 * @param defaultValue String This is the default text value for the servicenow parameter which is displayed to the user. The user can manually overwrite this.
 * @param description String This is a description of the servicenow ticket parameter
 * @param submitter String User IDs or windows global groups of people permitted to respond to the input, separated by ','. No whitespace.
 */
  def inputTicket(Map<String, Object> params) {
    def defaults = [
      time        : 1,
      unit        : 'DAYS',
      message     : 'Enter ticket',
      defaultValue: jenkins.env.SN_TICKET,
      description : 'Ticket',
      submitter   : '' //optional, leave blank for anonymous approval, can be a secure group, set up with "Configure Global Security" in a Jenkins instance
    ]
    def config = defaults + params
    def userInput
    def inputParams = []
    def ticketParam = jenkins.string(
      [
        defaultValue: config.defaultValue,
        description : 'Ticket',
        name        : 'TICKET',
      ]
    )
    jenkins.echo "inputTicket arguments: $config"

    try {
      inputParams.add(ticketParam)
      while (userInput == null || !(verifyTicketFormat(ticket: "${userInput['TICKET']}"))) {
        jenkins.echo "A valid servicenow ticket format must be entered e.g. CHG1234567 or INC1234567"
        jenkins.timeout(time: config.time, unit: config.unit) {
          userInput = jenkins.input([
            message           : config.message,
            parameters        : inputParams,
            submitter         : config.submitter,
            submitterParameter: 'APPROVER',
          ])
        }
      }
      jenkins.env.SN_TICKET = "${userInput['TICKET']}"
      jenkins.echo "SN_TICKET env var set to " + jenkins.env.SN_TICKET
    } catch (FlowInterruptedException e) {
      throw e
    } catch (Exception ex) {
      jenkins.error("inputTicket failed: " + ex.getMessage())
    }
  }

/**
 * verifyTicketFormat validates the format of a servicenow ticket
 * @param ticket String Ticket to be verified e.g. CHG1234567 or INC1234567
 */
  def verifyTicketFormat(Map<String, Object> params) {
    def defaults = [
      ticket: jenkins.env.SN_TICKET
    ]
    def config = defaults + params
    jenkins.echo "verifyTicketFormat arguments: $config"
    boolean validFormat = false
    try {
      if (!(config.ticket == null || config.ticket == "")) {
        if (config.ticket ==~ /(?i)CHG\d{7}/ || config.ticket ==~ /(?i)INC\d{7}/) {
          jenkins.echo "Valid servicenow ticket format"
          validFormat = true
        } else {
          jenkins.echo "Invalid servicenow ticket format"
          validFormat = false
        }
      }
      return validFormat
    } catch (Exception ex) {
      jenkins.error("verifyTicketFormat failed: " + ex.getMessage())
    }
  }

/**
 * parseApiResponse returns the api response in a map
 * Adds current date
 * @param apiResponse String Response from api call
 */
  def parseApiResponse(String apiResponse) {
    try {
      jenkins.echo "parseApiResponse arguments: $apiResponse"
      def jsonString = apiResponse - '{"result":['
      jsonString = jsonString - ']}'
      if (jsonString == "") {
        jenkins.error "Ticket not returned from ServiceNow API call"
      }
      Map responseMap = jenkins.readJSON text: jsonString
      responseMap.put("date_of_this_info", new Date().toString())
      jenkins.echo responseMap.toString()
      return responseMap
    } catch (Exception ex) {
      jenkins.error("parseApiResponse failed: " + ex.getMessage())
    }
  }
}
