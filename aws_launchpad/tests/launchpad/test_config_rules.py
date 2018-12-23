#!/usr/bin/python
import json
import boto3
import pytest


class TestConfigRules():

    # this file is produced as a temporary file from with in the Jenkins pipeline run
    tf_output_file = 'tests/launchpad/launchpad_output.json'

    # read terraform output json file 
    with open(tf_output_file) as tf_output:
        tf_output_data = json.load(tf_output) 
    
    # make the config rule list from terraform output file (i.e. output.tf)
    expected_config_rules = tf_output_data['config_aws_managed_rule_ids']['value'].split(",")
    expected_config_rules.extend(tf_output_data['config_aws_custom_scoped_rule_ids']['value'].split(","))

    # config client
    config_client = boto3.client('config')
    
    ### Tests ----------------------------------------------------------------

    # tests to make sure all of the config rule names in the terraform output are the same as the ones in the actual aws account
    def test_config_rules_exist(self):
        next_token = ''
        actual_config_rules = []

        while next_token is not None:
            config_response = self.config_client.describe_config_rules(NextToken=next_token)
            config_rules = config_response['ConfigRules']

            for config_rule in config_rules:
                the_rule = config_rule['ConfigRuleName']
                actual_config_rules.append(the_rule)
                assert the_rule in self.expected_config_rules
                
            next_token = config_response.get('NextToken', None)

        assert len(actual_config_rules) == len(self.expected_config_rules)


    def test_restricted_incoming_ports_rule(self):
        next_token = ''

        while next_token is not None:
            config_response = self.config_client.describe_config_rules(ConfigRuleNames=['RESTRICTED_INCOMING_TRAFFIC'], NextToken=next_token)
            config_rules = config_response['ConfigRules']

            for config_rule in config_rules:
                input_params = json.loads(config_rule['InputParameters'])
                assert input_params['blockedPort1'] == '3389'   # RDP
                assert input_params['blockedPort2'] == '3306'   # MySQL
                assert input_params['blockedPort3'] == '1433'   # MS SQL
                assert input_params['blockedPort4'] == '22'     # SSH
                
            next_token = config_response.get('NextToken', None)