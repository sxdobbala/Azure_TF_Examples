import com.optum.jenkins.pipeline.library.xldeploy.XLDeploy

/**
 * glXLDeployGenerateManifestXML.
 *   Generates XML needed to import a package to XL Deploy.
 *   Note: This method returns an XML string containing an XL Deploy manifest.
 *         Afterwards, you will want to add at least one deployable to the manifest
 *         via addDeployable
 *
 * @param xldWorkspace   String Required  Your XL Deploy workspace name
 * @param xldAppName     String Required  The application name in XL Deploy (udm.Application)
 * @param xldAppVersion  String Required  The application version (udm.DeploymentPackage)
 *
 */

def call(Map<String, Object> config){
  XLDeploy xld = new XLDeploy(this)
  xld.generateXLDeployManifestXML(config)
}

