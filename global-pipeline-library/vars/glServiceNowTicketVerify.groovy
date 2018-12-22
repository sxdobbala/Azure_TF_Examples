import com.optum.jenkins.pipeline.library.servicenow.Ticket
/**
 * verifyTicket validates change and incident tickets against ServiceNow
 * @param credentials String Required credentials to interact with ServiceNow
 * @param ticket String Ticket to be verified e.g. CHG1234567 or INC1234567
 * @param isChangeWindow boolean Verify that current time is within the Change Ticket change window for non incident Change Tickets
 */
def call(Map<String, Object> config){
  Ticket ticket = new Ticket(this)
  ticket.verifyTicket(config)
}
