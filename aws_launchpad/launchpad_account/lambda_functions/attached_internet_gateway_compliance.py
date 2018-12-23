# Attached Internet Gateway Compliance
# Description: Flags VPCs that have an internet gateway attached
# Trigger Type: Configuration Change
# Scope of Changes: AWS::EC2::InternetGateway
#
# This Python3 and Boto3 file assumes it will be ran as an AWS Lambda Function
# and ran with the minimum required Role. The Role that is used will be created
# in this Terraform example using the Data Source defined in the 
# lambda_attached_internet_gateway_compliance_exec_policy.tf as the Role Policy. 

import boto3, botocore, json

def evaluate_compliance(event):
    """ Function to evaluate the Event triggering the Lambda Function. The trigger is 
    assumed to be caused by a Configuration Change within a VPC Resource, as
    defined in the Config rule in this example. 

    Evalulation is determined by the relationship of a VPC and an Internet 
    Gateway resources. If there's a relationship of between both resources, the 
    VPC is NON_COMPLIANT, else COMPLIANT

    Arguments: 
    event -- The invokingEvent field of the event that triggered by Lambda.
        See "inovkingEvent" from link below for structure:
        https://docs.aws.amazon.com/config/latest/developerguide/evaluate-config_develop-rules_example-events.html

    Outputs:
    results -- An array of evaluations of a VPC that can be used as a Config Evaluation.
        Each result will be in the strucure of the dictionary below:
        {
            'ComplianceResourceType': Compliance Resource Type,
            'ComplianceResourceId': Compliance Resource ID,
            'ComplianceType': Compliance Result (COMPLIANT or NON_COMPLIANT),
            'Annotation': Annotation/Message of the Result,
            'OrderingTimestamp': Time the Event was Triggered
        }
    """

    # Collect the relationships under the VPC resource
    relationships = event['configurationItem']['relationships']
    results = []

    # Iterate through each relationship and flag if there's a relationship
    # between the VPC and an Internet Gateway, assigning fields used for the result
    complianceType = 'COMPLIANT'
    annotation = ''
    ig_count = 0
    for relationship in relationships:
        resourceType = relationship['resourceType']
        if resourceType == 'AWS::EC2::InternetGateway':
            complianceType = 'NON_COMPLIANT'
            annotation += '{ ' + relationship['name'] + ' | Internet Gateway: ' + relationship['resourceId'] + ' } '
            ig_count += 1

    # If there is more than one InternetGateway relationship per VPC, add a warning in the annotation
    if ig_count > 1:
        annotation += '** ERROR: There should be at most 1 Internet Gatweway attached. Number of Internet Gateways found: ' + ig_count + ' **'

    # Create the result of the VPC and return it
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

def lambda_handler(event, context):
    """ Function used as the Lambda Function Handler.
    https://docs.aws.amazon.com/lambda/latest/dg/python-programming-model-handler-types.html

    Handler will call the evaluate_compliance function to determine the 
    Compliance result of the event that triggers this Lambda Function. The 
    results are then added as Config Evaluations.

    Arguments: 
    event -- AWS Lambda uses this parameter to pass in event data to the handler. 
        Event's resource should be of type VPC.
    context -- AWS Lambda uses this parameter to provide runtime information to 
        your handler. This parameter is of the LambdaContext type.
    """

    # Collect the Invoking Event details and the Result Token
    invoking_event = json.loads(event["invokingEvent"])

    # Only evaluate if the resultToken exists
    if "resultToken" in event:
        result_token = event["resultToken"]

        # Call and collect evalulation results
        evaluations = evaluate_compliance(invoking_event)

        # Send results back to AWS Config API
        config = boto3.client('config')
        for result in evaluations:
            config.put_evaluations(
                Evaluations=[
                    result
                ],
                ResultToken=result_token
            )