#!/usr/bin/env python

import boto3
import pytest
import json

class TestRoleCreationWithFederatedPrincipals():
    """ Makes use of Pytest to test the creation of a role with an
    attached policy to allow a federated principal to assume that role and
    also the managed policies assigned to that role.
    """

    # Captured Terraform output
    tf_output_file = 'examples/create_role_with_federated_principal/create_role_with_federated_principal.json'     
    
    with open(tf_output_file) as tf_output_file_obj:
        tf_output = json.load(tf_output_file_obj) 

    # Extract expected values
    expected_role = tf_output['federated_principal_role']['value']
    expected_principal = tf_output['federated_principal']['value']
    expected_policy_arns = tf_output['managed_policies']['value']

    # IAM client
    iam_client = boto3.client('iam')

    ### Tests ----------------------------------------------------------------

    def test_role_exists(self):

        actual_role = self.iam_client.get_role( RoleName = self.expected_role )

        assert actual_role is not None
    
    
    def test_principal_exists(self):

        actual_role = self.iam_client.get_role( RoleName = self.expected_role )

        assert actual_role is not None

        actual_principal = actual_role['Role']['AssumeRolePolicyDocument']['Statement'][0]['Principal']['Federated']
            
        assert actual_principal is not None

        assert actual_principal in self.expected_principal

    
    def test_policies_exist(self):

        actual_policies = self.iam_client.list_attached_role_policies( RoleName = self.expected_role )

        assert len(actual_policies['AttachedPolicies']) == len(self.expected_policy_arns)

        for actual_policy in actual_policies['AttachedPolicies']:
            assert actual_policy['PolicyArn'] in self.expected_policy_arns
