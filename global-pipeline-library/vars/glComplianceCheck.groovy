import com.optum.jenkins.pipeline.library.compliance.ComplianceCheck

/**
 * Generates spread sheet with Compliance tabs CM1, CM2 and CM3.
 */

def call(Map<String, Object> config){
    ComplianceCheck complianceCheck = new ComplianceCheck(this)
    complianceCheck.createFinalComplianceReport(config)
}
