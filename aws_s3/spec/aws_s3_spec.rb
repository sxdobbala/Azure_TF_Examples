require_relative './spec_helper'
require "commercial_cloud/test/terraform"
require "commercial_cloud/test/matcher/terraform"

include CommercialCloud::Test

# Verify terraform plan works
describe 'aws_s3' do

  # Setup terraform client
  before(:all) do
    @tf = Terraform.new(
      default_target_dir: "#{__dir__}/fixtures"
    )
  end

  # Helper method to plan modules
  # # from within the `/modules` directory
  def terraform_plan(module_name)
    hcl = %{
      provider "aws" {
        region = "us-east-1"
      }
      provider "aws" {
        region = "us-west-1",
        alias      = "replication"
      }
    }
    @tf.plan(
      hcl: hcl, 
      files: ["#{__dir__}/../modules/#{module_name}/*"],
    )
  end

  describe "simple module" do

    it 'should plan resources' do
      expect(terraform_plan "simple")
        .to be_terraform_plan
          .to_add(2)
          .to_change(0)
          .to_destroy(0)
          .with_resources({
            "aws_s3_bucket.bucket" => {
              "id" => /<computed>/,
              "acceleration_status" => /<computed>/,
              "acl" => /"private"/,
              "arn" => /<computed>/,
              "bucket" => /"[0-9]{12}-default"/,
              "bucket_domain_name" => /<computed>/,
              "force_destroy" => /"false"/,
              "hosted_zone_id" => /<computed>/,
              "region" => /<computed>/,
              "request_payer" => /<computed>/,
              "tags.%" => /"3"/,
              "tags.s3_encryption_type" => /"AES256"/,
              "tags.s3_feature_flags" => /"simple"/,
              "versioning.#" => /"1"/,
              "versioning.0.enabled" => /"false"/,
              "versioning.0.mfa_delete" => /"false"/,
              "website_domain" => /<computed>/,
              "website_endpoint" => /<computed>/,
            },
            "aws_s3_bucket_policy.bucket_policy" => {
              "id" => /<computed>/,
              "bucket" => /"\${aws_s3_bucket.bucket.id}"/,
              "policy" => /"{\\n  \\"Version\\": \\"2012-10-17\\",\\n  \\"Statement\\": \[\\n  {\\n      \\"Sid\\": \\"DenyInsecureCommunications\\",\\n      \\"Effect\\": \\"Deny\\",\\n      \\"Action\\": \\"s3:\*\\",\\n      \\"Resource\\": \\"arn:aws:s3:::[0-9]{12}-default\/\*\\",\\n      \\"Principal\\": \\"\*\\",\\n      \\"Condition\\": {\\n        \\"Bool\\": {\\n          \\"aws:SecureTransport\\": \\"false\\"\\n        }\\n      }\\n    }\\n  \]\\n}\\n"/,
            }
          })
    end

    describe "bucket name" do

      def test_bucket_name(hcl_args, expected_name)
        hcl = %{
          provider "aws" {
            region     = "us-east-1"
            version    = "1.9.0"
          }

          module "test" {
            source = "#{__dir__}/../modules/simple"
            #{hcl_args}
          }
        }


        expect(@tf.plan(hcl: hcl))
          .to be_terraform_plan
            .to_add(2)
            .to_change(0)
            .to_destroy(0)
            .with_resources({
              "module.test.aws_s3_bucket.bucket" => {
                "bucket" => expected_name
              }
            })
      end

      it "[name]" do
        test_bucket_name(
          %{
            name = "my-bucket"
          },
          /"my-bucket"/
        )
      end

      it "[name]-[namespace]" do
        test_bucket_name(
          %{
            name = "my-bucket"
            namespace = "test"
          },
          /"my-bucket-test"/
        )
      end
      
      it "[name_prefix]-[name_suffix]" do
        test_bucket_name(
          %{
            name_prefix = "my-bucket"
            name_suffix = "foo"
          },
          /"my-bucket-foo"/
        )
      end
      
      it "[name_prefix]-[name_suffix]-[namespace]" do
        test_bucket_name(
          %{
            name_prefix = "my-bucket"
            name_suffix = "foo"
            namespace = "test"
          },
          /"my-bucket-foo-test"/
        )
      end
      
      it "[name_prefix]-default" do
        test_bucket_name(
          %{
            name_prefix = "my-bucket"
          },
          /"my-bucket-default"/
        )
      end
      
      it "[name_prefix]-default-[namespace]" do
        test_bucket_name(
          %{
            name_prefix = "my-bucket"
            namespace = "test"
          },
          /"my-bucket-default-test"/
        )
      end
      
      it "[account_id]-[name_suffix]" do
        test_bucket_name(
          %{
            name_suffix = "my-bucket"
          },
          /"[0-9]{12}-my-bucket"/
        )
      end

    end

    # See https://github.optum.com/CommercialCloud-EAC/aws_s3/issues/46
    it "should allow interpolated values in `name_suffix`" do
      hcl = %{
        provider "aws" {
          region     = "us-east-1"
          version    = "1.9.0"
        }
        module "my_bucket" {
          source = "#{__dir__}/../modules/simple"
          name_suffix = "test-${random_pet.bucket_name.id}"
        }

        resource "random_pet" "bucket_name" {}
      }

      # We're just checking that the plan didn't fail
      plan = @tf.plan(hcl: hcl)
      expect(plan).to match(/Terraform will perform the following actions/)
    end

  end

  describe "log module" do
    
    it "should plan resources" do
      expect(terraform_plan "log")
        .to be_terraform_plan
        .to_add(3)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
          "aws_s3_bucket.bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"private"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "logging.#" => /"1"/,
            "/logging.~[0-9]{10}.target_bucket/" => /"\${aws_s3_bucket.log_bucket.id}"/,
            "/logging.~[0-9]{10}.target_prefix/" => /"logs\/"/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_encryption_type" => /"AES256"/,
            "tags.s3_feature_flags" => /"logging"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"false"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket.log_bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"log-delivery-write"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default-logs"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_feature_flags" => /"logging"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"false"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket_policy.bucket_policy" => {
            "id" => /<computed>/,
            "bucket" => /"\${aws_s3_bucket.bucket.id}"/,
            "policy" => /"{\\n  \\"Version\\": \\"2012-10-17\\",\\n  \\"Statement\\": \[\\n  {\\n      \\"Sid\\": \\"DenyInsecureCommunications\\",\\n      \\"Effect\\": \\"Deny\\",\\n      \\"Action\\": \\"s3:\*\\",\\n      \\"Resource\\": \\"arn:aws:s3:::[0-9]{12}-default\/\*\\",\\n      \\"Principal\\": \\"\*\\",\\n      \\"Condition\\": {\\n        \\"Bool\\": {\\n          \\"aws:SecureTransport\\": \\"false\\"\\n        }\\n      }\\n    }\\n  \]\\n}\\n"/,
          }
        })
    end

  end

  describe "tflife module" do

    it "should plan resources" do
      expect(terraform_plan "tflife")
        .to be_terraform_plan
        .to_add(2)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
          "aws_s3_bucket.bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"private"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_encryption_type" => /"AES256"/,
            "tags.s3_feature_flags" => /"tflife"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"false"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket_policy.bucket_policy" => {
            "id" => /<computed>/,
            "bucket" => /"\${aws_s3_bucket.bucket.id}"/,
            "policy" => /"{\\n  \\"Version\\": \\"2012-10-17\\",\\n  \\"Statement\\": \[\\n  {\\n      \\"Sid\\": \\"DenyInsecureCommunications\\",\\n      \\"Effect\\": \\"Deny\\",\\n      \\"Action\\": \\"s3:\*\\",\\n      \\"Resource\\": \\"arn:aws:s3:::[0-9]{12}-default\/\*\\",\\n      \\"Principal\\": \\"\*\\",\\n      \\"Condition\\": {\\n        \\"Bool\\": {\\n          \\"aws:SecureTransport\\": \\"false\\"\\n        }\\n      }\\n    }\\n  \]\\n}\\n"/,
          }
        })
    end

  end
  
  describe "tflife-log module" do
    
    it "should plan resources" do
      expect(terraform_plan "tflife-log")
        .to be_terraform_plan
        .to_add(3)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
          "aws_s3_bucket.bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"private"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "logging.#" => /"1"/,
            "/logging.~[0-9]{10}.target_bucket/" => /"\${aws_s3_bucket.log_bucket.id}"/,
            "/logging.~[0-9]{10}.target_prefix/" => /"logs\/"/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_encryption_type" => /"AES256"/,
            "tags.s3_feature_flags" => /"tflife:logging"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"false"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket.log_bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"log-delivery-write"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default-logs"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_feature_flags" => /"tflife:logging"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"false"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket_policy.bucket_policy" => {
            "id" => /<computed>/,
            "bucket" => /"\${aws_s3_bucket.bucket.id}"/,
            "policy" => /"{\\n  \\"Version\\": \\"2012-10-17\\",\\n  \\"Statement\\": \[\\n  {\\n      \\"Sid\\": \\"DenyInsecureCommunications\\",\\n      \\"Effect\\": \\"Deny\\",\\n      \\"Action\\": \\"s3:\*\\",\\n      \\"Resource\\": \\"arn:aws:s3:::[0-9]{12}-default\/\*\\",\\n      \\"Principal\\": \\"\*\\",\\n      \\"Condition\\": {\\n        \\"Bool\\": {\\n          \\"aws:SecureTransport\\": \\"false\\"\\n        }\\n      }\\n    }\\n  \]\\n}\\n"/,
          }
        })
    end
    

  end

  describe "rep module" do
    
    it "should plan resources" do
      expect(terraform_plan "rep")
        .to be_terraform_plan
        .to_add(6)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
          "aws_s3_bucket.bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"private"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "/replication_configuration.0.rules.~[0-9]{6,10}.destination.~[0-9]{10}.bucket/" => /"\${aws_s3_bucket.rep_bucket.arn}"/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_encryption_type" => /"AES256"/,
            "tags.s3_feature_flags" => /"rep"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"true"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket.rep_bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"private"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default-replication"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_encryption_type" => /"AES256"/,
            "tags.s3_feature_flags" => /"rep"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"true"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket_policy.bucket_policy" => {
            "id" => /<computed>/,
            "bucket" => /"\${aws_s3_bucket.bucket.id}"/,
            "policy" => /"{\\n  \\"Version\\": \\"2012-10-17\\",\\n  \\"Statement\\": \[\\n  {\\n      \\"Sid\\": \\"DenyInsecureCommunications\\",\\n      \\"Effect\\": \\"Deny\\",\\n      \\"Action\\": \\"s3:\*\\",\\n      \\"Resource\\": \\"arn:aws:s3:::[0-9]{12}-default\/\*\\",\\n      \\"Principal\\": \\"\*\\",\\n      \\"Condition\\": {\\n        \\"Bool\\": {\\n          \\"aws:SecureTransport\\": \\"false\\"\\n        }\\n      }\\n    }\\n  \]\\n}\\n"/,
          }
        })
    end
    

  end
  

  describe "rep-log module" do
    
    it "should plan resources" do
      expect(terraform_plan "rep-log")
        .to be_terraform_plan
        .to_add(7)
        .to_change(0)
        .to_destroy(0)
        .with_resources({
          "aws_s3_bucket.bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"private"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "logging.#" => /"1"/,
            "/logging.~[0-9]{10}.target_bucket/" => /"\${aws_s3_bucket.log_bucket.id}"/,
            "/logging.~[0-9]{10}.target_prefix/" => /"logs\/"/,
            "/replication_configuration.0.rules.~[0-9]{6,10}.destination.~[0-9]{10}.bucket/" => /"\${aws_s3_bucket.rep_bucket.arn}"/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_encryption_type" => /"AES256"/,
            "tags.s3_feature_flags" => /"rep:logging"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"true"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket.log_bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"log-delivery-write"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default-logs"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_feature_flags" => /"rep:logging"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"false"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket.rep_bucket" => {
            "id" => /<computed>/,
            "acceleration_status" => /<computed>/,
            "acl" => /"private"/,
            "arn" => /<computed>/,
            "bucket" => /"[0-9]{12}-default-replication"/,
            "bucket_domain_name" => /<computed>/,
            "force_destroy" => /"false"/,
            "hosted_zone_id" => /<computed>/,
            "region" => /<computed>/,
            "request_payer" => /<computed>/,
            "tags.%" => /"3"/,
            "tags.s3_encryption_type" => /"AES256"/,
            "tags.s3_feature_flags" => /"rep:logging"/,
            "versioning.#" => /"1"/,
            "versioning.0.enabled" => /"true"/,
            "versioning.0.mfa_delete" => /"false"/,
            "website_domain" => /<computed>/,
            "website_endpoint" => /<computed>/
          },
          "aws_s3_bucket_policy.bucket_policy" => {
            "id" => /<computed>/,
            "bucket" => /"\${aws_s3_bucket.bucket.id}"/,
            "policy" => /"{\\n  \\"Version\\": \\"2012-10-17\\",\\n  \\"Statement\\": \[\\n  {\\n      \\"Sid\\": \\"DenyInsecureCommunications\\",\\n      \\"Effect\\": \\"Deny\\",\\n      \\"Action\\": \\"s3:\*\\",\\n      \\"Resource\\": \\"arn:aws:s3:::[0-9]{12}-default\/\*\\",\\n      \\"Principal\\": \\"\*\\",\\n      \\"Condition\\": {\\n        \\"Bool\\": {\\n          \\"aws:SecureTransport\\": \\"false\\"\\n        }\\n      }\\n    }\\n  \]\\n}\\n"/,
          }
        })
    end
    

  end

end
