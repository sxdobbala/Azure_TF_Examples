require 'spec_helper'

# Verify terraform plan works
describe 'example_simple_bucket_with_role' do
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

  context "example_simple_bucket_with_role" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures" )
      FileUtils.mkdir_p "#{Dir.pwd}/spec/fixtures"
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/simple_bucket_with_role/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'Verify terraform init is successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    it 'Verify terraform plan count is successful' do
      expect { terraform_plan }.to output(/Plan: 6 to add, 0 to change, 0 to destroy./).to_stdout_from_any_process
    end

    describe 'Verify terraform plan for example simple_bucket_with_role' do
      before (:all) do
        terraform_plan_output
        @output = terraform_show
      end

      it 'Verify terraform plan for example simple_bucket_with_role is successful' do
        expect( @output ).to match(/module.random_name.random_string.first_char/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/length:\s+"1"/)
        expect( @output ).to match(/lower:\s+"true"/)
        expect( @output ).to match(/min_lower:\s+"0"/)
        expect( @output ).to match(/min_numeric:\s+"0"/)
        expect( @output ).to match(/min_special:\s+"0"/)
        expect( @output ).to match(/min_upper:\s+"0"/)
        expect( @output ).to match(/number:\s+"false"/)
        expect( @output ).to match(/result:\s+<computed>/)
        expect( @output ).to match(/special:\s+"false"/)
        expect( @output ).to match(/upper:\s+"false"/)

        expect( @output ).to match(/module.random_name.random_string.rest_of_string/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/length:\s+"7"/)
        expect( @output ).to match(/lower:\s+"true"/)
        expect( @output ).to match(/min_lower:\s+"0"/)
        expect( @output ).to match(/min_numeric:\s+"0"/)
        expect( @output ).to match(/min_special:\s+"0"/)
        expect( @output ).to match(/min_upper:\s+"0"/)
        expect( @output ).to match(/number:\s+"true"/)
        expect( @output ).to match(/result:\s+<computed>/)
        expect( @output ).to match(/special:\s+"false"/)
        expect( @output ).to match(/upper:\s+"false"/)

        expect( @output ).to match(/\+ module.simple-bucket-with-role.aws_iam_role.bucket_role_read_write/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/assume_role_policy:\s+"{\\n  \\"Version\\": \\"2012-10-17\\",\\n  \\"Statement\\": \[\\n    {\\n      \\"Sid\\": \\"\\",\\n      \\"Effect\\": \\"Allow\\",\\n      \\"Action\\": \\"sts:AssumeRole\\",\\n      \\"Principal\\": {\\n        \\"AWS\\": \\"arn:aws:iam::[0-9]{12}:root\\"\\n      }\\n    }\\n  \]\\n}"/)
        expect( @output ).to match(/create_date:\s+<computed>/)
        expect( @output ).to match(/description:\s+"ReadWrite role constrained to a single S3 bucket"/)
        expect( @output ).to match(/force_detach_policies:\s+"false"/)
        expect( @output ).to match(/name:\s+"S3ReadWrite-\${local.name}"/)
        expect( @output ).to match(/path:\s+"\/"/)
        expect( @output ).to match(/unique_id:\s+<computed>/)

        expect( @output ).to match(/\+ module.simple-bucket-with-role.aws_iam_role_policy.bucket_role_read_write_inline/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/name:\s+"\${aws_iam_role.bucket_role_read_write.name}"/)
        expect( @output ).to match(/policy:\s+"\${data.aws_iam_policy_document.S3_ReadWrite.json}"/)
        expect( @output ).to match(/role:\s+"\${aws_iam_role.bucket_role_read_write.name}"/)

        expect( @output ).to match(/\+ module.simple-bucket-with-role.aws_s3_bucket.bucket/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/acceleration_status:\s+<computed>/)
        expect( @output ).to match(/acl:\s+"private"/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${local.name}"/)
        expect( @output ).to match(/bucket_domain_name:\s+<computed>/)
        expect( @output ).to match(/force_destroy:\s+"true"/)
        expect( @output ).to match(/hosted_zone_id:\s+<computed>/)
        expect( @output ).to match(/region:\s+<computed>/)
        expect( @output ).to match(/request_payer:\s+<computed>/)
        expect( @output ).to match(/tags.%:\s+"5"/)
        expect( @output ).to match(/tags.Name:\s+"simple-bucket-with-role"/)
        expect( @output ).to match(/tags.global_tag:\s+"example"/)
        expect( @output ).to match(/tags.s3_encryption_type:\s+"AES256"/)
        expect( @output ).to match(/tags.s3_feature_flags:\s+"simple"/)
        expect( @output ).to match(/versioning.#:\s+"1"/)
        expect( @output ).to match(/versioning.0.enabled:\s+"false"/)
        expect( @output ).to match(/versioning.0.mfa_delete:\s+"false"/)
        expect( @output ).to match(/website_domain:\s+<computed>/)
        expect( @output ).to match(/website_endpoint:\s+<computed>/)

        expect( @output ).to match(/\+ module.simple-bucket-with-role.aws_s3_bucket_policy.bucket_policy/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${aws_s3_bucket.bucket.id}"/)
        expect( @output ).to match(/policy:\s+"\${local.policy_statement}"/)
      end
    end

  end
end