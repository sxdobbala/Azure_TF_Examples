#!/usr/bin/env python

import boto3
import pytest
import json

class TestCreatingRoleWithManagedPolicies():

    # Captured Terraform output
    tf_output_file = 'examples/create_role_with_managed_policy/create_role_with_managed_policy.json'     
    
    with open(tf_output_file) as tf_output_file_obj:
        tf_output = json.load(tf_output_file_obj) 

    expected_role_name = tf_output['role']['value'] 
    expected_managed_policy_arns = tf_output['managed_policies']['value']
     
    # IAM client
    iam_client = boto3.client('iam')

    ### Tests ----------------------------------------------------------------

    def test_role_exists(self):
        role = self.iam_client.get_role( RoleName = self.expected_role_name )

        assert role is not None

    def test_managed_policies_exist(self):
        actual_managed_policies = self.iam_client.list_attached_role_policies( RoleName = self.expected_role_name )

        assert len(actual_managed_policies['AttachedPolicies']) == len(self.expected_managed_policy_arns)

        for actual_managed_policy in actual_managed_policies['AttachedPolicies']:
            assert actual_managed_policy['PolicyArn'] in self.expected_managed_policy_arns
