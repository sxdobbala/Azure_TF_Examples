# Open Security Group
# Description: Checks if a Security Group has a Cidr of IPv4 0.0.0.0/0 or IPv6 ::/0
# Trigger Type: Configuration Change
# Scope of Changes: AWS::EC2::SecurityGroup
#
# This Python3 and Boto3 file assumes it will be ran as an AWS Lambda Function
# and ran with the minimum required Role. The Role that is used will be created
# in this Terraform example using the Data Source defined in the 
# lambda_open_security_group_compliance_exec_policy.tf as the Role Policy. 

import boto3, botocore, json

def evaluate_compliance(event):
    """ Function to evaluate the Event triggering the Lambda Function. The trigger is 
    assumed to be caused by a Configuration Change within a Security Group Resource, as
    defined in the Config rule in this example. 

    Evalulation is determined by the Security Group's Cidr IPv4 and IPv6 range. 
    If the IPv4 range is '0.0.0.0/0' or IPv6 range is '::/0', the Security Group 
    will be flagged as NON_COMPLIANT, else COMPLIANT.

    Arguments: 
    event -- The invokingEvent field of the event that triggered by Lambda.
        See "inovkingEvent" from link below for structure:
        https://docs.aws.amazon.com/config/latest/developerguide/evaluate-config_develop-rules_example-events.html

    Outputs:
    results -- An evaluations of a Security Group that can be used as a Config Evaluation.
        Each result will be in the strucure of the dictionary below:
        {
            'ComplianceResourceType': Compliance Resource Type,
            'ComplianceResourceId': Compliance Resource ID,
            'ComplianceType': Compliance Result (COMPLIANT or NON_COMPLIANT),
            'Annotation': Annotation/Message of the Result
                Ex: IP Permissions Ingress { 'List of Ingress IP Permissions' } IP Permissions Egress { 'List of Egress IP Permissions' },
            'OrderingTimestamp': Time the Event was Triggered
        }
    """

    # Collect the Security Group's details and IPv4/6 Permissions list.
    ec2 = boto3.client('ec2')
    securityGroup = ec2.describe_security_groups(GroupIds = [event['configurationItem']['resourceId']])
    ipPermissions = securityGroup['SecurityGroups'][0]['IpPermissions']
    ipPermissionsEgress = securityGroup['SecurityGroups'][0]['IpPermissionsEgress']
    complianceType = 'COMPLIANT'

    # Iterate through IPv4/6 Ingress Permissions, determining Compliancy and
    # annotation with the IPv4/6 Ranges. 
    annotation = ''
    annotation += 'IP Permissions Ingress { '
    for permissions in ipPermissions:
        for ipRange in permissions['IpRanges']:
            ip = ipRange['CidrIp']
            annotation += 'CidrIp: ' + ip + ' | '
            if ip == '0.0.0.0/0':
                complianceType = 'NON_COMPLIANT'
        for ipv6Range in permissions['Ipv6Ranges']:
            ip = ipv6Range['CidrIpv6']
            annotation += 'CidrIpv6: ' + ip + ' | ' 
            if ip == '::/0':
                complianceType = 'NON_COMPLIANT'
    annotation += '} '
    
    # Iterate through IPv4/6 Egress Permissions, determining Compliancy and
    # annotation with the IPv4/6 Ranges. 
    annotation += 'IP Permissions Egress { '
    for permissions in ipPermissionsEgress:
        for ipRange in permissions['IpRanges']:
            ip = ipRange['CidrIp']
            annotation += 'CidrIp: ' + ip + ' | '
            if ip == '0.0.0.0/0':
                complianceType = 'NON_COMPLIANT'
        for ipv6Range in permissions['Ipv6Ranges']:
            ip = ipv6Range['CidrIpv6']
            annotation += 'CidrIpv6: ' + ip + ' | ' 
            if ip == '::/0':
                complianceType = 'NON_COMPLIANT'
    annotation += '} ' 

    # Create the result of the Security Group and return it
    return { 'ComplianceResourceType': event['configurationItem']['resourceType'],
             'ComplianceResourceId': event['configurationItem']['resourceId'], 
             'ComplianceType': complianceType,
             'Annotation': annotation, 
             'OrderingTimestamp': event['notificationCreationTime'] }

def lambda_handler(event, context):
    """ Function used as the Lambda Function Handler.
    https://docs.aws.amazon.com/lambda/latest/dg/python-programming-model-handler-types.html

    Handler will call the evaluate_compliance function to determine the 
    Compliance result of the event that triggers this Lambda Function. The 
    results are then added as Config Evaluations.

    Arguments: 
    event -- AWS Lambda uses this parameter to pass in event data to the handler. 
        Event's resource should be of type Security Group.
    context -- AWS Lambda uses this parameter to provide runtime information to 
        your handler. This parameter is of the LambdaContext type.
    """

    # Collect the Invoking Event details and the Result Token
    invoking_event = json.loads(event["invokingEvent"])

    # Only evaluate if the resultToken exists
    if "resultToken" in event:
        result_token = event["resultToken"]

        # Call and collect evalulation result
        evaluation = evaluate_compliance(invoking_event)

        # Send results back to AWS Config API
        config = boto3.client('config')
        config.put_evaluations(
            Evaluations=[
                evaluation
            ],
            ResultToken=result_token
        )