package com.optum.jenkins.pipeline.library.utils.http

interface RequestParameters {
  String[] getHosts()
  String getPath()
  String getContentType()
  Map getBody()
  Map getHeaders()

}