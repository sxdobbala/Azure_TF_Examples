package com.optum.jenkins.pipeline.library.servicenow

import com.optum.jenkins.pipeline.library.scm.Git
import com.optum.jenkins.pipeline.library.utils.JenkinsMock
import com.optum.jenkins.pipeline.library.utils.JenkinsMockErrorException
import hudson.model.Result
import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException
import spock.lang.Specification
import spock.lang.Unroll

class TicketSpec extends Specification {

  def "jenkins context is available"(){
    given: "Default jenkins context"
    def jenkins = new JenkinsMock()
    when: 'Creating class with jenkins context'
    def ticket = new Ticket(jenkins)
    then: "Jenkins context is available"
    ticket.getJenkins() == jenkins
  }

  def "error for missing jenkins context"(){
    when: 'Creating class without jenkins context'
    def ticket = new Ticket()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  @Unroll
  def "verifyTicket error for missing/invalid parameter or false isValid '#testName'"() {
    given:
    def jenkins = new JenkinsMock()
    when: "I run verifyTicket with missing required parameters"
    def ticket = new Ticket(jenkins) {
      @Override
      def verifyChangeTicket(Map<String, Object> params){
        return false
      }
      @Override
      def verifyIncidentTicket(Map<String, Object> params){
        return false
      }
    }
    ticket.verifyTicket(config)
    then: "Exception is thrown"
    JenkinsMockErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    testName               | config                                            | errorMessage
    'credentials'          | [:]                                               | 'The credentials for ServiceNow api are required'
    'ticketFormat'         | [credentials:'sn_read_user', ticket:'CHG1']       | 'Invalid servicenow ticket format'
    'verifyChangeTicket'   | [credentials:'sn_read_user', ticket:'CHG1234567'] | 'The servicenow ticket failed verification with ServiceNow'
    'verifyIncidentTicket' | [credentials:'sn_read_user', ticket:'INC1234567'] | 'The servicenow ticket failed verification with ServiceNow'
  }

  def "verifyTicket verifyChangeTicket ServiceNow command call created correctly and approval verified"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock(
      env     : [ SN_USER: 'sn_read_user', SN_PASS: 'snpassword']
    )
    def expectedCmd = ' curl -X GET --header "Accept:application/json" --user \'sn_read_user\':\'snpassword\'  \\ "https://optumworker.service-now.com/api/now/table/change_request?sysparm_query=number=CHG1234567^active=true&sysparm_fields=number,approval,active,start_date,end_date,short_description,state,type&sysparm_display_value=true&sysparm_exclude_reference_link=true&sysparm_limit=1" '
    when: "I run verifyTicket and the ticket is approved"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        return ["end_date":"08/23/2018 19:59:59","number":"CHG0295023","short_description":"CMM Monthly Data Refresh","sys_id":"db37b388db7513c038b9a026ca9619b5","approval":"Approved","active":"true","state":"Work In Progress","type":"Standard","start_date":"08/20/2018 20:00:00","date_of_this_info":"Wed Aug 22 06:24:14 CDT 2018"]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'CHG1234567', isChangeWindow:false]
    ticket.verifyTicket(config)
    then: "the command is structured correctly and no exception is thrown"
    String cmd = jenkins.calledJenkinsCommand
    cmd.replaceAll("\\s+"," ") == expectedCmd.replaceAll("\\s+"," ")
    noExceptionThrown()
  }

  def "verifyTicket verifyChangeTicket change ticket window verification "() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock()
    when: "I run verifyTicket and the ticket is currently within the change window"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        //future end date
        return ["end_date": "08/23/2099 19:59:59","number":"CHG0295023","short_description":"CMM Monthly Data Refresh","sys_id":"db37b388db7513c038b9a026ca9619b5","approval":"Approved","active":"true","state":"Work In Progress","type":"Standard","start_date": "08/20/2018 20:00:00","date_of_this_info": new Date().toString()]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'CHG1234567', isChangeWindow:true]
    ticket.verifyTicket(config)
    then: "No exception is thrown"
    noExceptionThrown()
  }

  def "verifyTicket verifyChangeTicket error when change ticket not approved "() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock()
    when: "I run verifyTicket and the ticket is not approved"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        // approval set to Rejected
        return ["end_date":"08/23/2018 19:59:59","number":"CHG0295023","short_description":"CMM Monthly Data Refresh","sys_id":"db37b388db7513c038b9a026ca9619b5","approval":"Rejected","active":"true","state":"Work In Progress","type":"Standard","start_date":"08/20/2018 20:00:00","date_of_this_info":"Wed Aug 22 06:24:14 CDT 2018"]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'CHG1234567', isChangeWindow:false]
    ticket.verifyTicket(config)
    then: "An exception is thrown"
    JenkinsMockErrorException e = thrown()
    e.message.contains('The servicenow ticket failed verification with ServiceNow')
  }

  def "verifyTicket verifyChangeTicket error when outside change ticket window "() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock()
    when: "I run verifyTicket with change window check and the ticket is outside the change window"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        return ["end_date":"08/23/2018 19:59:59","number":"CHG0295023","short_description":"CMM Monthly Data Refresh","sys_id":"db37b388db7513c038b9a026ca9619b5","approval":"Approved","active":"true","state":"Work In Progress","type":"Standard","start_date":"08/20/2018 20:00:00","date_of_this_info": new Date().toString()]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'CHG1234567', isChangeWindow:true]
    ticket.verifyTicket(config)
    then: "An exception is thrown"
    JenkinsMockErrorException e = thrown()
    e.message.contains('The servicenow ticket failed verification with ServiceNow')
  }

  def "verifyTicket verifyChangeTicket change window not checked for Incident Change Ticket "() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock()
    when: "I run verifyTicket with change window check and the ticket is an incident change"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        return ["end_date":"08/23/2018 19:59:59","number":"CHG0295023","short_description":"CMM Monthly Data Refresh","sys_id":"db37b388db7513c038b9a026ca9619b5","approval":"Approved","active":"true","state":"Work In Progress","type":"Incident","start_date":"08/20/2018 20:00:00","date_of_this_info": new Date().toString()]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'CHG1234567', isChangeWindow:true]
    ticket.verifyTicket(config)
    then: "No exception is thrown"
    noExceptionThrown()
  }

  def "verifyTicket verifyIncidentTicket ServiceNow command call created correctly"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock(
      env     : [ SN_USER: 'sn_read_user', SN_PASS: 'snpassword']
    )
    def expectedCmd = ' curl -X GET --header "Accept:application/json" --user \'sn_read_user\':\'snpassword\'  \\ "https://optumworker.service-now.com/api/now/table/incident?sysparm_query=number=INC8199523^active=true&sysparm_fields=number,approval,active,priority,short_description,state&sysparm_display_value=true&sysparm_exclude_reference_link=true&sysparm_limit=1" '
    when: "I run verifyTicket for an Pri 1 or 2 Incident"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        return ["parent_incident":"","number":"INC8199523","short_description":"DBSLP0306 Job ARA_DMN_UX_DMJ_DLY_03PM_PRD generated an FAIL alarm at 2018080506490000 (J=ARA_DMN_UX_","sys_id":"006343681373538c1fae3598d144b000","approval":"Not Yet Requested","active":"true","state":"Work In Progress","priority":"2","date_of_this_info":"Mon Aug 27 04:49:17 CDT 2018"]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'INC8199523']
    ticket.verifyTicket(config)
    then: "the command is structured correctly and no exception is thrown"
    String cmd = jenkins.calledJenkinsCommand
    cmd.replaceAll("\\s+"," ") == expectedCmd.replaceAll("\\s+"," ")
    noExceptionThrown()
  }

  def "verifyTicket verifyIncidentTicket error when Incident is Priority 3, i.e. not Pri 1 or 2 "() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock()
    when: "I run verifyTicket with priority 3 incident"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        // Priority 3
        return ["parent_incident":"","number":"INC8199523","short_description":"DBSLP0306 Job ARA_DMN_UX_DMJ_DLY_03PM_PRD generated an FAIL alarm at 2018080506490000 (J=ARA_DMN_UX_","sys_id":"006343681373538c1fae3598d144b000","approval":"Not Yet Requested","active":"true","state":"Work In Progress","priority":"3","date_of_this_info":"Mon Aug 27 04:49:17 CDT 2018"]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'INC8199523']
    ticket.verifyTicket(config)
    then: "An exception is thrown"
    JenkinsMockErrorException e = thrown()
    e.message.contains('The servicenow ticket failed verification with ServiceNow')
  }

  @Unroll
  def "getTicket error for missing/invalid parameter or false isValid '#testName'"() {
    given:
    def jenkins = new JenkinsMock()
    when: "I run getTicket with missing required paramaters"
    def ticket = new Ticket(jenkins) {}
    ticket.getTicket(config)
    then: "Exception is thrown"
    JenkinsMockErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    testName               | config                                            | errorMessage
    'credentials'          | [:]                                               | 'The credentials for ServiceNow api are required'
    'ticketFormat'         | [credentials:'sn_read_user', ticket:'CHG1']       | 'Invalid servicenow ticket format'
  }

  def "getTicket getChangeTicket ServiceNow command call created correctly"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock(
      env     : [ SN_USER: 'sn_read_user', SN_PASS: 'snpassword']
    )
    def expectedCmd = ' curl -X GET --header "Accept:application/json" --user \'sn_read_user\':\'snpassword\'  \\ "https://optumworker.service-now.com/api/now/table/change_request?sysparm_query=number=CHG1234567^active=true&sysparm_fields=number,approval,active,start_date,end_date,short_description,state,type,sys_id&sysparm_display_value=true&sysparm_exclude_reference_link=true&sysparm_limit=1" '
    when: "I run getTicket for a change"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        return ["end_date":"08/23/2018 19:59:59","number":"CHG0295023","short_description":"CMM Monthly Data Refresh","sys_id":"db37b388db7513c038b9a026ca9619b5","approval":"Approved","active":"true","state":"Work In Progress","type":"Standard","start_date":"08/20/2018 20:00:00","date_of_this_info":"Wed Aug 22 06:24:14 CDT 2018"]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'CHG1234567', isChangeWindow:false]
    ticket.getTicket(config)
    then: "the command is structured correctly and no exception is thrown"
    String cmd = jenkins.calledJenkinsCommand
    cmd.replaceAll("\\s+"," ") == expectedCmd.replaceAll("\\s+"," ")
    noExceptionThrown()
  }

  def "getTicket getChangeTicket SN_TICKET_DETAILS env var set"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock(
      env     : [ SN_USER: 'sn_read_user', SN_PASS: 'snpassword']
    )
    def expected_SN_TICKET_DETAILS = '{"end_date":"08/23/2018 19:59:59","number":"CHG0295023","short_description":"CMM Monthly Data Refresh","sys_id":"db37b388db7513c038b9a026ca9619b5","approval":"Approved","active":"true","state":"Work In Progress","type":"Standard","start_date":"08/20/2018 20:00:00","date_of_this_info":"Wed Aug 22 06:24:14 CDT 2018"}'
    when: "I run getTicket for a change"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        return ["end_date":"08/23/2018 19:59:59","number":"CHG0295023","short_description":"CMM Monthly Data Refresh","sys_id":"db37b388db7513c038b9a026ca9619b5","approval":"Approved","active":"true","state":"Work In Progress","type":"Standard","start_date":"08/20/2018 20:00:00","date_of_this_info":"Wed Aug 22 06:24:14 CDT 2018"]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'CHG1234567', isChangeWindow:false]
    ticket.getTicket(config)
    def snTicketDetails = jenkins.env.SN_TICKET_DETAILS
    then: "SN_TICKET_DETAILS env var is set"
    snTicketDetails.replaceAll("\\s+"," ") == expected_SN_TICKET_DETAILS.replaceAll("\\s+"," ")
  }

  def "getTicket getIncidentTicket ServiceNow command call created correctly"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock(
      env     : [ SN_USER: 'sn_read_user', SN_PASS: 'snpassword']
    )
    def expectedCmd = ' curl -X GET --header "Accept:application/json" --user \'sn_read_user\':\'snpassword\'  \\ "https://optumworker.service-now.com/api/now/table/incident?sysparm_query=number=INC8199523^active=true&sysparm_fields=number,approval,active,priority,short_description,state,parent_incident,sys_id&sysparm_display_value=true&sysparm_exclude_reference_link=true&sysparm_limit=1" '
    when: "I run getTicket for an incident"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        return ["parent_incident":"","number":"INC8199523","short_description":"DBSLP0306 Job ARA_DMN_UX_DMJ_DLY_03PM_PRD generated an FAIL alarm at 2018080506490000 (J=ARA_DMN_UX_","sys_id":"006343681373538c1fae3598d144b000","approval":"Not Yet Requested","active":"true","state":"Work In Progress","priority":"2","date_of_this_info":"Mon Aug 27 04:49:17 CDT 2018"]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'INC8199523']
    ticket.getTicket(config)
    then: "the command is structured correctly and no exception is thrown"
    String cmd = jenkins.calledJenkinsCommand
    cmd.replaceAll("\\s+"," ") == expectedCmd.replaceAll("\\s+"," ")
    noExceptionThrown()
  }

  def "getTicket getIncidentTicket SN_TICKET_DETAILS env var set"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock(
      env     : [ SN_USER: 'sn_read_user', SN_PASS: 'snpassword']
    )
    def expected_SN_TICKET_DETAILS = '{"parent_incident":"","number":"INC8199523","short_description":"DBSLP0306 Job ARA_DMN_UX_DMJ_DLY_03PM_PRD generated an FAIL alarm at 2018080506490000 (J=ARA_DMN_UX_","sys_id":"006343681373538c1fae3598d144b000","approval":"Not Yet Requested","active":"true","state":"Work In Progress","priority":"2","date_of_this_info":"Mon Aug 27 04:49:17 CDT 2018"}'
    when: "I run getTicket for an incident"
    def ticket = new Ticket(jenkins) {
      @Override
      def parseApiResponse(String apiResponse){
        return ["parent_incident":"","number":"INC8199523","short_description":"DBSLP0306 Job ARA_DMN_UX_DMJ_DLY_03PM_PRD generated an FAIL alarm at 2018080506490000 (J=ARA_DMN_UX_","sys_id":"006343681373538c1fae3598d144b000","approval":"Not Yet Requested","active":"true","state":"Work In Progress","priority":"2","date_of_this_info":"Mon Aug 27 04:49:17 CDT 2018"]
      }
    }
    def config = [credentials:'sn_read_user', ticket:'INC8199523']
    ticket.getTicket(config)
    def snTicketDetails = jenkins.env.SN_TICKET_DETAILS
    then: "SN_TICKET_DETAILS env var is set"
    snTicketDetails.replaceAll("\\s+"," ") == expected_SN_TICKET_DETAILS.replaceAll("\\s+"," ")
  }

  def "getTicketFromGitCommit reads ticket from start of commit message"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock()
    Git gitStub = Stub()
    GroovyStub(Git, global: true)
    new Git(jenkins) >> gitStub
    gitStub.getGitSubject() >> 'CHG1234567 plus some message'
    when: "I run getTicketFromGitCommit"
    def ticket = new Ticket(jenkins)
    def config = [envvar: true]
    def ticketNumber = ticket.getTicketFromGitCommit(config)
    def snTicket = jenkins.env.SN_TICKET
    then: "The ticket number is parsed from the commit message"
    ticketNumber == 'CHG1234567'
    snTicket == 'CHG1234567'
  }

  def "inputTicket sets SN_TICKET"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock() {
      @Override
      def string(java.util.LinkedHashMap h) {
      }
    }
    jenkins.input = { def inputData -> [TICKET:'CHG1234567']}
    when: "I run inputTicket"
    def ticket = new Ticket(jenkins)
    def config = [:]
    ticket.inputTicket(config)
    then: "SN_TICKET env var is set to the entered ticket"
    jenkins.env.SN_TICKET == 'CHG1234567'
    noExceptionThrown()
  }

  def "inputTicket allows flow interrupt"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock() {
      @Override
      def string(java.util.LinkedHashMap h) {
      }
    }
    jenkins.input = { def inputData -> throw new FlowInterruptedException(Result.ABORTED)}
    when: "I run inputTicket"
    def ticket = new Ticket(jenkins)
    def config = [:]
    ticket.inputTicket(config)
    then: "FlowInterruptedException thrown"
    thrown FlowInterruptedException
  }

  @Unroll
  def "verifyTicketFormat accepts valid ticket formats '#testName'"() {
    given:
    def jenkins = new JenkinsMock()
    when: "I run verifyTicket with valid ticket formats"
    def ticket = new Ticket(jenkins)
    def isValid = ticket.verifyTicketFormat(config)
    then: "True is returned"
    isValid == true
    where:
    testName            | config
    'upperChg'          | [ticket:'CHG1234567']
    'lowerChg'          | [ticket:'chg1234567']
    'mixedChg'          | [ticket:'Chg1234567']
    'upperInc'          | [ticket:'INC1234567']
    'lowerInc'          | [ticket:'inc1234567']
    'mixedInc'          | [ticket:'Inc1234567']
  }

  @Unroll
  def "verifyTicketFormat rejects invalid ticket formats '#testName'"() {
    given:
    def jenkins = new JenkinsMock()
    when: "I run verifyTicket with invalid ticket formats"
    def ticket = new Ticket(jenkins)
    def isValid = ticket.verifyTicketFormat(config)
    then: "False is returned"
    isValid == false
    where:
    testName               | config
    'notChgOrInc'          | [ticket:'abc1234567']
    'tooShort'             | [ticket:'CHG123456']
    'tooLong'              | [ticket:'INC1234567x']
    'wrongStart'           | [ticket:'1CHG1234567']
  }

  def "parseApiResponse returns map with date_of_this_info"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock()
    def jsonObject = ["parent_incident":"","number":"INC8199523","short_description":"DBSLP0306 Job ARA_DMN_UX_DMJ_DLY_03PM_PRD generated an FAIL alarm at 2018080506490000 (J=ARA_DMN_UX_","sys_id":"006343681373538c1fae3598d144b000","approval":"Not Yet Requested","active":"true","state":"Work In Progress","priority":"2"]
    jenkins.readJSON = { def text -> jsonObject }
    when: "I run parseApiResponse"
    def ticket = new Ticket(jenkins)
    def apiResponse = '{"result":[{"parent_incident":"","number":"INC8199523","short_description":"DBSLP0306 Job ARA_DMN_UX_DMJ_DLY_03PM_PRD generated an FAIL alarm at 2018080506490000 (J=ARA_DMN_UX_","sys_id":"006343681373538c1fae3598d144b000","approval":"Not Yet Requested","active":"true","state":"Work In Progress","priority":"2"}]}'
    def responseMap = ticket.parseApiResponse(apiResponse)
    then: "The map is returned correctly with date_of_this_info"
    responseMap.containsKey('date_of_this_info')
  }

  def "parseApiResponse error if no ticket in apiResponse"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = new JenkinsMock()
    def jsonObject = ["parent_incident":"","number":"INC8199523","short_description":"DBSLP0306 Job ARA_DMN_UX_DMJ_DLY_03PM_PRD generated an FAIL alarm at 2018080506490000 (J=ARA_DMN_UX_","sys_id":"006343681373538c1fae3598d144b000","approval":"Not Yet Requested","active":"true","state":"Work In Progress","priority":"2"]
    jenkins.readJSON = { def text -> jsonObject }
    when: "I run parseApiResponse with no ticket in apiResponse"
    def ticket = new Ticket(jenkins)
    def apiResponse = '{"result":[]}'
    ticket.parseApiResponse(apiResponse)
    then: "An exception is thrown"
    JenkinsMockErrorException e = thrown()
    e.message.contains('Ticket not returned from ServiceNow API call')
  }

}
