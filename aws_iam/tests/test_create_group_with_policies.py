#!/usr/bin/env python

import boto3
import pytest
import json

class TestCreatingGroupWithPolicies():

    # Captured Terraform output
    tf_output_file = 'examples/create_group_with_policies/create_group_with_policies.json'     
    
    with open(tf_output_file) as tf_output_file_obj:
        tf_output = json.load(tf_output_file_obj) 

    group_name = tf_output['group']['value']
    
    expected_inline_policies = tf_output['inline_policies']['value']
   
    expected_managed_policy_arns = tf_output['managed_policies']['value']

    # IAM client
    iam_client = boto3.client('iam')

    ### Tests ----------------------------------------------------------------

    def test_group_exists(self):
        group = self.iam_client.get_group( GroupName = self.group_name )

        assert group is not None

    def test_inline_policies_exist(self):
        num_expected_inline_policies = 0

        for expected_inline_policy in self.expected_inline_policies:
            actual_inline_policy = self.iam_client.get_group_policy(GroupName=self.group_name, PolicyName=expected_inline_policy['group_custom_inline_name'])
            expected_inline_policy_doc = json.loads(expected_inline_policy['custom_inline_policy'])
            assert actual_inline_policy['PolicyDocument']['Version'] == expected_inline_policy_doc['Version']
            num_expected_inline_policies += 1

        num_actual_inline_policies = len(self.iam_client.list_group_policies( GroupName = self.group_name )['PolicyNames'])
        assert num_expected_inline_policies == num_actual_inline_policies

    def test_managed_policies_exist(self):
        actual_managed_policies = self.iam_client.list_attached_group_policies( GroupName = self.group_name )['AttachedPolicies']
        
        actual_managed_policy_arns = []
        for actual_managed_policy in actual_managed_policies:
            actual_managed_policy_arn = actual_managed_policy['PolicyArn']
            assert actual_managed_policy_arn in self.expected_managed_policy_arns
            actual_managed_policy_arns.append(actual_managed_policy_arn)

        assert len(actual_managed_policy_arns) == len(self.expected_managed_policy_arns)
