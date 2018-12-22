package unittest.support

trait JPUnittestSupport {
  /**
   * Allow mocking of declarative pipeline keywords
   */
  void registerDeclarativeMethods() {
    helper.registerAllowedMethod("pipeline", [Closure.class], null)
    helper.registerAllowedMethod("agent", [Closure.class], null)
    helper.registerAllowedMethod('label', [String.class], null)
    helper.registerAllowedMethod('stages', [Closure.class], null)
    helper.registerAllowedMethod('steps', [Closure.class], null)
    helper.registerAllowedMethod("script", [Closure.class], null)
    helper.registerAllowedMethod("when", [Closure.class], null)
    helper.registerAllowedMethod("not", [Closure.class], null)
    helper.registerAllowedMethod("tag", [Map.class], null)
    helper.registerAllowedMethod("emailext", [Map.class], null)
    helper.registerAllowedMethod("buildingTag", [], null)
    helper.registerAllowedMethod('environment', [Closure.class], { Closure c ->
      def envBefore = [env: binding.getVariable('env')]
      println "Env section - original env vars: ${envBefore.toString()}"
      c.resolveStrategy = Closure.DELEGATE_FIRST
      c.delegate = envBefore
      c()

      def envNew = envBefore.env
      envBefore.each { k, v ->
        if (k != 'env') {
          envNew["$k"] = v
        }

      }
      println "Env section - env vars set to: ${envNew.toString()}"
      binding.setVariable('env', envNew)
    })

    helper.registerAllowedMethod('post', [Closure.class], null)

    /**
     * Handling the post sections
     */
    def postResultEmulator = { String section, Closure c ->

      def currentBuild = binding.getVariable('currentBuild')

      switch (section) {
        case 'always':
        case 'changed': // How to handle changed? It may happen so just run it..
          return c.call()
          break
        case 'success':
          if(currentBuild.result == 'SUCCESS') { return c.call() }
          else { println "post ${section} skipped as not SUCCESS"; return null}
          break
        case 'unstable':
          if(currentBuild.result == 'UNSTABLE') { return c.call() }
          else { println "post ${section} skipped as SUCCESS"; return null}
          break
        case 'failure':
          if(currentBuild.result == 'FAILURE') { return c.call() }
          else { println "post ${section} skipped as not FAILURE"; return null}
          break
        case 'aborted':
          if(currentBuild.result == 'ABORTED') { return c.call() }
          else { println "post ${section} skipped as not ABORTED"; return null}
          break
        default:
          assert false, "post section ${section} is not recognised. Check pipeline syntax."
          break
      }
    }
    helper.registerAllowedMethod('always', [Closure.class], postResultEmulator.curry('always'))
    helper.registerAllowedMethod('changed', [Closure.class], postResultEmulator.curry('changed'))
    helper.registerAllowedMethod('success', [Closure.class], postResultEmulator.curry('success'))
    helper.registerAllowedMethod('unstable', [Closure.class], postResultEmulator.curry('unstable'))
    helper.registerAllowedMethod('failure', [Closure.class], postResultEmulator.curry('failure'))
  }

  /**
   * Allow mocking of global functions
   */
  void registerGlobalLibraryMethods() {
    helper.registerAllowedMethod("command", [Object.class], null)
    helper.registerAllowedMethod("contrastVerification", [Object.class], null) // Req'd for example to demonstrate glContrastPullMetrics
    helper.registerAllowedMethod("glAngularBuild", [Object.class], null)
    helper.registerAllowedMethod("glAngularTest", [Object.class], null)
    helper.registerAllowedMethod("glArachniScan", [Object.class], null)
    helper.registerAllowedMethod("glContrastPullMetrics", [Object.class], null)
    helper.registerAllowedMethod("glDockerImageBuild", [Object.class], null)
    helper.registerAllowedMethod("glDockerImageBuildPush", [Object.class], null)
    helper.registerAllowedMethod("glDockerImagePush", [Object.class], null)
    helper.registerAllowedMethod("glDockerImageTag", [Object.class], null)
    helper.registerAllowedMethod("glDockerRepoCreate", [Object.class], null)
    helper.registerAllowedMethod("glDockerTagDelete", [Object.class], null)
    helper.registerAllowedMethod("glDotNetCoreBuild", [Object.class], null)
    helper.registerAllowedMethod("glDotNetCorePublish", [Object.class], null)
    helper.registerAllowedMethod("glDotNetCoreTest", [Object.class], null)
    helper.registerAllowedMethod("glFortifyScan", [Object.class], null)
    helper.registerAllowedMethod("glGitCheckout", [Object.class], null)
    helper.registerAllowedMethod("glGithubStatus", [Object.class], null)
    helper.registerAllowedMethod("glMavenArtifactoryDeploy", [Object.class], null)
    helper.registerAllowedMethod("glMavenBuild", [Object.class], null)
    helper.registerAllowedMethod("glSonarMavenScan", [Object.class], null)
    helper.registerAllowedMethod("glSonarNpmScan", [Object.class], null)
    helper.registerAllowedMethod("glSonarScan", [Object.class], null)
    helper.registerAllowedMethod("glSonarScanWithPropertiesFile", [Object.class], null)
    helper.registerAllowedMethod("glSvnCheckout", [Object.class], null)
    helper.registerAllowedMethod("glVersionsBump", [Object.class], null)
    helper.registerAllowedMethod("glVersionsGetLatestSemanticVersionFromTag", [Object.class], null)
    helper.registerAllowedMethod("glXamarinAndroidBuildApk", [Object.class], null)
    helper.registerAllowedMethod("glXamarinAndroidNameUtilsMakeApkNames", [Object.class], null)
    helper.registerAllowedMethod("glXamarinAndroidNameUtilsSetEnv", [Object.class], null)
    helper.registerAllowedMethod("glXamarinJarsign", [Object.class], null)
    helper.registerAllowedMethod("glXamarinMdtoolBuild", [Object.class], null)
    helper.registerAllowedMethod("glXamarinNugetRestore", [Object.class], null)
    helper.registerAllowedMethod("glXamarinXbuildClean", [Object.class], null)
    helper.registerAllowedMethod("glXamarinXbuildPackageForAndroid", [Object.class], null)
    helper.registerAllowedMethod("glXamarinZipalign", [Object.class], null)
  }

  /**
   * Set default job variables/environment variables
   */
  void setJobVariables() {
    binding.setVariable('currentBuild', new Expando(result: 'SUCCESS', displayName: 'Build #1234'))

     // Initialize envvars - these can be overridden in setup/specific tests
    addEnvVar('BUILD_NUMBER', '1234')
    addEnvVar('PATH', '/some/path')
    addEnvVar('BUILD_URL', 'https://jenkins')
    addEnvVar('JOB_NAME', 'someJobName')
    addEnvVar('JOB_URL', 'someJobURL')
  }

  /**
   * Helper for adding an environment variable
   */
  void addEnvVar(String name, String val) {
    if (!binding.hasVariable('env')) {
      binding.setVariable('env', new Expando(getProperty: { p -> this[p] }, setProperty: { p, v -> this[p] = v }))
    }
    def env = binding.getVariable('env') as Expando
    env[name] = val
  }


  /**
   * Allow mocking of methods not currently found in JenkinsPipelineUnit, or override them
   */
  void registerAdditionalMethods() {
    helper.registerAllowedMethod('timeout', [Integer.class, Closure.class], null)
    helper.registerAllowedMethod('waitUntil', [Closure.class], null)
    helper.registerAllowedMethod('writeFile', [Map.class], null)
    helper.registerAllowedMethod('build', [Map.class], null)
    helper.registerAllowedMethod('tool', [String.class], { 'toolpathMocked' })
    helper.registerAllowedMethod('withCredentials', [Map.class, Closure.class], null)
    helper.registerAllowedMethod('withCredentials', [List.class, Closure.class], null)
    helper.registerAllowedMethod('usernamePassword', [Map.class], { creds -> return creds })
    helper.registerAllowedMethod('deleteDir', [], null)
    helper.registerAllowedMethod('pwd', [], { 'workspaceDirMocked' })
    helper.registerAllowedMethod('stash', [Map.class], null)
    helper.registerAllowedMethod('unstash', [Map.class], null)
    helper.registerAllowedMethod('checkout', [Closure.class], null)
    helper.registerAllowedMethod('junit', [Map.class], null)
    helper.registerAllowedMethod('isUnix', [], { true })
    helper.registerAllowedMethod('bat', [String.class], null)
    helper.registerAllowedMethod('readMavenPom', [Map.class], {model -> return model})
    helper.registerAllowedMethod("sh", [Map.class], { "mockedSh" })
    helper.registerAllowedMethod('fileExists', [String.class], null)
    helper.registerAllowedMethod('withSonarQubeEnv', [String.class, Closure.class], null)
    helper.registerAllowedMethod('archiveArtifacts', [String.class], null)
    helper.registerAllowedMethod('withEnv', [List.class, Closure.class], { List list, Closure c ->
      list.each {
        //def env = helper.get
        def item = it.split('=')
        assert item.size() == 2, "withEnv list does not look right: ${list.toString()}"
        addEnvVar(item[0], item[1])
        c.delegate = binding
        c.call()
      }
    })

  }
}
