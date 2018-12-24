#!/usr/bin/env groovy
package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def call(Config config, Object deploymentBranchSetting)
{
    def notify = new Notify()
    def mavenBuild = new MavenBuild()
    def lambda = deploymentBranchSetting.lambda
    def basePipeline = new BasePipeline()
    def envs = lambda.deployToEnv
    def customExecution = new CustomScriptExecution()

    try
    {
        if(deploymentBranchSetting.lambda.preExecutionScript != null)
        {
            if(deploymentBranchSetting.lambda.preExecutionScript.commandset != null)
            {
                customExecution.customExecutionLogic(deploymentBranchSetting.lambda.preExecutionScript.commandset,deploymentBranchSetting.lambda.preExecutionScript.credentials)
            }
        }

        for (i = 0; i < envs.size(); i++)
        {
            def deployToEnv = envs[i]
            def envName = deployToEnv.envName

            stage("Update lambda function in the ${envName} environment")
            {
                if (lambda.deployPrompts && lambda.deployPrompts == true)
                {
                    def deployPromptAbortTimeout = Helpers.getTimeout(lambda.deployPromptAbortTimeout)
                    timeout(time: deployPromptAbortTimeout, unit: 'MINUTES')
                    {
                        input "Update lambda function ${lambda.functionName} in the ${envName} environment?"
                    }
                }

                basePipeline.awsAuth(deployToEnv.awsCredentialsId, deployToEnv.awsAccountId)

                // Update function but don't publish yet.
                def output = sh(script:". /etc/profile.d/jenkins.sh >/dev/null 2>&1 && aws lambda update-function-code --function-name ${lambda.functionName} --s3-bucket ${lambda.s3Bucket} --s3-key ${lambda.s3Key} --profile saml", returnStdout: true).trim()
                def outputObject = Helpers.jsonParse(output)
                def revisionId = outputObject.RevisionId

                // Run test
                checkout scm
                def outFile = "testOutput.json"
                output = sh(script:". /etc/profile.d/jenkins.sh >/dev/null 2>&1 && aws lambda invoke --function-name ${lambda.functionName} --invocation-type RequestResponse --payload file://${lambda.testInvocationFile} ${outFile} --profile saml", returnStdout: true).trim()
                outputObject = Helpers.jsonParse(output)
                def functionError = outputObject.FunctionError

                archiveArtifacts artifacts: outFile

                if (functionError && functionError.trim().length() > 0)
                {
                    def testOutput = readJSON file: outFile
                    error "The test invocation of the function ${lambda.functionName} failed with error: ${testOutput.errorMessage}"
                }

                // Check tags
                def promotedEnvironments = sh(script: ". /etc/profile.d/jenkins.sh >/dev/null 2>&1 && aws s3api get-object-tagging --bucket ${lambda.s3Bucket} --key ${lambda.s3Key} --query 'TagSet[?Key==`promotedEnvironments`].Value' --output text --profile saml", returnStdout: true).trim()

                if (envName == "dev")
                {
                    if (promotedEnvironments.length() == 0)
                    {
                        promotedEnvironments = "dev"
                    }
                }
                else if (envName == "test")
                {
                    if (!promotedEnvironments.contains("dev"))
                    {
                        error "The function ${lambda.functionName} has to be promoted to dev before promoting to test."
                    }
                    else if (!promotedEnvironments.contains("test"))
                    {
                        promotedEnvironments += ",test"
                    }
                }
                else if (envName == "stage")
                {
                    if (!promotedEnvironments.contains("test"))
                    {
                        error "The function ${lambda.functionName} has to be promoted to test before promoting to stage."
                    }
                    else if (!promotedEnvironments.contains("stage"))
                    {
                        promotedEnvironments += ",stage"
                    }
                }

                // Publish function
                output = sh(script:". /etc/profile.d/jenkins.sh >/dev/null 2>&1 && aws lambda publish-version --function-name ${lambda.functionName} --description ${deployToEnv.version} --revision-id ${revisionId} --profile saml", returnStdout: true).trim()
                outputObject = Helpers.jsonParse(output)
                def functionVersion = outputObject.Version

                // Update tags
                sh"""
                  . /etc/profile.d/jenkins.sh
                  aws s3api put-object-tagging --bucket ${lambda.s3Bucket} --key ${lambda.s3Key} --tagging 'TagSet=[{Key=version,Value=${deployToEnv.version}},{Key=promotedEnvironments,Value=${promotedEnvironments}}]' --profile saml
                """

                if (deployToEnv.deployType && deployToEnv.deployType == "AllAtOnce" || deployToEnv.initialWeight == null || deployToEnv.initialWeight == 100)
                {
                    sh '''
                        . /etc/profile.d/jenkins.sh >/dev/null 2>&1
                        exists=`aws lambda get-alias --function-name ''' + lambda.functionName + ''' --name ''' + deployToEnv.alias + ''' --profile saml | tail -1 | awk \' { print $1 } \'`
                        if [ -z "$exists" ]; then
                            aws lambda create-alias --function-name ''' + lambda.functionName + ''' --name ''' + deployToEnv.alias + ''' --function-version ''' + functionVersion + ''' --profile saml
                        else
                            aws lambda update-alias --function-name ''' + lambda.functionName + ''' --name ''' + deployToEnv.alias + ''' --function-version ''' + functionVersion + ''' --profile saml
                        fi
                    '''
                }
                else
                {
                    def initialWeight = deployToEnv.initialWeight / 100.0
                    def duration = (deployToEnv.duration == null ? 0 : deployToEnv.duration * 60)
                    def previousVersion = functionVersion.toInteger() - 1

                    sh '''
                        . /etc/profile.d/jenkins.sh >/dev/null 2>&1
                        exists=`aws lambda get-alias --function-name ''' + lambda.functionName + ''' --name ''' + deployToEnv.alias + ''' --profile saml | tail -1 | awk \' { print $1 } \'`
                        if [ -z "$exists" ]; then
                            aws lambda create-alias --function-name ''' + lambda.functionName + ''' --name ''' + deployToEnv.alias + ''' --function-version ''' + previousVersion + ''' --routing-config "AdditionalVersionWeights={''' + functionVersion + '''=''' + initialWeight + '''}" --profile saml
                        else
                            aws lambda update-alias --function-name ''' + lambda.functionName + ''' --name ''' + deployToEnv.alias + ''' --function-version ''' + previousVersion + ''' --routing-config "AdditionalVersionWeights={''' + functionVersion + '''=''' + initialWeight + '''}" --profile saml
                        fi
                    '''
                                                    
                    def startTime = new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone('UTC'))
                    sleep duration
                    def endTime = new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone('UTC'))

                    String metricsFile = "metric-data-queries.json"
                    generateMetricDataQueries(lambda.functionName, deployToEnv.alias, metricsFile)
                    output = sh(script:". /etc/profile.d/jenkins.sh >/dev/null 2>&1 && aws cloudwatch get-metric-data --metric-data-queries file://${metricsFile} --start-time ${startTime} --end-time ${endTime} --profile saml", returnStdout: true).trim()
                    outputObject = Helpers.jsonParse(output)
                    def values = outputObject.MetricDataResults[0].Values[0]

                    if (values == null || values.trim().length() == 0) // no errors
                    {
                        sh"""
                          . /etc/profile.d/jenkins.sh
                          aws lambda update-alias --function-name ${lambda.functionName} --name ${deployToEnv.alias} --function-version ${functionVersion} --routing-config '{}' --profile saml
                        """
                    }
                    else
                    {
                        error "Unable to route all traffic to the new version of the lambda function ${lambda.functionName} in the ${envName} environment because the function invocation caused errors."
                    }
                }
            }
        }
		
        if(deploymentBranchSetting.lambda.postExecutionScript != null)
        {
            if(deploymentBranchSetting.lambda.postExecutionScript.commandset != null)
            {
                customExecution.customExecutionLogic(deploymentBranchSetting.lambda.postExecutionScript.commandset,deploymentBranchSetting.lambda.postExecutionScript.credentials)
            }
        }
    }
    catch (err)
    {
        notify.call(config, "Failed ${err}")
        currentBuild.result = 'FAILURE'
        throw err
    }
}

def validatePrerequisites(Config config, Object deploymentBranchSetting)
{
    def notify = new Notify()
    def lambda = deploymentBranchSetting.lambda
    def envs = lambda.deployToEnv
    String validationError = ""
    def basePipeline = new BasePipeline()

    try
    {
        stage("Lambda prerequisites validation")
        {
            // Validation of supported runtime -- this should come out of the loop
            if (lambda.runtime != "java8")
            {
                validationError += "Currently, we only support java8 runtime. The supplied runtime ${lambda.runtime} is not supported.\n"
            }

            // S3 bucket with proper permission and bucket policy. S3 is used as a repository to manage the lambda artifacts [use terraform module to get this bucket] . S3 bucket key name is preferred configuration to support single s3 bucket to store artifacts for multiple lambda functions.
            basePipeline.awsAuth(lambda.s3CredentialsId, lambda.s3AccountId)

            try
            {
                sh """
                    . /etc/profile.d/jenkins.sh >/dev/null 2>&1
                    set +x
                    aws s3 ls ${lambda.s3Bucket} --profile saml
                """
            }
            catch (err)
            {
                validationError += "The S3 bucket ${lambda.s3Bucket} either does not exist or the provided credential does not have read access on the bucket.\n${err}\n"
            }

            try
            {
                sh """
                    . /etc/profile.d/jenkins.sh >/dev/null 2>&1
                    set +x
                    touch ./testFile
                    aws s3 cp ./testFile s3://${lambda.s3Bucket} --profile saml --dryrun
                """
            }
            catch (err)
            {
                validationError += "The provided credential does not have write access on the bucket ${lambda.s3Bucket}.\n${err}\n"
            }

            for (i = 0; i < envs.size(); i++)
            {
                def deployToEnv = envs[i]
                def envName = deployToEnv.envName

                basePipeline.awsAuth(deployToEnv.awsCredentialsId, deployToEnv.awsAccountId)

                // Lambda function is setup using Terraform with necessary event source integration IAM role, handler, VPC (optional), Memory, timeout configurations
                def functionJson = sh(script: ". /etc/profile.d/jenkins.sh >/dev/null 2>&1 && aws lambda get-function --function-name ${lambda.functionName} --profile saml", returnStdout: true).trim()

                if (functionJson == null || functionJson.trim().length() == 0)
                {
                    validationError += "The lambda function ${lambda.functionName} does not exist in the ${envName} environment.\n"
                }

                def functionObject = Helpers.jsonParse(functionJson)
                validationError += checkValue(lambda.functionName, "Role", functionObject.Configuration.Role)
                validationError += checkValue(lambda.functionName, "Handler", functionObject.Configuration.Handler)
                validationError += checkValue(lambda.functionName, "Memory size", Integer.toString(functionObject.Configuration.MemorySize))
                validationError += checkValue(lambda.functionName, "Timeout", Integer.toString(functionObject.Configuration.Timeout))

                // 3. Strict source control version management: We should adhere to Semantic Versioning Specification, i.e., version should include MAJOR.MINOR.PATCH Ex 2.1.0 MAJOR version when you make incompatible API changes, MINOR version when you add functionality in a backwards-compatible manner, PATCH version when you make backwards-compatible bug fixes
                def version = deployToEnv.version.split(/\./)
                if (version.size() != 3)
                {
                    validationError += "Provided version ${deployToEnv.version} in the ${envName} environment is not valid. Version must of the format MAJOR.MINOR.PATCH. Example 2.1.0\n"
                }
            }

            if (validationError.trim().length() > 0)
            {
                error "Lambda prerequisites validation failed:\n${validationError}"
            }
        }
    }
    catch (err)
    {
        notify.call(config, "Failed ${err}")
        currentBuild.result = 'FAILURE'
        throw err
    }
}

String checkValue(String functionName, String attribute, String value)
{
    return ((value == null || value.trim().length() == 0) ? "Lambda function ${functionName} is not setup with with the proper ${attribute}.\n" : "")
}

def generateMetricDataQueries(String functionName, String alias, String fileName)
{
    String json = '' +
            '[{' +
            '    "Id": "m1",' +
            '    "MetricStat": {' +
            '        "Metric": {' +
            '            "Namespace": "AWS/Lambda",' +
            '            "MetricName": "Errors",' +
            '            "Dimensions": [{' +
            '                "Name": "FunctionName",' +
            '                "Value": "' + functionName + '"' +
            '            }, {' +
            '                "Name": "Resource",' +
            '                "Value": "' + alias + '"' +
            '            }]' +
            '        },' +
            '        "Period": 60,' +
            '        "Stat": "SampleCount",' +
            '        "Unit": "Count"' +
            '    }' +
            '}]'

    writeFile file: fileName, text: json
}