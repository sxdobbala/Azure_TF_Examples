require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"
require 'spec_helper'

# Verify terraform plan works
 describe 'aws_egress_custom_vpc' do
# Setup terrafrom to run from fixtures directory to eliminate test contamination

  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
  )
  end

  it 'should execute plan' do
    expect(@tf.plan(
      files: ["#{__dir__}/../examples/egress_proxy_custom_vpc/*.tf*"]
    ))
    .to be_terraform_plan
    .to_add(83)
    .to_change(0)
    .to_destroy(0)
    .with_resources({
      "module.egress_proxy.aws_autoscaling_group.proxy" => {
        "id"                        => "<computed>",
        "arn"                       =>  "<computed>",
        "name"                      =>  "example-egress-proxy"
      }
    })
    
  end
end
