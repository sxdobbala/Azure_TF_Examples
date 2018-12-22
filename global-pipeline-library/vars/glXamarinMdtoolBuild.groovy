import com.optum.jenkins.pipeline.library.xamarin.mdtool.Mdtool

/**
 * Shortcut for running mdtool
 *
 * @params csproj String Required
 * @params configuration String Optional
 * @params verbose Boolean Optional Defaults to true
 */

def call(Map<String, Object> config){
  Mdtool mdtool = new Mdtool(this)
  mdtool.build(config)
}

