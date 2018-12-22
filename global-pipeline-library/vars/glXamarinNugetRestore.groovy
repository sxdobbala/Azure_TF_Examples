import com.optum.jenkins.pipeline.library.xamarin.nuget.Nuget

/**
 * Shortcut for running xbuild
 *
 * @params sln String Required Path to the .sln file
 */

def call(Map<String, Object> config){
  Nuget nuget = new Nuget(this)
  nuget.restore(config)
}
