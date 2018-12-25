#!/usr/bin/env python

import boto3
import pytest
import json

class TestAddingUserToGroup():

    # Captured Terraform output
    tf_output_file = 'examples/add_user_to_existing_group/add_user_to_existing_group.json'     
    
    with open(tf_output_file) as tf_output_file_obj:
        tf_output = json.load(tf_output_file_obj) 

    expected_user_name = tf_output['user']['value']
    expected_group_name = tf_output['group']['value']

    # IAM client
    iam_client = boto3.client('iam')

    ### Tests ----------------------------------------------------------------

    def test_user_exists(self):
        actual_user = self.iam_client.get_user( UserName = self.expected_user_name )

        assert actual_user is not None

    def test_group_exists(self):
        actual_group = self.iam_client.get_group( GroupName = self.expected_group_name )

        assert actual_group is not None

    def test_group_membership(self):
        groups = self.iam_client.list_groups_for_user( UserName = self.expected_user_name )

        user_has_group_membership = False

        for group in groups['Groups']:
            if( group['GroupName'] == self.expected_group_name ):
                user_has_group_membership = True
                break

        assert user_has_group_membership
