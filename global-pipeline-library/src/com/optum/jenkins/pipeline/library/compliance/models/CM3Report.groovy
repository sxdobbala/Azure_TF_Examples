package com.optum.jenkins.pipeline.library.compliance.models

class CM3Report implements Serializable {
  String itApprovers
  String itApproversThreshold
  List<CM3Approver> itApprovals = []

  String businessApprovers
  String businessApproversThreshold
  List<CM3Approver> businessApprovals = []

  List<String> errors = []

  List<CM3Approver> cm3Approvers = []
}
