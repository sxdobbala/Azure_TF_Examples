package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def call(Config config, String status)
{
    if (config.notificationEmail)
    {
        email(config, status)
    }
    else if (config.flowdock)
    {
        flowdock(config, status)
    }
}

def email(Config config, String status)
{
    String toEmail = config.notificationEmail
    
    emailext (
        to: "${toEmail}",
        subject: "${env.BUILD_TAG} - Status - ${status}!",
        attachLog: true,
        body: """
            <style>
                body, table, td, th, p {
                    font-family:verdana,helvetica,sans serif;
                    font-size:11px;
                    color:black;
                }
                td.bg1 { color:white; background-color:#595959; font-size:120% }
                td.console { font-family:courier new, lucida console; }
            </style>
            <body>
                <table border=2 cellspacing=2 cellpadding=2 width="40%">
                    <tr>
                        <td align="left" width="30%">
                            <img src="https://jenkins.optum.com/provisioning/static/d7f1766a/images/headshot.png" />
                        </td>
                        <td valign="center" width="70%">
                            <b style="font-size: 170%;">Jenkins Build Results</b>
                        </td>
                    </tr>
                    <tr>
                        <td>URL:</td>
                        <td>
                            <a href='${env.BUILD_URL}'>${env.JOB_NAME}</a>
                        </td>
                    </tr>
                    <tr>
                        <td>JOB NAME:</td>
                        <td>${env.JOB_NAME}</td> 
                    </tr>
                    <tr>
                        <td>BUILD STATUS:</td>
                        <td>${status}</td>
                    </tr>
                    <tr>
                        <td>BUILD NUMBER:</td>
                        <td>${env.BUILD_DISPLAY_NAME}</td>
                    </tr>
                    <tr>
                        <td>DATE/TIME:</td>
                        <td>${env.BUILD_TIMESTAMP}</td>
                    </tr>
                </table>
                <br />
                    
                <!-- change set -->
                <table width="40%">
                    <tr>
                        <td class="bg1">
                            <b>CHANGES</b>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <font face="geneva, arial" size=2>
                                <a href='${env.JOB_URL}/${env.BUILD_NUMBER}/changes'>View Changes</a>
                            </font>
                        </td>
                    </tr>
                </table>
                <br />
                
                <!-- artifacts -->
                <table width="40%">
                    <tr>
                        <td class="bg1">
                            <b>BUILD ARTIFACTS</b>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <font face="geneva, arial" size=2>
                                <a href='${env.JOB_URL}/lastSuccessfulBuild/artifact/'>View Artifacts</a>
                            </font>
                        </td>
                    </tr>
                </table>
                <br />
                
                <table border=0 cellspacing=2 cellpadding=2 width="40%">
                    <tr bgcolor="#595959" width="70%">
                        <td class="bg1">
                            <b>VIEW UNIT TEST REPORT</b>
                        </td>
                    </tr>
                    <tr width="70%">
                        <td>
                            <font face="geneva, arial" size=2>
                                <a href='${env.JOB_URL}lastBuild/testReport/'>View UnitTest</a>
                            </font>
                        </td>
                    </tr>
                </table>
                <br />
                
                <!-- console output -->
                <table border=0 cellspacing=2 cellpadding=2 width="40%">
                    <tr>
                        <td class="bg1">
                         <b>CONSOLE OUTPUT</b>
                        </td>
                    </tr>
                    <tr>
                        <td class="console" width="100%">
                            Please find attached build log file.
                        </td>
                    </tr>
                </table>
                <br />
            </body>"""
    )
}

def flowdock(Config config, String status)
{
    def message = "Notification generated by jenkins job: ${env.JOB_NAME}\n" + 
        "Job URL: ${env.BUILD_URL}\n" +
        "Build number: ${env.BUILD_DISPLAY_NAME}\n" +
        "Date time: ${env.BUILD_TIMESTAMP}\n" +
        "Build tag: ${env.BUILD_TAG}\n" +
        "Build status: ${status}\n"
    
    def flowdock = config.flowdock
    def requestBody = Helpers.requestBodyFlowdock(message)
    
    withCredentials([string(credentialsId: flowdock.token, variable: 'token')])
    {
        sh "curl -X POST --header 'Content-Type: application/json' -d '${requestBody}' https://${token}@api.flowdock.com/flows/${flowdock.org}/${flowdock.flow}/messages"
    }
}