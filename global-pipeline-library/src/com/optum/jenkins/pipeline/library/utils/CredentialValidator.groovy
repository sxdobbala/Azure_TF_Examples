package com.optum.jenkins.pipeline.library.utils

import org.jenkinsci.plugins.credentialsbinding.impl.CredentialNotFoundException

class CredentialValidator {
  /** This method validates the credentialId of a given type for existence.
    @param jenkins object
    @param credentialsId, jenkins credential Id
    @param type, credential type
   */
  static def validate(Object jenkins, String credentialsId, String type) {
      switch (type) {
        case 'UsernamePassword':
          try {
            jenkins.withCredentials([jenkins.usernamePassword(credentialsId: credentialsId, usernameVariable: 'username', passwordVariable: 'password')]) {
              jenkins.echo "Username/Password credential: '${credentialsId}' exists"
            }
          } catch (CredentialNotFoundException cnfe) {
            jenkins.error "ERROR: Unable to find the correctly configured Username/Password credentials in Jenkins '${credentialsId}'."
          } catch (Exception e) {
            jenkins.echo e.getMessage()
            jenkins.error "ERROR: Unexpected exception when validating the credential in Jenkins '${credentialsId}' of type '${type}'."
          }
          break
        case 'SecretText':
          try {
            jenkins.withCredentials([jenkins.string(credentialsId: credentialsId, variable: 'SecretText')]) {
              jenkins.echo "Secret Text credential: '${credentialsId}' exists"
            }
          } catch (CredentialNotFoundException cnfe) {
            jenkins.error "ERROR: Unable to find the correctly configured SecretText credentials in Jenkins '${credentialsId}'."
          } catch (Exception e) {
            jenkins.echo e.getMessage()
            jenkins.error "ERROR: Unexpected exception when validating the credential in Jenkins '${credentialsId}' of type '${type}'."
          }
          break
        case 'SecretFile':
          try {
            jenkins.withCredentials([jenkins.file(credentialsId: credentialsId, variable: 'FILE')]) {
              jenkins.echo "Secret File credential: '${credentialsId}' exists"
            }
          } catch (CredentialNotFoundException cnfe) {
            jenkins.error "ERROR: Unable to find the correctly configured SecretFile credentials in Jenkins '${credentialsId}'."
          } catch (Exception e) {
            jenkins.echo e.getMessage()
            jenkins.error "ERROR: Unexpected exception when validating the credential in Jenkins '${credentialsId}' of type '${type}'."
          }
          break
      }

  }
}
