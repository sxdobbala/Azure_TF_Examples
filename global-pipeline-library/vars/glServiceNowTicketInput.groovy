import com.optum.jenkins.pipeline.library.servicenow.Ticket
/**
 * inputTicket makes use of the input step to allow users to manually enter a ticket
 * @param time integer Time limit. If the time limit is reached an exception is thrown which leads to aborting the build
 * @param unit The time limit can be in one of these values: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS
 * @param message String This top level message will be displayed to the user
 * @param defaultValue String This is the default text value for the ticket parameter which is displayed to the user. The user can manually overwrite this.
 * @param description String This is a description of the ticket parameter
 * @param submitter String User IDs or windows global groups of people permitted to respond to the input, separated by ','. No whitespace.
 */
def call(Map<String, Object> config){
  Ticket ticket = new Ticket(this)
  ticket.inputTicket(config)
}
