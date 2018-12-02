require 'spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

describe 'network_interface_primary_and_secondary_ip_configuration' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should verify the plan output' do
    hcl = %{
      module "primary_and_secondary_ip_configuration" {
        source = "#{__dir__}/../modules/network_interface/primary_and_secondary_ip_configuration"
        ip_configuration_name = "test"
        ip_configuration_private_ip_address_allocation = "dynamic"
        ip_configuration_subnet_id = "/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/resource-group-test/providers/Microsoft.Network/virtualNetworks/three_tier_virtual_network-test/subnets/WebTier-test"
        resource_group_name = "rg"
        secondary_ip_configuration_name = "test2"
        secondary_ip_configuration_private_ip_address_allocation = "dynamic"
        secondary_ip_configuration_subnet_id = "/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/resource-group-test/providers/Microsoft.Network/virtualNetworks/three_tier_virtual_network-test/subnets/WebTier-test"
      }
    }
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(5)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
          "module.primary_and_secondary_ip_configuration.azurerm_network_interface.network_interface" => {
            "applied_dns_servers.#"=> /<computed>/,
            "enable_accelerated_networking" => /"false"/,
            "enable_ip_forwarding" => /"false"/,
            "id"=> /<computed>/,
            "internal_dns_name_label" => "${local.internal_dns_name_label}",
            "internal_fqdn"=> /<computed>/,
            "ip_configuration.#" => /"2"/,
            "ip_configuration.0.application_gateway_backend_address_pools_ids.#"=> /<computed>/,
            "ip_configuration.0.application_security_group_ids.#"=> /<computed>/,
            "ip_configuration.0.name" => /"test"/,
            "ip_configuration.0.primary" => /"true"/,
            "ip_configuration.0.private_ip_address_allocation" => /"dynamic"/,
            "ip_configuration.0.subnet_id" => "/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/resource-group-test/providers/Microsoft.Network/virtualNetworks/three_tier_virtual_network-test/subnets/WebTier-test",
            "location" => /"centralus"/,
            "mac_address"=> /<computed>/,
            "name" => "${module.namespace.name}",
            "private_ip_address"=> /<computed>/,
            "private_ip_addresses.#"=> /<computed>/,
            "resource_group_name" => /"rg"/,
            "tags.%"=> /"1"/,
            "tags.cc-eac_azure_virtual_network"=> /"v2.0.0"/,
            "virtual_machine_id"=> /<computed>/
          },
          "module.primary_and_secondary_ip_configuration.module.random_dns_label_postfix.random_string.first_char" => {
            "length" => /"1"/,
            "result"=> /<computed>/
          },
          "module.primary_and_secondary_ip_configuration.module.random_dns_label_postfix.random_string.rest_of_string" => {
            "length" => /"7"/,
            "result"=> /<computed>/
          },
          "module.primary_and_secondary_ip_configuration.module.namespace.null_resource.namespace" => {
            "id"=> /<computed>/,
            "triggers.%"=> "3",
            "triggers.format"=> "%s-%s",
            "triggers.name"=> "default",
            "triggers.namespace"=> ""              
          },
          "module.primary_and_secondary_ip_configuration.module.namespace.random_string.random_string" => {
            "length" => /"12"/,
            "result"=> /<computed>/                
          }
        })
  end
end