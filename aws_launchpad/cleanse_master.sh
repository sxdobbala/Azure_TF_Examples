#/bin/sh

#
# This is a simple script to deal with clean up of aws_config and aws_launchpad terraform template in a scorched earth way. YMMV.
# This script needs to be kept in sync with the resources generated in aws_config and in aws_launchpad in order to ensure that we 
# are deleting only those resources created by aws_config and aws_launchpad.
# expects $1 to be the account number.

# check if the account provided is valid format
if [ $# -ne 1 ] ; then
    echo "Need one argument: 12 Digit AWS Account ID to cleanse"
    exit 1
elif ! [[ "$1" =~ ^[0-9]{12}$ ]] ; then
    echo "$1 is an invalid 12 Digit AWS Account ID"
    exit 1
fi

# Clean up backend
aws s3 rm s3://$1-tfstate-launchpad-master-test --recursive
aws dynamodb delete-table --table-name $1-tflock-launchpad-master-test

# Clean up IAM
aws iam detach-role-policy --role-name LambdaExec-test_lambda_invoker --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
aws iam delete-role-policy --role-name LambdaExec-test_lambda_invoker --policy-name ComputeInvokerPolicy
aws iam delete-role-policy --role-name LambdaExec-test_lambda_invoker --policy-name LambdaInvokerPolicy
aws iam delete-role --role-name LambdaExec-test_lambda_invoker
aws iam detach-role-policy --role-name LambdaExec-lambda_invoker-test --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
aws iam delete-role-policy --role-name LambdaExec-lambda_invoker-test --policy-name ComputeInvokerPolicy
aws iam delete-role-policy --role-name LambdaExec-lambda_invoker-test --policy-name LambdaInvokerPolicy
aws iam delete-role --role-name LambdaExec-lambda_invoker-test

# Clean up lambda
aws lambda delete-function --function-name test_lambda_invoker
aws lambda delete-function --function-name lambda_invoker-test
aws events remove-targets --rule scheduler-for-lambda-invoker-test --ids EC2MetadataCollectionLambda
aws events delete-rule --name scheduler-for-lambda-invoker-test