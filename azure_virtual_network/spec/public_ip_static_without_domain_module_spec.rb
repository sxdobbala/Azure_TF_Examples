require 'spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

describe 'public_ip_static_without_domain' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end 

  it 'should verify the plan output' do
    hcl = %{
      module "static_without_domain" {
        source = "#{__dir__}/../modules/public_ip/static_without_domain"
        resource_group_name = "rg"
      }
    }
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(3)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
            "module.static_without_domain.azurerm_public_ip.public_ip" => {
                "sku" => /"Basic"/,
                "resource_group_name" => /"rg"/,
                "name" => "${module.namespace.name}",
                "fqdn"=> /<computed>/,
                "location" => /"centralus"/,
                "tags.%"=> /"1"/,
                "tags.cc-eac_azure_virtual_network"=> /"v2.0.0"/,
                "idle_timeout_in_minutes" => /"4"/,
                "ip_address"=> /<computed>/,
                "id"=> /<computed>/,
                "public_ip_address_allocation" => /"static"/
            },
            "module.static_without_domain.module.namespace.null_resource.namespace" => {
              "id"=> /<computed>/,
              "triggers.%"=> "3",
              "triggers.format"=> "%s-%s",
              "triggers.name"=> "public_ip",
              "triggers.namespace"=> ""             
            },
            "module.static_without_domain.module.namespace.random_string.random_string" => {
              "length" => /"12"/,
              "result"=> /<computed>/                
            }
        })
  end
end