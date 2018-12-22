#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.xamarin.xbuild
import groovy.transform.Field

class Xbuild implements Serializable {
  def jenkins

  Xbuild() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Xbuild(jenkins) {
    this.jenkins = jenkins
  }

  def xbuildPath = '/Library/Frameworks/Mono.framework/Commands/xbuild '

/**
 * Shortcut for running xbuild clean
 *
 * @params csproj String Required Path to the .csproj file
 * @params configuration String Optional Release or other
 */
  def clean(Map<String, Object> params) {
    def defaults = [
      csproj       : '',
      configuration: 'Release'
    ]
    def config = defaults + params

    jenkins.echo "clean arguments: $config"
    jenkins.sh xbuildPath +
      config.csproj + ' ' +
      '/p:Configuration=' + config.configuration + ' ' +
      '/t:Clean'
  }
/**
 * Shortcut for running xbuild packageForAndroid
 *
 * @params csproj String Required Path to the .csproj file
 * @params configuration String Optional Release or other
 */
  def packageForAndroid(Map<String, Object> params) {
    def defaults = [
      csproj       : '',
      configuration: 'Release'
    ]
    def config = defaults + params

    jenkins.echo "packageForAndroid arguments: $config"
    jenkins.sh xbuildPath +
      config.csproj + ' ' +
      '/p:Configuration=' + config.configuration + ' ' +
      '/t:PackageForAndroid'
  }

/**
 * Wrapper for backwards compatibility.
 */
  def clean(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    clean(config)
  }

/**
 * Wrapper for backwards compatibility.
 */
  def packageForAndroid(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    packageForAndroid(config)
  }
}