#!/usr/bin/env groovy
package com.optum.pipeline
import com.optum.config.Config
import com.optum.utils.Helpers

def verifyContrastServer(Config config)
{
    def notify = new Notify()
    try
    {
        sh '''
           . /etc/profile.d/jenkins.sh
           status_code=$(curl --write-out %{http_code} --silent --output /dev/null https://optum.contrastsecurity.com/Contrast/)
           if [[ "$status_code" == 302 ]] ; then
              echo "Contrast server is UP"
           else
              exit 1
           fi
           '''
    }
    catch(err)
    {
        notify.call(config, "Failed To connect to the Contrast Server ${err}")
        currentBuild.result = 'FAILURE'
        throw err
    }
}

def verifyContrastReport(Config config, Object contrast)
{
    def notify = new Notify()
    def configProfile = contrast.contrastProfile
    def configApplicationName = contrast.applicationName
    def configCount = contrast.vulnerabilityCount
    def configSeverity = contrast.vulnerabilitySeverity
    
    stage('Contrast Verification')
    {
        verifyContrastServer(config)
        try
        {
            contrastVerification profile: configProfile, applicationName: configApplicationName, count: configCount, severity: configSeverity
        }
        catch(err)
        {
            notify.call(config, "Failed To  retrieve from the Contrast Server ${err}")
            currentBuild.result = 'FAILURE'
            throw err
        }
    }
}
