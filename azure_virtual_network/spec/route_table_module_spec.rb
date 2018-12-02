require_relative './spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

# Verify terraform plan works
describe 'route_table' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  it 'should plan resources' do
        hcl = %{
            module "route_table" {
              source = "#{__dir__}/../modules/route_table"
              name = "route_table"
              namespace = "test"
              resource_group_name = "test_rg"
            }
          } 
    plan_out = @tf.plan(hcl: hcl)
    expect(plan_out)
      .to be_terraform_plan
        .to_add(3)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
          "module.route_table.azurerm_route_table.route_table" => {
            "id" => /<computed>/,
            "location" => /"centralus"/,
            "name" => "${module.namespace.name}",
            "resource_group_name" => /"test_rg"/,
            "subnets.#" => /<computed>/,
            "tags.%" => /"1"/,
            "tags.cc-eac_azure_virtual_network"=> /"v2.0.0"/
          },
          "module.route_table.module.namespace.null_resource.namespace" => {
            "id"=> /<computed>/,
            "triggers.%"=> "3",
            "triggers.format"=> "%s-%s",
            "triggers.name"=> "route_table",
            "triggers.namespace"=> ""             
          },
          "module.route_table.module.namespace.random_string.random_string" => {
            "length" => /"12"/,
            "result"=> /<computed>/                
          }
        })
  end
end