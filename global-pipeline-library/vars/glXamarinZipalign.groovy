import com.optum.jenkins.pipeline.library.xamarin.zipalign.Zipalign

/**
 * Shortcut for running zipalign
 *
 * @params inFile String Required Path to the input apk
 * @params outFile String Required Path of the output APK
 * @params forceOverwrite Boolean Optional Overwrite existing outfile.zip. Defaults to true.
 * @params verbose Boolean Optional Verbose Output. Defaults to true.
 * @params alignment Integer Optional Defaults to 4
 *
 */

def call(Map<String, Object> config){
  Zipalign zipalign = new Zipalign(this)
  zipalign.align(config)
}