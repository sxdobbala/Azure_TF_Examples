import com.optum.jenkins.pipeline.library.servicenow.Ticket
/**
 * getTicketFromGitCommit reads the first word of the latest git commit subject message.
 * @param envVar boolean When true, set the environment variable for the ticket
 */
def call(Map<String, Object> config){
  Ticket ticket = new Ticket(this)
  ticket.getTicketFromGitCommit(config)
}
