package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.compliance.models.AgileReleaseScope
import com.optum.jenkins.pipeline.library.compliance.services.Rally

import java.text.DateFormat
import java.text.SimpleDateFormat

class ReleaseScope implements Serializable {
  Rally rallyService
  String milestoneFormattedId
  String apiWorkspace

  ReleaseScope(String apiUrl,
               String apiToken,
               String apiWorkspace,
               String milestoneFormattedId) {
    this.rallyService = new Rally(apiUrl, apiToken)
    this.milestoneFormattedId = milestoneFormattedId
    this.apiWorkspace = apiWorkspace
  }

  AgileReleaseScope fetchReleaseScope() {
    def milestoneJsonMap = rallyService.fetchMilestoneMap(milestoneFormattedId, apiWorkspace)
    String deploymentDate = ((String)milestoneJsonMap['TargetDate'])
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    df.setTimeZone(TimeZone.getTimeZone('UTC'))
    return new AgileReleaseScope(
      timestamp: df.format(new Date()),
      id: milestoneJsonMap['FormattedID'],
      sourceUrl: milestoneJsonMap['_ref'],
      name: milestoneJsonMap['Name'],
      description: milestoneJsonMap['Description'],
      deploymentDate: deploymentDate,
      deploymentId: milestoneJsonMap['c_ProductionDeploymentID']
    )
  }
}
