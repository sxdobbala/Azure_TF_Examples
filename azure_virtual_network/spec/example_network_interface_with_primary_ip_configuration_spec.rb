require 'spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

describe 'example_network_interface_with_primary_ip_configuration' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should verify the plan output' do
    hcl = %{
      module "network_interface_with_primary_ip_configuration" {
        source = "#{__dir__}/../examples/network_interface_with_primary_ip_configuration"
      }
    }
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(26)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
            "module.network_interface_with_primary_ip_configuration.module.subnet.azurerm_subnet.subnet" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "virtual_network_name" => "${var.virtual_network_name}",
                "id"=> /<computed>/,
                "address_prefix" => /"10.0.1.0\/24"/,
                "network_security_group_id" => "${module.network_security_group.id}",
                "ip_configurations.#"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.subnet.module.network_security_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.network_interface.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.public_ip.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.network_interface.module.random_dns_label_postfix.random_string.rest_of_string" => {
                "length" => /"7"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.random_name.random_string.rest_of_string" => { 
                "length" => /"7"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.resource_group.azurerm_resource_group.resource_group" => {
                "tags.%"=> /"1"/,
                "tags.cc-eac_azure_resource_group" => /"v2.0.0"/,
                "id"=> /<computed>/,
                "name" => "${module.namespace.name}",
                "location" => /"centralus"/
            },
            "module.network_interface_with_primary_ip_configuration.module.public_ip.azurerm_public_ip.public_ip" => {
                "sku" => /"Basic"/,
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "fqdn"=> /<computed>/,
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "domain_name_label" => "${var.domain_name_label}",
                "location" => /"centralus"/,
                "tags.%" => /"1"/,
                "idle_timeout_in_minutes" => /"4"/,
                "ip_address"=> /<computed>/,
                "id"=> /<computed>/,
                "public_ip_address_allocation" => /"dynamic"/
            },
            "module.network_interface_with_primary_ip_configuration.module.subnet.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.subnet.module.network_security_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.network_interface.module.namespace.null_resource.namespace" => {
                "triggers.name" => /"network_interface"/,
                "triggers.namespace" => /""/,
                "id"=> /<computed>/,
                "triggers.%" => /"3"/,
                "triggers.format" => "%s-%s"
            },
            "module.network_interface_with_primary_ip_configuration.module.public_ip.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
                "module.network_interface_with_primary_ip_configuration.module.subnet.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.subnet.module.network_security_group.azurerm_network_security_rule.deny_all_inbound" => {
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
            "module.network_interface_with_primary_ip_configuration.module.subnet.module.network_security_group.azurerm_network_security_group.network_security_group" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "security_rule.#"=> /<computed>/,
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "location" => /"centralus"/,
                "tags.%" => /"1"/,
                "id"=> /<computed>/
                },
            "module.network_interface_with_primary_ip_configuration.module.subnet.module.network_security_group.azurerm_network_security_rule.deny_all_outbound" => {
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
            "module.network_interface_with_primary_ip_configuration.module.virtual_network.azurerm_virtual_network.virtual_network" => {
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
            "module.network_interface_with_primary_ip_configuration.module.subnet.module.network_security_group.azurerm_network_security_rule.allow_inbound_azure_load_balancer" => {
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
            "module.network_interface_with_primary_ip_configuration.module.random_name.random_string.first_char" => {
                "length" => /"1"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.network_interface.module.random_dns_label_postfix.random_string.first_char" => {
                "length" => /"1"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.network_interface.azurerm_network_interface.network_interface" => {
                "ip_configuration.0.application_gateway_backend_address_pools_ids.#"=> /<computed>/,
                "ip_configuration.0.application_security_group_ids.#"=> /<computed>/,
                "virtual_machine_id"=> /<computed>/,
                "internal_fqdn"=> /<computed>/,
                "tags.%" => /"1"/,
                "ip_configuration.0.primary" => /"true"/,
                "id"=> /<computed>/,
                "ip_configuration.0.subnet_id" => "${var.ip_configuration_subnet_id}",
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "ip_configuration.#" => /"1"/,
                "location" => /"centralus"/,
                "mac_address"=> /<computed>/,
                "applied_dns_servers.#"=> /<computed>/,
                "ip_configuration.0.public_ip_address_id" => "${var.ip_configuration_public_ip_address_id}",
                "enable_accelerated_networking" => /"false"/,
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "private_ip_addresses.#"=> /<computed>/,
                "ip_configuration.0.name" => /"primary_config"/,
                "enable_ip_forwarding" => /"false"/,
                "private_ip_address"=> /<computed>/,
                "internal_dns_name_label" => "${local.internal_dns_name_label}",
                "ip_configuration.0.private_ip_address_allocation" => /"dynamic"/
            },
            "module.network_interface_with_primary_ip_configuration.module.resource_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.resource_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.virtual_network.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.virtual_network.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.network_interface_with_primary_ip_configuration.module.subnet.module.network_security_group.module.nsg_diagnostic_log.null_resource.diagnostic_log" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            }  
        })
  end
end