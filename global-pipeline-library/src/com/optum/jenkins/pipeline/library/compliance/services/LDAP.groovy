package com.optum.jenkins.pipeline.library.compliance.services

import com.optum.jenkins.pipeline.library.utils.Constants
import groovy.json.JsonSlurperClassic
import org.acegisecurity.acls.NotFoundException

class LDAP implements Serializable {
  String graphQlUrl = Constants.LDAP_SERVICE_API_URL + '/api/v1/ldap/graphql'

  def fetchSecureGroupMemberEmails(String group) {
    def emails = []
    def responseMap = fetchGraphQlResponseMap(createLdapGroupRequestString(group))
    if (!(responseMap['data']) || !responseMap['data']['group']) {
      throw new NotFoundException("Secure group ${group} not found.")
    }
    (responseMap['data']['group']['members'] ?: []).each {
      emails.add(it['email'])
    }
    return emails
  }

  def fetchSecureMemberDetails(String msId) {
    def responseMap = fetchGraphQlResponseMap(createLdapMemberRequestString(msId))
    if (!(responseMap['data']) || !responseMap['data']['user']) {
      throw new NotFoundException("Secure user ${msId} not found.")
    }
    String name = responseMap['data']['user']['name']
    String email = responseMap['data']['user']['email']
    return [name, email]
  }

  def fetchGraphQlResponseMap(String requestBody) {
    def post = new URL(graphQlUrl).openConnection()
    post.setRequestMethod('POST')
    post.setDoOutput(true)
    post.setRequestProperty('Content-Type', 'application/json')
    post.getOutputStream().write(requestBody.getBytes('UTF-8'))
    if (!post.getResponseCode().equals(200)) {
      throw new NotFoundException('LDAP API request unsuccessful.')
    }
    def responseText = post.getInputStream().getText()
    post.getInputStream().close()
    def jsonSlurper = new JsonSlurperClassic()
    return jsonSlurper.parseText(responseText)
  }

  def createLdapMemberRequestString(String msId) {
    return """
    query {
      user(msId: "${msId}") {
        name
        email
      }
    }
    """
  }

  def createLdapGroupRequestString(String group) {
    return """
    query {
      group(name: "${group}") {
        members {
          email
        }
      }
    }
    """
  }
}
