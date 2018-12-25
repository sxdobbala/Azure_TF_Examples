require 'spec_helper'

# Verify terraform plan works
describe 'vpc_flow_log_map_to_log_group' do
  # Setup terrafrom to run from fixtures directory to eliminate test contamination

  def terraform_version
    system ("terraform --version")
  end

  def terraform_init
    system ("cd #{Dir.pwd}/spec/fixtures && terraform init")
  end


  def terraform_show
    `cd #{Dir.pwd}/spec/fixtures && terraform show plan_output.tfplan -no-color`
  end

  it 'Verify correct version of terraform' do
    expect { terraform_version }.to output(/Terraform v0.1[0-1].[0-9]/).to_stdout_from_any_process
  end

  context "config" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures" )
      FileUtils.mkdir_p "#{Dir.pwd}/spec/fixtures"
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/vpc_flow_log_map_to_log_group/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'Verify terraform init is successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end
    

    describe 'Verify terraform plan output' do
      before (:all) do
        # Create file required to run test
        `cd #{Dir.pwd}/spec/fixtures && terraform plan -out=plan_output.tfplan -no-color`
        @output = terraform_show
      end

      it 'Verify terraform plan output' do
        expect( @output ).to match(/module.vpc.aws_internet_gateway.default/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_http_from_private_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_http_from_public_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_https_from_private_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_https_from_public_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_response_to_internet_from_private_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_response_to_internet_from_public_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.private_inbound_http_within_VPC/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.private_inbound_https_within_VPC/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.private_inbound_return_traffic_via_nat/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_http/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_https/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_icmp_for_pmtu_discovery/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_response_to_internet/)
        expect( @output ).to match(/module.vpc.aws_security_group.default_private_instance/)
        expect( @output ).to match(/module.vpc.aws_security_group.default_public_elb/)
        expect( @output ).to match(/module.vpc.aws_security_group.default_public_instance/)
        expect( @output ).to match(/module.vpc.aws_security_group_rule.allow_icmp_pmtu/)
        expect( @output ).to match(/module.vpc.null_resource.private-cidr-helper\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.null_resource.public-cidr-helper\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.flow_logs.aws_flow_log.main_vpc/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.private/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.public/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route.internet_access\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table.private\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table.public\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table_association.private\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table_association.public\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_subnet.private\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_subnet.public\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.flow_logs.module.create-role-for-flow-logs.aws_iam_role.role/)
        expect( @output ).to match(/module.vpc.module.flow_logs.module.create-role-for-flow-logs.aws_iam_role_policy_attachment.role-managed-policy/)
        expect( @output ).to match(/module.vpc.module.flow_logs.module.managed-policy-for-flow-logs.aws_iam_policy.policy/)
      end
      it 'Verify terraform flowlog output' do
        expect( @output ).to match(/module.flow_logs.module.managed-policy-for-flow-logs.aws_iam_policy.policy/)
        expect( @output ).to match(/module.flow_logs.module.create-role-for-flow-logs.aws_iam_role.role/)
        expect( @output ).to match(/module.flow_logs.aws_flow_log.main_vpc/)
        expect( @output ).to match(/module.flow_logs.module.create-role-for-flow-logs.aws_iam_role_policy_attachment.role-managed-policy/)
      end
    end

  end
end
