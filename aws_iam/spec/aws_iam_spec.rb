require 'spec_helper'

# Verify terraform plan works
describe 'aws_iam' do
  # Setup terrafrom to run from fixtures directory to eliminate test contamination
  before (:all) do
    FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/*.tf" )
    FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/.terraform" )
  end

  def terraform_version
    system ("terraform --version")
  end

  def terraform_init
    system ("cd #{Dir.pwd}/spec/fixtures && terraform init")
  end

  def terraform_plan
    `cd #{Dir.pwd}/spec/fixtures && terraform plan -out=plan_output.tfplan -no-color`
  end

  def terraform_show
    `cd #{Dir.pwd}/spec/fixtures && terraform show plan_output.tfplan -no-color`
  end

  it 'expected version of terraform should be present' do
    expect { terraform_version }.to output(/Terraform v0.11.[0-9]/).to_stdout_from_any_process
  end

  context "iam-user" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/*.tf" )
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/.terraform" )
      FileUtils.cp_r(Dir["#{Dir.pwd}/modules/iam-user/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'terraform init should be successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    describe 'terraform plan for iam-user' do
      before (:all) do
        terraform_plan
        @output = terraform_show
      end

      it 'terraform plan for iam-user should be successful' do
        expect( @output ).to match(/\+ aws_iam_user.user/)
        expect( @output ).to match(/name:\s+"default"/)
        expect( @output ).to match(/force_destroy:\s+"false"/)
        expect( @output ).to match(/path:\s+"\/"/)
      end
    end
  end

  context "iam-role" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/*.tf" )
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/.terraform" )
      FileUtils.cp_r(Dir["#{Dir.pwd}/modules/iam-role/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'terraform init should be successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    describe 'terraform plan for iam-role' do
      before (:all) do
        terraform_plan
        @output = terraform_show
      end

      it 'terraform plan for iam-role should be successful' do
        expect( @output ).to match(/\+ aws_iam_role.role/)
        expect( @output ).to match(/description:\s+"default-description"/)
        expect( @output ).to match(/name:\s+"default-role"/)
        expect( @output ).to match(/force_detach_policies:\s+"false"/)
        expect( @output ).to match(/path:\s+"\/"/)
      end
    end
  end

  def terraform_plan_for_policy
    `cd #{Dir.pwd}/spec/fixtures && terraform plan -out=plan_output.tfplan -no-color -var 'document=\"{\\"Version\\": \\"2012-10-17\\",\\"Statement\\":[]}\"'`
  end

  context "iam-policy" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/*.tf" )
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/.terraform" )
      FileUtils.cp_r(Dir["#{Dir.pwd}/modules/iam-policy/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'terraform init should be successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    describe 'terraform plan for iam-policy' do
      before (:all) do
        terraform_plan_for_policy
        @output = terraform_show
      end

      it 'terraform plan for iam-policy should be successful' do
        expect( @output ).to match(/\+ aws_iam_policy.policy/)
        expect( @output ).to match(/description:\s+"default-policy-description"/)
        expect( @output ).to match(/name:\s+"default-policy"/)
      end

    end
  end

  context "iam-group" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/*.tf" )
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures/.terraform" )
      FileUtils.cp_r(Dir["#{Dir.pwd}/modules/iam-group/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'terraform init should be successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    describe 'terraform plan for iam-group' do
      before (:all) do
        terraform_plan
        @output = terraform_show
      end

      it 'terraform plan for iam-group should be successful' do
        expect( @output ).to match(/\+ aws_iam_group.group/)
        expect( @output ).to match(/name:\s+"default-group"/)
        expect( @output ).to match(/path:\s+"\/"/)
        expect( @output ).to match(/\+ aws_iam_group_membership.users/)
        expect( @output ).to match(/group:\s+"default-group"/)
      end
    end
  end

end
