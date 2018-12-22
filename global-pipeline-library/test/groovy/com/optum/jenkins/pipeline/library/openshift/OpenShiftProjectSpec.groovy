package com.optum.jenkins.pipeline.library.openshift

import spock.lang.Specification

class OpenShiftProjectSpec extends Specification {

    def "create a project"(){
        given:
        def calledJenkinsCommand
        def jenkins = [
                env     : [USER: 'username', PASS: 'password'],
                echo    : { String s -> println(s) },
                command : { String cmd, Boolean someBoolean, String shabang -> calledJenkinsCommand = cmd },
                withCredentials : { java.util.ArrayList a, Closure c -> c.call()}
        ]
        def params = [
                credentials: 'credentials',
                name: 'shift',
                displayName: null,
                description: null,
                cpu: 1.0,
                ram: 2.0,
                tmdbCode: 'TMDB-8550329',
                askId: 'UHGWM110-018158'
        ]

        when: "Create is called"
        def project = new Project(jenkins)
        project.create(params)

        then: "Script uses the correct creds"
        noExceptionThrown()
        calledJenkinsCommand.toString().contains('Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=')
        print(calledJenkinsCommand)
    }

    def "delete a project"(){
        given:
        def calledJenkinsCommand
        def jenkins = [
                env     : [USER: 'username', PASS: 'password'],
                echo    : { String s -> println(s) },
                command : { String cmd, Boolean someBoolean, String shabang -> calledJenkinsCommand = cmd },
                withCredentials : { java.util.ArrayList a, Closure c -> c.call()}
        ]
        def params = [
                credentials: 'credentials',
                name: 'shift'
        ]

        when: "Delete is called"
        def project = new Project(jenkins)
        project.delete(params)

        then: "Script uses the correct creds"
        noExceptionThrown()
        calledJenkinsCommand.toString().contains('Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=')
        print(calledJenkinsCommand)
    }

    def "update a resourceQuota"(){
        given:
        def calledJenkinsCommand
        def projectName = 'shift'
        def cpu = 2
        def jenkins = [
                env     : [USER: 'username', PASS: 'password'],
                echo    : { String s -> println(s) },
                command : { String cmd, Boolean someBoolean, String shabang -> calledJenkinsCommand = cmd },
                withCredentials : { java.util.ArrayList a, Closure c -> c.call()}
        ]
        def params = [
                credentials: 'credentials',
                name: projectName,
                cpu: cpu
        ]

        when:
        def project = new Project(jenkins)
        project.updateQuota(params)

        then: "Script targets the correct project"
        noExceptionThrown()
        calledJenkinsCommand.toString().contains('"name":"' + projectName + '"')
        calledJenkinsCommand.toString().contains('"cpu":' + cpu)
        print(calledJenkinsCommand)

    }
}
