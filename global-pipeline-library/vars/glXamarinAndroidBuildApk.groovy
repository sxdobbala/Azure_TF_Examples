import com.optum.jenkins.pipeline.library.xamarin.android.AndroidBuild
/**
 * Builds Android part of Xamarin project.
 * Stashes all the apks in 'All_APKS'
 *
 * @params appName String Required Short name of application. Use underscores instead of spoces
 * @params keyCredentials String Required Credentials for the jarsigner key
 * @params storeCredentials String Required Credentials for the jarsigner storepass key
 * @params buildEnv String Optional STAGE_ENV or PROD_ENV. Defaults to STAGE_ENV
 * @params endApk String Optional Name of the apk artifact
 * @params inputApk String Optional Path to input apk
 * @params keystore String  Name of the keystore with which to sign the APK
 * @params signedApk String Optional Path to signed apk
 * @params rootDir String Optional Prefix to all paths. If defined, should have trailing '/'
 */
def call(Map<String, Object> config){
  AndroidBuild androidBuild = new AndroidBuild(this)
  androidBuild.buildApk(config)
}