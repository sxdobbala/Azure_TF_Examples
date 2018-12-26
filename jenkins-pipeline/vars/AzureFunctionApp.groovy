def call(Map<String, Closure> hooks = [:], FunctionAppApapter adapter = null) {
    
    def optumfile, 
        dockerAgent, 
        applicationName, 
        subscriptions, 
        deploymentEnvironments, 
        credentials
    
    stage("Checkout") {
        node() {
            checkout scm
            optumfile = readYaml file: "Optumfile.yml"
            dockerAgent = optumfile.pipeline.functionApp.azure.node
            applicationName = optumfile.pipeline.functionApp.azure.application.name
            subscriptions = optumfile.pipeline.functionApp.azure.subscriptions
            deploymentEnvironments = [:]
        }
    }

    // Grab the environments to deploy to.
    optumfile.pipeline.functionApp.azure.environments.each { environment, environmentConfiguration ->
        if (env.BRANCH_NAME == "master" && environment != "pr" || env.CHANGE_ID && environment == "pr") {
            deploymentEnvironments.put(environment, environmentConfiguration)
        }
    }


    if (env.BRANCH_NAME == "master" || env.CHANGE_ID) {

        def namespace = env.CHANGE_ID ? "${env.CHANGE_ID}" : "" // Contains the PR number or empty
    
        milestone()

        node(dockerAgent) {
            stage("Test") {
                adapter.test()
            }

            stage("Build") {
                adapter.build()
            }

            stage("Code Quality") {
                def isPullRequest = false
                if (env.CHANGE_ID) {
                    isPullRequest = true
                }                    
                adapter.codeQuality(isPullRequest, env.BRANCH_NAME)
            }

            // Ensure "functions.zip" exists
            if (!fileExists("functions.zip")) {
                error("The 'Build' stage did not produce 'functions.zip'")
            }

            stash name: "function-zip", includes: "functions.zip", exludes: ""
        }

        deploymentEnvironments.each { environment, environmentConfiguration ->
            stage("Deploy to ${environment}") {

                credentials = subscriptions[environmentConfiguration.subscription].credentials
                def functionAppResourceGroup = environmentConfiguration.functionAppResourceGroup
                def functionAppName = environmentConfiguration.functionAppName

                milestone()

                lock("${applicationName}-${environment}${namespace}") {
            
                    try {
                        // Run preDeploy closure
                        node(dockerAgent) {
                            if (hooks.containsKey("preDeploy")) {
                                if (env.CHANGE_ID) {
                                    hooks.get("preDeploy")(true, environment)
                                } else {
                                    hooks.get("preDeploy")(false, environment)
                                }
                            }
                        }

                        // Prompt the user if they want to deploy
                        if (environmentConfiguration.prompt) {
                            timeout(time: 30, unit: "DAYS") {
                                input "Deploy to ${environment}?" 
                            }   
                        }

                        // Deploy to function app using a zip deployment
                        node(dockerAgent) {
                            withCredentials([azureServicePrincipal(credentialsId: credentials.servicePrincipal,
                                subscriptionIdVariable: 'ARM_SUBSCRIPTION_ID',
                                clientIdVariable: 'ARM_CLIENT_ID',
                                clientSecretVariable: 'ARM_CLIENT_SECRET',
                                tenantIdVariable: 'ARM_TENANT_ID')
                            ]) {
                                unstash "function-zip"

                                echo "Deploying to function app ${functionAppName} in resource group ${functionAppResourceGroup}"

                                sh """#!/bin/bash
                                . /etc/profile.d/jenkins.sh
                                az login --service-principal -u ${ARM_CLIENT_ID} -p ${ARM_CLIENT_SECRET} --tenant ${ARM_TENANT_ID}
                                az account set --subscription ${ARM_SUBSCRIPTION_ID}
                                az functionapp deployment source config-zip -g ${functionAppResourceGroup} -n ${functionAppName} --src functions.zip
                                """
                            }

                            if (hooks.containsKey("postDeploy")) {
                                if (env.CHANGE_ID) {
                                    hooks.get("postDeploy")(true, environment)
                                } else {
                                    hooks.get("postDeploy")(false, environment)
                                }
                            }

                            stage("Acceptance Tests") {
                                adapter.acceptanceTests(environment)
                            }
                        }
                    } catch (Exception e) {
                        echo "ERROR: Failed with: ${e}"

                        if (env.BRANCH_NAME == "master") {
                            error("Exiting")
                        }
                    } finally {
                        if (env.CHANGE_ID) {
                            stage("Teardown") {

                            }
                        }
                    }
                }
            } 
        }
    } else {
        return
    }
}