#!/usr/bin/python
import boto3
import pytest 
import json 
import time

class TestKMSKeyWithAlias():
    """ Pytest Class to test the create_kms_key_with_alias example, verifying the
    creation of the KMS Key and its respective Alias
    """

    # Read terraform output json file 
    terraformOutputJsonFile = 'tests/launchpad/launchpad_output.json'
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 

    # Extract outputs
    expectedId = tfOutputData['kms_id']['value']
    expectedArn = tfOutputData['kms_arn']['value']
    expectedAliasName = tfOutputData['kms_alias_name']['value']
    expectedAliasArn = tfOutputData['kms_alias_arn']['value']

    # Clients
    kmsClient = boto3.client('kms')

    def test_kms_key_properties(self):
        """ Test function to verify that the KMS Key created exists 
        and its fields/status.
        """

        # Check existence of key
        response = self.kmsClient.describe_key(KeyId = self.expectedId)
        assert response is not None

        # Verify the response
        metadata = response['KeyMetadata']
        assert metadata['KeyId'] == self.expectedId
        assert metadata['Arn'] == self.expectedArn
        assert metadata['Enabled'] == True
        assert metadata['KeyUsage'] == 'ENCRYPT_DECRYPT'
        assert metadata['KeyState'] == 'Enabled'
        assert metadata['Origin'] == 'AWS_KMS'
    
    def test_kms_key_alias(self):
        """ Test function to verify that the KMS Key Alias created exists 
        and matches its respective KMS Key.
        """

        # Check if the alias exists
        response = self.kmsClient.list_aliases()
        existCheck = False
        for alias in response['Aliases']:
            if alias['AliasName'] == self.expectedAliasName:
                existCheck = True
        assert existCheck

        # Verify the alias properties
        responseOriginal = self.kmsClient.describe_key(KeyId = self.expectedId)
        responseAlias = self.kmsClient.describe_key(KeyId = self.expectedAliasName)
        assert responseOriginal is not None
        assert responseAlias is not None
        metadataOriginal = responseOriginal['KeyMetadata']
        metadataAlias = responseAlias['KeyMetadata']
        assert metadataOriginal == metadataAlias