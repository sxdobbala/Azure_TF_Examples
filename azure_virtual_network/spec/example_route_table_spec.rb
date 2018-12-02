require 'spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

describe 'example_route_table' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should verify the plan output' do
    hcl = %{
      module "route_table" {
        source = "#{__dir__}/../examples/route_table"
      }
    }
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(29)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
            "module.route_table.module.subnet.module.network_security_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.generate_name1.random_string.rest_of_string" => {
                "number" => /"true"/,
                "length" => /"7"/
            },
            "module.route_table.module.route_table.azurerm_route.route[0]" => {
                "resource_group_name" => "${var.resource_group_name}",
                "next_hop_type" => "${element(split(\\\"|\\\", element(local.route_list, count.index)), 2)}",
                "name" => "${element(split(\\\"|\\\", element(local.route_list, count.index)), 0)}",
                "next_hop_in_ip_address" => "${element(split(\\\"|\\\", element(local.route_list, count.index)), 3)}",
                "route_table_name" => "${azurerm_route_table.route_table.name}",
                "address_prefix" => "${element(split(\\\"|\\\", element(local.route_list, count.index)), 1)}",
                "id"=> /<computed>/
            },
            "module.route_table.module.generate_name4.random_string.rest_of_string" => {
                "length" => /"7"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.subnet.module.network_security_group.azurerm_network_security_rule.deny_all_outbound" => {
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
            "module.route_table.module.resource_group.azurerm_resource_group.resource_group" => {
                "tags.%"=> /"1"/,
                "tags.cc-eac_azure_resource_group" => /"v2.0.0"/,
                "id"=> /<computed>/,
                "name" => "${module.namespace.name}",
                "location" => /"centralus"/
            },
            "module.route_table.module.subnet.module.network_security_group.module.namespace.null_resource.namespace" => {
                "triggers.name" => /"network_security_group"/,
                "triggers.namespace" => /""/,
                "id"=> /<computed>/,
                "triggers.%" => /"3"/,
                "triggers.format" => "%s-%s"
            },
            "module.route_table.module.subnet.module.network_security_group.azurerm_network_security_rule.deny_all_inbound" => {
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
            "module.route_table.module.generate_name3.random_string.rest_of_string" => {
                "length" => /"7"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.subnet.module.network_security_group.azurerm_network_security_group.network_security_group" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "security_rule.#"=> /<computed>/,
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "location" => /"centralus"/,
                "tags.%" => /"1"/,
                "id"=> /<computed>/
            },
            "module.route_table.module.generate_name1.random_string.first_char" => {
                "upper" => /"false"/,
                "lower" => /"true"/,
                "min_numeric" => /"0"/,
                "min_lower" => /"0"/,
                "number" => /"false"/,
                "length" => /"1"/,
                "result"=> /<computed>/,
                "min_upper" => /"0"/,
                "id"=> /<computed>/,
                "special" => /"false"/,
                "min_special" => /"0"/
            },
            "module.route_table.module.generate_name2.random_string.rest_of_string" => {
                "upper" => /"false"/,
                "lower" => /"true"/,
                "min_numeric" => /"0"/,
                "min_lower" => /"0"/,
                "number" => /"true"/,
                "length" => /"7"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.virtual_network.azurerm_virtual_network.virtual_network" => {
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
            "module.route_table.module.subnet.module.network_security_group.azurerm_network_security_rule.allow_inbound_azure_load_balancer" => {
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
            "module.route_table.module.generate_name2.random_string.first_char" => {
                "length" => /"1"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.route_table.module.namespace.null_resource.namespace" => {
                "triggers.name" => /"route_table"/,
                "triggers.namespace" => /"example"/,
                "id"=> /<computed>/,
                "triggers.%" => /"3"/,
                "triggers.format" => "%s-%s"
            },
            "module.route_table.module.route_table.azurerm_route_table.route_table" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "route.#"=> /<computed>/,
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "location" => /"centralus"/,
                "tags.%" => /"1"/,
                "id"=> /<computed>/,
                "subnets.#"=> /<computed>/
            },
            "module.route_table.module.route_table.azurerm_route.route[1]" => {
                "resource_group_name" => "${var.resource_group_name}",
                "next_hop_type" => "${element(split(\\\"|\\\", element(local.route_list, count.index)), 2)}",
                "name" => "${element(split(\\\"|\\\", element(local.route_list, count.index)), 0)}",
                "next_hop_in_ip_address" => "${element(split(\\\"|\\\", element(local.route_list, count.index)), 3)}",
                "route_table_name" => "${azurerm_route_table.route_table.name}",
                "address_prefix" => "${element(split(\\\"|\\\", element(local.route_list, count.index)), 1)}",
                "id"=> /<computed>/
            },
            "module.route_table.module.generate_name3.random_string.first_char" => {
                "length" => /"1"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.generate_name4.random_string.first_char" => {
                "length" => /"1"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.subnet.azurerm_subnet.subnet" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "virtual_network_name" => "${var.virtual_network_name}",
                "id"=> /<computed>/,
                "address_prefix" => /"10.0.1.0\/24"/,
                "route_table_id" => "${var.route_table_id}",
                "network_security_group_id" => "${module.network_security_group.id}",
                "ip_configurations.#"=> /<computed>/
            },
            "module.route_table.module.subnet.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.subnet.module.namespace.null_resource.namespace" => {
                "triggers.name" => /"subnet1"/,
                "triggers.namespace" => /""/,
                "id"=> /<computed>/,
                "triggers.%" => /"3"/,
                "triggers.format" => "%s-%s"
            },
            "module.route_table.module.route_table.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.resource_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.route_table.module.resource_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.virtual_network.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.route_table.module.virtual_network.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.route_table.module.subnet.module.network_security_group.module.nsg_diagnostic_log.null_resource.diagnostic_log" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            }       
        })
  end
end