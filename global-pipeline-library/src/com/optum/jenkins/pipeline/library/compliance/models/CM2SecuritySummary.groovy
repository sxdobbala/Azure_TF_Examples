package com.optum.jenkins.pipeline.library.compliance.models

class CM2SecuritySummary implements Serializable {
  def sourceType
  def testType
  def openCriticalIssues
  def openHighIssues
  def openMediumIssues
  def openLowIssues
  def totalCriticalIssues
  def totalHighIssues
  def totalMediumIssues
  def totalLowIssues
  def recentScanDate
}
