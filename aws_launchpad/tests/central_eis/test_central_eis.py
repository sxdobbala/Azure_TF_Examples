#!/usr/bin/python
import boto3
import pytest 
import json 
import time
from botocore.client import ClientError

# tests to make sure the central eis account has the correct setup
class TestCentralEIS():

    terraformOutputJsonFile = 'tests/central_eis/central_eis_output.json'

    # read terraform output json file 
    with open(terraformOutputJsonFile) as terraformOutput:
        tfOutputData = json.load(terraformOutput)

    # extract the outputs
    expectedReadRole = tfOutputData['central_security_ro_role']['value']
    expectedBGRole = tfOutputData['central_security_bg_role']['value']

    # boto3 clients / resources
    iamClient = boto3.client('iam')

    # test to confirm read role exists
    def test_read_role_exist(self):
        check = False
        for i in range(6):
            time.sleep(30)
            try:
                self.iamClient.get_role(RoleName = self.expectedReadRole)
                check = True
                break
            except ClientError:
                check = False
        assert check 

    # test to confirm break glass role exists
    def test_break_glass_role_exist(self):
        check = False
        for i in range(6):
            time.sleep(30)
            try:
                self.iamClient.get_role(RoleName = self.expectedBGRole)
                check = True
                break
            except ClientError:
                check = False
        assert check 
