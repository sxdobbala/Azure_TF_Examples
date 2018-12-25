#!/usr/bin/python
import boto3
import pytest 
import json 
import time
import os

class TestSimpleBucketWithoutPolicy():
    """ Pytest Class to test the simple_bucket_without_policy example, 
    verifying the creation of the S3 Bucket. 
    """

    # Read terraform output json file 
    terraformOutputJsonFile = 'examples/simple_bucket_to_storageclass/simple_bucket_to_storageclass.json'
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 

    # Extract outputs
    expectedId = tfOutputData['id']['value']
    expectedArn = tfOutputData['arn']['value']



    s3Client = boto3.client('s3')


    def test_s3_simple_properties(self):
        """ Test function to verify that the S3 Bucket created exists 
        and its properties are correct.
        """

        # Check existence of bucket
        response = self.s3Client.get_bucket_encryption(Bucket=self.expectedId)
        assert response is not None

        # Check bucket's properties
        assert response['ServerSideEncryptionConfiguration']['Rules'][0]['ApplyServerSideEncryptionByDefault']['SSEAlgorithm'] == 'AES256'
        response = self.s3Client.get_bucket_versioning(Bucket=self.expectedId)
        assert response['Status'] == 'Suspended'

        # Clear contents of bucket (terraform can only destroy an empty bucket)
        self.delete_bucket_contents(self.expectedId)

    def test_lifecycle(self):

        response = self.s3Client.get_bucket_lifecycle_configuration(Bucket=self.expectedId)
        assert response is not None
        assert response['Rules'][0]['Status'] == 'Enabled'
        assert response['Rules'][0]['Expiration']['Days'] == 365
        assert response['Rules'][0]['Transitions'][0]['Days'] == 30
        assert response['Rules'][0]['Transitions'][0]['StorageClass'] == 'INTELLIGENT_TIERING'
        assert response['Rules'][0]['Transitions'][1]['Days'] == 60
        assert response['Rules'][0]['Transitions'][1]['StorageClass'] == 'GLACIER'
        assert response['Rules'][0]['NoncurrentVersionTransitions'][0]['NoncurrentDays'] == 30
        assert response['Rules'][0]['NoncurrentVersionTransitions'][0]['StorageClass'] == 'GLACIER'


    def delete_bucket_contents(self, bucketId):
        """ Helper function to clear the contents/objects of a bucket. 
        Terraform destroy can only be done on an empty bucket
        """

        # Get the s3 bucket resource
        s3Resource = boto3.resource('s3')
        bucket = s3Resource.Bucket(bucketId)

        # Remove all objects within the bucket
        bucket.object_versions.all().delete()