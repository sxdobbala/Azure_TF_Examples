import com.optum.jenkins.pipeline.library.flowdock.Flowdock

/**
 * Sends a message to a Flowdock user identified by email address, using the credentials provided for auth.
 *
 * @param credentialsId String that is a valid Jenkins Credential ID exposing the flowdock authorization token used to post a message to Flowdock.  Default ID is 'FLOWDOCK_API_PERSONAL'.
 * @param credentialsType String that indicates the type of authentication to use, "user" or "oauth". Default type is 'user'.  NOTE: Flowdock doesn't allow messaging the same user who's credentials are used.
 * @param flow String that defines a flowdock flow the user belongs to.
 * @param email String that identifies the user's email address
 * @param message String that is the message to send.
 * @param org String that defines the flowdock organization. Default is 'uhg'.
 * */

def call(Map<String, Object> config){
  Flowdock flowdock = new Flowdock(this)
  // get the user's flowdock id
  def id = flowdock.getUserId(config)
  if(id != null) {
    echo "Flowdock user ID: ${id}"
    // message the user by their id
    flowdock.signalUser(config + [userId: id])
  } else {
    echo "User with the email address ${config.email} doesn't belong to the ${config.flow} flow"
  }
}
