import com.optum.jenkins.pipeline.library.aem.Aem

def call(Map<String, Object> config){
  Aem aem = new Aem(this)
  aem.uninstallPackage(config)
}
