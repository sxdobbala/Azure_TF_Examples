#!/usr/bin/python
# =================================================================================================
# Author: Mark Helgeson
# Date:   October 2018
# Purpose:
#   This module is expected to get invoked periodically by a Cloudwatch Event.
#   This functions main purpose is to invoke one or more lambda functions for each active account.
#   Initially the lambda functions to be invoked are to invoke configuration queries and send the information
#   to a Kinesis datastream where the target of that stream is Splunk.
#
#   This module as a matter of Agile coding
#   is originally developed to run as an AWS Lambda function.  Its scope is limited to the AWS account in which it runs.
#   As a future user story and/or if deemed this module could be enhanced to run across AWS accounts.
#   The versioning can initially be addressed by locating different versions in different subdirectories of the S3.
#
#   General program flow:
#   1. This lambda function will run periodically (initial thinking is 1/hour)
#   2. Will read environment variables for parameters and validate (see ENV variables below for more detail)
#   3. For each active AWS account a list of lambda functions will be invoked async.
#
# Dependancies:
#   1. Must be run as AWS Lambda Function with permissions to EC2, SNS, and S3 services
#   2. The S3 buckets located in the central accounts to be used must have permissions set to allow other the
#       master account to read/run this lambda.
#   3. The Lambda function should be set on a desired schedule in order to run periodically using CloudWatch Events.
#   4. Environment variables set - see environment variable comments below.
#   5. Created with Python v3.6
#   6. Boto 3 - AWS CLI
#
# Environment variables to set:
#   1. LAMBDA_ARN_LIST - this is a pipe delimited list of AWS lambda ARNs to get invoked on each account
#   2. ASSUME_ROLE_ARN - this is the ARN used to assume role into the central management account to invoke
#                        subsequent lambda functions.
#   3. SNS_TOPIC_ARN - This is the AWS SNS Topic ARN for communication.  One should exist.
#   4. LOGLEVEL  - This sets the runtime logging level. This is optional and will default to 'informational'.
#
# =================================================================================================
# Import modules
import boto3
from botocore.exceptions import ClientError
import sys
import os
import logging
import datetime
import time
# =================================================================================================
# Global Variables - all defined in a single class for self/easy documenting purposes
# These are meant to be set and remain static once set.
class GlobalVariables:
    ExitCode: int = 0
    Module = (__file__.split('/'))[-1]      # extract only the module name w/o the location.
    TopicARN = ""                           # Initialized and global b/c used at multiple points in the program
    AssumeRoleARN = None                    # AWS ARN of role and central management account to assume to
    LambdaARNList = []                      # List of lambda functions from environment variable to be executed
    AssumeRoleObject = None                 # Object of
    MasterAccount = ""                      # Place to keep the master account which this module runs under
    LambdaMaster = None                     # Lambda client for the master account
    FD = "|"                                # Field Delimiter used in parameter files
    Today: datetime = datetime.datetime.today()
GV = GlobalVariables()                      # instanciate a variable
# Set the initial default logging parameters
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(GV.Module + __name__)
boto3.set_stream_logger('', level='WARNING')  # Set logging higher for Boto3
# -------------------------------------------------------------------------------------------------
# This module is to pull environment variables and initialize global variables.
def prepareGlobalVariables():
    global GV
    RC = 0
    GV.TopicARN = os.environ.get('SNS_TOPIC_ARN', "")
    GV.AssumeRoleARN = os.environ.get('ASSUME_ROLE_ARN', "")
    try:
        tempstr = os.environ.get('LAMBDA_ARN_LIST', "")
        GV.LambdaARNList = tempstr.split(GV.FD)
        if len(GV.LambdaARNList) == 0:
            RC = 1
    except:
        RC = 2
    try:
        sts = boto3.client('sts')
        response = sts.get_caller_identity()
        GV.MasterAccount = response.get('Account')
    except:
        RC = 3
    tempstr = os.environ.get('LOGLEVEL', "INFO")  # Default setting
    logger.setLevel(logging.INFO)
    if tempstr.upper() == "DEBUG":
        logger.setLevel(logging.DEBUG)
    elif tempstr.upper() == "WARNING":
        logger.setLevel(logging.WARNING)
    elif tempstr.upper() == "ERROR":
        logger.setLevel(logging.ERROR)
    return (RC)
# -------------------------------------------------------------------------------------------------
# This function will get invoked to provide progress and notify of issues via an SNS topic.
def notifyTopic(accountList) -> int:
    global GV
    RC: int = 0
    if len(GV.TopicARN) == 0:
        RC = 1
    else:
        tempstr = '\n'.join(map(str, accountList))
        msg_body = """
                    AWS - list of accounts processed:
                    Master Account: (%s)
                    Linked Accounts: \n    %s
                    """ % \
                    (GV.MasterAccount, tempstr)
        try:
            sns = boto3.client('sns')
            response = sns.publish(
                TopicArn=GV.TopicARN,
                Subject="List of linked acounts processed.",
                Message=msg_body,
                MessageAttributes={"Master": {'DataType': 'String', 'StringValue': GV.MasterAccount}}
                )
            logger.debug('Sending SNS results for master acount(%s). \n MESSAGE: \n (%s).', GV.MasterAccount, msg_body)
        except:
            RC = 2
            logger.error('SNS Topic issue with topic (%s), and message body (%s)', GV.TopicARN, msg_body)
            GV.TopicARN = ""  #ensure do NOT hit this error again - somewhat self healing.
    return(RC)

# -------------------------------------------------------------------------------------------------
# This is intendended to ONLY assume role to the central management account.  It will then
# invoke all the lambda functions for each active account then proceed to the next active account.
#
def invoke_lambdas(accountList):
    global GV
    lambda_client = None
    if GV.AssumeRoleObject == None:  # One time setting
        sts = boto3.client('sts')
        GV.AssumeRoleObject = sts.assume_role(RoleArn=GV.AssumeRoleARN, RoleSessionName=GV.Module, DurationSeconds=900)
        GV.LambdaMaster = boto3.client('lambda')
    tempCreds = GV.AssumeRoleObject['Credentials']
    lambda_assume = boto3.client('lambda',
        aws_access_key_id=tempCreds['AccessKeyId'],
        aws_secret_access_key=tempCreds['SecretAccessKey'],
        aws_session_token=tempCreds['SessionToken']
        )
    for account in accountList:
        payload = '{"Account": "%s"}' % account  # This is what the 'event' variable contains in the target lambda
        for lambdaARN in GV.LambdaARNList:
            if lambdaARN.find(GV.MasterAccount) == -1:  # Master account not found so assume using central account
                lambda_client = lambda_assume
            else:
                lambda_client = GV.LambdaMaster
            try:
                response = lambda_client.invoke(FunctionName=lambdaARN, InvocationType='Event', Payload=payload)
                logger.info("Invoked %s for Account: %s", lambdaARN, account)
            except ClientError as err:
                print(err)
                if err.response['Error']['Code'] == 'ResourceNotFoundException':  # otherwise lose the EC2
                    GV.LambdaARNList.remove(lambdaARN) # removing an encountered Lambda that we have issues with
                    logger.warning('Warning in invocation of Lambda.  Error: %s)', err)
                else:
                    logger.error('Error occured in invocation of Lambda.  Error: %s)', err)
                    return 1
    return 0
# -------------------------------------------------------------------------------------------------
def lambda_handler(event, context):
    RC: int = 0
    RC = prepareGlobalVariables()
    activeAccountList = []
    Org = boto3.client('organizations')
    if RC == 0:      # no issues discovered in global variable handling.
        logger.debug('Found %s regioins. Starting tag checking process...', RC)
        accountListNextToken = None
        accountList = Org.list_accounts()
        while (accountListNextToken != "None"):
            for Account in accountList['Accounts']:
                if Account.get('Status', "None") == "ACTIVE":
                    activeAccountList.append(Account.get('Id'))
                logger.debug("Account: %s", Account)
            RC = invoke_lambdas(activeAccountList)
            RC = notifyTopic(activeAccountList)
            tempstr = '\n'.join(map(str, activeAccountList))
            activeAccountList = []
            if RC == 2:
                logger.info("Linked accounts processed: %s", tempstr)
                # logger.error('Issues found while checking tags in region %s. RC was: (%s).', MA.Account, RC)
            accountListNextToken = accountList.get('NextToken')
            if accountListNextToken != None:
                accountList = Org.list_accounts(NextToken=accountListNextToken)
            else:
                accountListNextToken = str(accountList.get('NextToken'))
    return RC
