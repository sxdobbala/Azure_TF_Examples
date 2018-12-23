#!/usr/bin/python
import boto3
import pytest 
import json 
import enable_support
from botocore.client import ClientError

# Tests to make sure the enable_support.py works populating the case fields
class TestEnableSupport():

    # File containing the aws account id
    accountFile = open('tests/launchpad/account.txt', 'r')

    # Account variables
    account = accountFile.read()

    # Test to confirm fields were populated correctly
    def test_support_case_fields(self):
        # Get response from the script
        caseId, subject, body = enable_support.enable(['--account', self.account, '--test'])
        
        # No caseId should be returned
        assert(caseId == "")
        # Ensure Subject is correct
        assert(subject == "Enterprise Support on Account " + self.account)
        # Ensure Communication Body is correct
        assert(body == "Please enable Enterprise Support on Account Number: " + self.account)

            