#!/usr/bin/env groovy
package unittest.jenkinsfiletests

import unittest.support.JPUnittestSupport
import com.lesfurets.jenkins.unit.BasePipelineTest
import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.GitSource.gitSource
import org.junit.Before
import org.junit.Test

class TestJenkinsfileMavenBuildVersioned extends BasePipelineTest implements JPUnittestSupport {

  @Override
  @Before
  void setUp() throws Exception {
    scriptRoots += 'examples'
    super.setUp()
    def library = library()
      .name('com.optum.jenkins.pipeline.library')
      .retriever(gitSource('https://github.optum.com/jenkins-pipelines/global-pipeline-library.git'))
      .targetPath("retrievedGPLibForTest")
      .defaultVersion("master")
      .allowOverride(true)
      .implicit(false)
      .build()
    helper.registerSharedLibrary(library)
    binding.setVariable('BUILD_URL', 'https://jenkins') // used in jenkinsfile
    binding.setVariable('JOB_NAME', 'jobNameMocked')  // used in jenkinsfile
    registerDeclarativeMethods()
    registerAdditionalMethods()
    setJobVariables()
    addEnvVar('GIT_TAG_PREFIX', 'v')
  }

  @Test
  void executeJFileWithMockedGLMethods() throws Exception {
    registerGlobalLibraryMethods()
    runScript("Jenkinsfile-MavenBuild_Versioned")
    printCallStack()
  }
}
