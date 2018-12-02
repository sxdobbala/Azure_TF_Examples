require_relative './spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

# Verify terraform plan works
describe 'subnet' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should plan resources' do
        hcl = %{
            module "subnet" {
              source = "#{__dir__}/../modules/subnet"
              address_prefix = "10.0.0.0/16"
              resource_group_name = "testrg"
              virtual_network_name = "testvnet"
              network_security_group_name = "network_security_group"
            }
          } 
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(10)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
          "module.subnet.module.network_security_group.module.namespace.random_string.random_string" => {
            "length" => /"12"/,
            "result"=> /<computed>/
          },
          "module.subnet.module.network_security_group.azurerm_network_security_group.network_security_group" => {
            "resource_group_name" => /"testrg"/,
            "name" => "${module.namespace.name}",
            "security_rule.#"=> /<computed>/,
            "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
            "location" => /"centralus"/,
            "tags.%" => /"1"/,
            "id"=> /<computed>/
          },
          "module.subnet.azurerm_subnet.subnet" => {
            "resource_group_name" => /"testrg"/,
            "name" => "${module.namespace.name}",
            "virtual_network_name" => /"testvnet"/,
            "id"=> /<computed>/,
            "address_prefix" => /"10.0.0.0\/16"/,
            "network_security_group_id" => "${module.network_security_group.id}",
            "ip_configurations.#"=> /<computed>/
          },
          "module.subnet.module.network_security_group.azurerm_network_security_rule.allow_inbound_azure_load_balancer" => {
            "priority" => /"1000"/,
            "destination_address_prefix" => "*",
            "protocol" => "*",
            "description" => /"Allow inbound access from Azure Load Balancer."/,
            "resource_group_name" => /"testrg"/,
            "direction" => /"Inbound"/,
            "access" => /"Allow"/,
            "source_port_range" => "*",
            "network_security_group_name" => "${azurerm_network_security_group.network_security_group.name}",
            "source_address_prefix" => /"AzureLoadBalancer"/,
            "id"=> /<computed>/,
            "destination_port_range" => "*",
            "name" => /"AllowInboundAzureLoadBalancer"/
          },
          "module.subnet.module.namespace.random_string.random_string" => {
            "length" => /"12"/,
            "result"=> /<computed>/
          },
          "module.subnet.module.namespace.null_resource.namespace" => {
            "triggers.name" => /"subnet"/,
            "triggers.namespace" => /""/,
            "id"=> /<computed>/,
            "triggers.%" => /"3"/,
            "triggers.format" => "%s-%s"
          },
          "module.subnet.module.network_security_group.module.namespace.null_resource.namespace" => {
            "triggers.name" => /"network_security_group"/,
            "triggers.namespace" => /""/,
            "id"=> /<computed>/,
            "triggers.%" => /"3"/,
            "triggers.format" => "%s-%s"
          },
          "module.subnet.module.network_security_group.azurerm_network_security_rule.deny_all_inbound" => {
            "priority" => /"4095"/,
            "destination_address_prefix" => "*",
            "protocol" => "*",
            "description" => /"Denies all inbound traffic."/,
            "resource_group_name" => /"testrg"/,
            "direction" => /"Inbound"/,
            "access" => /"Deny"/,
            "source_port_range" => "*",
            "network_security_group_name" => "${azurerm_network_security_group.network_security_group.name}",
            "source_address_prefix" => "*",
            "id"=> /<computed>/,
            "destination_port_range" => "*",
            "name" => /"DenyAllInboundTraffic"/
          },
          "module.subnet.module.network_security_group.azurerm_network_security_rule.deny_all_outbound" => {
            "priority" => /"4096"/,
            "destination_address_prefix" => "*",
            "protocol" => "*",
            "description" => /"Denies all outbound traffic."/,
            "resource_group_name" => /"testrg"/,
            "direction" => /"Outbound"/,
            "access" => /"Deny"/,
            "source_port_range" => "*",
            "network_security_group_name" => "${azurerm_network_security_group.network_security_group.name}",
            "source_address_prefix" => "*",
            "id"=> /<computed>/,
            "destination_port_range" => "*",
            "name" => /"DenyAllOutboundTraffic"/
          },
          "module.subnet.module.network_security_group.module.nsg_diagnostic_log.null_resource.diagnostic_log" => {
            "id"=> /<computed>/,
            "triggers.%"=> /<computed>/
          }
      })
  end
end