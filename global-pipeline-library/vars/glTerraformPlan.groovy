import com.optum.jenkins.pipeline.library.terraform.Terraform

/**
 * Generate and show an execution plan
 *
 * See documentation for all available flags: https://www.terraform.io/docs/commands/plan.html
 *
 * @param terraformVersion String Version of Terraform to use
 * @param out String The path to save the generated execution plan. This plan can then be used with terraform
 *                   apply to be certain that only the changes shown in this plan are applied.
 * @param additionalFlags Map An optional map of any additional properties that should be set.
 */

def call(Map<String, Object> config){
  Terraform terraform = new Terraform(this)
  terraform.terraformPlan(config)
}
