import com.optum.jenkins.pipeline.library.terraform.Terraform

/**
 * Provision or changes infrastructure
 *
 * See documentation for all available flags: https://www.terraform.io/docs/commands/apply.html
 *
 * @param terraformVersion String Version of Terraform to use
 * @param autoApprove Boolean Skip interactive approval of plan before applying.
 * @param additionalFlags Map An optional map of any additional properties that should be set.
 * @param environment String Where infrastructure you are deploying to (i.e. dev, test, stage, prod); Used for
 *          Devops event metadata
 * @param cloudProvider String Which cloud provider you are deploying your infrastructure to (i.e. azure, aws);
 *          Used for Devops event metadata
 */

def call(Map<String, Object> config){
  Terraform terraform = new Terraform(this)
  terraform.terraformApply(config)
}
