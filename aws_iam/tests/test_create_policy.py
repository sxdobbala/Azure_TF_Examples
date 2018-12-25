#!/usr/bin/env python

import boto3
import pytest
import json

class TestCreatePolicy():

    # Captured Terraform output
    tf_output_file = 'examples/create_policy/create_policy.json'     
    
    with open(tf_output_file) as tf_output_file_obj:
        tf_output = json.load(tf_output_file_obj) 

    expected_policy_arn = tf_output['policy_arn']['value']
    
    # IAM client
    iam_client = boto3.client('iam')

    ### Tests ----------------------------------------------------------------

    def test_policy_exists(self):
        actual_policy = self.iam_client.get_policy( PolicyArn = self.expected_policy_arn )

        assert actual_policy is not None
