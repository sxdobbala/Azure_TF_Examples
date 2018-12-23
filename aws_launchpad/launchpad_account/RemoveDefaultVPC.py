#!/usr/bin/python
# =================================================================================================
# Author: Mark Helgeson
# Date:   July 2017 - updated September 2018
# Purpose:
#   This module, at tenant setup time, is expected to spin through all available regions of an
#   AWS account and remove the default VPCs.
#   This module is capable of performing "DryRuns" to see if have permissions with out removal.
#   This module is also capable of removing any valid VPC that is entered on the command line.
#
#   When and Why this module is run:
#   In addition to the CloudTrail and Config components AWS Launchpad, upon initial account provisioning,
#   this module removes the default VPCs in all AWS regions order to remove any Optum non-standard configurations
#   regarding security. More specifically AWS default VPCs violate our AWS_COnfig rules,
#   all VPCs must have a deny all NSG and all resources should be created with IAC including our VPCs.
#
# Command Line Parameters:
#   1. vpcid - This should be the exact VPC ID as it shows in AWS.  If one is specified and does not match
#              a non-successful return code will be thrown.
#   2. dry run - this is used to test/validate that the run will work with the permissions implied by the process
#   3. debug - this is used to get more detailed logging of what is occuring in the run/process
#   4. force - this is used on conjunction with the vpcid to ensure this is really desired to remove a non-default VPC
#
# Dependancies:
#   1. Valid master account credentials that allow updates to all sub accounts
#   2. Cloud Trail is enabled to log all activities
#   3. Must be run command line
#   4. Created with Python v3.6
#   5. Boto 3 - AWS CLI
#
# To run help to get more information on how to run:
# > RemoveDefaultVPC.py -h
# =================================================================================================
# Import modules
import boto3
from botocore.exceptions import ClientError
from optparse import OptionParser    # recommended library for parsing arguments
import sys
import logging
import time
#
# =================================================================================================
# Global Variables
G_Module = (__file__.split('/'))[-1] # extract only the module name w/o the location.
G_VPCID = ""
G_DryRun = True                      # Indicates run AWS command only to check permissions to run - no updates
G_Default = True                     # Set to false if VPC ID is passed on the command line for specific VPC processing.
G_Force = False                      # Indicates force removal of non-default VPC.
# Set the initial default logging parameters
logging.basicConfig(level=logging.INFO, filename="marktest.txt", filemode="w")
logger = logging.getLogger(G_Module + __name__)
boto3.set_stream_logger('', level='WARNING') # Set logging higher for Boto3

# -------------------------------------------------------------------------------------------------
# This function is used to process command line parameters - if any.
# The approach of this function is to tend to tolerate invalid command line arguments and use defaults instead
def process_parms():
    global G_VPCID, G_DryRun, G_Default, G_Force, logger
    parser = OptionParser()
    parser.add_option("-v", "--vpcid", action="store", dest="vpcid", type="string",
                      help="""Specific VPCID starting with 'vpc-' to remove.
                            If not specified then entire set of default VPCs 
                            across all regions are to be removed.""")
    parser.add_option("-d", "--dryrun", action="store", dest="DR", default="DR",
                      help="Default is DR (Dry Run). Specify LIVE to run for real.")
    parser.add_option("-g", "--loglevel", action="store", dest="debug", default="INFO",
                      help="Loglevel can be INFO (default), WARN, ERROR or DEBUG")
    parser.add_option("-f", "--force", action="store", dest="force", default="NOTFORCE",
                      help="Must be specified for non-default or specific VPCID. Default is NOTFORCE.")
    (options, args) = parser.parse_args()

    if options.vpcid != None:
        if options.vpcid.startswith('vpc-'):  # all VPC IDs in AWS start with this.
            G_VPCID = options.vpcid
            G_Default = False     # indicate that a VPC ID is passed on the command line and NOT to check for default VPCs
        else:
            print('Invalid VPCID. Run only with "-h" option for more help.')
            return 1

    G_DryRun = True                 # default to dry run unless specifically requested to run live
    if options.DR.upper() == "LIVE":
        G_DryRun = False

    G_Force = False
    if options.force.upper() == "FORCE":
        G_Force = True

    logger.setLevel(logging.INFO)           # Default setting
    if options.debug.upper() == "DEBUG":
        logger.setLevel(logging.DEBUG)
    elif options.debug.upper() == "WARN":
        logger.setLevel(logging.WARNING)
    elif options.debug.upper() == "ERROR":
        logger.setLevel(logging.ERROR)
    return 0
# -------------------------------------------------------------------------------------------------
# Main control function that works through the removal of the sub components/dependencies of the VPC
# prior to removing the VPC.
def CheckCleanVPCResources(ec2, VPCID, DR):
    logger.debug('VPCID = %s', VPCID)
    RC = 0
    if RC == 0:
        RC = ProcessVPCEP(ec2, VPCID, DR)
    if RC == 0:
        RC = ProcessNATGW(ec2, VPCID, DR)
    if RC == 0:
        RC = ProcessIGWs(ec2, VPCID, DR)
    if RC == 0:
        RC = ProcessNIs(ec2, VPCID, DR)
    if RC == 0:
        RC = ProcessSGs(ec2, VPCID, DR)
    if RC == 0:
        RC = ProcessSubnets(ec2, VPCID, DR)
    if RC == 0:
        RC = ProcessNACL(ec2, VPCID, DR)
    if RC == 0:
        RC = ProcessRTs(ec2, VPCID, DR)
    if RC == 0:
        RC = ProcessVPNGW(ec2, VPCID, DR)
    if RC == 0:
        RC = ProcessVPCPeer(ec2, VPCID, DR)
    if RC == 0:     # if all other checks and removals are good we will attempt removal of the VPC
        try:
            logger.debug('VPC ID %s and DR Flag %s)',VPCID , DR)
            ec2.delete_vpc( VpcId=VPCID, DryRun = DR)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting VPC: %s', VPCID)
            else:
                logger.exception('Execution failed while deleting VPC: %s. Error = %s', VPCID, err)
                return -1
    return(RC)
# -------------------------------------------------------------------------------------------------
# Process Subnets associated w/ the VPC
def ProcessSubnets(ec2, VPCID, DR) -> int:
    response = ec2.describe_subnets(Filters=[{'Name': 'vpc-id', 'Values': [VPCID]}])
    for entry in response['Subnets']:
        SN = entry.get('SubnetId', "")
        try:
            if len(SN) > 0:
                logger.debug('Subnet ID %s and DR Flag %s)', SN, DR)
                ec2.delete_subnet(SubnetId=SN, DryRun=DR)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting subnet %s for VPC %s.', SN, VPCID)
            else:
                logger.exception('Execution failed while deleting subnet id = %s. Error = %s', SN, err)
                return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Process Internet Gateway
def ProcessIGWs(ec2, VPCID, DR) -> int:
    response = ec2.describe_internet_gateways(Filters=[{'Name': 'attachment.vpc-id', 'Values': [ VPCID ]}])
    for entry in response['InternetGateways']:
        IGW = entry.get('InternetGatewayId', "")
        try:
            if len(IGW) > 0:
                logger.debug("In Try detach internet gatway = %s and DR flag = %s", IGW, DR)
                ec2.detach_internet_gateway(InternetGatewayId = IGW, VpcId = VPCID, DryRun = DR)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Detaching internet gateway %s for VPC %s.', IGW, VPCID)
            else:
                logger.exception('Execution failed while detaching internet gateway = %s. Error = %s', IGW, err)
                return -1
        try:
            if len(IGW) > 0:
                logger.debug("In Try delete gatway = %s and DR flag = %s", IGW, DR)
                ec2.delete_internet_gateway(InternetGatewayId = IGW, DryRun = DR)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting internet gateway %s for VPC %s.', IGW, VPCID)
            else:
                logger.exception('Execution failed while removing internet gateway = %s. Error = %s', IGW, err)
                return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Process Network Interfaces
# The key part of this function is to detach and is generally required before removal of the IGW.
def ProcessNIs(ec2, VPCID, DR) -> int:
    response = ec2.describe_network_interfaces(Filters=[{'Name': 'vpc-id', 'Values': [VPCID]}])
    for entry in response['NetworkInterfaces']:
        NIID = entry.get('NetworkInterfaceId')
        ASSN = entry.get('Association', None)
        try:
            ASSNID = ""
            if ASSN != None:
                ASSNID = ASSN.get('AssociationId', "")
            if len(ASSNID) > 0:
                logger.debug("In Try disassicate address = %s and DR flag = %s", ASSN, DR)
                ec2.disassociate_address(AssociationId=ASSN['AssociationId'], DryRun=DR)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Disassociated network interface %s for VPC %s.', NIID, VPCID)
            elif err.response['Error']['Code'] == 'InvalidAssociationID.NotFound':
                pass  # This is an "OK" condition as wanted to remove it anyway
            else:
                logger.exception('Execution failed while removing network interface: %s. Error = %s', NIID, err)
                return -1
        try:
            logger.debug("In Try delete network interface = %s and DR flag = %s", NIID, DR)
            ec2.delete_network_interface(NetworkInterfaceId = NIID, DryRun = DR)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting network interface %s for VPC %s.', NIID, VPCID)
            elif err.response['Error']['Code'] == 'InvalidNetworkInterfaceID.NotFound':
                pass  # This is an "OK" condition as wanted to remove it anyway
            else:
                logger.exception('Execution failed while removing network interface: %s. Error = %s', NIID, err)
    return 0
# -------------------------------------------------------------------------------------------------
# Check for non-default routing tables (i.e. tables created & associated w/ VPC after VPC creation)
def ProcessRTs(ec2, VPCID, DR) -> int:
    response = ec2.describe_route_tables(Filters=[{'Name': 'vpc-id', 'Values': [VPCID]}])
    LoopBreak = False # used to break out of loops in route table structure.
    for entry in response['RouteTables']:
        RT = entry.get('RouteTableId')
        Associations = entry.get('Associations')
        for assn in Associations:
            # means this is default route table and can NOT be deleted.
            if assn['Main'] == True and assn['RouteTableId'] == RT:
                LoopBreak = True
                break
        if LoopBreak == True:
            LoopBreak = False   # reset the flag
            continue            # Loop to next route table entry if there is one
        try:
            logger.debug("In Try delete route table = %s and DR flag = %s", RT, DR)
            ec2.delete_route_table( RouteTableId = RT, DryRun = DR )
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting route table %s for VPC %s.', RT, VPCID)
            else:
                logger.exception('Execution failed while removing routing table = %s. Error = %s.', RT, err )
                return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Check for security groups that are created/added after the default security group.
def ProcessSGs(ec2, VPCID, DR) -> int:
    response = ec2.describe_security_groups(Filters=[{'Name': 'vpc-id', 'Values': [VPCID]}])
    for entry in response['SecurityGroups']:
        if entry.get('GroupName') != 'default': # avoid error in attempting to remove default security group.
            SG = entry.get('GroupId', "")
            try:
                if len(SG) > 0:
                    logger.debug("In Try delete security group = %s and DR flag = %s", SG, DR)
                    ec2.delete_security_group(GroupId = SG, DryRun = DR)
            except ClientError as err:
                if err.response['Error']['Code'] == 'DryRunOperation':
                    logger.info('DRY RUN Deleting security group %s for VPC %s.', SG, VPCID)
                else:
                    logger.exception('Execution failed while removing security group ID = %s. Error = %s.', SG, err)
                    return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Check for NACLs that are not default (i.e. added NACLs) associated with the VPC
def ProcessNACL(ec2, VPCID, DR) -> int:
    response = ec2.describe_network_acls( Filters=[{'Name': 'vpc-id', 'Values': [VPCID]}])
    for entry in response['NetworkAcls']:
        if entry.get('IsDefault') == False:
            NACL = entry.get('NetworkAclId', "")
            try:
                if len(NACL) > 0:
                    logger.debug("In Try delete NACL = %s and DR flag = %s", NACL, DR)
                    ec2.delete_network_acl(NetworkAclId = NACL, DryRun = DR)
            except ClientError as err:
                if err.response['Error']['Code'] == 'DryRunOperation':
                    logger.info('DRY RUN Deleting network ACL %s for VPC %s.', NACL, VPCID)
                else:
                    logger.exception('Execution failed while removing network ACL %s. Error = %s', NACL, err)
                    return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Check for any peerings and ensure they are removed.
def ProcessVPCPeer(ec2, VPCID, DR) -> int:
    response = ec2.describe_vpc_peering_connections(
        Filters=[{'Name': 'accepter-vpc-info.vpc-id', 'Values': [VPCID]},
                 {'Name': 'requester-vpc-info.vpc-id', 'Values': [VPCID]}])
    for entry in response['VpcPeeringConnections']:
        PEER = entry.get('VpcPeeringConnectionId', "")
        try:
            if len(PEER) > 0:
                logger.debug("In Try delete peer connection = %s and DR flag = %s", PEER, DR)
                ec2.delete_vpc_peering_connection(VpcPeeringConnectionId= PEER, DryRun = DR)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting security groiup %s for VPC %s.', PEER, VPCID)
            else:
                logger.exception('Execution failed while removing VPC Peer ID', PEER, err)
                return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Check for VPN Gateways
def ProcessVPNGW(ec2, VPCID, DR) -> int:
    response = ec2.describe_vpn_gateways( Filters=[{'Name': 'attachment.vpc-id', 'Values': [VPCID]}])
    for entry in response['VpnGateways']:
        VPNGW = entry.get('VpnGatewayId', "")
        try:
            if len(VPNGW ) > 0:
                logger.debug("In Try delete VPN gateway = %s and DR flag = %s", VPNGW, DR)
                ec2.detach_vpn_gateway(VpnGatewayId=VPNGW, DryRun = DR)
                ProcessVPNs(ec2, VPNGW, DR, VPCID)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting VPN gateway %s for VPC %s.', VPNGW, VPCID)
            else:
                logger.exception('Execution failed while removing VPN GW = %s. Error = %s.', VPNGW, err)
                return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Check/Process VPNs
def ProcessVPNs(ec2, VPNGWID, DR, VPCID) -> int:
    response = ec2.describe_vpn_connections(Filters=[{'Name': 'vpn-gateway-id', 'Values': [VPNGWID]}])
    for entry in response['VpnConnections']:
        VPN = entry.get('VpnConnectionId', "")
        try:
            if len(VPN ) > 0:
                logger.debug("In Try delete VPN = %s and DR flag = %s", VPN, DR)
                ec2.delete_vpn_connection(VpnConnectionId= VPN, DryRun = DR)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting VPN %s for VPN GW ID %s in VPC %s.', VPN, VPNGWID, VPCID)
            else:
                logger.exception('Execution failed while removing VPN ID = %s., Error = %s.', VPN, err)
                return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Check and process NAT gateways
def ProcessNATGW(ec2, VPCID, DR) -> int:
    response = ec2.describe_nat_gateways( Filter=[{'Name': 'vpc-id', 'Values': [VPCID]}])
    for entry in response['NatGateways']:
        NATGW = entry.get('NatGatewayId', "")
        try:
            logger.debug("In Try delete NAT gateway = %s and DR flag = %s", NATGW, DR)
            if DR == False and len(NATGW) > 0:
                ec2.delete_nat_gateway(NatGatewayId=NATGW)
                # provide some polling and wait until deleted.  End after 1 minute.
                # Need to wait to avoid dependency issues with removal if IGW resources.
                for timer in (15, 15, 15, 15):
                    time.sleep(timer)
                    STATE = ""
                    r2 = ec2.describe_nat_gateways(Filter=[{'Name': 'nat-gateway-id', 'Values': [NATGW]}])
                    for e2 in r2['NatGateways']:
                        STATE = e2.get('State')
                    if STATE == 'deleted' or STATE == 'failed':
                        break
                if STATE != 'deleted' and STATE != 'failed': # get this if exhausted the timer loop
                    logger.warning('Deleting NAT gateway %s for VPC %s taking longer than expected.', NATGW, VPCID)
            else:
                logger.info('DRY RUN Deleting NAT gateway %s for VPC %s.', NATGW, VPCID)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting NAT gateway %s for VPC %s.', NATGW, VPCID)
            else:
                logger.exception('Execution failed while removing VPN GW = %s. Error = %s.', NATGW, err)
                return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Check and process VPC endpoints
def ProcessVPCEP(ec2, VPCID, DR) -> int:
    response = ec2.describe_vpc_endpoints(Filters=[{'Name': 'vpc-id', 'Values': [VPCID]}])
    VPCEP = []  # need to define as list type for the subsequent delete operation
    for entry in response['VpcEndpoints']:
        VPCEP.append(entry.get('VpcEndpointId'))
    if len(VPCEP) > 0:  # attempt the delete if items were found
        try:
            logger.debug("In Try delete VPC end point = %s and DR flag = %s", VPCEP, DR)
            ec2.delete_vpc_endpoints(VpcEndpointIds=VPCEP, DryRun = DR)
        except ClientError as err:
            if err.response['Error']['Code'] == 'DryRunOperation':
                logger.info('DRY RUN Deleting VPC endpoint %s for VPC %s.', VPCEP, VPCID)
            else:
                logger.exception('Execution failed while removing VPN GW = %s. Error = %s.', VPCEP, err)
                return -1
    return 0
# -------------------------------------------------------------------------------------------------
# Need to first establish a connection to a service in order to make subsequent calls
#
def main(P_DryRun, P_VPCID, P_Default, P_Force):
    if (isinstance(P_DryRun, bool) and isinstance(P_Default, bool) and isinstance(P_Force, bool)):
        pass
    else:
        return 1
    RC = 0
    VPC_Process_Count = 0
    RegionCount = 0
    ec2 = boto3.client('ec2')
    RegionList = ec2.describe_regions()
    for element in RegionList['Regions']:   # Get count of the regions to be processed.
        RegionCount += 1
    logger.debug('Found %s regioins. Starting VPC removal process...', RegionCount)
    for region in RegionList['Regions']:
        logger.debug("Region: %s", region['RegionName'])
        ec2R = boto3.client('ec2', region_name=region['RegionName'])
        VpcList = ec2R.describe_vpcs()
        for element in VpcList['Vpcs']:
            VPC = element.get('VpcId')
            DEFAULT_FLAG = element.get('IsDefault')
            if VPC == P_VPCID and DEFAULT_FLAG == False and P_Force == False:
                logger.error('VPC: %s is NOT default and --force is false - Bypassing VPC.', P_VPCID)
                break
            if (DEFAULT_FLAG == True and P_Default == True) or VPC == P_VPCID:
                logger.debug('Found a VPC: %s. Default flag: %s', VPC, DEFAULT_FLAG)
                RC = CheckCleanVPCResources(ec2R, VPC, P_DryRun)
                if RC == 0:
                    logger.info("VPC %s and all dependancies removed.", VPC)
                    VPC_Process_Count += 1
                else:
                    logger.error('Removal of VPC: %s in region: %s failed.', VPC, region['RegionName'])
    if VPC_Process_Count == 0 and len(P_VPCID) > 0:
        logger.warning("VPC %s NOT found - nothing processed.", P_VPCID)
        return 3
    else:
        logger.info("VPCs successfully processed = %s", VPC_Process_Count)
        return 0
#
def Exit_Out(rc):
    sys.exit(rc)

# -------------------------------------------------------------------------------------------------
# Setup code for main control and to catch any exception not handled by the other functions.
ExitCode = 9
if __name__ == "__main__":
    logger.debug('Module: %s', G_Module)
    logger.debug('In main with args: %s', sys.argv)     # at the end where debug logging could get turned on.
    if len(sys.argv) > 1:
        ExitCode = process_parms()
        if ExitCode:
            Exit_Out(ExitCode)
    try:
        ExitCode = main(G_DryRun, G_VPCID, G_Default, G_Force)
        Exit_Out(ExitCode)
    except Exception as err:
        logger.exception('Global execution catch all excecption. Error = %s', err)
        Exit_Out(9)
