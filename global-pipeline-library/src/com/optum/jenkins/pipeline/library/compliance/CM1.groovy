package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.compliance.models.CM1Report
import com.optum.jenkins.pipeline.library.compliance.models.CM1UserStory
import com.optum.jenkins.pipeline.library.compliance.models.CM1UserStoryAcceptance
import com.optum.jenkins.pipeline.library.compliance.services.LDAP
import com.optum.jenkins.pipeline.library.compliance.services.Rally

// Responsible for getting/making all data required for a CM1 report.
class CM1 implements Serializable {
  Rally rallyService
  LDAP ldapService
  String milestoneFormattedId
  String apiWorkspace
  String userStoryApprovers

  CM1(String apiUrl,
      String apiToken,
      String apiWorkspace,
      String milestoneFormattedId,
      String userStoryApprovers) {
    this.rallyService = new Rally(apiUrl, apiToken)
    this.ldapService = new LDAP()
    this.milestoneFormattedId = milestoneFormattedId
    this.apiWorkspace = apiWorkspace
    this.userStoryApprovers = userStoryApprovers
  }

  CM1Report fetchReport() {
    List<String> errors = []
    List<CM1UserStory> userStories = []
    try {
      def milestoneJsonMap = rallyService.fetchMilestoneMap(
          milestoneFormattedId,
          apiWorkspace)

      userStories = fetchUserStories((String) milestoneJsonMap['ObjectID'], apiWorkspace)

    }
    catch (IOException e) {
      errors.add("Exception occurred: ${e.getMessage()}")
      userStories = []
    }

    // main userstory validation
    errors.addAll(validateAcceptanceStatus(userStories))
    errors.addAll(validateAcceptors(userStories, userStoryApprovers))
    println(userStories)
    return new CM1Report(userStories: userStories, errors: errors)
  }

  private List fetchUserStories(String milestoneObjectId, String apiWorkspace) {
    def userStories = []

    def userStoriesJsonMap = rallyService.fetchUserStoriesMap(
        milestoneObjectId,
        apiWorkspace)

    userStoriesJsonMap.each { userStoryMap ->
      String formattedId = userStoryMap['FormattedID']
      String sourceUrl = 'https://rally1.rallydev.com/#/detail/userstory/' + userStoryMap['ObjectID']
      String name = userStoryMap['Name']
      String description = userStoryMap['Description'] ?: ''
      String acceptanceCriteria = userStoryMap['c_AcceptanceCriteria'] ?: ''
      CM1UserStoryAcceptance accepted = null
      def revisionHistoryJsonMap = rallyService.fetchRevisionHistoryMap(
          Rally.fetchObjectIdFromRef((String) (userStoryMap['RevisionHistory']['_ref'])),
          apiWorkspace)
        for (Object revisionMap : revisionHistoryJsonMap) {
          def revisionDescription = (String) revisionMap['Description'] ?: ''
          if (!revisionDescription.startsWith('SCHEDULE STATE')) {
            continue
          }
        def stateChangeRow = revisionDescription.split('],')[0]
        if (stateChangeRow.endsWith('Accepted') && revisionMap['User']) {
          def userJsonMap = rallyService.fetchUserMap(Rally.fetchObjectIdFromRef((String) revisionMap['User']['_ref']), apiWorkspace)
          String acceptedDate = ((String)revisionMap['CreationDate'])
          accepted = new CM1UserStoryAcceptance(
              timestamp: acceptedDate,
              acceptorName: (String)userJsonMap['DisplayName'],
              acceptorEmail: (String)userJsonMap['EmailAddress'])
        }
        break
      }
      userStories.add(new CM1UserStory(
          formattedId: formattedId,
          sourceUrl: sourceUrl,
          name: name,
          description: Rally.stripHtml(description),
          acceptanceCriteria: Rally.stripHtml(acceptanceCriteria),
          accepted: accepted))
    }
    return userStories
  }

  private List validateAcceptanceStatus(List<CM1UserStory> userStories) {
    def errors = []
    if (!userStories) {
      return errors
    }
    userStories.each { userStory ->
      if (!userStory.accepted) {
        errors.add("${userStory.formattedId} has not been accepted.")
      }
    }
    return errors
  }

  private List validateAcceptors(List<CM1UserStory> userStories, String approvers) {
    def errors = []

    if (!userStories) {
      return errors
    }

    def emails = ldapService.fetchSecureGroupMemberEmails(approvers)
    if (!emails) {
      throw new ComplianceInvalidParameterException('Invalid or missing user story acceptors group!')
    }
    userStories.each { userStory ->
      if (userStory.accepted) {
        CM1UserStoryAcceptance accepted = userStory.accepted
        if (!(accepted.acceptorEmail in emails)) {
          errors.add("${userStory.formattedId} has been accepted by an inappropriate user: ${accepted.acceptorName}.")
        }
      }
    }
    return errors
  }
}
