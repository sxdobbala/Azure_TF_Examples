import com.optum.jenkins.pipeline.library.terraform.Terraform

/**
 * Initialize a new or existing Terraform configuration
 *
 * See documentation for all available flags: https://www.terraform.io/docs/commands/init.html
 *
 * @param terraformVersion String Version of Terraform to use
 * @param reconfigure Boolean Disregards any existing configuration, preventing migration of any existing state
 * @param additionalFlags Map An optional map of any additional properties that should be set.
 */

def call(Map<String, Object> config){
  Terraform terraform = new Terraform(this)
  terraform.terraformInit(config)
}
