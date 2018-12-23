import hudson.model.*
import groovy.json.JsonSlurperClassic
import com.optum.commercialcloud.pipeline.launchpad.BaseAdapter

/**
    Implemenatation of the BaseAdapter Class for AWS

    AWSAdapter is used within the BaseLaunchpadPipeline in executing Launchpad
    with the provided AWS Central and Customer Accounts. AWSAdapter.groovy file 
    is loaded in with the Jenkins 'load' function, as defined in the Jenkinsfile 
    pipeline. 

    Please see the doc for more information about the 'load' function:
    https://jenkins.io/doc/pipeline/steps/workflow-cps/#code-load-code-evaluate-a-groovy-source-file-into-the-pipeline-script
*/
class AWSAdapter extends BaseAdapter {
    def version = 'v0.1.0'
    def terraformVersion = '0.11.7'
    def pythonVersion = '3.6'
    def region = "us-east-1"
    def jenkins

    /**
        Default constructor
    */
    AWSAdapter(jenkins) {
        super(jenkins)
        this.jenkins = jenkins
    }

    /**
        Apply central_managment_account and central_eis_account terraforms with 
        the provided configuration. 

        masterAccountName : The Master Account Name (ex. PR, POC, Enterprise)
        config : The Configuration Object containing the Master Account's details
            derived from a Yaml file
    */
    @Override
    def deployCentralManagement(masterAccountName, config) {
        def customerAccounts = []
        def centralAccount = "${config.accountsInfo.central}" 
        def centralSecurity = "${config.accountsInfo.securityCentral}" 
        def masterAccount = "${config.accountsInfo.master}" 
        def cloudtrailBucket = "cloudtrail-${centralAccount}-logg-rcloudtrailbucket" 
        def launchpadBackendBucket = "launchpad-tfstate-${centralAccount}" 
        def configBucketName = "cc-cfg-central-${centralAccount}" 
        def lambdaBucketName = "cc-lambda-central-${centralAccount}"
        def successCentral = true 
        def successSecurity = true
        def successMaster = true
        def statusCentral = "Successful"
        def statusSecurity = "Successful"
        def statusMaster = "Successful"
        def returnData = []
        def activeAccountList = []
        this.jenkins.echo "This is the CHANGE_AUTHOR env variable:  ${this.jenkins.env.CHANGE_AUTHOR}"
        
        this.jenkins.withCredentials([
            this.jenkins.string(credentialsId: config.credentialsKey, variable: 'AWS_MASTER_KEY'), 
            this.jenkins.string(credentialsId: config.credentialsSecret, variable: 'AWS_MASTER_SECRET')])
        {
            activeAccountList = getActiveAccounts(this.jenkins.AWS_MASTER_KEY, this.jenkins.AWS_MASTER_SECRET)
        }
        // Deploy to Central Management/Logging
        try {
            // first time stash because we want to do so based on AWS vs. base adapter and create dependancy 
            this.jenkins.stash includes: 'tests/**', name: 'tests'
            this.jenkins.stash includes: 'launchpad_account/**', name: 'launchpad_account' 
            this.jenkins.stash includes: 'cleanse_launchpad.sh', name: 'clean_launchpad' 
            this.jenkins.stash includes: 'cleanse_master.sh', name : 'clean_master'

            //this.jenkins.echo "In deploy Central Management - happy day!"
            config.accountsInfo.customers.each { accountCategory, accountCategoryObj ->
                // proactively add all customer account categories
                accountCategoryObj.accounts.each { account ->
                    // Ensure only active accounts are added to the list
                    if (activeAccountList.contains(account)) 
                    {
                        customerAccounts.add(account)
                    }
                    else 
                    {
                        this.jenkins.echo "Account: ${account} is NOT active for Central Management deployment - bypassing."
                    }
                }
            }

            // assume role for central management acct 
            this.jenkins.withCredentials([
                this.jenkins.string(credentialsId: config.credentialsKey, variable: 'AWS_MASTER_KEY'), 
                this.jenkins.string(credentialsId: config.credentialsSecret, variable: 'AWS_MASTER_SECRET')])
            {
                assumeRole(centralAccount, this.jenkins.AWS_MASTER_KEY, this.jenkins.AWS_MASTER_SECRET)
            }

            // if we are working on a PR, then clean up the account.
            if (this.jenkins.env.CHANGE_ID) {
                cleanseLaunchPad(centralAccount)
            }

            successCentral = prepareCentralManagementAccount(centralAccount, masterAccount, customerAccounts, cloudtrailBucket, 
                launchpadBackendBucket, configBucketName, lambdaBucketName, this.jenkins.env.CHANGE_ID)

            // Sleep to ensure the bucket policies are updated between workspace and AWS
            // Don't bother to sleep on a PR to keep the pipe moving as fast as possible.
            if (this.jenkins.env.CHANGE_ID) {
                this.jenkins.echo "Skipping sleep on a PR request."
            } else {
                this.jenkins.sleep(180)   // this number is somewhat arbitrary based on observation
            }
        }
        catch (e) {
            this.jenkins.echo "EXCEPTION: ${e}"
            successCentral = false
            statusCentral = "Unsuccessful"
            if (this.jenkins.env.CHANGE_ID) { throw e }
        }
        returnData.add(CentralAccount: centralAccount, Status: statusCentral)

        // Deploy to Central Security
        try {

            // assume role for security acct 
            this.jenkins.withCredentials([
                this.jenkins.string(credentialsId: config.credentialsKey, variable: 'AWS_MASTER_KEY'), 
                this.jenkins.string(credentialsId: config.credentialsSecret, variable: 'AWS_MASTER_SECRET')])
            {
                assumeRole(centralSecurity, this.jenkins.AWS_MASTER_KEY, this.jenkins.AWS_MASTER_SECRET)
            }

            successSecurity = prepareCentralEISAccount(masterAccountName, centralSecurity, this.jenkins.env.CHANGE_ID)

            // Sleep to ensure the bucket policies are updated between workspace and AWS
            // Don't bother to sleep on a PR to keep the pipe moving as fast as possible.
            if (this.jenkins.env.CHANGE_ID) {
                this.jenkins.echo "Skipping sleep on a PR request."
            } else {
                this.jenkins.sleep(180)   // this number is somewhat arbitrary based on observation
            }
        }
        catch (e) {
            this.jenkins.echo "EXCEPTION: ${e}"
            successSecurity = false
            statusSecurity = "Unsuccessful"
            if (this.jenkins.env.CHANGE_ID) { throw e }
        }
        returnData.add(SecurityAccount: centralSecurity, Status: statusSecurity)

        // Deploy to Master Account
        try {
            
            // Set the Master credentials for AWS and get the Account ID
            this.jenkins.withCredentials([
                this.jenkins.string(credentialsId: config.credentialsKey, variable: 'AWS_MASTER_KEY'), 
                this.jenkins.string(credentialsId: config.credentialsSecret, variable: 'AWS_MASTER_SECRET')])
            {
                this.jenkins.env.AWS_ACCESS_KEY_ID = this.jenkins.AWS_MASTER_KEY
                this.jenkins.env.AWS_SECRET_ACCESS_KEY = this.jenkins.AWS_MASTER_SECRET
                this.jenkins.env.AWS_SESSION_TOKEN = ""
            }

            // Prepare/apply the Master account
            successMaster = prepareMasterAccount(masterAccount, centralAccount, this.jenkins.env.CHANGE_ID)
        }
        catch (e) {
            this.jenkins.echo "EXCEPTION: ${e}"
            successMaster = false
            statusMaster = "Unsuccessful"
            if (this.jenkins.env.CHANGE_ID) { throw e }
        }
        returnData.add(MasterAccount: masterAccount, Status: statusMaster)

        // Return 
        return [successCentral && successSecurity && successMaster, returnData]
    }

    /** 
        Apply launchpad_account terraforms on the accounts in the provided 
        Master Account configuration.

        nodeLabel : Node Label used to execute Launchpad
        masterAccount : The Master Account Name (ex. PR, POC, Enterprise)
        masterAccountConfig : The Configuration Object containing the Master Account's details
            derived from a Yaml file
    */
    @Override
    def deployLaunchpadOnAccount(nodeLabel, masterAccount, masterAccountConfig, singleAccount, deleteDefaultVPCs) {
        this.jenkins.echo 'AWS adapter: deployLaunchpadOnAccount()'
        def isEnterprise = "Enterprise".equals(masterAccount)
        def centralAccount = "${masterAccountConfig.accountsInfo.central}" 
        def centralSecurityAccount = "${masterAccountConfig.accountsInfo.securityCentral}" 
        def centralLoggingBucketName = "cloudtrail-${centralAccount}-logg-rcloudtrailbucket" 
        def backendBucketName = "launchpad-tfstate-${centralAccount}" 
        def configBucketName = "cc-cfg-central-${centralAccount}"
        def destroy = false
        def success = true
        def successLaunchpad = true
        def successEnterpriseSupport = true
        def successRemoveDefaultVPC = true
        def status = "Successful"
        def data = []
        def returnData = []
        def activeAccountList = []
        this.jenkins.node(nodeLabel) {
            this.jenkins.withCredentials([
                this.jenkins.string(credentialsId: masterAccountConfig.credentialsKey, variable: 'AWS_MASTER_KEY'), 
                this.jenkins.string(credentialsId: masterAccountConfig.credentialsSecret, variable: 'AWS_MASTER_SECRET')])
            {
                activeAccountList = getActiveAccounts(this.jenkins.AWS_MASTER_KEY, this.jenkins.AWS_MASTER_SECRET)
            }
        }
        masterAccountConfig.accountsInfo.customers.each { accountCategory, accountCategoryObj ->

            if (!isValidAccountCategory(accountCategory)) {
                // Unrecognized category, skip processing
                return [false, returnData]
            }

            this.jenkins.stage("${masterAccount} - ${accountCategory}: Apply Launchpad") {
                // Manual override to run based on yml file
                if ((masterAccountConfig.prompt == null || masterAccountConfig.prompt || accountCategoryObj.prompt) 
                    && !(this.jenkins.env.CHANGE_ID)) {
                    this.jenkins.timeout(time: 10, unit: 'DAYS') {
                        this.jenkins.input "Apply Launchpad on: ${masterAccount} - ${accountCategoryObj}?"
                    }
                }
                this.jenkins.node(nodeLabel) {
                    data = []
                    accountCategoryObj.accounts.each { account ->
                        if (!activeAccountList.contains(account))   // Check account in list against current active list
                        {
                            this.jenkins.echo "Account: ${account} is NOT active for launchpad deployment - bypassing."
                            return                            // in this context acts like continue in a for loop
                        }
                        this.jenkins.echo "Deploy launchpad to ${account}"
                        // assume role for each account
                        try {
                            status = "Successful"
                            this.jenkins.withCredentials([
                                this.jenkins.string(credentialsId: masterAccountConfig.credentialsKey, variable: 'AWS_MASTER_KEY'), 
                                this.jenkins.string(credentialsId: masterAccountConfig.credentialsSecret, variable: 'AWS_MASTER_SECRET')])
                            {
                                assumeRole(account, this.jenkins.AWS_MASTER_KEY, this.jenkins.AWS_MASTER_SECRET)
                            }

                            // if we are working on a PR, then clean up the account. 
                            if (this.jenkins.env.CHANGE_ID) { 
                                // we have already done a cleanse on the master account. doing it again will 
                                // destroy the central logging config we just set up. 
                                if (account != centralAccount) { 
                                    cleanseLaunchPad(account) 
                                } 
                                destroy = true
                                // this is somewhat arbitrary - to give time for config rules to delete
                                this.jenkins.sleep(60)
                            }

                            // Apply Launchpad
                            successLaunchpad = true
                            successEnterpriseSupport = true
                            successRemoveDefaultVPC = true
                            successLaunchpad = applyLaunchpad(account, centralLoggingBucketName, centralAccount, 
                                centralSecurityAccount, backendBucketName, configBucketName, destroy)

                            // Check if a single account and delete default vpc boolean is true
                            if (successLaunchpad && singleAccount != null && deleteDefaultVPCs) // we do this b/c of prev IF statement checking singleAccount
                            {
                                successRemoveDefaultVPC = removeDefaultVPCs(account)
                            }

                            // Check if Enterprise Support is enabled in the account
                            successEnterpriseSupport = enableEnterpriseSupport(account, masterAccountConfig.credentialsKey, masterAccountConfig.credentialsSecret)

                            // Succesfull launchpad and break glass application required
                            if(!successLaunchpad || !successEnterpriseSupport || !successRemoveDefaultVPC) {
                                success = false
                                status = "Unsuccessful"
                            }
                        }
                        catch (e) {
                            this.jenkins.echo "EXCEPTION AT ${account}: ${e}"
                            success = false
                            status = "Unsuccessful"
                            if (this.jenkins.env.CHANGE_ID) { throw e }
                        }
                        finally {
                            data.add(Account: account, Status: status)
                        }
                    }
                }
                returnData.add(Environment: accountCategory, Data: data)
            }
        }
        return [success, returnData]
    }

    /**
        This function runs based on the fact the appropriate master account credentials are 
        pulled from the Jenkins key store to establish a temporary key in order for this 
        process to assume an administrative role into the target AWS account in order to
        perform the required launch pad tasks and configurations. 

        accountNumber : The AWS Account ID to assume OrganizationAccountAccessRole into
        masterKey : The Secret ID Key to assume role 
        masterSecret : The Secret Pass Key to assume role
    */
    def assumeRole(accountNumber, masterKey, masterSecret) {
        def AWS_LOC = '$HOME/.aws'
        this.jenkins.env.AWS_ACCESS_KEY_ID = ""
        this.jenkins.env.AWS_SECRET_ACCESS_KEY = ""
        this.jenkins.env.AWS_SESSION_TOKEN = ""
        this.jenkins.sh """#!/bin/sh
        . /etc/profile.d/jenkins.sh
        export TERRAFORM_VERSION=${terraformVersion}
        #set -x
        # Make empty credentials files for AWS
        rm -rf ${AWS_LOC}
        mkdir ${AWS_LOC}
        cd ${AWS_LOC} 
        echo ${AWS_LOC} 
        echo "[default]" > config
        echo "region=${region}" >> config
        echo "output=text" >> config
        # write to creds file 
        echo "[default]" > credentials
        echo "aws_access_key_id = ${masterKey}" >> credentials
        echo "aws_secret_access_key = ${masterSecret}" >> credentials
        # assume the role in the desired account
        aws sts assume-role --role-arn arn:aws:iam::${accountNumber}:role/OrganizationAccountAccessRole --role-session-name session-${accountNumber} > ${this.jenkins.env.WORKSPACE}/creds.txt
        echo "AWS_ACCESS_KEY_ID="`awk '{ if ( NR == 2 ) print \$2 }' ${this.jenkins.env.WORKSPACE}/creds.txt` > ${this.jenkins.env.WORKSPACE}/creds2.properties
        echo "AWS_SECRET_ACCESS_KEY="`awk '{ if ( NR == 2 ) print \$4 }' ${this.jenkins.env.WORKSPACE}/creds.txt` >> ${this.jenkins.env.WORKSPACE}/creds2.properties
        echo "AWS_SESSION_TOKEN="`awk '{ if ( NR == 2 ) print \$5 }' ${this.jenkins.env.WORKSPACE}/creds.txt` >> ${this.jenkins.env.WORKSPACE}/creds2.properties
        """
        def props = this.jenkins.readProperties file: 'creds2.properties'
        this.jenkins.env.AWS_ACCESS_KEY_ID = props.AWS_ACCESS_KEY_ID
        this.jenkins.env.AWS_SECRET_ACCESS_KEY = props.AWS_SECRET_ACCESS_KEY
        this.jenkins.env.AWS_SESSION_TOKEN = props.AWS_SESSION_TOKEN
        this.jenkins.sh "rm -f creds2.properties"
        this.jenkins.sh "rm -f creds.txt"
    }
    /*
        This function is meant to pull and return the active list of linked accounts for a given master account.
    */
    def getActiveAccounts(masterKey, masterSecret) {
        def accountList = []
        def AWS_LOC = '$HOME/.aws'
        this.jenkins.env.AWS_ACCESS_KEY_ID = ""
        this.jenkins.env.AWS_SECRET_ACCESS_KEY = ""
        this.jenkins.env.AWS_SESSION_TOKEN = ""
        this.jenkins.sh """#!/bin/sh
        . /etc/profile.d/jenkins.sh
        export TERRAFORM_VERSION=${terraformVersion}
        #set -x
        # Make empty credentials files for AWS
        rm -rf ${AWS_LOC}
        mkdir ${AWS_LOC}
        cd ${AWS_LOC} 
        echo ${AWS_LOC} 
        echo "[default]" > config
        echo "region=${region}" >> config
        echo "output=text" >> config
        # write to creds file 
        echo "[default]" > credentials
        echo "aws_access_key_id = ${masterKey}" >> credentials
        echo "aws_secret_access_key = ${masterSecret}" >> credentials
        aws organizations list-accounts > ${this.jenkins.env.WORKSPACE}/accountlist.txt
        grep "ACTIVE" ${this.jenkins.env.WORKSPACE}/accountlist.txt > ${this.jenkins.env.WORKSPACE}/temp.txt
        awk '{print \$4}' ${this.jenkins.env.WORKSPACE}/temp.txt > ${this.jenkins.env.WORKSPACE}/accountlist.txt
        rm -f ${this.jenkins.env.WORKSPACE}/temp.txt
        rm -f config credentials
        """
        accountList = this.jenkins.readFile file: "${this.jenkins.env.WORKSPACE}/accountlist.txt"
        this.jenkins.sh "rm -f ${this.jenkins.env.WORKSPACE}/accountlist.txt"
        return accountList
	}

    /*
        This function will manually try to destroy anything that aws_config and/or aws_launchpad
        created in this account. Pay close attention to the output from the script. It is very 
        likely that this function is being called because something caused terraform to end in a 
        bad state.

        account : String containing account number
    */
    def cleanseLaunchPad(account) {
        try {
            this.jenkins.echo "Removing config and/or launchpad artifacts from  ${account} ..."
            this.jenkins.unstash 'clean_launchpad'
            this.jenkins.sh """
            . /etc/profile.d/jenkins.sh
            sh -x ./cleanse_launchpad.sh ${account}
            """
            this.jenkins.echo "Successfully removed config and/or launchpad artifacts from  ${account}."
        }
        catch (Exception ex) {
            this.jenkins.echo "Error Exception from cleanseLaunchPad:  ${ex}"
            this.jenkins.echo "ERROR: Failure to remove config and/or launchpad artifacts from  ${account}. Review the above shell script output carefully."
        }
    }

    /**  
        This function sets up the master account such that an lambda function 
        can be used to trigger subsequent lambda functions in customer accounts 
        to execute further monitoring/auditing

        masterAccount : The AWS Master Account ID
        centralAccount : The AWS Central Management Account ID
        test : Boolean flag to determine whether this a test run (true for test)
    */
    def prepareMasterAccount(masterAccount, centralAccount, test) {

        // Cleanse the Master account if under test/PR
        if(test) {
            try {
                this.jenkins.echo "Removing master account artifacts from  ${masterAccount} ..."
                this.jenkins.unstash 'clean_master'
                this.jenkins.sh """
                . /etc/profile.d/jenkins.sh
                sh -x ./cleanse_master.sh ${masterAccount}
                """
                this.jenkins.echo "Successfully removed master artifacts from  ${masterAccount}."
            }
            catch (Exception ex) {
                this.jenkins.echo "Error Exception from cleanse_master:  ${ex}"
                this.jenkins.echo "ERROR: Failure to remove master artifacts from  ${masterAccount}. Review the above shell script output carefully."
            }
        }

        // Set initial variables
        def namespace = test ? "launchpad-master-test" : "launchpad-master"
        def planFlags = "-out=tfplan -input=false"
        def backendS3 = masterAccount + "-tfstate-" + namespace
        def backendDynamoDB = masterAccount + "-tflock-" + namespace
        this.jenkins.env.planFlagsBootstrap = "-auto-approve=true -var name_space=${namespace} -var aws_region=${region}"
        this.jenkins.env.BOOTSTRAP_LOC = '$HOME/.bootstrap'

        // Set up the name for the lambda function, use a different name for testing/PRs
        def lambdaNamespace = test ? "test" : ""
        planFlags += " -var namespace=" + lambdaNamespace

        // Set up the S3 Bucket and the Zip path of the Lambda's Python script
        planFlags += " -var lambda_zip_bucket=cc-lambda-central-" + centralAccount
        planFlags += " -var lambda_zip_path=lambda_invoker.zip"
        planFlags += test ? " -var lambda_topic_name=topic_lambda_invoker-test" : " -var lambda_topic_name=topic_lambda_invoker"

        // Set up the environment variables for the Lambda Function
        planFlags += " -var env_loglevel=INFO"
        planFlags += " -var env_assume_role_arn=arn:aws:iam::${centralAccount}:role/OrganizationAccountAccessRole"
        planFlags += " -var env_lambda_arn_list=arn:aws:lambda:us-east-1:${centralAccount}:function:lambda_audit_ec2"
        planFlags += " -var env_sns_topic_arn=arn:aws:sns:us-east-1:${centralAccount}:launchpad_lambda_topic"

        // Script to set up bootstrap and refresh master account
        this.jenkins.echo "Preparing Master Account Resources on ${masterAccount}"
        this.jenkins.sh """
        # Export Python 3 and execute the jenkins mixin scripts
        export PYTHON_VERSION=${pythonVersion}
        export TERRAFORM_VERSION=${terraformVersion}
        . /etc/profile.d/jenkins.sh
        cd ./master_account
        rm -rf .terraform  
        # Set up the Bootstrap and run it through as a bash script
        ## Only prepare if the resources do not already exists
        aws s3 ls ${backendS3} || curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/v1.2.0/scripts/prepare.sh | bash -s
        aws --region ${region} dynamodb scan --table-name ${backendDynamoDB} || curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/v1.2.0/scripts/prepare.sh | bash -s
        
        terraform init \
        -backend-config="bucket=${backendS3}" \
        -backend-config="key=${namespace}/terraform.state" \
        -backend-config="region=${region}" \
        -backend-config="profile=saml" \
        -backend-config="encrypt=1" \
        -force-copy

        # Terraform plan and apply in the central_eis_accotun
        terraform plan ${planFlags}
        # Note: terraform apply is called from GPL function -- see below
        """

        this.jenkins.dir('master_account') {
            def options = [:]
            def eventData = [:]
            options.terraformVersion = terraformVersion
            options.autoApprove = true
            options.additionalFlags = [:]
            options.additionalFlags.input = false
            eventData.environment = "PROD"
            eventData.cloudProvider = "AWS"
            this.jenkins.glTerraformApply(options, "tfplan", eventData)
        }

        // Only in PRs, Get the output for testing and run pytest
        if(test) {
            this.jenkins.echo "Just before Master Account Pytest"
            this.jenkins.unstash 'tests'
            try {
                this.jenkins.sh """
                export TERRAFORM_VERSION=${terraformVersion}
                export PYTHON_VERSION=${pythonVersion}
                . /etc/profile.d/jenkins.sh
                cd master_account

                terraform init \
                -backend-config="bucket=${backendS3}" \
                -backend-config="key=${namespace}/terraform.state" \
                -backend-config="region=${region}" \
                -backend-config="profile=saml" \
                -backend-config="encrypt=1" \

                terraform output --json > ../tests/master/master_output.json

                cd ../tests/master
                pip3 install -r requirements.txt
                cd ../..
                python3 -m pytest tests/master/ --junitxml pytest.xml
                """ 
            } finally { this.jenkins.junit 'pytest.xml' }
        }
        return true
    }
    
    /**  
        This function sets up the central account such that all associated accounts have permissions
        to write to S3 buckets that are used for auditing.  It does this by configuring the bucket 
        policy.

        accountNumber : The Central Management AWS Account ID
        masterNumber : The Master AWS Account ID
        accountList : List of AWS Account ID to provide limited access to the Central Managment Account
        cloudtrailBucketName : The CloudTrail S3 Bucket name
        backendBucketName : The Backend S3 Bucket name
        configBucketName : The Config S3 Bucket name
        lambdaBucketName : The Lambda S3 Bucket name
        test : Boolean flag to determine whether this a test run (true for test)
    */
    def prepareCentralManagementAccount(accountNumber, masterNumber, accountList, cloudtrailBucketName, backendBucketName, 
            configBucketName, lambdaBucketName, test) {
        def namespace = "central-logging"
        def planFlags = "-out=tfplan -input=false"
        this.jenkins.env.planFlagsBootstrap = "-auto-approve=true -var name_space=${namespace} -var aws_region=${region}"
        this.jenkins.env.BOOTSTRAP_LOC = '$HOME/.bootstrap'
        def tfvarAccounts = "cloudtrail_client_accounts=["
        def arn = "arn:aws:s3:::${cloudtrailBucketName}/AWSLogs/"
        accountList.eachWithIndex { accountId, index ->
            if (index == 0) {
                tfvarAccounts += "\"${arn}${accountId}/*\""
            } else {
                tfvarAccounts += ", \"${arn}${accountId}/*\""
            }
        }
        tfvarAccounts += "]"

        def tfvarBackendAccounts = "launchpad_backend_accounts=["
        def arnBackendAcc = "arn:aws:s3:::${backendBucketName}/bootstrap-launchpad-"
        accountList.eachWithIndex { accountId, index ->
            if (index == 0) {
                tfvarBackendAccounts += "\"${arnBackendAcc}${accountId}/terraform.state\""
            } else {
                tfvarBackendAccounts += ", \"${arnBackendAcc}${accountId}/terraform.state\""
            }
        }
        tfvarBackendAccounts += "]"

        def tfvarBackendPrincipals = "launchpad_backend_principals=["
        def arnBackendPrinc = "arn:aws:iam::"
        def backendRole = ":role/OrganizationAccountAccessRole"
        accountList.eachWithIndex { accountId, index ->
            if (index == 0) {
                tfvarBackendPrincipals += "\"${arnBackendPrinc}${accountId}${backendRole}\""
            } else {
                tfvarBackendPrincipals += ", \"${arnBackendPrinc}${accountId}${backendRole}\""
            }
        }
        tfvarBackendPrincipals += "]"

        def tfvarConfig = "config_client_accounts=["
        def arnConfig = "arn:aws:s3:::${configBucketName}/AWSLogs/"
        accountList.eachWithIndex { accountId, index ->
            if (index == 0) {
                tfvarConfig += "\"${arnConfig}${accountId}/*\""
            } else {
                tfvarConfig += ", \"${arnConfig}${accountId}/*\""
            }
        }
        tfvarConfig += "]"

        def tfvarLogDestinationAllowedAccounts = "allowed_accounts=[" //allowed_accounts are allowed to put subscription filter on logdestination in centralaccount
        accountList.eachWithIndex { accountId, index ->
          if (index == 0) {
              tfvarLogDestinationAllowedAccounts += "\"${accountId}\""
            } else {
                tfvarLogDestinationAllowedAccounts += ", \"${accountId}\""
            }
        }
        tfvarLogDestinationAllowedAccounts += "]"

        def tfCentralAccount = "central_logging_account="
            tfCentralAccount += "\"${accountNumber}\""

        planFlags += " -var '" + tfCentralAccount + "'"
        planFlags += " -var '" + tfvarLogDestinationAllowedAccounts + "'"

        def tfvarLambda = "lambda_client_accounts=[\"${masterNumber}\", \"${accountNumber}\"]"

        planFlags += " -var '" + tfvarAccounts + "'"
        planFlags += " -var '" + tfvarBackendAccounts + "'"
        planFlags += " -var '" + tfvarBackendPrincipals + "'"
        planFlags += " -var '" + tfvarConfig + "'"
        planFlags += " -var '" + tfvarLambda + "'"
        planFlags += " -var central_account_id=${accountNumber}"
        planFlags += " -var bucket_name=${cloudtrailBucketName}"
        planFlags += " -var bootstrap_launchpad_bucket=${backendBucketName}"
        planFlags += " -var config_s3_bucket=${configBucketName}"
        planFlags += " -var lambda_s3_bucket=${lambdaBucketName}"
        planFlags += " -var name=firehose-${accountNumber}"
        planFlags += " -var data_stream_name=kinesis-data-stream-${accountNumber}"

        // Zip the python scripts
        this.jenkins.sh """
        # Export Python 3 and execute the jenkins mixin scripts
        export PYTHON_VERSION=${pythonVersion}
        export TERRAFORM_VERSION=${terraformVersion}
        . /etc/profile.d/jenkins.sh
        cd central_management_account/lambda_functions

        # Zip the lambda_invoker
        zip lambda_invoker.zip lambda_invoker.py
        zip lambda_audit_ec2.zip lambda_audit_ec2.py
        """

        // Script to set up bootstrap, refresh the list of accounts, and update the s3 bucket
        this.jenkins.echo "Namespace before Central Account: ${namespace}"
        this.jenkins.sh """
        # Export Python 3 and execute the jenkins mixin scripts
        export PYTHON_VERSION=${pythonVersion}
        export TERRAFORM_VERSION=${terraformVersion}
        . /etc/profile.d/jenkins.sh
        cd ./central_management_account
        rm -rf .terraform  
        # Set up the Bootstrap and run it through as a bash script
        ## Only prepare if the resources do not already exists
        aws s3 ls ${accountNumber}-tfstate-${namespace} || curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/v1.2.0/scripts/prepare.sh | bash -s
        aws --region ${region} dynamodb scan --table-name ${accountNumber}-tflock-${namespace} || curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/v1.2.0/scripts/prepare.sh | bash -s
        
        terraform init \
        -backend-config="bucket=${accountNumber}-tfstate-${namespace}" \
        -backend-config="key=${namespace}/terraform.state" \
        -backend-config="region=${region}" \
        -backend-config="profile=saml" \
        -backend-config="encrypt=1" \
        -force-copy

        # Terraform apply only on the fire_eye module and s3 lambda module to resolve dependency issue on the whole terraform apply
        terraform plan ${planFlags} -target=module.fire_eye -target=data.aws_iam_policy_document.bucket_lambda_policy -target=module.s3-central-lambda -target=aws_s3_bucket_object.upload_lambda_audit_ec2 
        terraform apply -auto-approve=true tfplan

        # Terraform plan and apply in the central_management_account
        terraform plan ${planFlags}
        # Note: terraform apply is called from GPL function -- see below
        """

        this.jenkins.dir('central_management_account') {
            def options = [:]
            def eventData = [:]
            options.terraformVersion = terraformVersion
            options.autoApprove = true
            options.additionalFlags = [:]
            options.additionalFlags.input = false
            eventData.environment = "PROD"
            eventData.cloudProvider = "AWS"
            this.jenkins.glTerraformApply(options, "tfplan", eventData)
        }

        // Only in PRs, Get the output for testing and run pytest
        if(test) {
            this.jenkins.echo "Just before Central Account Pytest"
            this.jenkins.unstash 'tests'
            try {
                this.jenkins.sh """
                export TERRAFORM_VERSION=${terraformVersion}
                export PYTHON_VERSION=${pythonVersion}
                . /etc/profile.d/jenkins.sh
                cd central_management_account

                terraform init \
                -backend-config="bucket=${accountNumber}-tfstate-${namespace}" \
                -backend-config="key=${namespace}/terraform.state" \
                -backend-config="region=${region}" \
                -backend-config="profile=saml" \
                -backend-config="encrypt=1"

                terraform output --json > ../tests/central/central_output.json

                cd ../tests/central
                pip3 install -r requirements.txt
                cd ../..
                python3 -m pytest tests/central/ --junitxml pytest.xml
                """ 
            } finally { this.jenkins.junit 'pytest.xml' }
        }
        return true
    }

    /**
        Applies a CloudFormation template to create the EIS Read and Break Glass
        Roles within the account and sync them with their respective Global Groups

        masterAccount : The AWS Master Account Name
        centralAccount : The Central EIS Security AWS Account ID
        test : Boolean flag to determine whether this a test run (true for test)
    */
    def prepareCentralEISAccount(masterAccount, centralAccount, test) {
        def namespace = "central-eis"
        def planFlags = "-out=tfplan -input=false"
        this.jenkins.env.planFlagsBootstrap = "-auto-approve=true -var name_space=${namespace} -var aws_region=${region}"
        this.jenkins.env.BOOTSTRAP_LOC = '$HOME/.bootstrap'

        // Determine the naming of the Read Roles based upon the account 
        planFlags += " -var account_id=${centralAccount}"
        
        // Set up the saml provider determined by the masterAccount
        this.jenkins.echo "Running Central EIS on ${masterAccount}"
        def saml = (masterAccount == "Enterprise") ? "UHG_AWS_FEDERATION" : "UHG_AWS_POC"
        planFlags += " -var saml_provider=${saml}"    

        // Script to set up bootstrap and refresh central eis account
        this.jenkins.sh """
        # Export Python 3 and execute the jenkins mixin scripts
        export PYTHON_VERSION=${pythonVersion}
        export TERRAFORM_VERSION=${terraformVersion}
        . /etc/profile.d/jenkins.sh
        cd ./central_eis_account
        rm -rf .terraform  
        # Set up the Bootstrap and run it through as a bash script
        ## Only prepare if the resources do not already exists
        aws s3 ls ${centralAccount}-tfstate-${namespace} || curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/v1.2.0/scripts/prepare.sh | bash -s
        aws --region ${region} dynamodb scan --table-name ${centralAccount}-tflock-${namespace} || curl https://github.optum.com/raw/CommercialCloud-EAC/aws_bootstrap/v1.2.0/scripts/prepare.sh | bash -s
        
        terraform init \
        -backend-config="bucket=${centralAccount}-tfstate-${namespace}" \
        -backend-config="key=${namespace}/terraform.state" \
        -backend-config="region=${region}" \
        -backend-config="profile=saml" \
        -backend-config="encrypt=1" \
        -force-copy

        # Terraform plan and apply in the central_eis_accotun
        terraform plan ${planFlags}
        # Note: terraform apply is called from GPL function -- see below
        """

        this.jenkins.dir('central_eis_account') {
            def options = [:]
            def eventData = [:]
            options.terraformVersion = terraformVersion
            options.autoApprove = true
            options.additionalFlags = [:]
            options.additionalFlags.input = false
            eventData.environment = "PROD"
            eventData.cloudProvider = "AWS"
            this.jenkins.glTerraformApply(options, "tfplan", eventData)
        }

        // Only in PRs, Get the output for testing and run pytest
        if(test) {
            this.jenkins.echo "Just before Central EIS Pytest"
            this.jenkins.unstash 'tests'
            try {
                this.jenkins.sh """
                export TERRAFORM_VERSION=${terraformVersion}
                export PYTHON_VERSION=${pythonVersion}
                . /etc/profile.d/jenkins.sh
                cd central_eis_account

                terraform init \
                -backend-config="bucket=${centralAccount}-tfstate-${namespace}" \
                -backend-config="key=${namespace}/terraform.state" \
                -backend-config="region=${region}" \
                -backend-config="profile=saml" \
                -backend-config="encrypt=1"

                terraform output --json > ../tests/central_eis/central_eis_output.json

                cd ../tests/central_eis
                pip3 install -r requirements.txt
                cd ../..
                python3 -m pytest tests/central_eis/ --junitxml pytest.xml
                """ 
            } finally { this.jenkins.junit 'pytest.xml' }
        }
        return true
    }
    
    /**  
        This function sets up the launchpad account with Cloudtrail, Config, 
        and EIS IAM Roles through the launchpad_account terraform files.

        account : The Launchpad AWS Account ID
        centralLoggingBucketName : The Central Management Account's Cloudtrail Bucket name
        centralSecurityAccountNumber : The Central EIS Security's AWS Account ID
        backendBucketName : The Backend S3 Bucket name
        configBucketName : The Central Management Account's Config Bucket name
        destroy : Boolean flag to determine whether this a test run (true for test)
    */
    def applyLaunchpad(account, centralLoggingBucketName, centralAccount, centralSecurityAccountNumber, backendBucketName, configBucketName, destroy) {
        try {
            this.jenkins.echo "Applying launchpad on ${account}"
            // Script to set up launchpad for the specified account in this stage
            // Stage to bootstrap the launchpad terraform project into the backend and apply it
            def namespace = "bootstrap-launchpad-${account}"
            def planFlags = "-out=tfplan -input=false"

            // Plan flags for Cloudtrail, Config, and EIS Access
            planFlags += " -var cloudtrail_name=\"Cloudtrail-central-logging-rCloudTrailLoggingRemote-${account}\""
            planFlags += " -var cloudtrail_s3_bucket=${centralLoggingBucketName}"
	        planFlags += " -var central_logging_account=${centralAccount}"
            planFlags += " -var config_s3_bucket=${configBucketName}"
            planFlags += " -var config_name=${account}-config-aws"
            planFlags += " -var eis_central_security_id=${centralSecurityAccountNumber}"
            planFlags += " -var launchpad_account=${account}"
            //planFlags += " -var cloudtrail_log_group_name=\"launchpad-cloudtrail-log-group-${account}\""
           // planFlags += " -var cw_log_group_name=\"vpc-flow-logs\""

            this.jenkins.unstash 'launchpad_account'
            this.jenkins.sh """
            # Export Python 3 and execute the jenkins mixin scripts
            export PYTHON_VERSION=${pythonVersion}
            export TERRAFORM_VERSION=${terraformVersion}
            . /etc/profile.d/jenkins.sh
            rm -rf exec_launchpad_account
            mkdir exec_launchpad_account
            cp -R launchpad_account/* exec_launchpad_account/
            ls -la exec_launchpad_account/
            cd exec_launchpad_account
            terraform init \
            -backend-config="bucket=${backendBucketName}" \
            -backend-config="key=${namespace}/terraform.state" \
            -backend-config="region=${region}" \
            -backend-config="profile=saml" \
            -backend-config="encrypt=1" \
            -force-copy

            # Terraform apply only on the launchpad module first, to resolve any race conditions replacing the old CloudTrail resource.
            terraform plan ${planFlags} -target=module.launchpad
            terraform apply -auto-approve=true tfplan

            # Terraform plan and apply the exec_launchpad_account folder
            terraform plan ${planFlags}
            
            # Note: terraform apply is called from GPL function -- see below
            """
            this.jenkins.dir('exec_launchpad_account') {
                def options = [:]
                def eventData = [:]
                options.terraformVersion = terraformVersion
                options.autoApprove = true
                options.additionalFlags = [:]
                options.additionalFlags.input = false
                eventData.environment = "PROD"
                eventData.cloudProvider = "AWS"
                this.jenkins.glTerraformApply(options, "tfplan", eventData)
            }
        } 
        catch (Exception ex) 
        { 
            this.jenkins.echo "Exception caught in applyLaunchPad:  ${ex}"
            return false 
        }
        finally {
            // Only in PRs, Get the output for testing and run pytest.
            // Note that for the RemoveDefaultVPC.py module the dryrun option is used for code test coverage. 
            this.jenkins.unstash 'tests'
            if (destroy) {
                // config pytest begin 
                try { 
                    this.jenkins.sh """ 
                    # Pytest config rule tests  
                    export PYTHON_VERSION=${pythonVersion}
                    export TERRAFORM_VERSION=${terraformVersion} 
                    . /etc/profile.d/jenkins.sh 
                    cd launchpad_account
                    export PYTHONPATH=\$PWD:\$PYTHONPATH
                    cd ../exec_launchpad_account 
                    terraform output --json > ../tests/launchpad/launchpad_output.json 
                    echo "${account}" > ../tests/launchpad/account.txt
                    cd ../tests/launchpad 
                    pip3 install -r requirements.txt 
                    cd ../.. 
                    python3 -m pytest tests/launchpad/ --junitxml pytest.xml 
                    """ 
                }
                catch (Exception ex) 
                { 
                    this.jenkins.echo "Exception caught in destroy block of applylauchpad:  ${ex}"
                } finally { this.jenkins.junit 'pytest.xml' } 
                // config pytest end 
                this.jenkins.echo "Destroying ${account}"
                this.jenkins.dir('exec_launchpad_account') {
                def options = [:]
                options.additionalFlags = [:]
                options.additionalFlags.input = false
                options.terraformVersion = terraformVersion
                options.autoApprove = true
                this.jenkins.glTerraformDestroy(options)
                }
            }
        }
        return true
    }

    /**
        Check for Enterpise Support in account. 
        If there's no Enterprise Support enabled, create a support case within
        the Master account to get the Enterprise Support enabled.

        account : The Launchpad AWS Account ID
        masterKey : The Master Account Key
        masterSecret : The Master Account Secret
    */
    def enableEnterpriseSupport(account, masterKey, masterSecret) {
        // Check if Enterprise Support is enabled in the account
        def enterpriseEnabled = true
        this.jenkins.sh """
        . /etc/profile.d/jenkins.sh
        aws support describe-severity-levels &> enable_support.txt || echo "Error occured in call." > enable_support.txt
        """
        def numSevLevels = (this.jenkins.sh (script: "wc -l < enable_support.txt", returnStdout: true)).trim()

        // If number of severity levels is 5 (5 separate lines), then Enterprise Support is enabled
        if(numSevLevels != "5") {
            this.jenkins.echo "Enterprise Support (${numSevLevels}) is not enabled for account: ${account}"
            enterpriseEnabled = false
        }
        else {
            this.jenkins.echo "Enterprise Support is enabled for account: ${account}"
        }

        // If Enterprise Support isn't enabled, use the Master creds to create a ticket
        if(!enterpriseEnabled) {
            try {
                // Generate the flags for the python script
                def supportFlags = "--account ${account}"
                if (this.jenkins.env.CHANGE_ID) { 
                    supportFlags += " --test"
                } 

                // Set the credentials to the Master and run the enable_support script
                this.jenkins.unstash 'launchpad_account'
                this.jenkins.withCredentials([
                    this.jenkins.string(credentialsId: masterKey, variable: 'AWS_MASTER_KEY'), 
                    this.jenkins.string(credentialsId: masterSecret, variable: 'AWS_MASTER_SECRET')])
                {
                    this.jenkins.env.AWS_ACCESS_KEY_ID = this.jenkins.AWS_MASTER_KEY
                    this.jenkins.env.AWS_SECRET_ACCESS_KEY = this.jenkins.AWS_MASTER_SECRET
                    this.jenkins.env.AWS_SESSION_TOKEN = ""
                    this.jenkins.sh """#!/bin/sh
                    export TERRAFORM_VERSION=${terraformVersion}
                    . /etc/profile.d/jenkins.sh

                    # run python to enable_support
                    pip3 install boto3
                    python3 launchpad_account/enable_support.py ${supportFlags}
                    """
                }
            }
            catch (Exception ex) {
                this.jenkins.echo "Error Exception from enable_support.py:  ${ex}"
                return false
            }
        }
        return true
    }

    /**
        Helper method to determine if a Account Category is valid for AWS Launchpad

        accountCategory : String used for the Account Category
    */
    def isValidAccountCategory(accountCategory) {
        def allowedCategories = ["test", "beta", "nonProd", "prod"]
        return allowedCategories.contains(accountCategory)
    }
    /*
        The removing will run when there is a single account requested to run either via prompt in JPL or 
        via the commit message.  
        For PRs this function can/will get bypassed upon the use of the Jenkins "Build with Parameters" where 
        a check box is provided to really remove default VPCs for the specified account.  That is NOT checking
        the check box will bypass. 

        The RemoveDefaultVPC.py can take a '-d DR' parameter to only perform a dryrun where no removals will happen.
        
        Also note that for PRs there is pytest unit testing that is performed for the RemoveDefaultVPC and as 
        part of the pytest unit testing the dryrun option is invoked.
    */
    def removeDefaultVPCs(account)
    {
        this.jenkins.echo "Removing Default VPCs for ${account}"
        this.jenkins.unstash 'launchpad_account'
        try { 
            this.jenkins.sh """ 
            # Pytest config rule tests  
            export PYTHON_VERSION=${pythonVersion}
            export TERRAFORM_VERSION=${terraformVersion} 
            . /etc/profile.d/jenkins.sh 
            cd launchpad_account 
            python3 RemoveDefaultVPC.py
            """ 
            this.jenkins.echo "Finished Running Removing Default VPCs for ${account}"
            return true
        } 
        catch (Exception ex) { 
            this.jenkins.echo "Exception caught in removeDefaultVPCs:  ${ex}"
            return false 
        }
    }
}

/**
    Helper function return an instance of the AWSAdapter class to the 
    Jenkinsfile. 

    jenkinsInstance: The jenkins instance used to construct AWSAdapter
*/
def getAWSAdapter(jenkinsInstance) {
    return new AWSAdapter(jenkinsInstance)
}

// This is needed for Jenkins to load in main Jenkinsfile
return this;
