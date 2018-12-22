#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.xamarin.zipalign

class Zipalign implements Serializable {
  def jenkins

  Zipalign() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Zipalign(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Shortcut for running zipalign
 *
 * @params inFile String Required Path to the input apk
 * @params outFile String Required Path of the output APK
 * @params forceOverwrite Boolean Optional Overwrite existing outfile.zip. Defaults to true.
 * @params verbose Boolean Optional Verbose Output. Defaults to true.
 * @params alignment Integer Optional Defaults to 4
 *
 */
  def align(Map<String, Object> params) {
    def defaults = [
      // Required
      inFile        : '',
      outFile       : '',
      // Optional
      forceOverwrite: true,
      verbose       : true,
      alignment     : 4
    ]
    def config = defaults + params

    jenkins.echo "align arguments: $config"
    jenkins.sh '/Users/jenkins/Library/Developer/Xamarin/android-sdk-macosx/build-tools/23.0.1/zipalign ' +
      (config.forceOverwrite ? '-f ' : '') +
      (config.verbose ? '-v ' : '') +
      config.alignment + ' ' +
      config.inFile + ' ' +
      config.outFile
  }

/**
 * Wrapper for backwards compatibility.
 */
  def align(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    align(config)
  }
}