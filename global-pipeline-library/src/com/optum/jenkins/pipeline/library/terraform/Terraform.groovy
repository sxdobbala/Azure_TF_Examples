#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.terraform

import com.optum.jenkins.pipeline.library.event.TerraformEvent
import com.optum.jenkins.pipeline.library.utils.Constants
import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.utils.Utils

class Terraform implements Serializable {
  def jenkins

  Terraform() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Terraform(jenkins) {
    this.jenkins = jenkins
  }

  /**
   * Initialize a new or existing Terraform configuration
   *
   * See documentation for all available flags: https://www.terraform.io/docs/commands/init.html
   *
   * @param terraformVersion String Version of Terraform to use
   * @param reconfigure Boolean Disregards any existing configuration, preventing migration of any existing state
   * @param additionalFlags Map An optional map of any additional properties that should be set.
   */
  def terraformInit(Map<String, Object> params) {
    def defaults = [
      terraformVersion : Constants.TERRAFORM_VERSION,
      reconfigure      : false, // optional
      additionalFlags  : null
    ]
    def config = defaults + params
    jenkins.echo "terraformInit arguments: $config"
    def flags = ""
    if (config.additionalFlags) {
      for (def entry : config.additionalFlags) {
        flags += "-$entry.key=\"$entry.value\" "
      }
    }
    flags += (config.reconfigure ? "-reconfigure" : "")
    def initCmd = "terraform init $flags"

    jenkins.withEnv(["TERRAFORM_VERSION=${config.terraformVersion}"]) {
      jenkins.command(initCmd)
    }
  }

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
  def terraformPlan(Map<String, Object> params) {
    def defaults = [
      terraformVersion : Constants.TERRAFORM_VERSION,
      additionalFlags  : null
    ]
    def config = defaults + params
    jenkins.echo "terraformPlan arguments: $config"
    def flags = ""
    if (config.additionalFlags) {
      for (def entry : config.additionalFlags) {
        flags += "-$entry.key=\"$entry.value\" "
      }
    }
    def planCmd = "terraform plan $flags"

    jenkins.withEnv(["TERRAFORM_VERSION=${config.terraformVersion}"]) {
      jenkins.command(planCmd)
    }
  }

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
  def terraformApply(Map<String, Object> params) {
    def defaults = [
      terraformVersion : Constants.TERRAFORM_VERSION,
      autoApprove      : true, // optional
      additionalFlags  : null,
      environment      : "", // optional
      cloudProvider    : "" // optional
    ]
    def config = defaults + params
    def startTime = new Date()
    jenkins.echo "terraformApply arguments: $config"
    def flags = ""
    if (config.additionalFlags) {
      for (def entry : config.additionalFlags) {
        flags += "-$entry.key=\"$entry.value\" "
      }
    }
    flags += (config.autoApprove ? "-auto-approve" : "")
    def applyCmd = "terraform apply $flags"

    try {
      jenkins.withEnv(["TERRAFORM_VERSION=${config.terraformVersion}"]) {
        jenkins.command(applyCmd)
      }
    } catch (Exception e) {
      sendCloudEvent(jenkins, startTime, config.environment, config.cloudProvider, EventStatus.FAILURE)
      jenkins.error "Problem running terraform apply: " + e.getMessage()
    }
    sendCloudEvent(jenkins, startTime, config.environment, config.cloudProvider, EventStatus.SUCCESS)
  }

  /**
   * Destroy Terraform-managed infrastructure
   *
   * See documentation for all available flags: https://www.terraform.io/docs/commands/destroy.html
   *
   * @param terraformVersion String Version of Terraform to use
   * @param force Boolean If -force is set, then the destroy confirmation will not be shown.
   * @param additionalFlags Map An optional map of any additional properties that should be set.
   */
  def terraformDestroy(Map<String, Object> params) {
    def defaults = [
      terraformVersion : Constants.TERRAFORM_VERSION,
      force            : true, // optional
      additionalFlags  : null
    ]
    def config = defaults + params
    jenkins.echo "terraformDestroy arguments: $config"
    def flags = ""
    if (config.additionalFlags) {
      for (def entry : config.additionalFlags) {
        flags += "-$entry.key=\"$entry.value\" "
      }
    }
    flags += (config.force ? "-force" : "")
    def destroyCmd = "terraform destroy $flags"

    jenkins.withEnv(["TERRAFORM_VERSION=${config.terraformVersion}"]) {
      jenkins.command(destroyCmd)
    }
  }

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
  def cloudConfig(Map<String, Object> params) {
    def defaults = [
        credentialsId   : null, // required for aws; should point to id of Jenkins credentials
        cloudProvider   : null, // required; expects 'aws' or 'azure'
        region          : null, // required for aws
    ]
    def config = defaults + params
    jenkins.echo "cloudConfig arguments: $config"

    if (!config.cloudProvider) {
      jenkins.error "Cloud provider flag is required"
    }

    if (config.cloudProvider.equals("azure")) {
      jenkins.command("az login")
    } else if (config.cloudProvider.equals("aws")) {
      if (!config.credentialsId) {
        jenkins.error "Credentials id required"
      }
      if (!config.region) {
        jenkins.error "Region required"
      }
      jenkins.echo "Setting AWS environment variables..."
      jenkins.withCredentials([[$class           : 'UsernamePasswordMultiBinding', credentialsId: config.credentialsId,
                                usernameVariable : 'CLOUD_USER', passwordVariable: 'CLOUD_PASS']]) {
        jenkins.command (
            """
              aws configure set aws_access_key_id ${jenkins.env.CLOUD_USER}
              aws configure set aws_secret_access_key ${jenkins.env.CLOUD_PASS}
              aws configure set default.region $config.region
            """
        )
      }
    }
  }

  def sendCloudEvent(jenkins, Date start, String env, String provider, EventStatus status) {
    new TerraformEvent(jenkins, [status: status, environment: env, cloudProvider: provider, duration: new Utils(jenkins).getDuration(start)]).send()
  }
}
