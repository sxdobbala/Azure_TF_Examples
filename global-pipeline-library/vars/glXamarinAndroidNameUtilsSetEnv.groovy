import com.optum.jenkins.pipeline.library.xamarin.android.NameUtils
/**
 * Replace the BUILD_ENVIRONMENT string in AppSetup.cs
 *
 * @params appSetupPath String Required Path to AppSetup.cs
 * @params buildEnv String Required Name of the environment
 */
def call(Map<String, Object> config){
  NameUtils nameUtils = new NameUtils(this)
  nameUtils.setEnvironment(config)
}
