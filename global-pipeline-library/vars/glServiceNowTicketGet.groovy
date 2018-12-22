import com.optum.jenkins.pipeline.library.servicenow.Ticket
/**
 * getTicket retrieves key fields for a ticket
 * @param credentials String Required credentials to interact with ServiceNow
 * @param ticket String Ticket to be verified e.g. CHG1234567 or INC1234567
 * @param serviceNowApiUrl String Base of the url for accessing the ServiceNow API
 */
def call(Map<String, Object> config){
  Ticket ticket = new Ticket(this)
  ticket.getTicket(config)
}
