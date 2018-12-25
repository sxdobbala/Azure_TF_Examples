#!/usr/bin/python
import boto3
import pytest 
import json 
import time

class TestBucketWithReplicationAndLogging():
    """ Pytest Class to test the bucket_with_replication_and_logging example, 
    verifying the creation of all of the S3 Bucket resources.
    """

    # Read terraform output json file 
    terraformOutputJsonFile = 'examples/bucket_with_replication_and_logging/bucket_with_replication_and_logging.json'
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 

    # Extract outputs
    expectedBucketId = tfOutputData['id']['value']
    expectedBucketArn = tfOutputData['arn']['value']
    expectedLogBucketId = tfOutputData['log_bucket_id']['value']
    expectedLogBucketArn = tfOutputData['log_bucket_arn']['value']
    expectedLogBucketKey = tfOutputData['log_bucket_key']['value']
    expectedRepBucketId = tfOutputData['rep_bucket_id']['value']
    expectedRepBucketArn = tfOutputData['rep_bucket_arn']['value']
    expectedRepBucketKey = tfOutputData['rep_bucket_key']['value']

    # Clients
    s3Client = boto3.client('s3')

    def test_s3_bucket_properties(self):
        """ Test function to verify that the S3 Bucket created exists 
        and its properties are correct.
        """

        # Check existence of bucket
        response = self.s3Client.get_bucket_encryption(Bucket=self.expectedBucketId)
        assert response is not None

        # Check bucket's properties
        assert response['ServerSideEncryptionConfiguration']['Rules'][0]['ApplyServerSideEncryptionByDefault']['SSEAlgorithm'] \
            == 'AES256'
        response = self.s3Client.get_bucket_versioning(Bucket=self.expectedBucketId)
        assert response['Status'] == 'Enabled'

        # Clear contents of bucket (terraform can only destroy an empty bucket)
        self.delete_bucket_contents(self.expectedBucketId)

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
        response = self.s3Client.get_bucket_logging(Bucket=self.expectedBucketId)
        assert response['LoggingEnabled']['TargetBucket'] == self.expectedLogBucketId

        # Clear contents of bucket (terraform can only destroy an empty bucket)
        self.delete_bucket_contents(self.expectedLogBucketId)

    def test_s3_rep_bucket_properties(self):
        """ Test function to verify that the S3 Replication Bucket created exists 
        and its properties are correct.
        """

        # Check existence of bucket
        response = self.s3Client.get_bucket_encryption(Bucket=self.expectedRepBucketId)
        assert response is not None

        # Check bucket's properties
        assert response['ServerSideEncryptionConfiguration']['Rules'][0]['ApplyServerSideEncryptionByDefault']['SSEAlgorithm'] \
            == 'AES256'
        response = self.s3Client.get_bucket_versioning(Bucket=self.expectedRepBucketId)
        assert response['Status'] == 'Enabled'
        response = self.s3Client.get_bucket_location(Bucket=self.expectedRepBucketId)
        assert response['LocationConstraint'] == 'us-west-1'

        # Check that this bucket is the target replicated bucket
        response = self.s3Client.get_bucket_replication(Bucket=self.expectedBucketId)
        assert response['ReplicationConfiguration']['Rules'][0]['Destination']['Bucket'] \
            == self.expectedRepBucketArn

        # Clear contents of bucket (terraform can only destroy an empty bucket)
        self.delete_bucket_contents(self.expectedRepBucketId)

    def delete_bucket_contents(self, bucketId):
        """ Helper function to clear the contents/objects of a bucket. 
        Terraform destroy can only be done on an empty bucket
        """
        
        # Get the s3 bucket resource
        s3Resource = boto3.resource('s3')
        bucket = s3Resource.Bucket(bucketId)

        # Remove all objects within the bucket
        bucket.object_versions.all().delete()