package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 *  The flowdock api call looks like this: https://${token}@api.flowdock.com/flows/${org}/${flow}/messages
 *  The token is the personal api token from the Flowdock account.
 *  The org is the organization the flow belongs to. In our case, it is uhg.
 *  The flow is the name of the flow that the message is being sent to. Example, ct-instrumentation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Flowdock implements Serializable
{    
    String org
    String flow
    String token
}