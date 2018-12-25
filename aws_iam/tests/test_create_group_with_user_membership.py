#!/usr/bin/env python

import boto3
import pytest
import json

class TestCreateGroupWithUserMembership():
    """Tests users' group membership. Note that group policies are tested by
    test_create_group_with_policies.py and hence there is no need repeat them
    here.
    
    """
    
    # Captured Terraform output
    tf_output_file = 'examples/create_group_with_user_membership/create_group_with_user_membership.json'     
    
    with open(tf_output_file) as tf_output_file_obj:
        tf_output = json.load(tf_output_file_obj) 

    expected_user1_name = tf_output['user1']['value']
    expected_user2_name = tf_output['user2']['value']
    expected_group_name = tf_output['group']['value']

    # IAM client
    iam_client = boto3.client('iam')

    ### Tests ----------------------------------------------------------------

    def test_user1_exists(self):
        actual_user1 = self.iam_client.get_user( UserName = self.expected_user1_name )

        assert actual_user1 is not None

    def test_user2_exists(self):
        actual_user2 = self.iam_client.get_user( UserName = self.expected_user2_name )

        assert actual_user2 is not None

    def test_group_exists(self):
        actual_group = self.iam_client.get_group( GroupName = self.expected_group_name )

        assert actual_group is not None

    def test_group_membership(self):
        assert self.is_user_member_of_expected_group( 
            self.iam_client.list_groups_for_user( UserName = self.expected_user1_name )
         )

        assert self.is_user_member_of_expected_group( 
            self.iam_client.list_groups_for_user( UserName = self.expected_user2_name )
         )

    def is_user_member_of_expected_group(self, groups):
        user_has_group_membership = False

        for group in groups['Groups']:
            if( group['GroupName'] == self.expected_group_name ):
                user_has_group_membership = True
                break

        return user_has_group_membership
