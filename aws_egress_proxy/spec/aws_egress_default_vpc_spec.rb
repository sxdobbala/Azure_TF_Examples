require 'spec_helper'

# Verify terraform plan works
 describe 'aws_egress_default_vpc' do
# Setup terrafrom to run from fixtures directory to eliminate test contamination

  def terraform_version
    system ("terraform --version")
  end

  def terraform_init
    system ("cd #{Dir.pwd}/spec/fixtures && terraform init")
  end

  def terraform_plan
    system ("cd #{Dir.pwd}/spec/fixtures && terraform plan -no-color")
  end

  it 'Verify correct version of terraform' do
    expect { terraform_version }.to output(/Terraform v0.1(0|1).[0-9]/).to_stdout_from_any_process
  end

  context "egress" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures" )
      FileUtils.mkdir_p "#{Dir.pwd}/spec/fixtures"
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/egress_proxy_default_vpc/*"], "#{Dir.pwd}/spec/fixtures" )
    end

    it 'Verify terraform init' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    it 'Verify terraform plan' do
      expect { terraform_plan }.to output(/Plan: [2-3][0-9] to add, 0 to change, 0 to destroy./).to_stdout_from_any_process
    end

  end

end
