#!/usr/bin/env python

import boto3
import pytest
import json
import os

class TestSecurityTeamAccess():

    # Captured Terraform output
    tf_output_file = 'tests/launchpad/launchpad_output.json'     
    
    with open(tf_output_file) as tf_output_file_obj:
        tf_output = json.load(tf_output_file_obj) 

    account_type = "member"

    # Extract expected values
    expected_ro_role = tf_output[account_type + '_security_ro_role']['value']
    expected_ro_policies = tf_output[account_type + '_security_ro_managed_role_policies']['value']

    expected_bg_role = tf_output[account_type + '_security_bg_role']['value']
    expected_bg_policies = tf_output[account_type + '_security_bg_managed_role_policies']['value']

    # IAM client
    iam_client = boto3.client('iam')

    ### EIS read only access role tests --------------------------------------

    def test_readonly_role_exists(self):

        role = self.iam_client.get_role( RoleName = self.expected_ro_role )

        assert role is not None
    

    def test_readonly_role_principal_exists(self):

        role = self.iam_client.get_role( RoleName = self.expected_ro_role )

        assert role is not None

        principal_type = self.__class__.get_principal_type()

        principal = role['Role']['AssumeRolePolicyDocument']['Statement'][0]['Principal'][principal_type]
 
        assert principal is not None


    def test_readonly_role_policies_exist(self):

        policies = self.iam_client.list_attached_role_policies( RoleName = self.expected_ro_role )

        for policy in policies['AttachedPolicies']:
            assert policy['PolicyArn'] in self.expected_ro_policies


    ### EIS breakglass access role tests -------------------------------------

    def test_breakglass_role_exists(self):

        role = self.iam_client.get_role( RoleName = self.expected_bg_role )

        assert role is not None


    def test_breakglass_role_principal_exists(self):

        role = self.iam_client.get_role( RoleName = self.expected_bg_role )

        assert role is not None

        principal_type = self.__class__.get_principal_type()

        principal = role['Role']['AssumeRolePolicyDocument']['Statement'][0]['Principal'][principal_type]

        assert principal is not None


    def test_breakglass_role_policies_exist(self):

        policies = self.iam_client.list_attached_role_policies( RoleName = self.expected_bg_role )

        for policy in policies['AttachedPolicies']:
            assert policy['PolicyArn'] in self.expected_bg_policies


    ### Utilities ------------------------------------------------------------

    @classmethod
    def get_principal_type(cls):
        if cls.account_type == 'central':
            return 'Federated'
        elif cls.account_type == 'member':
            return 'AWS'
        else:
            return None