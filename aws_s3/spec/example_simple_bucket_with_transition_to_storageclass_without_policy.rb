require 'spec_helper'

# Verify terraform plan works
describe 'example_simple-bucket-with-storage-class-without-policy' do
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

  context "example_simple-bucket-with-storage-class-without-policy" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures" )
      FileUtils.mkdir_p "#{Dir.pwd}/spec/fixtures"
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/simple_bucket_with_transition_to_storageclass_without_policy/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'Verify terraform init is successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    it 'Verify terraform plan count is successful' do
      expect { terraform_plan }.to output(/Plan: 4 to add, 0 to change, 0 to destroy./).to_stdout_from_any_process
    end

    describe 'Verify terraform plan for example simple-bucket-with-storage-class-without-policy' do
      before (:all) do
        terraform_plan_output
        @output = terraform_show
      end

      it 'Verify terraform plan for example simple-bucket-with-storage-class-without-policy is successful' do
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

        expect( @output ).to match(/\+ module.simple-bucket-with-storage-class-without-policy.aws_s3_bucket.bucket/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/acceleration_status:\s+<computed>/)
        expect( @output ).to match(/acl:\s+"private"/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${local.name}"/)
        expect( @output ).to match(/bucket_domain_name:\s+<computed>/)
        expect( @output ).to match(/force_destroy:\s+"false"/)
        expect( @output ).to match(/hosted_zone_id:\s+<computed>/)
        expect( @output ).to match(/lifecycle_rule.#:\s+"1"/)
        expect( @output ).to match(/lifecycle_rule.0.enabled:\s+"true"/)
        expect( @output ).to match(/lifecycle_rule.0.expiration.#:\s+"1"/)
        expect( @output ).to match(/lifecycle_rule.0.expiration.2613713285.date:\s+""/)
        expect( @output ).to match(/lifecycle_rule.0.expiration.2613713285.days:\s+"365"/)
        expect( @output ).to match(/lifecycle_rule.0.expiration.2613713285.expired_object_delete_marker:\s+""/)
        expect( @output ).to match(/lifecycle_rule.0.id:\s+"lifecycle-rule"/)
        expect( @output ).to match(/lifecycle_rule.0.noncurrent_version_transition.#:\s+"1"/)
        expect( @output ).to match(/lifecycle_rule.0.noncurrent_version_transition.3891883947.days:\s+"30"/)
        expect( @output ).to match(/lifecycle_rule.0.noncurrent_version_transition.3891883947.storage_class:\s+"GLACIER"/)
        expect( @output ).to match(/lifecycle_rule.0.transition.#:\s+"2"/)
        expect( @output ).to match(/lifecycle_rule.0.transition.1300905083.date:\s+""/)
        expect( @output ).to match(/lifecycle_rule.0.transition.1300905083.days:\s+"30"/)
        expect( @output ).to match(/lifecycle_rule.0.transition.1300905083.storage_class:\s+"INTELLIGENT_TIERING"/)
        expect( @output ).to match(/lifecycle_rule.0.transition.6450812.date:\s+""/)
        expect( @output ).to match(/lifecycle_rule.0.transition.6450812.days:\s+"60"/)
        expect( @output ).to match(/lifecycle_rule.0.transition.6450812.storage_class:\s+"GLACIER"/)
        expect( @output ).to match(/region:\s+<computed>/)
        expect( @output ).to match(/request_payer:\s+<computed>/)
        expect( @output ).to match(/server_side_encryption_configuration.#:\s+"1"/)
        expect( @output ).to match(/server_side_encryption_configuration.0.rule.0.apply_server_side_encryption_by_default.#:\s+"1"/)
        expect( @output ).to match(/server_side_encryption_configuration.0.rule.0.apply_server_side_encryption_by_default.0.sse_algorithm:\s+"AES256"/)
        expect( @output ).to match(/tags.%:\s+<computed>/)
        expect( @output ).to match(/versioning.#:\s+"1"/)
        expect( @output ).to match(/versioning.0.enabled:\s+"false"/)
        expect( @output ).to match(/versioning.0.mfa_delete:\s+"false"/)
        expect( @output ).to match(/website_domain:\s+<computed>/)
        expect( @output ).to match(/website_endpoint:\s+<computed>/)

        expect( @output ).to match(/\+ module.simple-bucket-with-storage-class-without-policy.aws_s3_bucket_policy.bucket_policy/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${aws_s3_bucket.bucket.id}"/)
        expect( @output ).to match(/policy:\s+"\${local.policy_statement}"/)
      end
    end

  end
end
