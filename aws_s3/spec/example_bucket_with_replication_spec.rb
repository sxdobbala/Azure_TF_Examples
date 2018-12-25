require 'spec_helper'

# Verify terraform plan works
describe 'example_bucket_with_replication' do
  # Setup terrafrom to run from fixtures directory to eliminate test contamination

  def terraform_init
    system ("cd #{Dir.pwd}/spec/fixtures && terraform init")
  end

  def terraform_plan
    system ("cd #{Dir.pwd}/spec/fixtures && terraform plan -no-color")
  end

  def terraform_plan_output
    `cd #{Dir.pwd}/spec/fixtures && terraform plan -out=plan_output.tfplan -no-color`
  end

  def terraform_show
    `cd #{Dir.pwd}/spec/fixtures && terraform show plan_output.tfplan -no-color`
  end

  context "example_bucket_with_replication" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures" )
      FileUtils.mkdir_p "#{Dir.pwd}/spec/fixtures"
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/bucket_with_replication/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'Verify terraform init is successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    it 'Verify terraform plan count is successful' do
      expect { terraform_plan }.to output(/Plan: 6 to add, 0 to change, 0 to destroy./).to_stdout_from_any_process
    end

    describe 'Verify terraform plan for example bucket_with_replication' do
      before (:all) do
        terraform_plan_output
        @output = terraform_show
      end

      it 'Verify terraform plan for example bucket_with_replication is successful' do
        expect( @output ).to match(/\+ module.bucket_with_replication.aws_s3_bucket.bucket/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/acceleration_status:\s+<computed>/)
        expect( @output ).to match(/acl:\s+"private"/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"[0-9]{12}-bucket-with-replication-example"/)
        expect( @output ).to match(/bucket_domain_name:\s+<computed>/)
        expect( @output ).to match(/force_destroy:\s+"false"/)
        expect( @output ).to match(/hosted_zone_id:\s+<computed>/)
        expect( @output ).to match(/replication_configuration.0.rules.~[0-9]{10}.destination.~[0-9]{10}.bucket:\s+"\${aws_s3_bucket.rep_bucket.arn}"/)
        expect( @output ).to match(/region:\s+<computed>/)
        expect( @output ).to match(/request_payer:\s+<computed>/)
        expect( @output ).to match(/tags.%:\s+"5"/)
        expect( @output ).to match(/tags.Name:\s+"bucket-with-replication"/)
        expect( @output ).to match(/tags.global_tag:\s+"example"/)
        expect( @output ).to match(/tags.s3_encryption_type:\s+"AES256"/)
        expect( @output ).to match(/tags.s3_feature_flags:\s+"rep"/)
        expect( @output ).to match(/versioning.#:\s+"1"/)
        expect( @output ).to match(/versioning.0.enabled:\s+"true"/)
        expect( @output ).to match(/versioning.0.mfa_delete:\s+"false"/)
        expect( @output ).to match(/website_domain:\s+<computed>/)
        expect( @output ).to match(/website_endpoint:\s+<computed>/)

        expect( @output ).to match(/\+ module.bucket_with_replication.aws_s3_bucket.rep_bucket/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/acceleration_status:\s+<computed>/)
        expect( @output ).to match(/acl:\s+"private"/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"bucket-with-replication-replication-example"/)
        expect( @output ).to match(/bucket_domain_name:\s+<computed>/)
        expect( @output ).to match(/force_destroy:\s+"false"/)
        expect( @output ).to match(/hosted_zone_id:\s+<computed>/)
        expect( @output ).to match(/region:\s+<computed>/)
        expect( @output ).to match(/request_payer:\s+<computed>/)
        expect( @output ).to match(/tags.%:\s+"5"/)
        expect( @output ).to match(/tags.Name:\s+"bucket-with-replication"/)
        expect( @output ).to match(/tags.global_tag:\s+"example"/)
        expect( @output ).to match(/tags.s3_encryption_type:\s+"AES256"/)
        expect( @output ).to match(/tags.s3_feature_flags:\s+"rep"/)
        expect( @output ).to match(/versioning.#:\s+"1"/)
        expect( @output ).to match(/versioning.0.enabled:\s+"true"/)
        expect( @output ).to match(/versioning.0.mfa_delete:\s+"false"/)
        expect( @output ).to match(/website_domain:\s+<computed>/)
        expect( @output ).to match(/website_endpoint:\s+<computed>/)


        expect( @output ).to match(/\+ module.bucket_with_replication.aws_s3_bucket_policy.bucket_policy/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${aws_s3_bucket.bucket.id}"/)
        expect( @output ).to match(/policy:\s+"{\\n  \\"Version\\": \\"2012-10-17\\",\\n  \\"Statement\\": \[\\n  {\\n      \\"Sid\\": \\"DenyInsecureCommunications\\",\\n      \\"Effect\\": \\"Deny\\",\\n      \\"Action\\": \\"s3:\*\\",\\n      \\"Resource\\": \\"arn:aws:s3:::[0-9]{12}-bucket-with-replication-example\/\*\\",\\n      \\"Principal\\": \\"\*\\",\\n      \\"Condition\\": {\\n        \\"Bool\\": {\\n          \\"aws:SecureTransport\\": \\"false\\"\\n        }\\n      }\\n    }\\n  \]\\n}\\n"/)
      end
    end

  end
end
