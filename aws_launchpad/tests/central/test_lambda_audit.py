#!/usr/bin/python
import boto3
import pytest 
import json 
from botocore.client import ClientError

# tests to make sure the central management account has the correct lambda function
class TestLambdaAudit():

    terraformOutputJsonFile = 'tests/central/central_output.json'

    # read terraform output json file 
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 
    
    # extract the outputs
    expectedLambdaAuditEC2Name = tfOutputData['lambda_audit_ec2_function_name']['value']
    expectedLambdaAuditEC2Arn = tfOutputData['lambda_audit_ec2_function_arn']['value'] 
    expectedLambdaAuditTopicId = tfOutputData['launchpad_lambda_topic_id']['value']
    expectedLambdaAuditTopicArn = tfOutputData['launchpad_lambda_topic_arn']['value']

    # boto3 clients / resources
    lambdaClient = boto3.client('lambda')
    snsClient = boto3.client('sns')

    # test to confirm the lambda function exists
    def test_lambda_exist(self):
        function = self.lambdaClient.get_function(FunctionName = self.expectedLambdaAuditEC2Name)
        
        # check that it exists and its fields are correct
        assert function is not None
        assert function['Configuration']['FunctionName'] == self.expectedLambdaAuditEC2Name
        assert function['Configuration']['FunctionArn'] == self.expectedLambdaAuditEC2Arn
        assert function['Code']['RepositoryType'] == 'S3'

    # test to confirm a successful invocation of the function
    def test_lambda_invocation(self):
        response = self.lambdaClient.invoke(FunctionName = self.expectedLambdaAuditEC2Name)

        # check the response
        assert response is not None
        assert response['StatusCode'] == 200

    # test to confirm the sns topic exists
    def test_lambda_topic(self):
        response = self.snsClient.list_topics()
        exists = False

        while True:
            topics = response['Topics']
            for topic in topics:
                if topic['TopicArn'] == self.expectedLambdaAuditTopicArn:
                    exists = True
                    break
            
            if exists or 'NextToken' not in response:
                break
            token = response['NextToken']
            response = self.snsClient.list_topics(NextToken=token)
        assert exists

        message = self.snsClient.publish(TopicArn=self.expectedLambdaAuditTopicArn, Message="Test Lambda Audit EC2")
        assert message is not None
            