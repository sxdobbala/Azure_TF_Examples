#!/bin/sh

# Set local variable to Subscription ID, passed in from Jenkinsfile
PROGNAME=$0
SubID=$1

# Create policy definitions, create the initiatives, and assign the initiatives

sh ./create_policy_definitions.sh
sh ./create_policy_initiative_definitions.sh $SubID
sh ./assign_policy_initiatives.sh