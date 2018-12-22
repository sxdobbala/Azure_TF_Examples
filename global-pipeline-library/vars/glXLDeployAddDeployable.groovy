import com.optum.jenkins.pipeline.library.xldeploy.XLDeploy

/**
 * glXLDeployAddDeployable
 *   This methods inserts the XML for one deployable into the input param xldManifestXML.
 *
 * @param xldManifestXML   String Required    See method glXLDeployGenerateManifestXML
 * @param xldType          String Required    The XL Deploy type of deployable to add
 * @param xldName          String Required    The XL Deploy Name of this deployable
 * @param fileUri          String Required    URI location of artifact
 * @param scanPlaceholders String Optional    Default 'true'
 * @param tags             [String] Optional  List of tag(s)
 * @param additionalAttrs  [Map] Optionali    [String:String] map of additional attributes
 */

def call(Map<String, Object> config){
  XLDeploy xld = new XLDeploy(this)
  xld.addDeployable(config)
}

