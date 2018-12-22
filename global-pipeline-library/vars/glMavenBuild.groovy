import com.optum.jenkins.pipeline.library.maven.MavenBuild

/**
 * Runs a Maven build
 * @param mavenGoals String Providing a way to override the default Maven Goals
 * @param mavenOpts String Sets the environment variable MAVEN_OPTS
 * @param mavenProfiles String Comma seperated list of Maven Profiles to pass into the command line.  Defaults to null.  Ex: j2ee1.6,sonar,
 * @param javaVersion String The Java version to use. Defaults to Constants.JAVA_VERSION.
 * @param mavenVersion String The Maven version to use. Defaults to Constants.MAVEN_VERSION
 * @param jacocoMavenPluginVersion String The JaCoCo Maven plugin version. (See http://www.eclemma.org/jacoco/trunk/doc/maven.html)
 * @param mavenVersionPluginVersion String The Maven Versions Plugin Version, allows you to specify a new version number to your artifact
 * @param isDebugMode boolean Indicates if Maven should be run in debug (-X). Defaults to false.
 * @param isBatchMode boolean Indicates if Maven should be run in batch mode (-B). Defaults to true.
 * @param skipTests boolean Indicates if UnitTests should be skipped. Defaults to false.
 * @param surefireReportsPath Allows you to override the default surefireReportsPath
 * @param uploadUnitTestResults Defaults to true.  Allows you to override the default
 * @param uploadJacocoResults Defaults to true.  Allows you to override the default
 * @param runJacocoCoverage boolean Indicates if JaCoCo Code Coverage should be run. Defaults to true.
 * @param ignoreTestFailures boolean Indicates if Test Failures should be ignored. Defaults to true.  The following Sonar Scan will handle test failures.
 * @param settingsXml Allows you to override the settings.xml on the command line so you can use one from your repo
 * @param additionalProps Map An optional map of any additional properties that should be set.
 * @param pomFile Allows you to override the pom.xml on the command line so you can use one from your repo that's different from standard
 * @param newVersion Allows you to give your artifact a unique version number other than the one mentioned in pom.xml
 */

def call(Map<String, Object> config){
  MavenBuild build = new MavenBuild(this)
  build.buildWithMaven(config)
}