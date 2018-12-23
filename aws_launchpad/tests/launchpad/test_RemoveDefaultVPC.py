#!/usr/bin/python
# https://docs.pytest.org/en/latest/usage.html#cmdline
import sys
import boto3
from botocore.client import ClientError
import pytest
import unittest
from unittest.mock import patch
from unittest.mock import MagicMock
import unittest
from unittest import mock
from unittest.mock import Mock
#sys.path.insert(0, "C:\Work\mhelge2")
import RemoveDefaultVPC
# --------------------------------------------------------------------------------
# Note the AWS CLI command to create a 'default' VPC is:  aws ec2 create-default-vpc
# To remove we would need to run the RemoveDefaultVPC module as it addresses dependencies.
# Just using the aws ec2 delete-vpc --vpc-id <vpc-999999> will fail on a dependency error.
#
class TestRemoveDefaultVPC(unittest.TestCase):
    @mock.patch('RemoveDefaultVPC.OptionParser.parse_args')
    def test_process_parms_valid(self, mock_parser):
        v_vpcid = 'vpc-aaa'
        v_dr = 'DR'
        v_debug = 'DEBUG'
        v_force = 'FORCE'
        mock_parser.return_value = Mock(vpcid=v_vpcid, DR=v_dr, debug=v_debug, force=v_force), None # values in Mock equate to dest values.
        rc = RemoveDefaultVPC.process_parms()
        assert rc == 0

    # Testing of other 'invalid' parameters is not done
    @mock.patch('RemoveDefaultVPC.OptionParser.parse_args')
    def test_process_parms_invalid(self, mock_parser):
        v_vpcid = 'vp-aaa'   #
        v_dr = 'LIVE'
        v_debug = 'WARN'
        v_force = 'FORCE'
        mock_parser.return_value = Mock(vpcid=v_vpcid, DR=v_dr, debug=v_debug, force=v_force), None # values in Mock equate to dest values.
        rc = RemoveDefaultVPC.process_parms()
        assert rc == 1
        # For this function only thing valid is VPC others are invalid.  If parm is invalid it will get set to default
        v_vpcid = 'vpc-valid'
        v_dr = 'INVALID'
        v_dr = 'INVALID'
        v_debug = 'INVALID'
        mock_parser.return_value = Mock(vpcid=v_vpcid, DR=v_dr, debug=v_debug, force=v_force), None # values in Mock equate to dest values.
        rc = RemoveDefaultVPC.process_parms()
        assert rc == 0  # will have return code zero b/c if invalid will get set to the default

    # this test is to "handle" the sys.exit challenges with using pytest
    def test_exit(mymodule):
        with mock.patch.object(RemoveDefaultVPC.sys, "exit") as mock_exit:
            RemoveDefaultVPC.Exit_Out(0)
            assert mock_exit.call_args[0][0] == 0
            RemoveDefaultVPC.Exit_Out(1)
            assert mock_exit.call_args[0][0] == 1
            RemoveDefaultVPC.Exit_Out(9)
            assert mock_exit.call_args[0][0] == 9

    def test_main_valids(self):
        # Purposely NOT testing DRYRUN == FALSE in any test as will remove the default VPCs in the account
        # With the AWS dry run option this will allow for coverage of all lines of code for unit testing.
        #
        G_VPCID = "vpc-0c0649b90d8a2757"   # Invalid VPC yet with other options will still run correctly
        G_DryRun = True
        G_Default = True        # If true the actual VPCID can be non-existent and return as valid
        G_Force = False         # Will still run correctly b/c force != True
        rc = RemoveDefaultVPC.main(G_DryRun, G_VPCID, G_Default, G_Force)
        assert rc == 0
        #
        G_VPCID = ""            # Empty string this is the most common value for the VPCID
        G_DryRun = True
        G_Default = True        # If true the actual VPCID can be non-existent and return as valid
        G_Force = False
        rc = RemoveDefaultVPC.main(G_DryRun, G_VPCID, G_Default, G_Force)
        assert rc == 0
        # Most common combination of parameters.
        G_VPCID = ""            # Empty string this is the most common value for the VPCID
        G_DryRun = True
        G_Default = True        # If true the actual VPCID can be non-existent and return as valid
        G_Force = True
        rc = RemoveDefaultVPC.main(G_DryRun, G_VPCID, G_Default, G_Force)
        assert rc == 0

    def test_main_invalid_parms(self):
        # Purposely NOT testing DRYRUN == FALSE in any test as will remove the default VPCs in the account
        # With the AWS dry run option this will allow for coverage of all lines of code for unit testing.
        #
        G_VPCID = "vpc-mickeymouse"
        G_DryRun = True
        G_Default = False
        G_Force = False
        rc = RemoveDefaultVPC.main(G_DryRun, G_VPCID, G_Default, G_Force)
        assert rc == 3
        # Set invalid VPCID yet Default == True
        G_VPCID = "vpc-0c0649b90d8a2757"
        G_DryRun = True
        G_Default = True        # If true the actual VPCID can be non-existent and return as valid
        G_Force = False
        rc = RemoveDefaultVPC.main(G_DryRun, G_VPCID, G_Default, G_Force)
        assert rc == 0
        # Set Force to invalid value
        G_VPCID = "vpc-0c0649b90d8a2757"
        G_DryRun = True
        G_Default = True
        G_Force = "false"
        rc = RemoveDefaultVPC.main(G_DryRun, G_VPCID, G_Default, G_Force)
        assert rc == 1
        # Set Default to invalid value
        G_VPCID = "vpc-0c0649b90d8a2757"
        G_DryRun = True
        G_Default = "true"
        G_Force = False
        rc = RemoveDefaultVPC.main(G_DryRun, G_VPCID, G_Default, G_Force)
        assert rc == 1
        # Set Dryrun to invalid value
        G_VPCID = "vpc-0c0649b90d8a2757"
        G_DryRun = "True"
        G_Default = True
        G_Force = False
        rc = RemoveDefaultVPC.main(G_DryRun, G_VPCID, G_Default, G_Force)
        assert rc == 1
