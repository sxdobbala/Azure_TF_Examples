package com.optum.jenkins.pipeline.library.utils

class JenkinsMock extends HashMap implements Serializable {
  JenkinsMock() {
    super()
    addAnyMissingKeys()
  }

  JenkinsMock(java.util.LinkedHashMap h) {
    super(h)
    addAnyMissingKeys()
  }

  def command(String cmd) {
    this.calledJenkinsCommand += "$cmd\n"
  }

  def command(String cmd, Boolean bool) {
    this.command(cmd)
    return bool ? this.get(cmd.split()[0].trim(), cmd) : cmd //if retrunStdOut is set to true, return the expected result (for that command) stored in the mock Jenkins object
  }

  def command(String cmd, Boolean bool, String shabang) {
    this.command(cmd)
    return bool ? this.get(cmd.split()[0].trim(), cmd) : cmd //if retrunStdOut is set to true, return the expected result (for that command) stored in the mock Jenkins object
  }


  def string(java.util.LinkedHashMap h) {
    this.env[h.variable] = this.env[h.credentialsId]
  }


  def withCredentials(java.util.ArrayList a, Closure c) {
    c.call()
  }

  def echo(String msg) {
    println(msg)
  }

  def error(String msg) {
    throw new JenkinsMockErrorException(msg)
  }

  def usernamePassword(java.util.LinkedHashMap h) {
  }

  def timeout(java.util.LinkedHashMap h, Closure c) {
    c.call()
  }

  private def addAnyMissingKeys() {
    this.calledJenkinsCommand = ""
    if (this.env == null)
      this.env = {}
  }
}
