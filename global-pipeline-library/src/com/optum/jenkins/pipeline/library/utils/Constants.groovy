#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.utils

// I should probably move this to the vars directory
class Constants {

  // build constants
  static final String JACOCO_MAVEN_PLUGIN_VERSION = '0.8.2'
  static final String MAVEN_VERSION = '3.3.9'
  static final String JAVA_VERSION = '1.8.0'
  static final String JAVA_VERSION_FOR_SONAR = '1.8.0'
  static final String MAVEN_VERSIONS_PLUGIN_VERSION = '2.2'
  static final String OC_VERSION = '3.6.1'

  // Contrast TeamServer constants
  static final String CONTRAST_TEAMSERVER_API_URL = 'https://optum.contrastsecurity.com/Contrast/api'

  // sonar constants
  static final String SONAR_HOST_URL = 'http://sonar.optum.com'
  static final String SONAR_LOGIN = '092c919905283a6d35ff1abe2128fc52e48a6156'
  static final String SONAR_MAVEN_PLUGIN_VERSION = '3.4.1.1168'
  static final String SONAR_MAIN_BRANCH_NAME = 'master'
  static final String SONAR_TOOL_VERSION = 'sonar-scanner-2.8'
  static final String SONAR_DOTNET_TOOL_VERSION = '4.3.1.1372'

  // NodeJS - NPM constants
  static final String NODEJS_VERSION = '7.9.0'
  static final String NPM_AUTH_KEY = 'npmAuthKey'
  static final String NPM_EMAIL = 'noreply@optum.com'

  // Angular Cli Constants
  static final String ANGULAR_CLI_VERSION = '1.7.3'

  // .Net Core constants
  static final String DOTNETCORE_VERSION = '2.1.0'

  // Terraform constants
  static final String TERRAFORM_VERSION = '0.11'

  // GITHUB ApiUrl
  static final String GITHUB_API_URL = 'https://github.optum.com/api/v3'

  // ServiceNow ApiUrl
  static final String SERVICENOW_API_URL = 'https://optumworker.service-now.com/api'

  // evnet topics
  static final String FORTIFY_LOCAL_EVENT_TOPIC = 'devops.fortify.local'
  static final String TEST_EVENT_TOPIC = 'devops.test'
  static final String COMPLIANCE_EVENT_TOPIC = 'devops.compliance'
  static final String COMPLIANCE_CM1CM2_EVENT_TOPIC = 'devops.compliance.cm1cm2'
  static final String APPROVAL_EVENT_TOPIC = 'devops.approval'

  // Rally constants
  static final String RALLY_API_URL = 'https://rally1.rallydev.com'

  // Ldap service constants
  static final String LDAP_SERVICE_API_URL = 'http://service-engine-ldap-automated-compliance-non-prod.ocp-elr-core-nonprod.optum.com'
}

