require 'spec_helper'

# Verify terraform plan works
describe 'example_cf_origin_bucket_with_policy_spec' do
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

  context "example_cf_origin_bucket_with_policy_spec" do
    before (:all) do
      FileUtils.rm_rf Dir.glob("#{Dir.pwd}/spec/fixtures" )
      FileUtils.mkdir_p "#{Dir.pwd}/spec/fixtures"
      FileUtils.cp_r(Dir["#{Dir.pwd}/examples/cf_origin_bucket_with_policy/*"],"#{Dir.pwd}/spec/fixtures", )
    end

    it 'Verify terraform init is successful' do
      expect { terraform_init }.to output(/Terraform has been successfully initialized!/).to_stdout_from_any_process
    end

    it 'Verify terraform plan count is successful' do
      expect { terraform_plan }.to output(/Plan: 7 to add, 0 to change, 0 to destroy./).to_stdout_from_any_process
    end

    describe 'Verify terraform plan for example cf_origin_bucket_with_policy' do
      before (:all) do
        terraform_plan_output
        @output = terraform_show
      end

      it 'Verify terraform plan for example cf_origin_bucket_with_policy is successful' do
        expect( @output ).to match(/\+ aws_cloudfront_origin_access_identity.for_static_content/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/caller_reference:\s+<computed>/)
        expect( @output ).to match(/cloudfront_access_identity_path:\s+<computed>/)
        expect( @output ).to match(/comment:\s+"for S3 static content examaple"/)
        expect( @output ).to match(/etag:\s+<computed>/)
        expect( @output ).to match(/iam_arn:\s+<computed>/)
        expect( @output ).to match(/s3_canonical_user_id:\s+<computed>/)

        expect( @output ).to match(/\<\= module.cf-origin-bucket-with-policy.data.aws_iam_policy_document.bucket_enforce_cf_origin_only_access/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/json:\s+<computed>/)
        expect( @output ).to match(/statement.#:\s+"2"/)
        expect( @output ).to match(/statement.0.actions.#:\s+"1"/)
        expect( @output ).to match(/statement.0.actions.\d+:\s+"s3:GetObject"/)
        expect( @output ).to match(/statement.0.effect:\s+"Allow"/)
        expect( @output ).to match(/statement.0.principals.#:\s+"1"/)
        expect( @output ).to match(/statement.0.principals.\~\d+.identifiers.#:\s+<computed>/)
        expect( @output ).to match(/statement.0.principals.\~\d+.type:\s+"AWS"/)
        expect( @output ).to match(/statement.0.resources.#:\s+"1"/)
        expect( @output ).to match(/statement.0.resources.\d+:\s+"arn:aws:s3:::\d+-cf-origin-bucket-example\/\*"/)
        expect( @output ).to match(/statement.1.actions.#:\s+"1"/)
        expect( @output ).to match(/statement.1.actions.\d+:\s+"s3:ListBucket"/)
        expect( @output ).to match(/statement.1.effect:\s+"Allow"/)
        expect( @output ).to match(/statement.1.principals.#:\s+"1"/)
        expect( @output ).to match(/statement.1.principals.\~\d+.identifiers.#:\s+<computed>/)
        expect( @output ).to match(/statement.1.principals.\~\d+.type:\s+"AWS"/)
        expect( @output ).to match(/statement.1.resources.#:\s+"1"/)
        expect( @output ).to match(/statement.1.resources.\d+:\s+"arn:aws:s3:::\d+-cf-origin-bucket-example"/)

        expect( @output ).to match(/\+ module.cf-origin-bucket-with-policy.aws_s3_bucket.bucket/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/acceleration_status:\s+<computed>/)
        expect( @output ).to match(/acl:\s+"private"/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\d+-cf-origin-bucket-example"/)
        expect( @output ).to match(/bucket_domain_name:\s+<computed>/)
        expect( @output ).to match(/force_destroy:\s+"false"/)
        expect( @output ).to match(/hosted_zone_id:\s+<computed>/)
        expect( @output ).to match(/region:\s+<computed>/)
        expect( @output ).to match(/request_payer:\s+<computed>/)
        expect( @output ).to match(/server_side_encryption_configuration.0.rule.0.apply_server_side_encryption_by_default.0.sse_algorithm:\s+"AES256"/)
        expect( @output ).to match(/tags.%:\s+"5"/)
        expect( @output ).to match(/cors_rule.0.allowed_headers.0:\s+"*"/)
        expect( @output ).to match(/cors_rule.0.allowed_methods.0:\s+"POST"/)
        expect( @output ).to match(/cors_rule.0.allowed_origins.0:\s+"*"/)
        expect( @output ).to match(/cors_rule.0.expose_headers.0:\s+"ETag"/)
        expect( @output ).to match(/cors_rule.0.max_age_seconds:\s+"3000"/)
        expect( @output ).to match(/tags.Name:\s+"cf-origin-bucket-with-policy-and-role"/)
        expect( @output ).to match(/tags.global_tag:\s+"example"/)
        expect( @output ).to match(/tags.s3_encryption_type:\s+"AES256"/)
        expect( @output ).to match(/tags.s3_feature_flags:\s+"cf-origin"/)
        expect( @output ).to match(/versioning.#:\s+"1"/)
        expect( @output ).to match(/versioning.0.enabled:\s+"true"/)
        expect( @output ).to match(/versioning.0.mfa_delete:\s+"false"/)
        expect( @output ).to match(/website_domain:\s+<computed>/)
        expect( @output ).to match(/website_endpoint:\s+<computed>/)

        expect( @output ).to match(/\+ module.cf-origin-bucket-with-policy.aws_s3_bucket_policy.bucket_policy/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${aws_s3_bucket.bucket.id}"/)
        expect( @output ).to match(/policy:\s+"\${local.policy_statement}"/)

        expect( @output ).to match(/\+ module.log_bucket.aws_s3_bucket.bucket/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/acceleration_status:\s+<computed>/)
        expect( @output ).to match(/acl:\s+"log-delivery-write"/)
        expect( @output ).to match(/arn:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${local.name}"/)
        expect( @output ).to match(/bucket_domain_name:\s+<computed>/)
        expect( @output ).to match(/force_destroy:\s+"false"/)
        expect( @output ).to match(/hosted_zone_id:\s+<computed>/)
        expect( @output ).to match(/region:\s+<computed>/)
        expect( @output ).to match(/request_payer:\s+<computed>/)
        expect( @output ).to match(/server_side_encryption_configuration.#:\s+"1"/)
        expect( @output ).to match(/server_side_encryption_configuration.0.rule.0.apply_server_side_encryption_by_default.#:\s+"1"/)
        expect( @output ).to match(/server_side_encryption_configuration.0.rule.0.apply_server_side_encryption_by_default.0.sse_algorithm:\s+"AES256"/)
        expect( @output ).to match(/tags.%:\s+"5"/)
        expect( @output ).to match(/tags.Name:\s+"cf-origin-bucket-with-policy-and-role"/)
        expect( @output ).to match(/tags.cc-eac_aws_s3:\s+"v2.0.0"/)
        expect( @output ).to match(/tags.global_tag:\s+"example"/)
        expect( @output ).to match(/tags.s3_encryption_type:\s+"AES256"/)
        expect( @output ).to match(/tags.s3_feature_flags:\s+"simple"/)
        expect( @output ).to match(/versioning.#:\s+"1"/)
        expect( @output ).to match(/versioning.0.enabled:\s+"false"/)
        expect( @output ).to match(/versioning.0.mfa_delete:\s+"false"/)
        expect( @output ).to match(/website_domain:\s+<computed>/)
        expect( @output ).to match(/website_endpoint:\s+<computed>/)

        expect( @output ).to match(/\+ module.log_bucket.aws_s3_bucket_policy.bucket_policy/)
        expect( @output ).to match(/id:\s+<computed>/)
        expect( @output ).to match(/bucket:\s+"\${aws_s3_bucket.bucket.id}"/)
        expect( @output ).to match(/policy:\s+"\${local.policy_statement}"/)

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
      end
    end

  end
end
