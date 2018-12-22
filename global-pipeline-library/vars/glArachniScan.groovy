import com.optum.jenkins.pipeline.library.security.Arachni

/**
 * Runs Arachni scan on target web app.
 * https://github.optum.com/devops-security/jenkins_security_slave is a dependency for this project.
 * Please fill out a Docker template in Manage Jenkins to use this tool, according to docs
 * kept here: https://hubconnect.uhg.com/docs/DOC-75528
 * Parameter list:
 * @param targetAddress [Required] URL or IP address to scan with Arachni.
 * @param scanType For selecting the checks included in scan, based on severity of potential vulns. "low" entails low severity checks and above
 * @param scanUsername The username that Arachni will use to authenticate with your web app. If 2 factor auth, please supply pathToLoginScript instead
 * @param scanPassword The password that Arachni will use to authenticate with your web app. If 2 factor auth, please supply pathToLoginScript instead
 * @param OOSSBucketName The name of the Optum Object Storage Service bucket for storing the Arachni reports
 * @param OOSSUsername The username to authenticate to the OOSS bucket for storing Arachni reports.
 * @param OOSSPassword The password to authenticate to the OOSS bucket for storing Arachni reports.
 * @param scope Used to limit scope of site that Arachni will scan. supply subdomain as string
 * @param pathToLoginScript Path to the login script
 */

def call(Map<String, Object> config){
  Arachni arachni = new Arachni(this)
  arachni.scanWithArachni(config)
}
