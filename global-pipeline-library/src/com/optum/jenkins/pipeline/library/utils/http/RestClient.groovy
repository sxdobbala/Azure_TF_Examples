package com.optum.jenkins.pipeline.library.utils.http

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonOutput

class RestClient {

  static def post(jenkins, RequestParameters params) {
    String[] hosts = params.getHosts()

    // Todo add fallback hosts in case the request times out
    def host = hosts[0]
    def url = host + params.getPath()
    def body = params.getBody()
    jenkins.echo "Sending devops event: " + JsonOutput.toJson(body)
    RestResponse response = doPost(url, params.getContentType(), body,  params.getHeaders())
    jenkins.echo "Event send, response code: ${response.rc}, message: ${response.message}"
    return response
  }

  static def get(jenkins, RequestParameters params) {
    String[] hosts = params.getHosts()

    // Todo add fallback hosts in case the request times out
    def host = hosts[0]
    def url = host + params.getPath()

    def body = params.getBody()
    RestResponse response = doGet(url, params.getContentType(), body,  params.getHeaders())
    return response
  }


  //NonCPS because HttpURLConnection is not serializable -> function not allowed to contain jenkins objects
  @NonCPS
  static private RestResponse doPost(String url, String contentType, Map payload, Map headers){
    HttpURLConnection post = new URL(url).openConnection()
    post.setRequestMethod("POST")
    post.setDoOutput(true)
    post.setUseCaches(false)
    post.setRequestProperty("Content-Type", contentType)
    headers.each { k, v -> post.setRequestProperty(k, v) }
    def jsonMessage = JsonOutput.toJson(payload)
    post.getOutputStream().write(jsonMessage.getBytes("UTF-8"));
    def postRC = post.getResponseCode();
    return new RestResponse(rc: postRC, message: (postRC == 200 ?
      "Created devops metrics event, response: ${post.getInputStream().getText()}" :
      "Error sending devops to $url\n with body: \n $jsonMessage \n RestResponse code: $postRC, message:\n ${post.getErrorStream().getText()}") )
  }

  //NonCPS because HttpURLConnection is not serializable -> function not allowed to contain jenkins objects
  @NonCPS
  static private RestResponse doGet(String url, String contentType, Map payload, Map headers){
    HttpURLConnection get = new URL(url).openConnection()
    get.setRequestMethod("GET")
    get.setDoOutput(true)
    get.setUseCaches(false)
    get.setRequestProperty("Content-Type", contentType)
    headers.each { k, v -> get.setRequestProperty(k, v) }
    def jsonMessage = JsonOutput.toJson(payload)
    get.getOutputStream().write(jsonMessage.getBytes("UTF-8"));
    def getRC = get.getResponseCode();
    return new RestResponse(rc: getRC, message: (getRC == 200 ?
            "${get.getInputStream().getText()}" :
            "Error making a http get call to $url\n with body: \n $jsonMessage \n RestResponse code: $getRC, message:\n ${get.getErrorStream().getText()}") )
  }

  static class RestResponse implements Serializable{
    def rc
    def message
  }
}
