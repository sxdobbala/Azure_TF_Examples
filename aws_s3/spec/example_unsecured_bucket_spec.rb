require 'spec_helper'

# Verify terraform plan works
describe 'example_unsecured_bucket' do
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

  context "example_unsecured_bucket" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures" )
      FileUtils.mkdir_p "#{Dir.pwd}/spec/fixtures"
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/unsecured_bucket/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'Verify terraform init is successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    it 'Verify terraform plan count is successful' do
      expect { terraform_plan }.to output(/Plan: 2 to add, 0 to change, 0 to destroy./).to_stdout_from_any_process
    end

    describe 'Verify terraform plan for example unsecured_bucket' do
      before (:all) do
        terraform_plan_output
        @output = terraform_show
      end

      it 'Verify terraform plan for example unsecured_bucket is successful' do
        expect( @output ).to match(/\+ module.s3_bucket.aws_s3_bucket.bucket/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/acceleration_status:\s+<computed>/)
        expect( @output ).to match(/acl:\s+"private"/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"unsecured-bucket-example-\d+"/)
        expect( @output ).to match(/bucket_domain_name:\s+<computed>/)
        expect( @output ).to match(/bucket_regional_domain_name:\s+<computed>/)
        expect( @output ).to match(/force_destroy:\s+"true"/)
        expect( @output ).to match(/hosted_zone_id:\s+<computed>/)
        expect( @output ).to match(/region:\s+<computed>/)
        expect( @output ).to match(/request_payer:\s+<computed>/)
        expect( @output ).to match(/server_side_encryption_configuration.#:\s+"1"/)
        expect( @output ).to match(/server_side_encryption_configuration.0.rule.#:\s+"1"/)
        expect( @output ).to match(/server_side_encryption_configuration.0.rule.0.apply_server_side_encryption_by_default.#:\s+"1"/)
        expect( @output ).to match(/server_side_encryption_configuration.0.rule.0.apply_server_side_encryption_by_default.0.sse_algorithm:\s+"AES256"/)
        expect( @output ).to match(/tags.%:\s+"3"/)
        expect( @output ).to match(/tags.s3_encryption_type:\s+"AES256"/)
        expect( @output ).to match(/tags.s3_feature_flags:\s+"simple"/)
        expect( @output ).to match(/versioning.#:\s+"1"/)
        expect( @output ).to match(/versioning.0.enabled:\s+"true"/)
        expect( @output ).to match(/versioning.0.mfa_delete:\s+"false"/)
        expect( @output ).to match(/website_domain:\s+<computed>/)
        expect( @output ).to match(/website_endpoint:\s+<computed>/)

        expect( @output ).to match(/\+ module.s3_bucket.aws_s3_bucket_policy.bucket_policy/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${aws_s3_bucket.bucket.id}"/)
        expect( @output ).to match(/policy:\s+"{\\n  \\"Version\\": \\"2012-10-17\\",\\n  \\"Statement\\": \[\\n  {\\n      \\"Sid\\": \\"AWSGetBucketAcl\\",\\n      \\"Effect\\": \\"Allow\\",\\n      \\"Action\\": \\"s3:GetBucketAcl\\",\\n      \\"Resource\\": \\"arn:aws:s3:::unsecured-bucket-example-\d+\\",\\n      \\"Principal\\": \\"\*\\"\\n    },\\n    {\\n      \\"Sid\\": \\"AWSGetPut\\",\\n      \\"Effect\\": \\"Allow\\",\\n      \\"Action\\": \[\\n        \\"s3:PutObject\\",\\n        \\"s3:GetObject\\"\\n      \],\\n      \\"Resource\\": \\"arn:aws:s3:::unsecured-bucket-example-\d+\/AWSLogs\/\d+\/\*\\",\\n      \\"Principal\\": \\"\*\\"\\n    },{\\n      \\"Sid\\": \\"DenyInsecureCommunications\\",\\n      \\"Effect\\": \\"Deny\\",\\n      \\"Action\\": \\"s3:\*\\",\\n      \\"Resource\\": \\"arn:aws:s3:::unsecured-bucket-example-\d+\/\*\\",\\n      \\"Principal\\": \\"\*\\",\\n      \\"Condition\\": {\\n        \\"Bool\\": {\\n          \\"aws:SecureTransport\\": \\"false\\"\\n        }\\n      }\\n    }\\n  \]\\n}\\n"/)
      end
    end

  end
end
