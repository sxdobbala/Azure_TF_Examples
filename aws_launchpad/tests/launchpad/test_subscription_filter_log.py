#!/usr/bin/python
import boto3
import pytest 
import json 
import time

class TestSubscriptionFilterToLogDestination():
    """ Pytest Class to test the subscription_filter_to_log_destination example, 
    verifying the creation of the cloudwatch log group, subscription filter, 
    log destination, and the kinesis stream
    """

    # Read terraform output json file 
    terraformOutputJsonFile = 'tests/launchpad/launchpad_output.json'
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 

    # Extract outputs
    expectedLogGroupName = tfOutputData['flow_log_group_name']['value']
    expectedLogGroupArn = tfOutputData['flow_loggroup_arn']['value']
    expectedSubscriptionFilterName = tfOutputData['flow_log_subscription_filter_name']['value']
    expectedSubscriptionFilterId = tfOutputData['flow_log_subscription_filter_id']['value']
    expectedKmsId = tfOutputData['kms_arn']['value']


    # Clients
    logsClient = boto3.client('logs')

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