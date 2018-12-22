import com.optum.jenkins.pipeline.library.xamarin.android.NameUtils
/**
 * Come up with names for apk artifacts, if not specified
 * Uses existing apk as a naming convention for signedApk
 * Use this function AFTER `xbuild package`
 *
 * @params appName String Required Name of app
 *
 * @params folderPath String Optional Path to folder where apk is.
 *      Format: leading '/', no trailing '/'
 * @params rootDir String Optional Path of folders between workspace root
 *      and appName. For most projects, is empty
 * @params inputApk String Optional Explicitly set name of inputApk
 * @params signedApk String Optional Explicitly set name of signedApk
 * @params endApk String Optional Explicitly set name of endApk
 *
 */
def call(Map<String, Object> config){
  NameUtils nameUtils = new NameUtils(this)
  nameUtils.makeDefaultApkNames(config)
}
