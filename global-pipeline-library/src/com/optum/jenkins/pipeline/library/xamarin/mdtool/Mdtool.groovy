#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.xamarin.mdtool

class Mdtool implements Serializable {
  def jenkins

  Mdtool() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Mdtool(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Shortcut for running mdtool
 *
 * @params csproj String Required 
 * @params configuration String Optional
 * @params verbose Boolean Optional Defaults to true
 */
  def build(Map<String, Object> params) {
    def defaults = [
      // Required
      csproj       : '',
      //Optional
      configuration: 'Release',
      verbose      : true
    ]
    def config = defaults + params

    jenkins.echo "build arguments: $config"
    jenkins.sh '/Applications/Xamarin\\ Studio.app/Contents/MacOS/mdtool ' +
      config.verbose ? '-v ' : '' +
      'build ' +
      '"--configuration:${config.configuration}" ' +
      config.csproj
  }

/**
 * Wrapper for backwards compatibility.
 */
  def build(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    build(config)
  }
}