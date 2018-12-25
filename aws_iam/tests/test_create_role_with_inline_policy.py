#!/usr/bin/env python

import boto3
import pytest
import json

class TestCreatingRoleWithInlinePolicies():

    # Captured Terraform output
    tf_output_file = 'examples/create_role_with_inline_policy/create_role_with_inline_policy.json'     
    
    with open(tf_output_file) as tf_output_file_obj:
        tf_output = json.load(tf_output_file_obj) 

    expected_role_name = tf_output['role']['value']
    
    expected_inline_policies = tf_output['inline_policies']['value']
    expected_inline_policy_names = [policy['custom_inline_name'] for policy in expected_inline_policies]
    
    # IAM client
    iam_client = boto3.client('iam')

    ### Tests ----------------------------------------------------------------

    def test_role_exists(self):
        actual_role = self.iam_client.get_role( RoleName = self.expected_role_name )

        assert actual_role is not None

    def test_inline_policies_exist(self):
        num_expected_inline_policies = 0

        for expected_inline_policy in self.expected_inline_policies:
            actual_inline_policy = self.iam_client.get_role_policy(RoleName=self.expected_role_name, PolicyName=expected_inline_policy['custom_inline_name'])
            expected_inline_policy_doc = json.loads(expected_inline_policy['custom_inline_policy'])
            assert actual_inline_policy['PolicyDocument']['Version'] == expected_inline_policy_doc['Version']
            num_expected_inline_policies += 1

        num_actual_inline_policies = len(self.iam_client.list_role_policies(RoleName = self.expected_role_name )['PolicyNames'])
        assert num_expected_inline_policies == num_actual_inline_policies