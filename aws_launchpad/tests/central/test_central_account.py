#!/usr/bin/python
import boto3
import pytest 
import json 
from botocore.client import ClientError

# tests to make sure the central managment accounts has the correct setup
class TestCentralAccount():

    terraformOutputJsonFile = 'tests/central/central_output.json'

    # read terraform output json file 
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput) 

    # extract the outputs
    expectedBackendBucketId = tfOutputData['backend_bucket_id']['value']
    expectedBackendTableId = tfOutputData['backend_table_id']['value']

    expectedFireEyeArnRole = tfOutputData['fire_eye_forwarder_role_arn']['value']

    expectedCloudtrailBucketId = tfOutputData['cloudtrail_bucket_id']['value']
    expectedCloudtrailBucketArn = tfOutputData['cloudtrail_bucket_arn']['value']

    expectedConfigBucketId = tfOutputData['config_bucket_id']['value']
    expectedConfigBucketArn = tfOutputData['config_bucket_arn']['value']
    expectedConfigQueueId = tfOutputData['config_queue_id']['value'].replace('sqs.us-east-1', 'queue')
    expectedConfigQueueArn = tfOutputData['config_queue_arn']['value']
    expectedDeadletterQueueId = tfOutputData['deadletter_queue_id']['value'].replace('sqs.us-east-1', 'queue')
    expectedDeadletterQueueArn = tfOutputData['deadletter_queue_arn']['value']


    expectedFirehoseStreamArn = tfOutputData['firehose_stream_arn']['value']
    expectedFirehoseName = tfOutputData['firehose_name']['value']
    expectedLambdaBucketId = tfOutputData['lambda_bucket_id']['value']
    expectedLambdaBucketArn = tfOutputData['lambda_bucket_arn']['value']
    expectedLambdaZips = ['lambda_invoker.zip', 'lambda_audit_ec2.zip']

    expectedDataStreamId = tfOutputData['data_stream_id']['value']
    expectedDataStreamArn = tfOutputData['data_stream_arn']['value']
    expectedDataStreamName = tfOutputData['data_stream_name']['value']
    expectedDataStreamShardCount = tfOutputData['data_stream_shard_count']['value']

    expectedFunctionName = tfOutputData['lambda_name']['value']
    expectedFunctionArn = tfOutputData['lambda_arn']['value']
    expectedSourceS3BucketId = tfOutputData['source_s3_bucket_id']['value']
    expectedDestinationS3BucketId = tfOutputData['destination_s3_bucket_id']['value']
    expectedLogGroupArn = tfOutputData['log_group_arn']['value']
    expectedLogGroupName = tfOutputData['log_group_name']['value']
    expectedSqsId = tfOutputData['sqs_id']['value']
    expectedSqsArn = tfOutputData['sqs_arn']['value']
    expectedKmsId = tfOutputData['kms_id']['value']
    expectedKmsArn = tfOutputData['kms_arn']['value']
    expectedKmsAliasName = tfOutputData['kms_alias_name']['value']


    # boto3 clients / resources
    dynamodbClient = boto3.client('dynamodb')
    s3Client = boto3.client('s3')
    sqsClient = boto3.client('sqs')
    firehoseClient = boto3.client('firehose')
    kinesisDataStreamClient = boto3.client('kinesis')
    logsClient = boto3.client('logs')
    lambdaClient = boto3.client('lambda')
    kmsClient = boto3.client('kms')

    # test to confirm backend bucket exists
    def test_backend_bucket_exist(self):
        check = False
        try: 
            self.s3Client.head_bucket(Bucket = self.expectedBackendBucketId)
            check = True
        except ClientError:
            check = False
        assert check 

    # test to confirm backend table exists
    table_list = dynamodbClient.list_tables()
    def test_backend_table_exist(self):
        assert self.expectedBackendTableId in self.table_list['TableNames']

    # test to confirm cloudtrail bucket exists
    def test_cloudtrail_bucket_exist(self):
        check = False
        try: 
            self.s3Client.head_bucket(Bucket = self.expectedCloudtrailBucketId)
            check = True
        except ClientError:
            check = False
        assert check 

    # test to confirm config bucket exists
    def test_config_bucket_exist(self):
        check = False
        try: 
            self.s3Client.head_bucket(Bucket = self.expectedConfigBucketId)
            check = True
        except ClientError:
            check = False
        assert check 

    # tests to confirm queues exist
    queue_list = sqsClient.list_queues()
    def test_config_queue_exist(self):
        assert self.expectedConfigQueueId in self.queue_list['QueueUrls']

    def test_deadletter_queue_exist(self):
        assert self.expectedDeadletterQueueId in self.queue_list['QueueUrls']

    # test to confirm lambda bucket and zip files exists
    def test_lambda_bucket_exist(self):
        check = False
        try: 
            self.s3Client.head_bucket(Bucket = self.expectedLambdaBucketId)
            check = True
        except ClientError:
            check = False
        assert check 

    def test_lambda_zip_files(self):
        for zip_file in self.expectedLambdaZips:
            obj = self.s3Client.get_object(Bucket = self.expectedLambdaBucketId, 
                Key = zip_file)
            assert obj is not None

    # test to confirm bucket notification exist
    bucket_notification = s3Client.get_bucket_notification_configuration(Bucket = expectedConfigBucketId)
    def test_config_bucket_notification_exist(self):
        assert self.expectedConfigQueueArn == self.bucket_notification['QueueConfigurations'][0]['QueueArn']

    # test to confirm fire-eye has access to the cloudtrail bucket
    cloudtrail_bucket_policy = s3Client.get_bucket_policy(Bucket = expectedCloudtrailBucketId)
    def test_fire_eye_in_cloudtrail_bucket(self):
        check = False
        policy_json = json.loads(self.cloudtrail_bucket_policy['Policy'])
        for statement in policy_json['Statement']:
            if statement['Sid'] == 'FEYEReadOnly' and statement['Principal']['AWS'] == self.expectedFireEyeArnRole:
                check = True
        assert check

    # test to confirm fire-eye cloudtrail bucket notification
    cloudtrail_bucket_notification = s3Client.get_bucket_notification_configuration(Bucket = expectedCloudtrailBucketId)
    def test_cloudtrail_bucket_notification(self):
        check_lambda_1 = False
        check_lambda_2 = False
        for lambda_function in self.cloudtrail_bucket_notification['LambdaFunctionConfigurations']:
            if lambda_function['LambdaFunctionArn'] == "arn:aws:lambda:us-east-1:264756907367:function:LambdaCT":
                prefix_value = lambda_function['Filter']['Key']['FilterRules'][0]['Value']
                if prefix_value == "AWSLogs/361326022344/":
                    check_lambda_1 = True
                elif prefix_value == "AWSLogs/769738661673/":
                    check_lambda_2 = True
        assert check_lambda_1 and check_lambda_2
    
    # test to confirm the kinesis firehose delivery stream is created
    def test_firehose_logging(self):
        """ Test function to verify that the Kinesis Firehose created exists 
        and its fields/status.
        """

        # Check existence of delivery stream
        response = self.firehoseClient.describe_delivery_stream(DeliveryStreamName = self.expectedFirehoseName)
        assert response is not None

        deliveryStreamDescription = response['DeliveryStreamDescription']

        assert deliveryStreamDescription['DeliveryStreamName'] == self.expectedFirehoseName
        assert deliveryStreamDescription['DeliveryStreamARN'] == self.expectedFirehoseStreamArn

    # test to confirm the kinesis data stream is created
    def test_data_stream(self):
        """ Test function to verify that the Kinesis data stream created exists 
        and its fields/status.
        """

        # Check existence of delivery stream
        response = self.kinesisDataStreamClient.describe_stream(StreamName = self.expectedDataStreamName)
        assert response is not None

        deliveryStreamDescription = response['StreamDescription']

        assert deliveryStreamDescription['StreamName'] == self.expectedDataStreamName
        assert deliveryStreamDescription['StreamARN'] == self.expectedDataStreamArn
        assert deliveryStreamDescription['EncryptionType'] == "KMS"
        assert deliveryStreamDescription['KeyId'] == "alias/aws/kinesis"

    # test to confirm Lambda function is created
    def test_lambda_properties(self):
        response = self.lambdaClient.get_function( FunctionName = self.expectedFunctionName )
        actual_function = response['Configuration']
        assert actual_function['FunctionArn'] == self.expectedFunctionArn
        assert actual_function['DeadLetterConfig']['TargetArn'] == self.expectedSqsArn

    # test to confirm source s3 bucket exists
    def test_source_s3_bucket_exists(self):
        s3 = boto3.resource('s3')
        bucket = s3.Bucket(self.expectedSourceS3BucketId)
        assert bucket is not None
    
    # test to confirm the s3 bucket notification for the source s3 bucket is created
    def test_source_s3_bucket_notification_exists(self):
        s3 = boto3.resource('s3')
        bucket_notification = s3.BucketNotification(self.expectedSourceS3BucketId)
        assert bucket_notification is not None

    # test to confirm destination s3 bucket is created
    def test_destination_s3_bucket_exists(self):
        s3 = boto3.resource('s3')
        bucket = s3.Bucket(self.expectedDestinationS3BucketId)
        assert bucket is not None

    # test to confirm CloudWatch Log Group is created with correct properties
    def test_log_group_properties(self):
        # Get the response of the log group
        response = self.logsClient.describe_log_groups(logGroupNamePrefix = self.expectedLogGroupName)
        assert response is not None

        # Confirm that log group has the correct properties
        logGroup = response['logGroups'][0]
        assert logGroup['logGroupName'] == self.expectedLogGroupName
        assert logGroup['retentionInDays'] == 365
        assert logGroup['arn'] == self.expectedLogGroupArn
        assert logGroup['kmsKeyId'] == self.expectedKmsArn
    
    # test to confirm that sqs exists
    def test_sqs_exists(self):
        queue_attributes = self.sqsClient.get_queue_attributes(QueueUrl=self.expectedSqsId,AttributeNames=["QueueArn","KmsMasterKeyId"])
        assert queue_attributes['Attributes']['QueueArn'] == self.expectedSqsArn
        assert queue_attributes['Attributes']['KmsMasterKeyId'] == "alias/aws/sqs"

    # test to confirm that created cmk exists
    def test_kms_key_properties(self):
        # Check existence of key
        response = self.kmsClient.describe_key(KeyId = self.expectedKmsId)
        assert response is not None

        # Verify the response
        metadata = response['KeyMetadata']
        assert metadata['KeyId'] == self.expectedKmsId
        assert metadata['Arn'] == self.expectedKmsArn
        assert metadata['Enabled'] == True
        assert metadata['KeyUsage'] == 'ENCRYPT_DECRYPT'
        assert metadata['KeyState'] == 'Enabled'
        assert metadata['Origin'] == 'AWS_KMS'
    
    # Test function to verify that the KMS Key Alias created exists and matches its respective KMS Key.
    def test_kms_key_alias(self):
        # Check if the alias exists
        response = self.kmsClient.list_aliases()
        existCheck = False
        for alias in response['Aliases']:
            if alias['AliasName'] == self.expectedKmsAliasName:
                existCheck = True
        assert existCheck

        # Verify the alias properties
        responseOriginal = self.kmsClient.describe_key(KeyId = self.expectedKmsId)
        responseAlias = self.kmsClient.describe_key(KeyId = self.expectedKmsAliasName)
        assert responseOriginal is not None
        assert responseAlias is not None
        metadataOriginal = responseOriginal['KeyMetadata']
        metadataAlias = responseAlias['KeyMetadata']
        assert metadataOriginal == metadataAlias


 