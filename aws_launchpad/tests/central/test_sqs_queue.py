#!/usr/bin/python
import boto3
import pytest 
import json 
import time
from botocore.client import ClientError

# tests to make sure the central managment accounts has the correct setup
class TestSQSQueue():

    terraformOutputJsonFile = 'tests/central/central_output.json'

    # read terraform output json file 
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 

    # extract the outputs
    expectedConfigBucketId = tfOutputData['config_bucket_id']['value']
    expectedConfigBucketArn = tfOutputData['config_bucket_arn']['value']
    expectedConfigQueueId = tfOutputData['config_queue_id']['value'].replace('sqs.us-east-1', 'queue')
    expectedConfigQueueArn = tfOutputData['config_queue_arn']['value']
    expectedDeadletterQueueId = tfOutputData['deadletter_queue_id']['value'].replace('sqs.us-east-1', 'queue')
    expectedDeadletterQueueArn = tfOutputData['deadletter_queue_arn']['value']

    # boto3 clients / resources
    s3Client = boto3.client('s3')
    sqsClient = boto3.client('sqs')

    # test to confirm that a message is sent to the SQS Queue when the Config Bucket is updated
    def test_sqs_queue_update(self):
        # put sample object file into the bucket to alert the queue
        self.s3Client.put_object(Bucket = self.expectedConfigBucketId, 
            Body = 'tests/central/requirements.txt', 
            Key = 'AWSLogs/304095320850/requirements.txt',
            ServerSideEncryption = 'AES256')

        # check if the queue has received the message regarding the upload above
        check = False
        for i in range(6):
            time.sleep(30)
            messages = self.sqsClient.receive_message(QueueUrl = self.expectedConfigQueueId, MaxNumberOfMessages = 10)
            for message in messages['Messages']:
                body = json.loads(message['Body'].replace('\\', ''))
                if 'Records' in body:
                    for record in body['Records']:
                        if record['eventName'] == 'ObjectCreated:Put' \
                            and record['s3']['bucket']['name'] == self.expectedConfigBucketId \
                            and record['s3']['object']['key'] == 'AWSLogs/304095320850/requirements.txt':
                            check = True
                self.sqsClient.delete_message(QueueUrl = self.expectedConfigQueueId, ReceiptHandle = message['ReceiptHandle'])
            if check:
                break
        assert check

