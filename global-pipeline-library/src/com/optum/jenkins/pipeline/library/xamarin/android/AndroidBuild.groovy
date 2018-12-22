#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.xamarin.android

import  com.optum.jenkins.pipeline.library.xamarin.nuget.Nuget
import  com.optum.jenkins.pipeline.library.xamarin.xbuild.Xbuild
import  com.optum.jenkins.pipeline.library.xamarin.zipalign.Zipalign
import  com.optum.jenkins.pipeline.library.xamarin.jarsigner.Jarsigner
import  com.optum.jenkins.pipeline.library.xamarin.android.NameUtils

class AndroidBuild implements Serializable {
  def jenkins

  AndroidBuild() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  AndroidBuild(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Builds Android part of Xamarin project.
 * Stashes all the apks in 'All_APKS'
 *
 * @params appName String Required Short name of application. Use underscores instead of spoces
 * @params keyCredentials String Required Credentials for the jarsigner key
 * @params storeCredentials String Required Credentials for the jarsigner storepass key
 * @params buildEnv String Optional STAGE_ENV or PROD_ENV. Defaults to STAGE_ENV
 * @params endApk String Optional Name of the apk artifact
 * @params inputApk String Optional Path to input apk
 * @params keystore String  Name of the keystore with which to sign the APK
 * @params signedApk String Optional Path to signed apk
 * @params rootDir String Optional Prefix to all paths. If defined, should have trailing '/'
 */
  def buildApk(Map<String, Object> params) {
    def defaults = [
      appName         : 'TEST_PROJECT',
      buildEnv        : 'STAGE_ENV',
      endApk          : '',
      inputApk        : '',
      keystore        : '',
      signedApk       : '',
      rootDir         : '',
      keyCredentials  : '',
      storeCredentials: ''
    ]
    def config = defaults + params

    jenkins.echo "buildApk arguments: $config"
    // TODO: validate config vars

    Nuget nuget = new Nuget(jenkins)
    Xbuild xbuild = new Xbuild(jenkins)
    Jarsigner jarsigner = new Jarsigner(jenkins)
    Zipalign zipalign = new Zipalign(jenkins)
    NameUtils utils = new NameUtils(jenkins)

    def projectRoot = "$config.rootDir$config.appName"

    // Swap the environment variable in the code to the current buildEnv
    utils.setEnvironment({
      def appSetupPath = "$projectRoot/$config.appName"
      def buildEnv = "$config.buildEnv"
    })

    nuget.restore({
      def sln = "$projectRoot/${config.appName}.sln"
    })

    xbuild.clean({
      def csproj = "$projectRoot/Droid/${config.appName}.Droid.csproj"
    })

    cleanApks({
      def appName = "$config.appName"
    })

    xbuild.packageForAndroid({
      def csproj = "$projectRoot/Droid/${config.appName}.Droid.csproj"
    })

    def apkNames = utils.makeDefaultApkNames({
      def appName = "$config.appName"
      def endApk = "$config.endApk"
      def inputApk = "$config.inputApk"
      def signedApk = "$config.signedApk"
      def rootDir = "$config.rootDir"
    })

    jarsigner.sign({
      def keyCredentials = "$config.keyCredentials"
      def storeCredentials = "$config.storeCredentials"
      def signedjar = "$apkNames.signedApk"
      def inputApk = "$apkNames.inputApk"
      def alias = "$config.keystore"
    })

    zipalign.align({
      def inFile = "$apkNames.signedApk"
      def outFile = "$apkNames.endApk"
    })

    jenkins.stash includes: '*.apk', name: 'All_APKS', useDefaultExcludes: false
  }

/**
 * Remove existing .apks in appName/folderPath
 */
  private def cleanApks(Map<String, Object> params) {
    def defaults = [
      appName   : '',
      folderPath: '/Droid/bin/Release'
    ]
    def config = defaults + params

    if (config.appName != '') {
      jenkins.sh "rm -rf ./$config.appName$config.folderPath/*"
    }
  }
}
