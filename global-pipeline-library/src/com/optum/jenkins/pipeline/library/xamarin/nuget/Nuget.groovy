#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.xamarin.nuget

class Nuget implements Serializable {
  def jenkins

  Nuget() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Nuget(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Shortcut for running xbuild
 *
 * @params sln String Required Path to the .sln file
 */
  def restore(Map<String, Object> params) {
    def defaults = [
      sln: ''
    ]
    def config = defaults + params

    jenkins.echo "restore: $config"
    jenkins.sh '/Library/Frameworks/Mono.framework/Versions/Current/Commands/nuget restore ' +
      config.sln
  }
}
