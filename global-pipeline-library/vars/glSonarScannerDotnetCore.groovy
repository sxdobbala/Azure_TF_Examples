
import com.optum.jenkins.pipeline.library.sonar.Sonar

def call(Map<String, Object> config){
  Sonar sonar = new Sonar(this)
  sonar.scanWithSonarScannerDotnetCore(config)
}
