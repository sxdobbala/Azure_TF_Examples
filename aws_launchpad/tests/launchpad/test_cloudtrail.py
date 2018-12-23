#!/usr/bin/python
import boto3
import pytest 
import json 
from botocore.client import ClientError

# tests to make sure an aws account has the correct cloudtrail implemented by launchpad 
class TestCloudtrail():

    terraformOutputJsonFile = 'tests/launchpad/launchpad_output.json'

    # read terraform output json file 
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 
    
    # extract the outputs
    expectedCloudtrailId = tfOutputData['cloudtrail_id']['value']
    expectedLogGroupName = tfOutputData['cloudtrail_loggroup_name']['value']
    expectedLogGroupArn = tfOutputData['cloudtrail_loggroup_arn']['value']
    expectedSubscriptionFilterName = tfOutputData['cloudtrail_subscription_filter_name']['value']
    expectedSubscriptionFilterId = tfOutputData['cloudtrail_subscription_filter_id']['value']
    expectedKmsId = tfOutputData['kms_arn']['value']

    # boto3 clients / resources
    cloudtrailClient = boto3.client('cloudtrail')
    logsClient = boto3.client('logs')

    # test to confirm cloudtrail trail exists
    def test_cloudtrail_exist(self):
        check = False
        try: 
            self.cloudtrailClient.get_trail_status(Name = self.expectedCloudtrailId)
            check = True
        except ClientError:
            check = False
        assert check 
    def test_log_group_properties(self):
        """ Test function to verify that the CloudWatch Log Group created 
        exists and its properties are correct.
        """
        
        # Get the response of the log group
        response = self.logsClient.describe_log_groups(logGroupNamePrefix = 
            self.expectedLogGroupName)
        assert response is not None

        # Confirm that log group has the correct properties
        logGroup = response['logGroups'][0]
        assert logGroup['logGroupName'] == self.expectedLogGroupName
        assert logGroup['retentionInDays'] == 365
        assert logGroup['arn'] == self.expectedLogGroupArn
        assert logGroup['kmsKeyId'] == self.expectedKmsId

    def test_subscription_filter_properties(self):
        """ Test function to verify that the CloudWatch Subscription Filter
        created exists and its properties are correct.
        """
        
        # Get the response of the log group
        response = self.logsClient.describe_subscription_filters(
            logGroupName = self.expectedLogGroupName,
            filterNamePrefix = self.expectedSubscriptionFilterName
            )
        assert response is not None

        # Confirm that log group has the correct properties
        subscriptionFilter = response['subscriptionFilters'][0]
        assert subscriptionFilter['filterName'] == self.expectedSubscriptionFilterName
        assert subscriptionFilter['logGroupName'] == self.expectedLogGroupName
        
