#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.xldeploy

class XLDeploy implements Serializable {
  def jenkins

  XLDeploy() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
      'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  XLDeploy(jenkins) {
    this.jenkins = jenkins
  }

/**
 * generateXLDeployManifestXML.
 *   Generates XML needed to import a package to XL Deploy.
 *   Note: This method returns an XML string containing an XL Deploy manifest.
 *         Afterwards, you will want to add at least one deployable to the manifest
 *         via addDeployable
 *
 * @param xldWorkspace String Required Your XL Deploy workspace name
 * @param xldAppName String Required The application name in XL Deploy (udm.Application)
 * @param xldAppVersion String Required The application version (udm.DeploymentPackage)
 *
 */

  def String generateXLDeployManifestXML(Map<String,Object> params) {
    def defaults = [
      xldWorkspace       : null, //required
      xldAppName         : null, //required
      xldAppVersion      : null, //required
    ]
    def config = defaults + params

    jenkins.echo "generateXLDeployManifestXML arguments: $config"

    def xmlString = """<?xml version="1.0" encoding="UTF-8"?>
    <udm.DeploymentPackage application="$config.xldWorkspace/$config.xldAppName" version="$config.xldAppVersion">
      <application />
      <orchestrator />
      <satisfiesFirstSignOffForDeployment>false</satisfiesFirstSignOffForDeployment>
      <satisfiesSecondSignOffForDeployment>false</satisfiesSecondSignOffForDeployment>
      <deployables>
      </deployables>
      <applicationDependencies />
      <dependencyResolution>LATEST</dependencyResolution>
      <undeployDependencies>false</undeployDependencies>
    </udm.DeploymentPackage>"""
  return xmlString
  }

/**
 * addDeployable
 *   This methods inserts the XML for one deployable into the input param xldManifestXML.
 *
 * @param xldWorkspace String Required Your XL Deploy workspace name
 * @param xldAppName String Required The application name in XL Deploy (udm.Application)
 * @param xldAppVersion String Required The application version (udm.DeploymentPackage)
 *
 */

  def String addDeployable(Map<String,Object> params) {
    def defaults = [
        xldManifestXML     : null    //required, see method generateXLDeployManifestXML
      , xldType            : null    //required, the type of deployable to add
      , xldName            : null    //required
      , fileUri            : null    //required, URI location of artifact
      , scanPlaceholders   : 'true'  //optional, true|false
      , tags               : [ ]     //optional, list of tag(s)
      , additionalAttrs    : [:]     //optional, map of additional attributes
    ]
    def config = defaults + params

    jenkins.echo "addDeployable arguments: $config"

    def xmlString = """  <$config.xldType name="$config.xldName">
          <tags></tags>
          <scanPlaceholders>$config.scanPlaceholders</scanPlaceholders>
          <fileUri>$config.fileUri</fileUri>
        </$config.xldType>
      </deployables>"""

    def myTags = config.tags
    if (myTags.size() > 0)
    {
      def myTagStr = "<value>" + config.tags.join("</value><value>") + "</value></tags>"
      xmlString  = xmlString.replace("</tags>",myTagStr)
    }

    def myAttrs = config.additionalAttrs
    if (myAttrs.size() > 0)
    {
      def myAttrStr = "</fileUri>"
      for (item in myAttrs)
      {
        myAttrStr = myAttrStr + "\n          <" + item.key + ">" + item.value + "</" + item.key + ">"
      } 
      xmlString  = xmlString.replace("</fileUri>",myAttrStr)
    }
    def newXML = "$config.xldManifestXML"
    newXML = newXML.replace("</deployables>", xmlString)
  return newXML
  }


/**
 * importXLDPackage
 *   This methods imports a package into XL Deploy
 *
 * @param xldManifestXML   The package definition.  See method generateXLDeployManifestXML
 * @param xldServerLabel   The label in your Jenkins global config for your XLD Server definition
 * @param xldCredentialID  The jenkins credential ID used for authenticating to XLD Server
 *
 */

  def String importXLDPackage(Map<String,Object> params) {
    def defaults = [
        xldManifestXML     : null    //required, see method generateXLDeployManifestXML
      , xldServerLabel     : null    //required, label in your Jenkins global config for your XLD Server definition
      , xldCredentialID    : null,   //required, jenkins credential ID used for authenticating to XLD Server
    ]
    def config = defaults + params

    jenkins.echo "importXLDPackage arguments: $config"

    jenkins.writeFile( 'file': 'deployit-manifest.xml', 'text': config.xldManifestXML)    
    
    // Use XLD plugin to create the package
    jenkins.xldCreatePackage artifactsPath: ".", manifestPath: "deployit-manifest.xml", darPath: "pkg-manifest.dar"
    // Now import package to XL Deploy
    jenkins.xldPublishPackage serverCredentials: config.xldServerLabel, darPath: "pkg-manifest.dar", overrideCredentialId: config.xldCredentialID
  }


/**
 * deployXLDPackage
 *   This methods imports a package into XL Deploy
 *
 * @param xldEnvironmentID  The XL Deploy Environment CI ID
 * @param xldPackageID      The XL Deploy package CI ID
 * @param xldServerLabel    The label in your Jenkins global config for your XLD Server definition
 * @param xldCredentialID   The jenkins credential ID used for authenticating to XLD Server
 *
 */


  def String deployXLDPackage(Map<String,Object> params) {
    def defaults = [
        xldEnvironmentID   : null    //required, The XL Deploy Environment CI ID
      , xldPackageID       : null    //required, The XL Deploy package CI ID
      , xldServerLabel     : null    //required, label in your Jenkins global config for your XLD Server definition
      , xldCredentialID    : null,   //required, jenkins credential ID used for authenticating to XLD Server
    ]
    def config = defaults + params

    jenkins.echo "deployXLDPackage arguments: $config"

    jenkins.xldDeploy serverCredentials: config.xldServerLabel, environmentId: config.xldEnvironmentID, packageId: config.xldPackageID, overrideCredentialId: config.xldCredentialID
    
  }

}

