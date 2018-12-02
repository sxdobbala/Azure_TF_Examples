require_relative './spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

# Verify terraform plan works
describe 'network_security_group' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should plan resources' do
        hcl = %{
            module "network_security_group" {
              source = "#{__dir__}/../modules/network_security_group"
              name = "network_security_group"
              resource_group_name = "test_rg"
            }
          } 
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(7)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
          "module.network_security_group.azurerm_network_security_rule.allow_inbound_azure_load_balancer" => {
              "priority" => /"1000"/,
              "destination_address_prefix" => "*",
              "protocol" => "*",
              "description" => /"Allow inbound access from Azure Load Balancer."/,
              "resource_group_name" => /"test_rg"/,
              "direction" => /"Inbound"/,
              "access" => /"Allow"/,
              "source_port_range" => "*",
              "network_security_group_name" => "${azurerm_network_security_group.network_security_group.name}",
              "source_address_prefix" => /"AzureLoadBalancer"/,
              "id"=> /<computed>/,
              "destination_port_range" => "*",
              "name" => /"AllowInboundAzureLoadBalancer"/
          },
          "module.network_security_group.azurerm_network_security_rule.deny_all_inbound" => {
              "priority" => /"4095"/,
              "destination_address_prefix" => "*",
              "protocol" => "*",
              "description" => /"Denies all inbound traffic."/,
              "resource_group_name" => /"test_rg"/,
              "direction" => /"Inbound"/,
              "access" => /"Deny"/,
              "source_port_range" => "*",
              "network_security_group_name" => "${azurerm_network_security_group.network_security_group.name}",
              "source_address_prefix" => "*",
              "id"=> /<computed>/,
              "destination_port_range" => "*",
              "name" => /"DenyAllInboundTraffic"/
          },
          "module.network_security_group.azurerm_network_security_rule.deny_all_outbound" => {
              "priority" => /"4096"/,
              "destination_address_prefix" => "*",
              "protocol" => "*",
              "description" => /"Denies all outbound traffic."/,
              "resource_group_name" => /"test_rg"/,
              "direction" => /"Outbound"/,
              "access" => /"Deny"/,
              "source_port_range" => "*",
              "network_security_group_name" => "${azurerm_network_security_group.network_security_group.name}",
              "source_address_prefix" => "*",
              "id"=> /<computed>/,
              "destination_port_range" => "*",
              "name" => /"DenyAllOutboundTraffic"/
          },
          "module.network_security_group.azurerm_network_security_group.network_security_group" => {
              "resource_group_name" => /"test_rg"/,
              "name" => "${module.namespace.name}",
              "security_rule.#"=> /<computed>/,
              "location" => /"centralus"/,
              "tags.%"=> /"1"/,
              "tags.cc-eac_azure_virtual_network"=> /"v2.0.0"/,
              "id"=> /<computed>/
          },
          "module.network_security_group.module.namespace.null_resource.namespace" => {
            "id"=> /<computed>/,
            "triggers.%"=> "3",
            "triggers.format"=> "%s-%s",
            "triggers.name"=> "network_security_group",
            "triggers.namespace"=> ""             
          },
          "module.network_security_group.module.namespace.random_string.random_string" => {
            "length" => /"12"/,
            "result"=> /<computed>/                
          },
          "module.network_security_group.module.nsg_diagnostic_log.null_resource.diagnostic_log" => {
            "id"=> /<computed>/,
            "triggers.%"=> /<computed>/
          }
        })
  end
end