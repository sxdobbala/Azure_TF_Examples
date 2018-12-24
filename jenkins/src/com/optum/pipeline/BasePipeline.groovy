package com.optum.pipeline

import com.optum.config.BuildVerificationTests
import com.optum.config.Config
import com.optum.config.Docker
import com.optum.config.DeploymentBranchSetting
import com.optum.config.Ose
import com.optum.config.Project
import com.optum.config.Sonar
import com.optum.config.AWS
import com.optum.config.Helm
import com.optum.utils.Helpers

def init()
{
    init("config.yml")
}

def init(String configFile)
{
    branch = getBranchName()

    Config config = readConfig(configFile)

    def repos = config.deploymentBranchSetting
    def mavenBuild = new MavenBuild()
    def gradleBuild = new GradleBuild()
    def sonarScan = new SonarScan()
    def standardDeploy = new StandardDeploy()
    def awsDeploy = new AWSDeploy()
    def helmDeploy = new HelmDeploy()
    def uiDeploy = new UIDeploy()
    def buildVerificationTests = new BuildVerificationTests()
    def jenkinsNode = Helpers.getNode(config.jenkinsNode)
    def lambdaDeploy = new LambdaDeploy()
    def contrast = new Contrast()
    def dockerBuild = new DockerBuild()
    boolean runMavenDependency = false

	node(jenkinsNode)
	{
        for (i = 0; i < repos.size(); i++)
        {
            def deploymentBranchSetting = repos[i]
    
            if (branch.startsWith(deploymentBranchSetting.name))
            {
                if (deploymentBranchSetting.lambda)
                {
                    lambdaDeploy.validatePrerequisites(config, deploymentBranchSetting)
                }
    
                if(deploymentBranchSetting.gradleBuild != null && deploymentBranchSetting.gradleBuild == true)
                {
                    stage('Gradle Build')
                    {
                          gradleBuild.call(config, deploymentBranchSetting)
                    }
                }
    
                if(deploymentBranchSetting.sonar)
                {
                    sonarScan.call(config, deploymentBranchSetting, branch)
                    runMavenDependency = true
                }
                else if (deploymentBranchSetting.mavenBuild && deploymentBranchSetting.mavenBuild == true)
                {
                    stage('Maven build')
                    {
                        mavenBuild.call(config, deploymentBranchSetting)
                    }
                    runMavenDependency = true
                }
    
                if (deploymentBranchSetting.docker != null && runMavenDependency == true)
                {
                    def cloudEnv = deploymentBranchSetting.docker.cloudEnv
                    if( cloudEnv != null && cloudEnv.equalsIgnoreCase("aws"))
                    {
                        stage ('AWS Docker image build and push')
                        {
                            dockerBuild.awsDockerBuild(config, deploymentBranchSetting)
                        }
                    }
                    else
                    {
                        stage ('OSE Docker image build and push')
                        {
                            dockerBuild.oseDockerBuild(config, deploymentBranchSetting)
                        }
                    }
                }
    
                if (deploymentBranchSetting.lambda && runMavenDependency == true)
                {
                    def pomDirectory = (config.project.pomPathExtension ? "./${config.project.pomPathExtension}/" : "./")
                    def pomFile = "${pomDirectory}pom.xml"
                    def pom = readMavenPom file: "${pomFile}"
                    def lambda = deploymentBranchSetting.lambda
                    def archivePath = "${pomDirectory}target/${pom.artifactId}-${pom.version}.jar"
                    def s3Path = "${lambda.s3Bucket}/${lambda.s3Key}"
                    def timestamp = new Date().format("yyyyMMddHHmmss", TimeZone.getTimeZone('UTC'))
                    awsAuth(lambda.s3CredentialsId, lambda.s3AccountId)
    
                    sh '''
                        . /etc/profile.d/jenkins.sh
                        export AWS_PROFILE=saml
                        BUCKET_EXISTS=`aws s3 ls ''' + s3Path + ''' || true`
                        if [ "$BUCKET_EXISTS" ]; then
                         aws s3 mv s3://''' + s3Path + ''' s3://''' + s3Path + '''_retired_''' + timestamp + '''
                        fi
                        aws s3 cp ''' + archivePath + ''' s3://''' + s3Path
    
                    lambdaDeploy.call(config, deploymentBranchSetting)
                }
    
                if (deploymentBranchSetting.ose != null  && deploymentBranchSetting.docker != null)
                {
                    standardDeploy.call(config, deploymentBranchSetting)
                }
    
                if (deploymentBranchSetting.aws != null && deploymentBranchSetting.docker != null)
                {
                    if(branch == 'master' || branch == 'dev' || branch == 'develop' || branch.contains('release') || branch.contains('HotFix'))
                    {
                        awsDeploy.deploy(branch, config, deploymentBranchSetting)
                    }
                }
    
                if(deploymentBranchSetting.helm != null && deploymentBranchSetting.docker != null)
                {
                    if(branch == 'master' || branch == 'dev' || branch == 'develop' || branch.contains('release') || branch.contains('HotFix'))
                    {
                        helmDeploy.deploy(branch, config, deploymentBranchSetting)
                    }
                }
    
                if(deploymentBranchSetting.ui != null)
                {
                    uiDeploy.npmBuild(branch, config , deploymentBranchSetting)
                    if(deploymentBranchSetting.ui.deployToEnv)
                    {
                        uiDeploy.deploy(config, deploymentBranchSetting)
                    }
                }
    
                if (deploymentBranchSetting.automationTestPath != null)
                {
                    stage('Automation Testing')
                    {
                        def automationBuild = build job: deploymentBranchSetting.automationTestPath,
                        parameters: [
                            string(name: 'testConfig', value: deploymentBranchSetting.testConfig)
                        ]
                        def automationStatus = automationBuild.getResult()
                        echo "Build returned result: ${automationStatus}"
    
                        if (automationStatus == "FAILURE")
                        {
                            currentBuild.result = "FAILURE"
                        }
                    }
                }
    
                if (deploymentBranchSetting.buildVerificationTests != null)
                {
                    stage('Build Verification Testing')
                    {
                        try
                        {
                            def automationBuild = build job: deploymentBranchSetting.buildVerificationTests.jobName
                            def automationStatus = automationBuild.getResult()
                            echo "Build Verification Test result: ${automationStatus}"
                        }
                        catch (error)
                        {
                            echo "Build Verification Test result: FAILED"
                            currentBuild.result = "FAILURE"
                        }
                    }
                }
                break
            }
        }
	}
}

Config readConfig(String configFileName)
{
    node('docker-maven-slave')
    {
        checkout scm
        def configFile = readYaml file: configFileName
        return Helpers.convertValue(configFile, Config.class)
    }
}

String getBranchName()
{
    def branch = "${scm.getBranches()[0].getName()}"
    if (branch.startsWith("*/"))
    {
        branch = branch.replaceFirst("[*]/", "")
    }
    return branch
}

def awsAuth(String awsCredentialsId, String awsAccountId)
{
    withCredentials([usernamePassword(credentialsId: "${awsCredentialsId}", usernameVariable: 'USER', passwordVariable: 'PASS')])
    {
        sh '''
            . /etc/profile.d/jenkins.sh
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
