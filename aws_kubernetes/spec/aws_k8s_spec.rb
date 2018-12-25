require 'spec_helper'

# Verify terraform plan works
 describe 'aws_k8s' do
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
    expect { terraform_version }.to output(/Terraform v0.1[0-2].[0-9]/).to_stdout_from_any_process
  end

  context "k8s" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures" )
      FileUtils.mkdir_p "#{Dir.pwd}/spec/fixtures"
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/k8s_with_vpc/*"],"#{Dir.pwd}/spec/fixtures", )
    end
    

    it 'Verify terraform init is successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end


    describe 'Verify terraform plan output' do
      before (:all) do
        `cd #{Dir.pwd}/spec/fixtures && terraform plan -out=plan_output.tfplan -no-color -var k8s_route53_parent_zoneid=TEST`
        @output = terraform_show
      end
    
    it 'Verify terraform base vpc plan output' do
        expect( @output ).to match(/module.vpc.aws_vpc.main/)
        expect( @output ).to match(/assign_ipv6_address_on_creation:\s+"false"/)
        expect( @output ).to match(/cidr_block:\s+"10.1.0.0\/16"/)
        expect( @output ).to match(/enable_dns_hostnames:\s+"true"/)
        expect( @output ).to match(/enable_dns_support:\s+"true"/)
        expect( @output ).to match(/module.vpc.aws_internet_gateway.default/)
        expect( @output ).to match(/tags.terraform:\s+"true"/)
        expect( @output ).to match(/module.vpc.null_resource.private-cidr-helper\[[0-2]\]/)
        expect( @output ).to match(/triggers.list_of_cidr_block_for_public_subnets:\s+"10.1.[0-9][0-9].0\/20"/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_subnet.private\[[0-2]\]/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_subnet.public\[[0-2]\]/)
        expect( @output ).to match(/availability_zone:\s+"us-east-1[abc]/)
        expect( @output ).to match(/map_public_ip_on_launch:\s+"false"/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.private/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.public/)
        expect( @output ).to match(/module.vpc.module.vpc-subnets.aws_network_acl.private/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_http_from_private_subnet/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.outbound_http_from_public_subnet/)
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
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_http/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_http/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_http/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_http/)
        expect( @output ).to match(/module.vpc.aws_network_acl_rule.public_inbound_http/)
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
      it 'Verify terraform egress proxy output' do
        expect( @output ).to match(/module.egress_proxy.module.managed-policy-for-ec2-proxy.aws_iam_policy.policy/)
        expect( @output ).to match(/module.egress_proxy.module.create-role-for-ec2-proxy.aws_iam_role_policy_attachment.role-managed-policy/)
        expect( @output ).to match(/module.egress_proxy.module.create-role-for-codedeploy.aws_iam_role.role/)
        expect( @output ).to match(/module.egress_proxy.module.create-role-for-ec2-proxy.aws_iam_role.role/)
        expect( @output ).to match(/module.egress_proxy.aws_s3_bucket_object.proxy_url/)
        expect( @output ).to match(/module.egress_proxy.aws_launch_configuration.proxy/)
        expect( @output ).to match(/module.egress_proxy.aws_elb.proxy/)
        expect( @output ).to match(/module.egress_proxy.aws_codedeploy_deployment_group.proxy/)
        expect( @output ).to match(/module.egress_proxy.aws_codedeploy_app.proxy/)
        expect( @output ).to match(/module.egress_proxy.aws_cloudwatch_log_group.proxy/)
        expect( @output ).to match(/module.egress_proxy.aws_autoscaling_attachment.asg_attachment_bar/)
        expect( @output ).to match(/from_port:\s+"3128"/)
        expect( @output ).to match(/to_port:\s+"3128"/)
        expect( @output ).to match(/security_group_id:\s+"\${module.egress_proxy.egress_proxy_security_group}"/)
      end
      it 'Verify terraform flowlog output' do
        expect( @output ).to match(/module.flow_logs.module.managed-policy-for-flow-logs.aws_iam_policy.policy/)
        expect( @output ).to match(/module.flow_logs.module.create-role-for-flow-logs.aws_iam_role.role/)
        expect( @output ).to match(/module.flow_logs.aws_flow_log.main_vpc/)
        expect( @output ).to match(/module.flow_logs.module.create-role-for-flow-logs.aws_iam_role_policy_attachment.role-managed-policy/)
      end
      it 'Verify terraform k8s output' do
        expect( @output ).to match(/module.k8s.aws_autoscaling_attachment.master\[[0-2]\]/)
        expect( @output ).to match(/module.k8s.aws_autoscaling_group.master-asg\[[0-2]\]/)
        expect( @output ).to match(/tags.0.key:\s+"KubernetesCluster"/)
        expect( @output ).to match(/module.k8s.aws_autoscaling_group.nodes-asg/)
        expect( @output ).to match(/module.k8s.aws_ebs_volume.etcd-events\[[0-2]\]/)
        expect( @output ).to match(/module.k8s.aws_elb.api/)
        expect( @output ).to match(/health_check.0.target:\s+"TCP:443"/)
        expect( @output ).to match(/root_block_device.0.volume_size:\s+"60"/)
        expect( @output ).to match(/module.k8s.aws_iam_instance_profile.masters/)
        expect( @output ).to match(/module.k8s.aws_launch_configuration.master\[[0-2]\]/)
        expect( @output ).to match(/module.k8s.aws_iam_instance_profile.nodes/)
        expect( @output ).to match(/module.k8s.aws_launch_configuration.nodes/)
        expect( @output ).to match(/root_block_device.0.volume_size:\s+"120"/)
        expect( @output ).to match(/module.k8s.aws_network_acl_rule.private_inbound_within_VPC/)
        expect( @output ).to match(/module.k8s.aws_network_acl_rule.private_outbound_within_VPC/)
        expect( @output ).to match(/module.k8s.aws_route53_record.api-k8s/)
        expect( @output ).to match(/module.k8s.aws_security_group.api-elb/)
        expect( @output ).to match(/module.k8s.aws_security_group.masters/)
        expect( @output ).to match(/module.k8s.aws_security_group.nodes/)
        expect( @output ).to match(/module.k8s.aws_security_group_rule.all-master-to-master/)
        expect( @output ).to match(/module.k8s.aws_security_group_rule.all-master-to-node/)
        expect( @output ).to match(/module.k8s.aws_security_group_rule.all-node-to-node/)
        expect( @output ).to match(/module.k8s.aws_security_group_rule.api-elb-egress/)
        expect( @output ).to match(/module.k8s.aws_security_group_rule.https-api-elb/)
        expect( @output ).to match(/module.k8s.aws_security_group_rule.https-elb-to-master/)
        expect( @output ).to match(/module.k8s.aws_security_group_rule.master-egress/)
     end

    end

  end

end
