require 'spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

describe 'example_public_ip' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should verify the plan output' do
    hcl = %{
      module "public_ip" {
        source = "#{__dir__}/../examples/public_ip"
      }
    }
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(8)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
            "module.public_ip.module.random_name.random_string.first_char" => {
                "length" => /"1"/,
                "result"=> /<computed>/
            },
            "module.public_ip.module.public_ip.azurerm_public_ip.public_ip" => {
                "sku" => /"Basic"/,
                "resource_group_name" => "${var.resource_group_name}",
                "name" => "${module.namespace.name}",
                "fqdn"=> /<computed>/,
                "domain_name_label" => "${var.domain_name_label}",
                "location" => /"centralus"/,
                "tags.%"=> /"1"/,
                "tags.cc-eac_azure_virtual_network"=> /"v2.0.0"/,
                "idle_timeout_in_minutes" => /"4"/,
                "ip_address"=> /<computed>/,
                "id"=> /<computed>/,
                "public_ip_address_allocation" => /"dynamic"/
            },
            "module.public_ip.module.resource_group.azurerm_resource_group.resource_group" => {
                "tags.%"=> /"1"/,
                "tags.cc-eac_azure_resource_group" => /"v2.0.0"/,
                "id"=> /<computed>/,
                "name" => "${module.namespace.name}",
                "location" => /"centralus"/
            },
            "module.public_ip.module.random_name.random_string.rest_of_string" => {
                "length" => /"7"/,
                "result"=> /<computed>/
            },
            "module.public_ip.module.public_ip.module.namespace.null_resource.namespace" => {
              "id"=> /<computed>/,
              "triggers.%"=> /<computed>/              
            },
            "module.public_ip.module.public_ip.module.namespace.random_string.random_string" => {
              "length" => /"12"/,
              "result"=> /<computed>/                
            },
            "module.public_ip.module.resource_group.module.namespace.null_resource.namespace" => {
                "id"=> /<computed>/,
                "triggers.%"=> /<computed>/
            },
            "module.public_ip.module.resource_group.module.namespace.random_string.random_string" => {
                "length" => /"12"/,
                "result"=> /<computed>/
            }                        
        })
  end
end