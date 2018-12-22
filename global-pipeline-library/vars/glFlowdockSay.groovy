import com.optum.jenkins.pipeline.library.flowdock.Flowdock

/**
 * Posts a message to a Flowdock flow using the credentials provided for auth. This message is posted to the main page as a new thread.
 *
 * @param credentialsId String that is a valid Jenkins Credential ID exposing the flowdock authorization token used to post a message to Flowdock.  Default ID is 'FLOWDOCK_API_PERSONAL'.
 * @param flow String that defines the flowdock flow you are seeking to post a message onto.
 * @param message String that is the message to post.
 * @param org String that defines the flowdock organization. Default is 'uhg'.
 * */

def call(Map<String, Object> config){
    Flowdock flowdock = new Flowdock(this)
    return flowdock.signalFlowdock(config)
}
