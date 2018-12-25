#!/usr/bin/python
import boto3
import pytest 
import json 
import time

class TestCFOriginBucketWithPolicy():
    """ Pytest Class to test the cf_origin_bucket_with_policy example, 
    verifying the creation of all of the S3 Bucket resources.
    """

    # Read terraform output json file 
    terraformOutputJsonFile = 'examples/cf_origin_bucket_with_policy/cf_origin_bucket_with_policy.json'
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 

    # Extract outputs
    expectedCFBucketId = tfOutputData['id']['value']
    expectedCFBucketArn = tfOutputData['arn']['value']
    expectedLogBucketId = tfOutputData['log_bucket_id']['value']
    expectedLogBucketArn = tfOutputData['log_bucket_arn']['value']
    expectedDomainName = tfOutputData['domain_name']['value']

    # Clients
    s3Client = boto3.client('s3')

    def test_s3_cf_bucket_properties(self):
        """ Test function to verify that the S3 CF Bucket created exists 
        and its properties are correct.
        """

        # Check existence of bucket
        response = self.s3Client.get_bucket_encryption(Bucket=self.expectedCFBucketId)
        assert response is not None

        # Check bucket's properties
        assert response['ServerSideEncryptionConfiguration']['Rules'][0]['ApplyServerSideEncryptionByDefault']['SSEAlgorithm'] \
            == 'AES256'
        response = self.s3Client.get_bucket_versioning(Bucket=self.expectedCFBucketId)
        assert response['Status'] == 'Enabled'
        response = self.s3Client.get_bucket_cors(Bucket=self.expectedCFBucketId)
        assert response['CORSRules'][0]['AllowedHeaders'] == ["*"]
        assert response['CORSRules'][0]['AllowedMethods'] == ["POST"]
        assert response['CORSRules'][0]['AllowedOrigins'] == ["*"]
        assert response['CORSRules'][0]['ExposeHeaders'] == ["ETag"]
        assert response['CORSRules'][0]['MaxAgeSeconds'] == 3000

        # Clear contents of bucket (terraform can only destroy an empty bucket)
        self.delete_bucket_contents(self.expectedCFBucketId)

    def test_s3_log_bucket_properties(self):
        """ Test function to verify that the S3 Log Bucket created exists 
        and its properties are correct.
        """

        # Check existence of bucket
        response = self.s3Client.get_bucket_encryption(Bucket=self.expectedLogBucketId)
        assert response is not None

        # Check bucket's properties
        assert response['ServerSideEncryptionConfiguration']['Rules'][0]['ApplyServerSideEncryptionByDefault']['SSEAlgorithm'] \
            == 'AES256'
        response = self.s3Client.get_bucket_versioning(Bucket=self.expectedLogBucketId)
        assert response['Status'] == 'Suspended'

        # Check that this bucket is the target logging bucket
        response = self.s3Client.get_bucket_logging(Bucket=self.expectedCFBucketId)
        assert response['LoggingEnabled']['TargetBucket'] == self.expectedLogBucketId

        # Clear contents of bucket (terraform can only destroy an empty bucket)
        self.delete_bucket_contents(self.expectedLogBucketId)

    def delete_bucket_contents(self, bucketId):
        """ Helper function to clear the contents/objects of a bucket. 
        Terraform destroy can only be done on an empty bucket
        """
        
        # Get the s3 bucket resource
        s3Resource = boto3.resource('s3')
        bucket = s3Resource.Bucket(bucketId)

        # Remove all objects within the bucket
        bucket.object_versions.all().delete()