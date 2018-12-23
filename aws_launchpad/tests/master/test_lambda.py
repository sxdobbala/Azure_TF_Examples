#!/usr/bin/python
import boto3
import pytest 
import json 
from botocore.client import ClientError

# tests to make sure the master account has the correct lambda function
class TestLambda():

    terraformOutputJsonFile = 'tests/master/master_output.json'

    # read terraform output json file 
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 
    
    # extract the outputs
    expectedLambdaName = tfOutputData['lambda_function_name']['value']
    expectedLambdaArn = tfOutputData['lambda_function_arn']['value']
    expectedSchedulerRuleName = tfOutputData['scheduler_rule_name']['value']
    expectedSchedulerTargetArn = tfOutputData['scheduler_target_arn']['value']
    expectedLambdaTopicArn = tfOutputData['lambda_topic_arn']['value']

    # boto3 clients / resources
    lambdaClient = boto3.client('lambda')
    cwEventsClient = boto3.client('events')
    snsClient = boto3.client('sns')

    # test to confirm the lambda function exists
    def test_lambda_exist(self):
        function = self.lambdaClient.get_function(FunctionName = self.expectedLambdaName)
        
        # check that it exists and its fields are correct
        assert function is not None
        assert function['Configuration']['FunctionName'] == self.expectedLambdaName
        assert function['Configuration']['FunctionArn'] == self.expectedLambdaArn
        assert function['Code']['RepositoryType'] == 'S3'

    # test to confirm a successful invocation of the function
    def test_lambda_invocation(self):
        response = self.lambdaClient.invoke(FunctionName = self.expectedLambdaName)

        # check the response
        assert response is not None
        assert response['StatusCode'] == 200

    def test_scheduler_exists(self):
        response = self.cwEventsClient.list_rule_names_by_target(TargetArn=self.expectedSchedulerTargetArn)
        assert response is not None
        assert len(response['RuleNames']) == 1
        assert self.expectedSchedulerRuleName in response['RuleNames']

    # test to confirm the sns topic is reachable
    def test_lambda_topic(self):
        message = self.snsClient.publish(TopicArn=self.expectedLambdaTopicArn, Message="Test Lambda Invoker")
        assert message is not None
