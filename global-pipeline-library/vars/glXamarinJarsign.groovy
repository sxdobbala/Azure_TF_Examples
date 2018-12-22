import com.optum.jenkins.pipeline.library.xamarin.jarsigner.Jarsigner

/**
 * Shortcut for running jarsigner
 * https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jarsigner.html
 *
 * @params inputApk String Required
 * @params keystore String Required
 *      Specifies a keystore to be used if you don't want to use the .keystore
 *      default database.
 * @params keystoreName String Required
 * @params signedjar String Required
 *      Specifies the name of the signed JAR file to be generated if you don't
 *      want the original unsigned file to be overwritten with the signed file.
 * @params storepass String Required
 *      Specifies the password that is required to access the keystore. This
 *      is only needed when signing (not verifying) a JAR file. In that case,
 *      if a -storepass option is not provided at the command line, then the
 *      user is prompted for the password.
 *
 * @params sigalg String Optional
 *      Specifies the name of the signature algorithm to use to sign the JAR file.
 * @params verbose Boolean Optional
 */

def call(Map<String, Object> config){
  Jarsigner jarsigner = new Jarsigner(this)
  jarsigner.sign(config)
}
