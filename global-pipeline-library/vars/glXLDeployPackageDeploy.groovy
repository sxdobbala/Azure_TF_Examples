import com.optum.jenkins.pipeline.library.xldeploy.XLDeploy

/**
 * glXLDeployPackageDeploy
 *   This methods deploys an existing XL Deploy package to the environment specified
 *
 * @param xldEnvironmentID  String Required  The XL Deploy Environment CI ID
 * @param xldPackageID      String Required  The XL Deploy package CI ID
 * @param xldServerLabel    String Required  The label in your Jenkins global config for your XLD Server definition
 * @param xldCredentialID   String Required  The jenkins credential ID used for authenticating to XLD Server
 *
 */

def call(Map<String, Object> config){
  XLDeploy xld = new XLDeploy(this)
  xld.deployXLDPackage(config)
}

