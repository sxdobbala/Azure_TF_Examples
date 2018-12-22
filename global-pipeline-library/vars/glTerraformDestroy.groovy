import com.optum.jenkins.pipeline.library.terraform.Terraform

/**
 * Destroy Terraform-managed infrastructure
 *
 * See documentation for all available flags: https://www.terraform.io/docs/commands/destroy.html
 *
 * @param terraformVersion String Version of Terraform to use
 * @param force Boolean If -force is set, then the destroy confirmation will not be shown.
 * @param additionalFlags Map An optional map of any additional properties that should be set.
 */

def call(Map<String, Object> config){
  Terraform terraform = new Terraform(this)
  terraform.terraformDestroy(config)
}
