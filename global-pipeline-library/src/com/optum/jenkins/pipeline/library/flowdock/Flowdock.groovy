#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.flowdock

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class Flowdock implements Serializable {
  def jenkins

  Flowdock() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Flowdock(jenkins) {
    this.jenkins = jenkins
  }

  /**
  * Posts a message to a Flowdock flow using the credentials provided for auth.  This message is posted to a new thread or existing thread if threadId is provided
  * @param credentialsId String that is a valid Jenkins Credential ID exposing the flowdock authorization token used to post a message to Flowdock.  Default ID is 'FLOWDOCK_API_PERSONAL'.
  * @param credentialsType String that indicates the type of authentication to use, "user", "source", or "oauth"
  * @param flow String that defines the flowdock flow you are seeking to post a message onto.
  * @param message String that is the message to post.
  * @param org String that defines the flowdock organization. Default is UHG.
  * @param threadId String that identifies an existing thread to post to, message will post as a reply to the first message of that thread
  */
  def signalFlowdock(Map<String, Object> params) {
    def defaults = [
            credentialsId : 'FLOWDOCK_API_PERSONAL',  // optional, the jenkins ENV ID storing a flowdock API token, if not specified, it attempts to look up 'FLOWDOCK_API_PERSONAL'
            credentialsType: 'user', //optional, the type of credentials specified by credentialsId, defaults to user (http basic)
            flow          : null,  // required, the flow you want to write to
            message       : null,  // required, the message you wish written
            org           : 'uhg',  // optional, UHG has one primary Flowdock Org
            threadId      : null   // optional, a thread to post a reply to; if left null, will create a new thread and return the new threadId
    ]
    def config = defaults + params
    validateInputs(config, ['flow', 'message'])
    jenkins.echo "Flowdock arguments: $config"

    String fToken = lookupToken(config.credentialsId).trim() // fToken is the actual Flowdock token

    String curlBeginning = 'curl -s -X POST '
    String headers = "--header 'Content-Type: application/json' --header 'X-flowdock-wait-for-message: 10000' " // wait for message response to retrieve new thread_id

    def body = [content: config.message]

    // Add authorization credentials
    if(config.credentialsType == 'user') {
      String fTokenEncoded = (fToken + ':\n').bytes.encodeBase64().toString()  // fToken is the actual Flowdock token base64 encoded
      headers += "--header 'Authorization: Basic $fTokenEncoded' "
    } else if(config.credentialsType == 'oauth') {
      headers += "--header 'Authorization: Bearer $fToken' "
    } else if(config.credentialsType == 'source') {
      body = body + [flow_token: fToken]
    } else {
      jenkins.error "ERROR:  The 'credentialsType' is not a supported type of token authentication, for this method"
    }

    String jsonBody = config.threadId?.trim() //include thread_id if its not null or empty
      ? requestBodyFlowdock(body + [thread_id: config.threadId])
      : requestBodyFlowdock(body)
    String jsonDataSwitch = " -d '$jsonBody' "

    String flowdockURL = " https://api.flowdock.com/flows/$config.org/$config.flow/messages "

    String flowdockCommand = curlBeginning + headers + jsonDataSwitch + flowdockURL

    String res = jenkins.command(flowdockCommand, true, '#!/bin/bash +x') //+x flag keeps command from being echo-ed
    print "\nFlowdock reply:\n$res"
    return getThreadId(res)
  }

  /*
  * Send a private message to a Flowdock user identified by the provided user id, using a personal user or oauth token for auth.
  * @param credentialsId String that is a valid Jenkins Credential ID exposing the flowdock authorization token used to make Flowdock API requests.  Default ID is 'FLOWDOCK_API_PERSONAL'.
  * @param credentialsType String that indicates the type of authentication to use, "user" or "oauth".
  * @param userId Integer that identifies the Flowdock user who will be the recipient of the message.
  */
  def signalUser(Map<String, String> params) {
    def defaults = [
      credentialsId  : 'FLOWDOCK_API_PERSONAL',  // optional, the jenkins ENV ID storing a flowdock API token, if not specified, it attempts to look up 'FLOWDOCK_API_PERSONAL'
      credentialsType: 'user', // optional, the type of credentials specified by credentialsId, defaults to user (http basic)
      userId         : null, // required, the email corresponding to the defsired user id
      message        : null,  // required, the message you wish written
    ]
    def config = defaults + params
    validateInputs(config, ['userId', 'message'])
    jenkins.echo "Flowdock arguments: $config"

    String fToken = lookupToken(config.credentialsId).trim() // fToken is the actual Flowdock token

    String curlBeginning = 'curl -s -X POST '
    String headers = "--header 'Content-Type: application/json' "

    // Add authorization credentials
    if(config.credentialsType == 'user') {
      String fTokenEncoded = (fToken + ':\n').bytes.encodeBase64().toString()  // fToken is the actual Flowdock token base64 encoded
      headers += "--header 'Authorization: Basic $fTokenEncoded' "
    } else if(config.credentialsType == 'oauth') {
      headers += "--header 'Authorization: Bearer $fToken' "
    } else {
      jenkins.error "ERROR:  The 'credentialsType' is not a supported type of token authentication, for this method"
    }

    def jsonBody = " -d '${requestBodyFlowdock([content: config.message])}' "
    String flowdockURL = "https://api.flowdock.com/private/${config.userId}/messages"

    String flowdockCommand = curlBeginning + headers + jsonBody + flowdockURL
    jenkins.command(flowdockCommand, false, '#!/bin/bash +x') //+x flag keeps command from being echo-ed
  }

  /*
  * given a Flowdock user email, retrieve that user's Flowdock id
  * @param credentialsId String that is a valid Jenkins Credential ID exposing the flowdock authorization token used to make Flowdock API requests.  Default ID is 'FLOWDOCK_API_PERSONAL'.
  * @param credentialsType String that indicates the type of authentication to use, "user" or "oauth".
  * @param email String which is a valid email address for a user belonging to the specified flow
  * @param flow String that defines the flowdock flow you are seeking to post a message onto.
  * @param org String that defines the flowdock organization. Default is UHG.
  */
  def getUserId(Map<String, String> params) {
    def defaults = [
      credentialsId  : 'FLOWDOCK_API_PERSONAL',  // optional, the jenkins ENV ID storing a flowdock API token, if not specified, it attempts to look up 'FLOWDOCK_API_PERSONAL'
      credentialsType: 'user', // optional, the type of credentials specified by credentialsId, defaults to user (http basic)
      email          : null, // required, the email corresponding to the defsired user id
      flow           : null,  // required, the flow you want to write to
      org            : 'uhg',  // optional, UHG has one primary Flowdock Org
    ]
    def config = defaults + params
    validateInputs(config, ['email', 'flow'])
    jenkins.echo "Flowdock arguments: $config"

    String fToken = lookupToken(config.credentialsId).trim() // fToken is the actual Flowdock token

    String curlBeginning = 'curl -s -X GET '
    String headers = "--header 'Accept: application/json' "

    // Add authorization credentials
    if(config.credentialsType == 'user') {
      String fTokenEncoded = (fToken + ':\n').bytes.encodeBase64().toString()  // fToken is the actual Flowdock token base64 encoded
      headers += "--header 'Authorization: Basic $fTokenEncoded' "
    } else if(config.credentialsType == 'oauth') {
      headers += "--header 'Authorization: Bearer $fToken' "
    } else {
      jenkins.error "ERROR:  The 'credentialsType' is not a supported type of token authentication, for this method"
    }

    String flowdockURL = " https://api.flowdock.com/flows/$config.org/$config.flow/users "
    String flowdockCommand = curlBeginning + headers + flowdockURL

    String res = jenkins.command(flowdockCommand, true, '#!/bin/bash +x') //+x flag keeps command from being echo-ed
    print "\nFlowdock reply:\n$res"

    // parse response and find user with given email
    def slurper = new JsonSlurper()
    try {
      def flowUsers = slurper.parseText(res.trim())

      for (user in flowUsers) {
        if (user.email == config.email) {
          return user.id
        }
      }

      return null

    } catch (groovy.json.JsonException e) {
      return null
    }
  }

  private validateInputs(Map<String, String> config, List<String> required) {
    if (required.contains('flow') && !config.flow)
      jenkins.error "ERROR:  The 'flow' parameter was not specified when calling glFlowdockSay.  You can look up the exact name of your desired flow by opening your flowdock's account settings."
    if (required.contains('message') && !config.message)
      jenkins.error "ERROR:  The 'message' parameter was not specified when calling glFlowdockSay.  You need to tell the method what text to write into flowdock."
    if (required.contains('email') && !config.email)
      jenkins.error "ERROR:  The 'email' parameter was not specified when calling Flowdock.getUserId.  You need to tell the method what email to use to look up the user."
    if (required.contains('userId') && !config.userId)
      jenkins.error "ERROR:  The 'userId' parameter was not specified when calling Flowdock.signalUser.  You need to tell the method what user id"
  }

  // Generates the request body including the flowdock message.
  @NonCPS
  private requestBodyFlowdock(Map<String, String> details) {
    def jsonBuilder = new JsonBuilder()
    def root = jsonBuilder.call([event: 'message'] + details)
    return jsonBuilder.toString()
  }

  // Attempts to read the Flowdock token from Jenkins' configured crendentials
  private lookupToken(String credentialsId) {
    jenkins.withCredentials([ jenkins.string(credentialsId: credentialsId, variable: 'FLOWDOCK_API_PERSONAL_LITERAL') ]) {
      String fToken = jenkins.env.FLOWDOCK_API_PERSONAL_LITERAL
      if (!fToken || fToken == ""){
        jenkins.error "ERROR:  glFlowdockSay could not retrieved the Flowdock API token from Jenkins credentials.  Did you configure credentials and specify to lookup the correct credential ID?"
      }
      return fToken
    }
  }

  private getThreadId(String flowResponse) {
    def slurper = new JsonSlurper()
    try {
      return slurper.parseText(flowResponse.trim() ?: '{}').thread_id //return the thread_id from the response; if empty response, trim spaces and replace null with empty json
    } catch (groovy.json.JsonException e) {
      return null //if the response can not be parsed into json, return null; Note: effectively fails silently
    }
  }
}
