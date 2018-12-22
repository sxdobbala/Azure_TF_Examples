import com.optum.jenkins.pipeline.library.xldeploy.XLDeploy

/**
 * glXLDeployPackageImport
 *   This methods imports a package into XL Deploy
 *
 * @param xldManifestXML   String Required  The package definition.  See method glXLDeployGenerateManifestXML.
 * @param xldServerLabel   String Required  The label in your Jenkins global config for your XLD Server definition
 * @param xldCredentialID  String Required  The jenkins credential ID used for authenticating to XLD Server
 *
 */

def call(Map<String, Object> config){
  XLDeploy xld = new XLDeploy(this)
  xld.importXLDPackage(config)
}

