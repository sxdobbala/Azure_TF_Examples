require 'spec_helper'

# Verify terraform plan works
describe 'vpc_base' do
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
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/vpc_base/*"],"#{Dir.pwd}/spec/fixtures", )
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
        expect( @output ).to match(/tags.terraform:\s+"true"/)
        expect( @output ).to match(/module.vpc.null_resource.private-cidr-helper\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_subnet.private\[[0-2]\]/)
        expect( @output ).to match(/availability_zone:\s+"us-east-1[abc]/)
        expect( @output ).to match(/map_public_ip_on_launch:\s+"false"/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.private/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.private/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_http_from_private_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_https_from_private_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_response_to_internet_from_private_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.private_inbound_http_within_VPC/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.private_inbound_https_within_VPC/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.private_inbound_return_traffic_via_nat/)
        expect( @output ).to match(/module.vpc.aws_security_group.default_private_instance/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table.private\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_route_table_association.private\[[0-2]\]/)
      end
    end

  end
end
