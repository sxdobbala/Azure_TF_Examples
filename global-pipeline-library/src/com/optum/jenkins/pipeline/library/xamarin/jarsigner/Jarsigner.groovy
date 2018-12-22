#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.xamarin.jarsigner

class Jarsigner implements Serializable {
  def jenkins

  Jarsigner() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Jarsigner(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Shortcut for running jarsigner
 * https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jarsigner.html
 *
 * @params inputApk String Required  
 * @params keystore String Required 
 *      Specifies a keystore to be used if you don't want to use the .keystore
 *      default database.
 * @params keystoreName String Required 
 * @params signedjar String Required 
 *      Specifies the name of the signed JAR file to be generated if you don't
 *      want the original unsigned file to be overwritten with the signed file.
 * @params storepass String Required
 *      Specifies the password that is required to access the keystore. This 
 *      is only needed when signing (not verifying) a JAR file. In that case, 
 *      if a -storepass option is not provided at the command line, then the 
 *      user is prompted for the password.
 *
 * @params sigalg String Optional
 *      Specifies the name of the signature algorithm to use to sign the JAR file.
 * @params verbose Boolean Optional
 */
  def sign(Map<String, Object> params) {
    def defaults = [
      // Required
      alias           : '',
      inputApk        : '',
      signedjar       : '',
      keyCredentials  : '',
      storeCredentials: '',
      // Optional
      digestalg       : 'SHA1',
      sigalg          : 'MD5withRSA',
      verbose         : true
    ]
    def config = defaults + params

    jenkins.echo "sign arguments: $config"
    jenkins.withCredentials([
      jenkins.string(credentialsId: config.storeCredentials, variable: 'storePass'),
      jenkins.file(credentialsId: config.keyCredentials, variable: 'key')
    ]) {
      jenkins.echo "signing jar with $key and $storePass"
      jenkins.sh 'jarsigner ' +
        (config.verbose ? '-verbose ' : '') +
        '-sigalg ' + config.sigalg + ' ' +
        '-digestalg ' + config.digestalg + ' ' +
        '-keystore ' + key + ' ' +
        '-storepass ' + storePass + ' ' +
        '-signedjar ' + config.signedjar + ' ' +
        config.inputApk + ' ' +
        "\'" + config.alias + "\'"
    }
  }

/**
 * Wrapper for backwards compatibility.
 */
  def sign(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    sign(config)
  }
}