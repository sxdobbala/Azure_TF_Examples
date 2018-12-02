require 'spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

describe 'example_virtual_network' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should verify the plan output' do
    hcl = %{
      module "virtual_network" {
        source = "#{__dir__}/../examples/virtual_network"
      }
    }
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(19)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
            "module.virtual_network.module.subnet.module.network_security_group.azurerm_network_security_rule.deny_all_inbound" => {
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
            "module.virtual_network.module.subnet.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.virtual_network.module.random_name.random_string.rest_of_string" => {
                "length" => /"7"/,
                "result"=> /<computed>/
            },
            "module.virtual_network.module.subnet.module.network_security_group.azurerm_network_security_rule.allow_inbound_azure_load_balancer" => {
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
            "module.virtual_network.module.resource_group.azurerm_resource_group.resource_group" => {
                "tags.%"=> /"1"/,
                "tags.cc-eac_azure_resource_group" => /"v2.0.0"/,
                "id"=> /<computed>/,
                "name" => "${module.namespace.name}",
                "location" => /"centralus"/
            },
            "module.virtual_network.module.random_name.random_string.first_char" => {
                "length" => /"1"/,
                "result"=> /<computed>/
            },
            "module.virtual_network.module.subnet.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.virtual_network.module.subnet.module.network_security_group.azurerm_network_security_group.network_security_group" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "security_rule.#"=> /<computed>/,
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "location" => /"centralus"/,
                "tags.%" => /"1"/,
                "id"=> /<computed>/
            },
            "module.virtual_network.module.subnet.module.network_security_group.azurerm_network_security_rule.deny_all_outbound" => {
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
            "module.virtual_network.module.virtual_network.azurerm_virtual_network.virtual_network" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "address_space.0" => /"10.0.0.0\/16"/,
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "location" => /"centralus"/,
                "tags.%" => /"1"/,
                "subnet.#"=> /<computed>/,
                "id"=> /<computed>/,
                "address_space.#" => /"1"/
            },
            "module.virtual_network.module.subnet.module.network_security_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.virtual_network.module.subnet.module.network_security_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.virtual_network.module.subnet.azurerm_subnet.subnet" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "virtual_network_name" => "${var.virtual_network_name}",
                "id"=> /<computed>/,
                "address_prefix" => /"10.0.1.0\/24"/,
                "network_security_group_id" => "${module.network_security_group.id}",
                "ip_configurations.#"=> /<computed>/
            },
            "module.virtual_network.azurerm_network_security_rule.network_security_rule" => {
                "priority" => /"200"/,
                "destination_address_prefix" => /"10.0.1.0\/24"/,
                "protocol" => /"Tcp"/,
                "description" => /"An example network security rule."/,
                "resource_group_name" => "${module.resource_group.name}",
                "direction" => /"Inbound"/,
                "access" => /"Allow"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${module.subnet.network_security_group_name}",
                "source_address_prefix" => /"Internet"/,
                "id"=> /<computed>/,
                "destination_port_range" => /"443"/,
                "name" => /"ExampleRule"/
            },
            "module.virtual_network.module.resource_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.virtual_network.module.resource_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.virtual_network.module.virtual_network.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.virtual_network.module.virtual_network.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.virtual_network.module.subnet.module.network_security_group.module.nsg_diagnostic_log.null_resource.diagnostic_log" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            }              
        })
  end
end