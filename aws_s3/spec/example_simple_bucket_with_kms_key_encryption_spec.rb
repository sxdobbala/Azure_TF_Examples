require 'spec_helper'

# Verify terraform plan works
describe 'example_simple_bucket_with_kms_key_encryption' do
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

  context "example_simple_bucket_with_kms_key_encryption" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures" )
      FileUtils.mkdir_p "#{Dir.pwd}/spec/fixtures"
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/simple_bucket_with_kms_key_encryption/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'Verify terraform init is successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    it 'Verify terraform plan count is successful' do
      expect { terraform_plan }.to output(/Plan: 6 to add, 0 to change, 0 to destroy./).to_stdout_from_any_process
    end

    describe 'Verify terraform plan for example simple_bucket_with_kms_key_encryption' do
      before (:all) do
        terraform_plan_output
        @output = terraform_show
      end

      it 'Verify terraform plan for example simple_bucket_with_kms_key_encryption is successful' do
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

        expect( @output ).to match(/\+ module.s3_kms_key_example.aws_kms_alias.kms-key-alias/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/name:\s+"\${format \(\\"alias\/\%s\%s\\", element\(var.alias_names, count.index\),\\n                    length\(var.namespace\) \=\= 0 \?\\n                      format\(\\"\%s\\", \\"\\" \)\\n                      :\\n                      format\(\\"\-\%s\\", var.namespace \)\\n                    \) }"/)
        expect( @output ).to match(/target_key_id:\s+"\${aws_kms_key.kms-key.key_id}"/)

        expect( @output ).to match(/\+ module.s3_kms_key_example.aws_kms_key.kms-key/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/description:\s+"\${length\(var.description\)\s+\=\=\s+0\s+\?\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+format\(\\"\%s\%s\\",\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+format\(\\"\%s\\",\s+\\"default\\"\),\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+length\(var.namespace\)\s+\=\=\s+0\s+\?\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+format\(\\"\%s\\",\s+\\"\\"\s+\)\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+:\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+format\(\\"\-\%s\\",\s+var.namespace\s+\)\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\)\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+:\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+format\(\\"\%s\%s\\",\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+format\(\\"\%s\\",\s+var.description\),\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+length\(var.namespace\)\s+\=\=\s+0\s+\?\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+format\(\\"\%s\\",\s+\\"\\"\s+\)\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+:\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+format\(\\"\-\%s\\",\s+var.namespace\s+\)\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\)\\n\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+\s+}"/)
        expect( @output ).to match(/enable_key_rotation:\s+"true"/)
        expect( @output ).to match(/is_enabled:\s+"true"/)
        expect( @output ).to match(/key_id:\s+<computed>/)
        expect( @output ).to match(/key_usage:\s+<computed>/)
        expect( @output ).to match(/policy:\s+<computed>/)
        expect( @output ).to match(/tags.%:\s+"3"/)
        expect( @output ).to match(/tags.Name:\s+"s3-kms-key-example"/)
        expect( @output ).to match(/tags.global_tag:\s+"example"/)

        expect( @output ).to match(/\+ module.simple_bucket_with_kms_key_encryption.aws_s3_bucket.bucket/)
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
        expect( @output ).to match(/tags.Name:\s+"simple-bucket-with-kms-key-encryption"/)
        expect( @output ).to match(/tags.global_tag:\s+"example"/)
        expect( @output ).to match(/tags.s3_encryption_type:\s+"aws:kms"/)
        expect( @output ).to match(/tags.s3_feature_flags:\s+"simple"/)
        expect( @output ).to match(/versioning.#:\s+"1"/)
        expect( @output ).to match(/versioning.0.enabled:\s+"false"/)
        expect( @output ).to match(/versioning.0.mfa_delete:\s+"false"/)
        expect( @output ).to match(/website_domain:\s+<computed>/)
        expect( @output ).to match(/website_endpoint:\s+<computed>/)

        expect( @output ).to match(/\+ module.simple_bucket_with_kms_key_encryption.aws_s3_bucket_policy.bucket_policy/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${aws_s3_bucket.bucket.id}"/)
        expect( @output ).to match(/policy:\s+"\${local.policy_statement}"/)

      end
    end

  end
end