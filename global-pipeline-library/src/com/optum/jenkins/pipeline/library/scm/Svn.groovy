#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.scm

class Svn implements Serializable {
  def jenkins

  Svn() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Svn(jenkins) {
    this.jenkins = jenkins
  }

/**
 * Wrapper for `checkout scm` for SVN
 */

  def checkout() {
    // checkout source into the node
    jenkins.checkout scm
  }

  def getBranch() {
    return scm.branches[0].name
  }

/**
 * Get the last commit hash.
 */
  def getRevision() {
    jenkins.command 'svn info -r HEAD', true
  }

  def getRevisionShort() {
    jenkins.command 'svn info -r HEAD', true
  }
}