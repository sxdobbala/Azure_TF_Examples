require 'spec_helper'

# Verify terraform plan works
describe 'vpc_with_nat_gateway_and_flow_logs' do
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
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/vpc_with_nat_gateway_and_flow_logs/*"],"#{Dir.pwd}/spec/fixtures", )
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
        expect( @output ).to match(/module.vpc.aws_vpc.main/)
        expect( @output ).to match(/assign_ipv6_address_on_creation:\s+"false"/)
        expect( @output ).to match(/cidr_block:\s+"10.0.0.0\/16"/)
        expect( @output ).to match(/enable_dns_hostnames:\s+"true"/)
        expect( @output ).to match(/enable_dns_support:\s+"true"/)
        expect( @output ).to match(/module.vpc.aws_internet_gateway.default/)
        expect( @output ).to match(/tags.terraform:\s+"true"/)
        expect( @output ).to match(/module.vpc.null_resource.private-cidr-helper\[[0-2]\]/)
        expect( @output ).to match(/triggers.list_of_cidr_block_for_public_subnets:\s+"10.0.[0-9][0-9].0\/20"/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_subnet.private\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_subnet.public\[[0-2]\]/)
        expect( @output ).to match(/availability_zone:\s+"us-east-1[abc]/)
        expect( @output ).to match(/map_public_ip_on_launch:\s+"false"/)        
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.private/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.public/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.private/)
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
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_response_to_internet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_icmp_for_pmtu_discovery/)
        expect( @output ).to match(/module.vpc.aws_security_group.default_private_instance/)
        expect( @output ).to match(/module.vpc.aws_security_group.default_public_elb/)
        expect( @output ).to match(/module.vpc.aws_security_group.default_public_instance/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route.internet_access\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table.private\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table.public\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table_association.private\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table_association.public\[[0-2]\]/)   
        expect( @output ).to match(/module.vpc.aws_vpc_endpoint.dynamodb/)
        expect( @output ).to match(/module.vpc.aws_vpc_endpoint.s3/)
        expect( @output ).to match(/private_dns_enabled:\s+"false"/)
        expect( @output ).to match(/vpc_endpoint_type:\s+"Gateway"/)
        expect( @output ).to match(/service_name:\s+"com.amazonaws.us-east-1.dynamodb"/)
        expect( @output ).to match(/service_name:\s+"com.amazonaws.us-east-1.s3"/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_vpc_endpoint_route_table_association.private_dynamodb\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_vpc_endpoint_route_table_association.private_s3\[[0-2]\]/)
      end
      it 'Verify terraform nat gateway' do
        expect( @output ).to match(/module.vpc.aws_nat_gateway.nat_gw\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route.to_nat_gw\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.aws_eip.nat_eip/)
      end
      it 'Verify terraform ssm output' do
        expect( @output ).to match(/module.ssm.aws_iam_instance_profile.ssm_agent/)
        expect( @output ).to match(/module.ssm.aws_iam_role.ssm_agent/)
        expect( @output ).to match(/module.ssm.aws_iam_role_policy_attachment.ssm_agent/)
        expect( @output ).to match(/policy_arn:\s+"arn:aws:iam::aws:policy\/service-role\/AmazonEC2RoleforSSM"/)
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
