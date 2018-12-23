#!/usr/bin/python
import boto3
import sys, getopt
from botocore.client import ClientError
from optparse import OptionParser

# Called from main or from within another python class/script (testing)
# Enables Enterprise Support for an AWS Account through a Support Case
def enable(argv):

    # Get arguments
    parser = OptionParser()    
    parser.add_option("-a", "--account", action="store", dest="account", type="string",
        help="AWS Account ID to Enable Support")
    parser.add_option("-t", "--test", action="store_true", dest="test",
        help="Flag to indicate Enable Support Test Run")
    (options, args) = parser.parse_args(argv)
    if len(args) != 0 :
        print("Please only provide the '-a'/'--account' flag with the AWS Account or the '-t'/'--test' flag. Exiting.")
        return

    # Populate case fields
    account = ""
    subject = ""
    communicationBody = ""
    if options.account != None :
        account = options.account
        subject = "Enterprise Support on Account " + account
        communicationBody = "Please enable Enterprise Support on Account Number: " + account
    else :
        print("No account provided, please provide the '-a'/'--account' flag with the AWS Account. Exiting.")
        return 
    print("Subject: " + subject)
    print("Communcication Body: " + communicationBody)

    # Boto 3 Clients
    support_client = boto3.client('support')

    # Attempt to create a case if not a test run
    if not options.test :
        # Check if there's already a support ticket created
        cases = support_client.describe_cases()
        for case in cases['cases'] :
            if case['subject'] == subject :
                print("Existing Case ID for " + account + " : " + case['caseId'])
                return case['caseId'], subject, communicationBody

        # Create a case to enable customer support
        response = support_client.create_case(
            subject=subject,
            serviceCode='customer-account', 
            severityCode='4',
            categoryCode='change-account-details',
            communicationBody=communicationBody,
            issueType='customer-service',
            ccEmailAddresses=['iac_support@optum.com']
        )
        print("New Case ID for " + account + " : " + response['caseId'])
        return response['caseId'], subject, communicationBody
    # If testing, do nothing
    else :
        print("Test Run - No Case Created")
        return "", subject, communicationBody

# Main call used when running through command line
def main(argv):
    return enable(argv)

# If the script is called through the command line, pass in the command line args
if __name__ == "__main__":
   main(sys.argv[1:])