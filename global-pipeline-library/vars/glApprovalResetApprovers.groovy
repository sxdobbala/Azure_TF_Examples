import com.optum.jenkins.pipeline.library.approval.Approval

/**
 * approvalReset Resets the APPROVERS environment variable. For example, it could be used in a flow where an approver approves stage
 * and the same approver is authorized to approve production
 */

def call(Map<String, Object> config){
  Approval approval = new Approval(this)
  approval.approvalReset(config)
}
