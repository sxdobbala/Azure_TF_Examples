# title 'Unsecured S3 Bucket Validation'

# Before running this inspec test run terraform apply in the unsecured_bucket module as follows:
# terraform apply 
# Then push the output to the "files" folder in the inspec folder structure:
#   (If inspec/files doesn't exist create it first)
# terraform output --json > <PATH_TO_INSPEC>/files/unsecured_bucket.json

control 'Unsecured S3 AWS Launchpad' do
    impact 1.0
    title 'Ensure that the S3 Bucket Provided is Unsecured/Public'

    content = inspec.profile.file("unsecured_bucket.json")
    params = JSON.parse(content)
    S3_BUCKET_NAME = params['s3_bucket_name']['value']

    describe aws_s3_bucket(bucket_name: S3_BUCKET_NAME) do
        it { should exist }
        it { should be_public }
    end
end