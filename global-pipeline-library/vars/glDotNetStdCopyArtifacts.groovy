import com.optum.jenkins.pipeline.library.dotnet.DotNetStd

def call(Map<String, Object> config){
  DotNetStd dotNetStd = new DotNetStd(this)
  dotNetStd.CopyArtifactsDotNetStdApp(config)
}
