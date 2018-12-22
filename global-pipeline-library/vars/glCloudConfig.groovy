import com.optum.jenkins.pipeline.library.terraform.Terraform

/**
 * Cloud agnostic config, currently supports AWS and Azure. AWS requires the access key id, secret, and the region
 * to be set, while Azure uses two-factor authentication, so does not require any parameters to be passed at login
 *
 * Does not require a logout
 *
 * @param credentialsId String Your cloud provider credentials stored in Jenkins
 * @param cloudProvider String Which cloud provider you want to login and deploy infrastructure to
 * @param region String Region Provides details about a specific AWS/Azure region
 */

def call(Map<String, Object> config){
  Terraform terraform = new Terraform(this)
  terraform.cloudConfig(config)
}
