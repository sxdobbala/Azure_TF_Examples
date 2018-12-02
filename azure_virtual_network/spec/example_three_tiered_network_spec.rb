require 'spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

describe 'example_three_tiered_network' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should verify the plan output' do
    hcl = %{
      module "three_tiered_network" {
        source = "#{__dir__}/../examples/three_tiered_network"
      }
    }
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(43)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
            "module.three_tiered_network.module.business_tier_subnet.module.network_security_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.module.virtual_network.azurerm_virtual_network.virtual_network" => {
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
            "module.three_tiered_network.module.random_name.random_string.first_char" => {
                "length" => /"1"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.azurerm_network_security_rule.allow_inbound_from_business_tier_rule" => {
                "priority" => /"100"/,
                "destination_address_prefix" => /"10.0.3.0\/24"/,
                "protocol" => /"tcp"/,
                "description" => /"Allow inbound traffic from business tier."/,
                "resource_group_name" => "${module.resource_group.name}",
                "direction" => /"Inbound"/,
                "access" => /"Allow"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${module.data_tier_subnet.network_security_group_name}",
                "source_address_prefix" => /"10.0.2.0\/24"/,
                "id"=> /<computed>/,
                "destination_port_range" => /"3306"/,
                "name" => /"AllowInboundFromBusinessTier"/
            },
            "module.three_tiered_network.module.web_tier_subnet.module.network_security_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.business_tier_subnet.module.network_security_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.data_tier_subnet.module.network_security_group.azurerm_network_security_rule.deny_all_outbound" => {
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
            "module.three_tiered_network.module.business_tier_subnet.module.network_security_group.azurerm_network_security_rule.deny_all_outbound" => {
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
            "module.three_tiered_network.module.data_tier_subnet.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.web_tier_subnet.module.network_security_group.azurerm_network_security_group.network_security_group" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "security_rule.#"=> /<computed>/,
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "location" => /"centralus"/,
                "tags.%" => /"1"/,
                "id"=> /<computed>/
            },
                "module.three_tiered_network.module.random_name.random_string.rest_of_string" => {
                "length" => /"7"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.module.resource_group.azurerm_resource_group.resource_group" => {
                "tags.%"=> /"1"/,
                "tags.cc-eac_azure_resource_group" => /"v2.0.0"/,
                "id"=> /<computed>/,
                "name" => "${module.namespace.name}",
                "location" => /"centralus"/
            },
            "module.three_tiered_network.module.web_tier_subnet.module.network_security_group.azurerm_network_security_rule.allow_inbound_azure_load_balancer" => {
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
            "module.three_tiered_network.azurerm_network_security_rule.allow_inbound_from_web_tier_rule" => {
                "priority" => /"100"/,
                "destination_address_prefix" => /"10.0.2.0\/24"/,
                "protocol" => /"tcp"/,
                "description" => /"Allow inbound traffic from web tier."/,
                "resource_group_name" => "${module.resource_group.name}",
                "direction" => /"Inbound"/,
                "access" => /"Allow"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${module.business_tier_subnet.network_security_group_name}",
                "source_address_prefix" => /"10.0.1.0\/24"/,
                "id"=> /<computed>/,
                "destination_port_range" => /"8080"/,
                "name" => /"AllowInboundFromWebTier"/
            },
            "module.three_tiered_network.module.data_tier_subnet.module.network_security_group.azurerm_network_security_rule.deny_all_inbound" => {
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
            "module.three_tiered_network.module.data_tier_subnet.module.network_security_group.azurerm_network_security_rule.allow_inbound_azure_load_balancer" => {
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
            "module.three_tiered_network.module.data_tier_subnet.module.network_security_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.azurerm_network_security_rule.allow_outbound_to_data_tier_rule" => {
                "priority" => /"200"/,
                "destination_address_prefix" => /"10.0.3.0\/24"/,
                "protocol" => /"tcp"/,
                "description" => /"Allow outbound traffic to data tier."/,
                "resource_group_name" => "${module.resource_group.name}",
                "direction" => /"Outbound"/,
                "access" => /"Allow"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${module.business_tier_subnet.network_security_group_name}",
                "source_address_prefix" => /"10.0.2.0\/24"/,
                "id"=> /<computed>/,
                "destination_port_range" => /"3306"/,
                "name" => /"AllowOutboundToDataTier"/
            },
            "module.three_tiered_network.module.business_tier_subnet.module.network_security_group.azurerm_network_security_rule.allow_inbound_azure_load_balancer" => {
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
            "module.three_tiered_network.module.web_tier_subnet.module.network_security_group.azurerm_network_security_rule.deny_all_outbound" => {
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
            "module.three_tiered_network.module.business_tier_subnet.module.network_security_group.azurerm_network_security_group.network_security_group" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "security_rule.#"=> /<computed>/,
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "location" => /"centralus"/,
                "tags.%" => /"1"/,
                "id"=> /<computed>/
            },
            "module.three_tiered_network.module.business_tier_subnet.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.data_tier_subnet.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.module.web_tier_subnet.azurerm_subnet.subnet" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "virtual_network_name" => "${var.virtual_network_name}",
                "id"=> /<computed>/,
                "address_prefix" => /"10.0.1.0\/24"/,
                "network_security_group_id" => "${module.network_security_group.id}",
                "ip_configurations.#"=> /<computed>/
            },
            "module.three_tiered_network.module.data_tier_subnet.azurerm_subnet.subnet" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "virtual_network_name" => "${var.virtual_network_name}",
                "id"=> /<computed>/,
                "address_prefix" => /"10.0.3.0\/24"/,
                "network_security_group_id" => "${module.network_security_group.id}",
                "ip_configurations.#"=> /<computed>/
            },
            "module.three_tiered_network.module.web_tier_subnet.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.data_tier_subnet.module.network_security_group.azurerm_network_security_group.network_security_group" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "security_rule.#"=> /<computed>/,
                "tags.cc-eac_azure_virtual_network" => /"v2.0.0"/,
                "location" => /"centralus"/,
                "tags.%" => /"1"/,
                "id"=> /<computed>/
            },
            "module.three_tiered_network.module.web_tier_subnet.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.azurerm_network_security_rule.allow_outbound_to_business_tier_rule" => {
                "priority" => /"200"/,
                "destination_address_prefix" => /"10.0.2.0\/24"/,
                "protocol" => /"tcp"/,
                "description" => /"Allow outbound traffic to business tier."/,
                "resource_group_name" => "${module.resource_group.name}",
                "direction" => /"Outbound"/,
                "access" => /"Allow"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${module.web_tier_subnet.network_security_group_name}",
                "source_address_prefix" => /"10.0.1.0\/24"/,
                "id"=> /<computed>/,
                "destination_port_range" => /"8080"/,
                "name" => /"AllowOutboundToBusinessTier"/
            },
            "module.three_tiered_network.module.data_tier_subnet.module.network_security_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.web_tier_subnet.module.network_security_group.azurerm_network_security_rule.deny_all_inbound" => {
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
            "module.three_tiered_network.module.web_tier_subnet.module.network_security_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.azurerm_network_security_rule.allow_inbound_https_rule" => {
                "priority" => /"100"/,
                "destination_address_prefix" => /"10.0.1.0\/24"/,
                "protocol" => /"tcp"/,
                "description" => /"Allow inbound traffic using https."/,
                "resource_group_name" => "${module.resource_group.name}",
                "direction" => /"Inbound"/,
                "access" => /"Allow"/,
                "source_port_range" => "*",
                "network_security_group_name" => "${module.web_tier_subnet.network_security_group_name}",
                "source_address_prefix" => /"Internet"/,
                "id"=> /<computed>/,
                "destination_port_range" => /"443"/,
                "name" => /"AllowInboundHttps"/
            },
            "module.three_tiered_network.module.business_tier_subnet.module.network_security_group.azurerm_network_security_rule.deny_all_inbound" => {
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
            "module.three_tiered_network.module.business_tier_subnet.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.module.business_tier_subnet.azurerm_subnet.subnet" => {
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "virtual_network_name" => "${var.virtual_network_name}",
                "id"=> /<computed>/,
                "address_prefix" => /"10.0.2.0\/24"/,
                "network_security_group_id" => "${module.network_security_group.id}",
                "ip_configurations.#"=> /<computed>/
            },
            "module.three_tiered_network.module.resource_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.resource_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.module.virtual_network.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.virtual_network.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            },
            "module.three_tiered_network.module.web_tier_subnet.module.network_security_group.module.nsg_diagnostic_log.null_resource.diagnostic_log" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.data_tier_subnet.module.network_security_group.module.nsg_diagnostic_log.null_resource.diagnostic_log" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.three_tiered_network.module.business_tier_subnet.module.network_security_group.module.nsg_diagnostic_log.null_resource.diagnostic_log" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            }
        })
  end
end