require 'spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

describe 'example_network_security_group' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should verify the plan output' do
    hcl = %{
      module "network_security_group" {
        source = "#{__dir__}/../examples/network_security_group"
      }
    }
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(13)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
            "module.network_security_group.module.random_name.random_string.rest_of_string" => {
                "length" => /"7"/,
                "result"=> /<computed>/
            },
            "module.network_security_group.azurerm_network_security_rule.network_security_rule" => {
                "priority" => /"300"/,
                "destination_address_prefix" => /"10.0.3.0\/24"/,
                "protocol" => /"Tcp"/,
                "description" => /"An example network security rule."/,
                "resource_group_name" => "${module.resource_group.name}",
                "direction" => /"Outbound"/,
                "access" => /"Allow"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${module.network_security_group.name}",
                "source_address_prefix" => /"10.0.2.0\/24"/,
                "id"=> /<computed>/,
                "destination_port_range" => /"3306"/,
                "name" => /"ExampleRule"/
            },
            "module.network_security_group.module.random_name.random_string.first_char" => {
                "length" => /"1"/,
                "result"=> /<computed>/
            },
            "module.network_security_group.module.network_security_group.azurerm_network_security_rule.deny_all_inbound" => {
                "priority" => /"4095"/,
                "destination_address_prefix" => "*",
                "protocol" => "*",
                "description" => /"Denies all inbound traffic."/,
                "resource_group_name" => "${var.resource_group_name}",
                "direction" => /"Inbound"/,
                "access" => /"Deny"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${azurerm_network_security_group.network_security_group.name}",
                "source_address_prefix" => "*",
                "id"=> /<computed>/,
                "destination_port_range" => "*",
                "name" => /"DenyAllInboundTraffic"/
            },
            "module.network_security_group.module.network_security_group.azurerm_network_security_rule.allow_inbound_azure_load_balancer" => {
                "priority" => /"1000"/,
                "destination_address_prefix" => "*",
                "protocol" => "*",
                "description" => /"Allow inbound access from Azure Load Balancer."/,
                "resource_group_name" => "${var.resource_group_name}",
                "direction" => /"Inbound"/,
                "access" => /"Allow"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${azurerm_network_security_group.network_security_group.name}",
                "source_address_prefix" => /"AzureLoadBalancer"/,
                "id"=> /<computed>/,
                "destination_port_range" => "*",
                "name" => /"AllowInboundAzureLoadBalancer"/
            },
            "module.network_security_group.module.network_security_group.azurerm_network_security_group.network_security_group" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "security_rule.#"=> /<computed>/,
                "location" => /"centralus"/,
                "tags.%"=> /"1"/,
                "tags.cc-eac_azure_virtual_network"=> /"v2.0.0"/,
                "id"=> /<computed>/
            },
            "module.network_security_group.module.network_security_group.azurerm_network_security_rule.deny_all_outbound" => {
                "priority" => /"4096"/,
                "destination_address_prefix" => "*",
                "protocol" => "*",
                "description" => /"Denies all outbound traffic."/,
                "resource_group_name" => "${var.resource_group_name}",
                "direction" => /"Outbound"/,
                "access" => /"Deny"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${azurerm_network_security_group.network_security_group.name}",
                "source_address_prefix" => "*",
                "id"=> /<computed>/,
                "destination_port_range" => "*",
                "name" => /"DenyAllOutboundTraffic"/
            },
            "module.network_security_group.module.resource_group.azurerm_resource_group.resource_group" => {
                "tags.%" => /"1"/,
                "tags.cc-eac_azure_resource_group" => /"v2.0.0"/,
                "id" => /<computed>/,
                "name" => "${module.namespace.name}",
                "location" => /"centralus"/
            },
            "module.network_security_group.module.network_security_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/              
            },
            "module.network_security_group.module.network_security_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/                
            },
            "module.network_security_group.module.resource_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.network_security_group.module.resource_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.network_security_group.module.network_security_group.module.nsg_diagnostic_log.null_resource.diagnostic_log" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            }                      
        })
  end
end