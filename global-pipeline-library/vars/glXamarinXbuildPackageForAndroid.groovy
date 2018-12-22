import com.optum.jenkins.pipeline.library.xamarin.xbuild.Xbuild
/**
 * Shortcut for running xbuild packageForAndroid
 *
 * @params csproj String Required Path to the .csproj file
 * @params configuration String Optional Release or other
 */
def call(Map<String, Object> config){
  Xbuild xbuild = new Xbuild(this)
  xbuild.packageForAndroid(config)
}

