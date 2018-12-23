#!/usr/bin/python

import logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)

def lambda_handler(event, context):
    # Dummy Lambda Function to show invocation from cross account
    logger.info('Hello - lambda_audit_ec2')
    logger.info('Event - event{}'.format(event))
    return {
        'message' : "Hello - lambda_audit_ec2",
        'event' : event
    }
