#!/usr/bin/env python

import boto3
import pytest
import json

class TestRoleCreationWithServicePrincipals():

    # Captured Terraform output
    tf_output_file = 'examples/create_role_with_service_principal/create_role_with_service_principal.json'     
    
    with open(tf_output_file) as tf_output_file_obj:
        tf_output = json.load(tf_output_file_obj) 

    # Extract expected values
    expected_role = tf_output['role']['value']
    expected_principal = tf_output['service_principal']['value']

    # IAM client
    iam_client = boto3.client('iam')

    ### Tests ----------------------------------------------------------------

    def test_role_exists(self):
        actual_role = self.iam_client.get_role( RoleName = self.expected_role )

        assert actual_role is not None
    
    
    def test_principal_exists(self):
        actual_role = self.iam_client.get_role( RoleName = self.expected_role )

        assert actual_role is not None

        actual_principal = actual_role['Role']['AssumeRolePolicyDocument']['Statement'][0]['Principal']['Service']

        assert actual_principal is not None

        assert actual_principal in self.expected_principal
