package com.optum.jenkins.pipeline.library.openshift

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonBuilder

class Project implements Serializable{
    def jenkins

    Project() throws Exception {
        throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
                'This enables access to jenkinsfile steps, global variables, functions and envvars.')
    }

    Project(jenkins) {
        this.jenkins = jenkins
    }

    /**
     * Creates an OpenShift Project
     *
     * @param credentials of the owner creating the project
     * @param name String to set the name of the project being created.
     *    @Pattern(
     *      regexp = "([a-z0-9]+-)*[a-z0-9]+$",
     *      message = "project name cannot start with hyphen and must contain only lowercase letters, numbers and dashes"
     *      )
     *    @Size
     *      min = 6,
     *      max = 63,
     *      message = "Project name must be between 6 and 63 charact
     * @param displayName String (Optional)
     *    @Pattern(
     *      regexp = "[A-Za-z0-9-]+"
     *      message = "Upper or lower case with hyphens, no spaces"
     *      )
     *    @Size
     *      min = 0,
     *      max = 63,
     *      message = "Project name must be between 0 and 63 characters.
     * @param description String (Optional)
     * @param cpu String (Optional)
     * @param ram String (Optional)
     * @param tmdbCode String (Required)
     * @param askId String (Required)
     * @param platform String such as 'nonprod-origin'
     * @param datacenter String such as 'elr'
     * @param zone String such as 'core'
     */
    def create(Map<String, Object> params){
        def defaults = [
                credentials: null,  // required
                name: null, /* required, w/ validation:      	@Pattern(
                                                                regexp = "([a-z0-9]+-)*[a-z0-9]+$",
                                                                message = "project name cannot start with hyphen and must contain only lowercase letters, numbers and dashes"

                                                                )
                                                                @Size(
                                                                min = 6,
                                                                max = 63,
                                                                message = "Project name must be between 6 and 63 characters"
                                                        )*/
                displayName: null, /*optional, w/ validation:   @Pattern(
                                                                regexp = "[A-Za-z0-9-]+",
                                                                message = "Upper or lower case with hyphens, no spaces"
                                                                )
                                                                @Size(
                                                                min = 0,
                                                                max = 63,
                                                                message = "Project name must be between 0 and 63 characters"
                                                        )*/
                description: null, //optional
                cpu: 1.0, //optional
                ram: 2.0, //optional
                tmdbCode: null, //required
                askId: null, //required
                platform: 'nonprod-origin', //optional
                datacenter: 'elr', //optional
                zone: 'core' //optional
        ]
        def config = defaults + params

        jenkins.echo "Create arguments: $config"

        if (!config.displayName) {
            config.displayName = config.name
        }

        if (!config.description) {
            config.description = "Project " + config.name
        }

        String encoded = encodeCredentials(config.credentials)

        String createCommand = """
               curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'Authorization: Basic $encoded' -d '{
               "name": "$config.name", 
               "description": "$config.description",
               "displayName": "$config.displayName",
               "cpu": "$config.cpu",
               "ram": "$config.ram",
               "openShiftBilling": {
                 "tmdbCode": "$config.tmdbCode",
                 "askId": "$config.askId"
               }
            }' "https://dtc.optum.com/api/paas/$config.platform/$config.datacenter/$config.zone/project"
        """

        jenkins.command(createCommand, false, "#!/bin/sh -e")

    }

    /**
     * deletes an OpenShift Project
     *
     * @param credentials of the owner of the project for deletion.
     * @param name String to set the name of the project being deleted.
     * @param platform String such as 'nonprod-origin'
     * @param datacenter String such as 'elr'
     * @param zone String such as 'core'
     *
     */

    def delete(Map<String, Object> params) {
        def defaults = [
                credentials: null,  //required
                name       : null,  //required
                platform   : 'nonprod-origin', //optional
                datacenter : 'elr', //optional
                zone       : 'core' //optional
        ]
        def config = defaults + params

        jenkins.echo "Delete arguments: $config"

        String encoded = encodeCredentials(config.credentials)

        String deleteCommand = """
            curl -X DELETE --header 'Accept: application/json' --header 'Authorization: Basic $encoded' "https://dtc.optum.com/api/paas/$config.platform/$config.datacenter/$config.zone/project/$config.name"
        """

        jenkins.command(deleteCommand, false, "#!/bin/sh -e")
    }
    /**
     * update the Quota for any or all resources related to your OpenShift Project
     *
     * @param credentials of the owner of the project for deletion.
     * @param name String to set the name of the project being deleted.
     * @param platform String such as 'nonprod-origin'
     * @param datacenter String such as 'elr'
     * @param zone String such as 'core'
     * @param cpu String
     * @param ram String
     * @param pods String
     * @param secrets String
     * @param volumes String
     * @param replicationControllers String
     * @param storage String
     */

    def updateQuota(Map<String, Object> params) {
        def defaults = [
                credentials: null,  //required
                name       : null,  //required
                platform   : 'nonprod-origin', //optional
                datacenter : 'elr', //optional
                zone       : 'core',//optional
                cpu        : null,  //optional
                ram        : null,  //optional
                pods       : null,  //optional
                secrets    : null,  //optional
                services   : null,  //optional
                volumes    : null,  //optional
                replicationControllers: null, //optional
                storage    : null,  //optional

        ]
        def config = defaults + params

        jenkins.echo "Update quota arguments: $config"

        String encoded = encodeCredentials(config.credentials)

        String updateQuotaCommand = """
            curl -X PUT --header 'Content-Type: application/json' --header 'Accept: application/plain' --header 'Authorization: Basic $encoded' -d '""" +
                requestBodyUpdateQuota(config) + """' "https://dtc.optum.com/api/paas/$config.platform/$config.datacenter/$config.zone/project"
        """

        jenkins.command(updateQuotaCommand, false, "#!/bin/sh -e")

    }

    def encodeCredentials(String creds){
        jenkins.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: creds,
    usernameVariable: 'USER', passwordVariable: 'PASS']]) {
        String unencoded = "$jenkins.env.USER" + ":" + "$jenkins.env.PASS"
        String encoded = unencoded.getBytes("UTF-8").encodeBase64().toString()
        return encoded
    }
}

    @NonCPS
    private requestBodyUpdateQuota(Map<String, String> config) {
        def jsonBuilder = new JsonBuilder()
        def root = jsonBuilder.call(
                name: config.name,
                cpu: config.cpu,
                ram: config.ram,
                pods: config.pods,
                secrets: config.secrets,
                services: config.services,
                volumes: config.volumes,
                replicationControllers: config.replicationControllers,
                storage: config.storage
        )
        return jsonBuilder.toString()
    }
}


