#!/usr/bin/python
# =================================================================================================
# Author: Mark Helgeson
# Date:   December 2018
# Purpose:
#   This module is expected to get invoked by via Cloudwatch Event from a configuration change.
#   This module is expected to get deployed via launchpad and run in all accounts.  It will be configured
#   as the target of an AWS custom config rule.
#
# Dependancies:
#   1. Must be run as an AWS Lambda Function with permissions to Cloudwatch logs, STS, and AWS Config.
#   2. Environment variables set - see environment variable comments below.
#   3. Created with Python v3.6
#   4. Boto 3 - AWS CLI
#
# Environment variables to set:
#   1. CONFIGTEST - This sets the testmode of the config.put_evalution.  The default is false. If just testing use TRUE
#   2. LOGLEVEL   - This sets the runtime logging level. This is optional and will default to 'informational'.
#                   Valid values are INFO, DEBUG, WARNING, ERROR
#   3. RANGELIMIT - This sets the range of ports to be considered non-compliant.
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
import json
import platform   # used to determine platform and deal with differences.
# =================================================================================================
# Global Variables - all defined in a single class for self/easy documenting purposes
# These are meant to be set and remain static once set.
class GlobalVariables:
    module = (__file__.split('/'))[-1]      # extract only the module name w/o the location.
    account = ""
    configTestMode = False
    logLevel = ""
    portRange: int = 0
    today: datetime = datetime.datetime.today()
GV = GlobalVariables()                      # instanciate a variable
# Set the initial default logging parameters
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(GV.module + __name__)
boto3.set_stream_logger('', level='WARNING')  # Set logging higher for Boto3
# -------------------------------------------------------------------------------------------------
# This module is to pull environment variables and initialize global variables.
def prepareGlobalVariables():
    global GV
    RC: int = 0
    MAXPORT: int = 65535
    RC = int(os.environ.get('RANGELIMIT', MAXPORT))  # Default setting
    if abs(RC) <= int(MAXPORT):
        GV.portRange = abs(RC)
    else:
        GV.portRange = int(MAXPORT)
    RC = 0  # reset after using as a temp numeric variable
    tempstr = (os.environ.get('CONFIGTEST', "FALSE")).upper()  # Default setting
    if tempstr == 'TRUE':
        GV.configTestMode = True
    tempstr = (os.environ.get('LOGLEVEL', "INFO")).upper()  # Default setting
    if tempstr == "DEBUG":
        logger.setLevel(logging.DEBUG)
    elif tempstr == "WARNING":
        logger.setLevel(logging.WARNING)
    elif tempstr == "ERROR":
        logger.setLevel(logging.ERROR)
    else:                                     # Use default if not any other valid option
        tempstr = "INFO"
        logger.setLevel(logging.INFO)

    GV.logLevel = tempstr[0]

    try:
        sts = boto3.client('sts')
        response = sts.get_caller_identity()
        GV.account = response.get('Account')
    except:
        RC = 1

    return (RC)
# -------------------------------------------------------------------------------------------------
# Function to evaluate the Event triggering the Lambda Function. The trigger is
# assumed to be caused by a Configuration Change within a VPC Resource (i.e. security group), as
# defined in the Config rule.
#
# Evalulation is determined by checking the port range (in the JSON will be from/to 0 - 65535 or
# non-existant and will be NON_COMPLIANT, else COMPLIANT
#
# Arguments:
#   event -- The invokingEvent field of the event that triggered by Lambda.
#       See "inovkingEvent" from link below for structure:
#       https://docs.aws.amazon.com/config/latest/developerguide/evaluate-config_develop-rules_example-events.html
#
#   Outputs:
#   results -- An array of evaluations of the security group.
#       Each result will be in the structure of the dictionary below:
#       {
#           'ComplianceResourceType': Compliance Resource Type,
#           'ComplianceResourceId': Compliance Resource ID,
#           'ComplianceType': Compliance Result (COMPLIANT or NON_COMPLIANT),
#           'Annotation': Annotation/Message of the Result,
#           'OrderingTimestamp': Time the Event was Triggered
#       }
def evaluate_compliance(event):

    results = []                          # list is required for return and ultimately for the config.put_evaluation
    annotationDict = {}
    protocolList = ["TCP", "UDP", "-1"]   # -1 value represents the value for ALL protocols
    complianceType = 'COMPLIANT'
    mySession = boto3.session.Session()
    annotationDict['Account'] = GV.account
    annotationDict['Region']  = mySession.region_name
    annotationDict['Message'] = complianceType   # Setting a default value for message
    for permission in event['configurationItem']['configuration']['ipPermissions']:
        fromPort: int = permission.get('fromPort', 0)      # if nothing then very likely all ports were specified
        toPort: int   = permission.get('toPort', 65535)    # if nothing then very likely all ports were specified
        protocol      = permission.get('ipProtocol', '-1') # if nothing then all protocols assumed
        if (toPort - fromPort) >= GV.portRange and (protocol.upper() in protocolList):
            complianceType = 'NON_COMPLIANT'
            annotationDict['Message'] = 'NON-COMPLIANT to allow ALL ports for inbound networking traffic.'

    annotation = json.dumps(annotationDict)
    results.append(
        {
            'ComplianceResourceType': event['configurationItem']['resourceType'],
            'ComplianceResourceId': event['configurationItem']['resourceId'],
            'ComplianceType': complianceType,
            'Annotation': annotation,
            'OrderingTimestamp': event['notificationCreationTime']
        }
    )
    return results
# -------------------------------------------------------------------------------------------------
#  Main entry point into the Lambda function
def lambda_handler(event, context):
    global GV
    RC: int = 0
    invoking_event = event.get('invokingEvent', None)
    if invoking_event == None:    # bypass events that don't apply by getting out ASAP
        return RC
    RC = prepareGlobalVariables()
    if RC != 0:      # issues discovered in global variable handling.
        logger.error('Issue with Lambda %s getting environment variables.  Return code %s.', GV.module, RC)
        return RC
    #  this code is needed for AWS lambda Python to properly handle the event
    if platform.system() != 'Windows':
        invoking_event = json.loads(event["invokingEvent"])
    if GV.logLevel == 'D':      # If debug level
        tempstr = str(invoking_event)
        logger.debug("INVOKING EVENT:  %s", tempstr)
    # Only evaluate if the resultToken exists as we know this a valid invoking event
    # As part of the custom config using lambda the resource type only looking for security groups
    tempConfigItem = invoking_event['configurationItem']
    tempstr = tempConfigItem.get('resourceType', "None")
    if "resultToken" in event and tempstr == "AWS::EC2::SecurityGroup":
        # Call and collect evalulation results
        evaluations = evaluate_compliance(invoking_event)
        # Send results back to AWS Config API
        try:
            resultToken = event["resultToken"]  # config.put has issues if not in a variable
            config = boto3.client('config')
            config.put_evaluations(Evaluations=evaluations, ResultToken=resultToken, TestMode=GV.configTestMode)
        except Exception as err:
            RC = 2
            logger.error('Issue with Lambda %s put evaluations for config. Return code %s.', GV.module, err)
    if RC == 0:
        logger.info('Sucessfully processed config rule evaluation. Date/Time: %s', GV.today)
    else:
        logger.error('Issues with running config rule evaluation. Date/Time: %s, Return code: %s', GV.today, RC)
    return RC