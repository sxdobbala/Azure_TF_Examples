/*
   Base Launchpad Pipeline

   This BaseLaunchpadPipeline is invoked by the respective Cloud Launchpad Pipelines
   (Azure or AWS). The Caller of the Base Launchpad will implement the BaseAdapterPipeline 
   respective to the Cloud Service, defining how the Central and Launchpad Accounts are 
   tested and applied (example: adapter.deployCentralManagement, adapter.deployLaunchpadOnAccount).

   Please look at the BaseAdapter class defintion.
   https://github.optum.com/CommercialCloud-EAC/jenkins-pipeline/blob/master/src/com/optum/commercialcloud/pipeline/launchpad/BaseAdapter.groovy
*/

// Imports
import com.optum.commercialcloud.pipeline.launchpad.BaseAdapter
import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

/*
   Pipeline call that assumes single account run fields are null 
*/
def call(BaseAdapter adapter, String yamlFile, String nodeLabel) {
    call(adapter, yamlFile, nodeLabel, null, null, false)
}

/*
   Pipeline call that will process the single account run
*/
def call(BaseAdapter adapter, String yamlFile, String nodeLabel, String singleMasterID, String singleAccountID, boolean newAccount) {

    // Only run under master or PR
    if (env.BRANCH_NAME == "master" || env.CHANGE_ID) {
        def yamlConfig

        // Load in yamlFile as text
        yamlConfig = readYaml text: yamlFile

        // Pass derived values to execute launchpad
        launchpadPipeline(adapter, yamlConfig, nodeLabel, singleMasterID, singleAccountID, newAccount)
    }
}

/*
   The Main Pipeline code structuring the Launchpad flow.
   This will use the implemented Abstract BaseAdapater, depending on Azure or AWS,
   and execute the pipeline on the provided yaml file.
*/
def launchpadPipeline(BaseAdapter adapter, Map yamlConfig, String nodeLabel, String singleMasterID, String singleAccountID, boolean newAccount) {
    def log = [] 
    def launchpadFailuresExists = false
    def success = true
    def tempMasterAccountConfig
    def targetingOneAccount = false
    def dataSet = []
    def result = []
    def centralUpdated = true
    def centralLog = []
    def singleRun = false
    def tagged = false
    def scmVars

    // Check if the single account fields were processed and vlid
    // example AWS : [ci] {'masterAccountId':'POC','accountId':'123456789012'}
    // example Azure : [ci] {'masterAccountId':'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxx','accountId':'yyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyy'}
    if (singleMasterID != null && singleAccountID != null) {
        singleRun = true
        echo "Single Account Run on ${singleMasterID} - ${singleAccountID}"
    }

    // Iterate through all the valid master accounts
    yamlConfig.each { masterAccount, masterAccountConfig ->

        // If it is a single account run, only run in that specific master
        if(!singleRun || (singleRun && masterAccount == singleMasterID)) {
            lock(resource: masterAccount) {
                stage("${masterAccount}: Central Management Account") {

                    // Initialize fields
                    dataSet = []
                    centralUpdated = true
                    centralLog = []

                    // if not explicity set to ```prompt: false```, default to showing prompt in Jenkins build
                    // to continue execution of Launchpad (useful for waiting to deploy to production stages)
                    if (!singleRun && (masterAccountConfig.prompt == null || masterAccountConfig.prompt)) {
                        // If timeout or abort input, catch and continue to next master
                        try {
                            timeout(time: 10, unit: 'DAYS') {
                                input "Apply Launchpad on: ${masterAccount}?"
                            }
                        }
                        catch (e) {
                            return
                        }
                    }

                    // Run central account
                    node(nodeLabel) {
                        
                        // Checkout to the latest release-tag if doing a single account run
                        scmVars = checkout scm
                        currentAdapter = adapter
                        if (env.BRANCH_NAME == "master" && singleRun) {
                            // Get the tags and checkout to the latest one
                            sh (script: "git pull")
                            def latestRelease = sh (script: "git describe --tags --abbrev=0", returnStdout: true)
                            echo "Applying Launchpad Release: ${latestRelease}"
                            sh (script: "git checkout ${latestRelease}")
                        }

                        // Tag the current repo if this a production run, indicating a new release. Only tag once per build.
                        if(env.BRANCH_NAME == "master" && !singleRun && !tagged
                            && masterAccountConfig.production != null && masterAccountConfig.production){
                            // Get the version to tag with
                            def version = sh (script: "cat next_version.txt", returnStdout: true)

                            // Verify the format of the tag, if not correct format, do not tag and continue with run
                            if(verify_version(version)) {

                                // Get the git url and credentials to push the tag
                                def gitUrl = sh (script: "git config --get remote.origin.url", returnStdout: true)
                                def repo = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/.')[-2]
                                withCredentials([
                                    usernamePassword(credentialsId: 'GIT_CREDS', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')
                                ]){
                                    gitUrl = gitUrl.replace("https://", "https://${GIT_USER}:${GIT_PASS}@")

                                    // Check out to the commit from this build before tagging
                                    // Tag based on a source control file, will not create/update the tag if already exists
                                    def buildCommit = scmVars.GIT_COMMIT
                                    echo "Build Commit to Tag: ${buildCommit} - ${version}"
                                    sh """
                                    mkdir temp_git
                                    cd temp_git
                                    git clone ${gitUrl}
                                    cd ${repo}
                                    git checkout ${buildCommit}
                                    git tag ${version} || echo "Can't create tag ${version}"
                                    git push origin ${version} || echo "Can't push tag ${version}"
                                    cd ../..
                                    rm -rf temp_git
                                    """
                                }

                                // Ensure to not tag again in the build
                                tagged = true
                            }
                            else {
                                echo "next_version.txt ($version) is not correctly formatted in vX.Y.Z[alpla|beta] format. Will not tag run."
                            }
                        }

                        // Deploy to Central Management
                        echo "About to deployCentralManagement"
                        result = adapter.deployCentralManagement(masterAccount, masterAccountConfig) // Configurable per cloud provider
                        echo "${result}"
                        success = result[0]
                        centralLog = result[1]
                        if(!success) {
                            launchpadFailuresExists = true
                            centralUpdated = false
                        }
                    }

                    // Only run if the central was updated successfully
                    if(centralUpdated) {

                        // Check to determine master account == same as commit message if so,
                        // create the a temporary config for a single account run
                        if (masterAccount != singleMasterID)
                        {
                            tempMasterAccountConfig = masterAccountConfig
                        }
                        else 
                        {
                            tempMasterAccountConfig = oneAccountTenants(singleAccountID, masterAccountConfig)
                        }

                        // Deploy launchpad on account(s)
                        result = adapter.deployLaunchpadOnAccount(nodeLabel, masterAccount, tempMasterAccountConfig, singleAccountID, newAccount)
                        success = result[0]
                        dataSet = result[1]

                        // If the launchpad wasn't successful, raise the boolean flag
                        if (!success) {
                            launchpadFailuresExists = true
                        }
                    }
                }

                // Add results to the log
                log.add([
                    MasterAccount: masterAccount,
                    CentralManagementAccount: centralLog,
                    Dataset: dataSet
                ])
            } 
        }
    }   

    // Create the log file and archive in the Jenkins pipeline
    node(){
        archiveLaunchpadLog(log)

        // If there was a failure in the launchpad, send an email 
        if (launchpadFailuresExists) {
            sendStatusEmail()
        }
    }
}

/* 
   This function is meant to format only one account for processing.
   This function should only be called when the commit message of the master 
   branch has the proper format and contents.
*/
def oneAccountTenants(accountId, masterAccountConfig) {
    try { 
        def config = readYaml text: """
        prompt: false
        credentialsKey: ${masterAccountConfig.credentialsKey}
        credentialsSecret: ${masterAccountConfig.credentialsSecret}
        accountsInfo:
            central: "${masterAccountConfig.accountsInfo.central}"
            securityCentral: "${masterAccountConfig.accountsInfo.securityCentral}"
            customers: 
              prod:
                accounts:
                - "${accountId}"
        """
        return config
    }
    catch (Exception e) { echo "${e}:  Issue with oneAccountTenant" }
}

/*
   Function that creates the json log file to store in the Jenkins run
*/
def archiveLaunchpadLog(log) {
    echo "Archiving logs"
    def outJson = JsonOutput.toJson(log)
    def prettyJson = JsonOutput.prettyPrint(outJson)
    writeFile(file: 'logging.json', text: prettyJson)
    archiveArtifacts artifacts: 'logging.json', fingerprint: true
}

/*
   Function that sends the json log file if there were any errors as an email
*/
def sendStatusEmail(launchpadFailureExists) {
    if (env.BRANCH_NAME == 'master') {
        echo "Sending email"
        try {
            // Might want to include if this is from an aws run or azure run
            emailext attachLog: true, attachmentsPattern: 'logging.json', body: "A Launchpad failure has occurred on build ${env.BUILD_NUMBER}: ${env.BUILD_URL}. See attachment.", subject: 'CC: Launchpad Failure', to: 'cc_customerrelations@optum.com'
        }
        catch (Exception ex) { echo "${ex}:  Failed to send Launchpad status email" }
    }
    return
}

/*
   Function that verifies if the account category inside the yaml file is valid
*/
def isValidAccountCategory(accountCategory) {
    def allowedCategories = ["test", "beta", "nonProd", "prod"]
    return allowedCategories.contains(accountCategory)
}

/**
    Helper function to verify the version in the next_version.txt is in the correct format.
    Correct format: vX.Y.Z[alpha|beta]

    version: The version string to verify 
*/
def verify_version(version) {
    return version ==~ /v\d+\.\d+\.\d+(alpha|beta)?/
}
