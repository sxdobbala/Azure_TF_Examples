import com.optum.jenkins.pipeline.library.aem.AemDocker

def call(Map<String, Object> config){
  AemDocker aem = new AemDocker(this)
  aem.deploy(config)
}
