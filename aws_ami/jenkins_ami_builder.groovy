node('docker-aws-slave') {
    properties([
        parameters([
            choice(
            choices: "create\ndestroy",
            description: 'create or destroy',
            name: 'iac_decide')
        ]),
        pipelineTriggers([
            parameterizedCron('''
                H 0 * * 7% iac_decide=create
                H 2 * * 7% iac_decide=destroy''')
        ])
    ])

    stage('Clone plan') { checkout scm }

    def ubuntuProps = readJSON file: './packer/ubuntu_var.json'
    def egressProps = readJSON file: './packer/amazon_linux2_egress_proxy_var.json'
    def eksProps = readJSON file: './packer/amazon_linux2_eks_var.json'
    def awsAccountToCredAssociation = readProperties file: './packer/awsAccountToCredAssociation.prop'
    def osTypes = [
        ubuntuProps.os_type,
        egressProps.os_type,
        eksProps.os_type
    ]
    def parentAccountId = ubuntuProps.ami_users.split(",")[0]
    def parentCredentialsId = awsAccountToCredAssociation[parentAccountId]
    
    stage('Configure AWS') { awsAuth(parentCredentialsId, parentAccountId) }

    wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
        try {
            def userInput = true
            def didTimeout = false

            env.TERRAFORM_HOME = "/tools/terraform/terraform-0.11.5"
            env.PATH = "${env.TERRAFORM_HOME}:${env.PATH}"

            if(params.iac_decide == "create") {
                stage('Review plan') {
                    sh '''
                        set +x
                        export AWS_PROFILE=saml
                        zip -j ami.zip packer/debian_var.json packer/ubuntu_var.json packer/amazon_linux2_eks_var.json packer/amazon_linux2_egress_proxy_var.json packer/packer.json packer/scripts/build-ami packer/scripts/run_packer
                        terraform init terraform/
                        terraform plan terraform/
                    ''' 
                }

                try {
                    timeout(time: 10, unit: 'MINUTES') { input 'Do you want to proceed with Provisioning?' }
                }
                catch(err) {
                    def user = err.getCauses()[0].getUser()
                    if('SYSTEM' == user.toString()) {
                        // SYSTEM means timeout.
                        didTimeout = true
                    }
                    else {
                        userInput = false
                        echo "Aborted by: [${user}]"
                        throw err
                    }
                }

                stage('Create packer builder') { 
                    sh '''
                        export AWS_PROFILE=saml
                        terraform apply -auto-approve terraform/
                    ''' 
                }
                notify('Successfully created')

                stage('Validate packer builder') { 
                    sh '''
                        set +x
                        export AWS_PROFILE=saml
                        instance_id=`aws ec2 describe-instances --filters "Name=tag:Name,Values=*_packer_builder" "Name=instance-state-name,Values=running,pending" --query "Reservations[*].Instances[*].InstanceId" --output text`
                        status=`aws ec2 describe-instance-status --instance-id $instance_id --query "InstanceStatuses[*].InstanceStatus.Details[*].Status" --output text`
                        while [ $status != "passed" ]
                        do
                            sleep 60
                            status=`aws ec2 describe-instance-status --instance-id $instance_id --query "InstanceStatuses[*].InstanceStatus.Details[*].Status" --output text`
                            echo "Waiting for instance to start......"
                        done
                        echo "instance validated successfully"
                    ''' 
                }

                stage('Run image build') { 
                    sh '''
                        set +x
                        export AWS_PROFILE=saml
                        instance_id=`aws ec2 describe-instances --filters "Name=tag:Name,Values=*_packer_builder" "Name=instance-state-name,Values=running,pending" --query "Reservations[*].Instances[*].InstanceId" --output text`
                        command_id=`aws ssm send-command --instance-ids $instance_id --document-name "AWS-RunShellScript" --parameters commands="sh /home/ec2-user/workspace/run_packer" --query "Command.CommandId" --output text`
                        command_status=`aws ssm list-command-invocations --command-id $command_id --query "CommandInvocations[*].StatusDetails" --output text`
                        while [ $command_status == "InProgress" ]
                        do
                            sleep 120
                            echo "command is still executing....." 
                            command_status=`aws ssm list-command-invocations --command-id $command_id --query "CommandInvocations[*].StatusDetails" --output text`
                        done
                        echo "command completed with the status: $command_status"
                    ''' 
                }

                stage('Get S3 logs') { 
                    sh '''
                        export AWS_PROFILE=saml
                        set +x
                        bucket=`aws s3 ls | grep packer-artifacts-* | awk '{ print $3 }'`
                        aws s3 cp s3://${bucket}/output_eks.txt .
                        aws s3 cp s3://${bucket}/output_ubuntu.txt .
                        aws s3 cp s3://${bucket}/output_egress_proxy.txt .
                    ''' 
                }

                step([$class: 'ArtifactArchiver',
                    artifacts: 'output_eks.txt,output_egress_proxy.txt,output_ubuntu.txt',
                    excludes: null])

                osTypes.each { osType ->
                    def originalAmi
                    def amiAccountList
                    def destinationRegions
                    
                    switch (osType) {
                        case "amazon-linux2-egress-proxy":
                            originalAmi = egressProps.ami_name
                            amiAccountList = egressProps.ami_users.split(",")
                            destinationRegions = egressProps.ami_regions.split(',')
                            break
                        case "amazon-linux2-eks":
                            originalAmi = eksProps.ami_name
                            amiAccountList = eksProps.ami_users.split(",")
                            destinationRegions = eksProps.ami_regions.split(',')
                            break
                        default:
                            originalAmi = ubuntuProps.ami_name
                            amiAccountList = ubuntuProps.ami_users.split(",")
                            destinationRegions = ubuntuProps.ami_regions.split(',')
                            break
                    }
                    
                    def amiName = originalAmi + "-" + new Date().format("yyyy-MM-dd", TimeZone.getTimeZone("UTC"))
                    def encryptedAmiName = "encrypted-${amiName}"
                    def amiAccounts = [:]
                    def parentSourceRegion = destinationRegions[0]
                    def encryptedImageMap = [:]
                    def snapshotId
                    def sourceImageId
                
                    amiAccountList.each { amiAccount ->
                        amiAccounts[amiAccount] = awsAccountToCredAssociation[amiAccount]
                    }
                    
                    stage("Encrypt AMI for ${osType}") {
                        amiAccounts.each { awsAccountId, awsCredentialsId ->
                            awsAuth(awsCredentialsId, awsAccountId)
                            imageId = sh(script: 'aws ec2 describe-images --filters "Name=name,Values=' + amiName + '" --query "Images[*].ImageId" --output text --profile saml', returnStdout: true).trim()
                            destinationRegions.each { destinationRegion ->
                                encryptedImageId = sh(script: "aws ec2 copy-image --encrypted --name ${encryptedAmiName} --source-image-id ${imageId} --source-region ${parentSourceRegion} --region ${destinationRegion} --output text --profile saml", returnStdout: true).trim()
                                encryptedImageMap["${awsAccountId}-${destinationRegion}"] = encryptedImageId
                            }
                            if (awsAccountId == parentAccountId) {
                                sourceImageId = imageId
                            }
                        }
                    }

                    stage('Verfiy encrypted images are ready') {
                        sleep 120
                        amiAccounts.each { awsAccountId, awsCredentialsId ->
                            awsAuth(awsCredentialsId, awsAccountId)
                            destinationRegions.each { destinationRegion ->
                                encryptedImageId = encryptedImageMap."${awsAccountId}-${destinationRegion}"
                                state = sh(script: "aws ec2 describe-images --image-ids ${encryptedImageId} --query \"Images[*].State\" --region ${destinationRegion} --output text --profile saml", returnStdout: true).trim()

                                while (state != "available") {
                                    sleep 120
                                    state = sh(script: "aws ec2 describe-images --image-ids ${encryptedImageId} --query \"Images[*].State\" --region ${destinationRegion} --output text --profile saml", returnStdout: true).trim()
                                }
                            }
                        }
                    }

                    stage('Deregister unencrypted AMI') {
                        awsAuth(parentCredentialsId, parentAccountId)
                        snapshotId = sh(script: "aws ec2 describe-images --image-ids ${sourceImageId} --query \"Images[*].BlockDeviceMappings[*].Ebs.SnapshotId\" --region ${parentSourceRegion} --output text --profile saml", returnStdout: true).trim()
                        sh "aws ec2 deregister-image --image-id ${sourceImageId} --profile saml"
                        sh "aws ec2 delete-snapshot --snapshot-id ${snapshotId} --region ${parentSourceRegion} --profile saml"
                    }

                    stage('Deleting AMIs and snapshots older than 90 days') {
                        def today = new Date()
                        def threeMonthsAgo = today - 90
                        threeMonthsAgo = threeMonthsAgo.format("yyyy-MM-dd")
                        amiAccounts.each { awsAccountId, awsCredentialsId ->
                            awsAuth(awsCredentialsId, awsAccountId)
                            destinationRegions.each { destinationRegion ->
                                sh "aws ec2 describe-images --filters \"Name=name,Values=${originalAmi}-*\" --query \"Images[?CreationDate<\'${threeMonthsAgo}\'].ImageId\" --region ${destinationRegion} --output text --profile saml"
                                oldImages = sh(script: "aws ec2 describe-images --filters \"Name=name,Values=${amiName}-*\" --query \"Images[?CreationDate<\'${threeMonthsAgo}\'].ImageId\" --region ${destinationRegion} --output text --profile saml", returnStdout: true).trim()
                                for (i = 0; i < oldImages.size(); i++) {
                                    imageId = oldImages[i]
                                    snapshotIds = sh(script: "aws ec2 describe-images --image-ids ${imageId} --query \"Images[*].BlockDeviceMappings[*].Ebs.SnapshotId\" --region ${destinationRegion} --output text --profile saml", returnStdout: true).trim()
                                    sh "aws ec2 deregister-image --image-id ${imageId} --profile saml"
                                    for (j = 0; j < snapshotIds.size(); j++) {
                                        snapshotId = snapshotIds[j]
                                        sh "aws ec2 delete-snapshot --snapshot-id ${snapshotId} --region ${destinationRegion} --profile saml"
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                stage('Review destroy plan') { 
                    sh '''
                        set +x          
                        export AWS_PROFILE=saml
                        terraform init terraform/
                        terraform plan -destroy terraform/
                    ''' 
                }

                try {
                    timeout(time: 10, unit: 'MINUTES') { input 'Confirm Destroy?' }
                }
                catch(err) {
                    def user = err.getCauses()[0].getUser()
                    if('SYSTEM' == user.toString()) {
                        // SYSTEM means timeout.
                        didTimeout = true
                    }
                    else {
                        userInput = false
                        echo "Aborted by: [${user}]"
                        throw err
                    }
                }

                stage('Destroy') { 
                    sh '''
                        export AWS_PROFILE=saml
                        terraform destroy -force terraform/
                    ''' 
                }
                notify('Successfully destroyed')
            }
        }
        catch (err) {
            notify("Failed ${err}")
            currentBuild.result = 'FAILURE'
        }
    }
}

def notify(status) {
    emailext (
        to: "rajendra.raghavendra@optum.com",
        subject: "'${env.BUILD_TAG} - Status - ${status}!'",
        attachLog: true,
        body: """ JOB COMPLETE """ )
}

def awsAuth(String awsCredentialsId, String awsAccountId) {
    withCredentials([
        usernamePassword(credentialsId: awsCredentialsId, usernameVariable: 'USER', passwordVariable: 'PASS')
    ]) {
        sh '''
            set +x
            # Export Python 3 and execute the jenkins mixin scripts
            export PYTHON_VERSION=3.6
            . /etc/profile.d/jenkins.sh

            # Download python script and files to authenticate to AWS
            export AUTH_LOC=$HOME/aws-cli-saml
            rm -rf $AUTH_LOC
            mkdir $AUTH_LOC
            cd $AUTH_LOC
            for file in authenticate_py3.py prod.cer sandbox.cer; do \
                curl https://github.optum.com/raw/CommercialCloud-EAC/python-scripts/master/aws-cli-saml/$file > $AUTH_LOC/$file
            done;
            export AWS_SAML_ROLE="arn:aws:iam::''' + awsAccountId + ''':role/AWS_'''+ awsAccountId + '''_Service"
            python3 authenticate_py3.py -u ${USER} -p ${PASS}
        '''
    }
}
