import com.optum.jenkins.pipeline.library.sca.Fortify

/**
 *
 * This class allows you to create a Fortify Scan
 *
 * In Jenkins -> Manage Jenkins -> Configure System
 * 1 - Look for the "Global properties" section.
 *     You must add Environment Variable FORTIFY_VERSION=HP_Fortify_SCA_and_Apps_16.11
 * 2 - Look in the section Cloud -> BuildSwarm -> find the "docker-maven-slave" and click on "Container Settings".
 *     Find the "Volumes From" section and add "fortify".  This will make the fortify mixin available to your "docker-maven-slave".
 *     See: https://github.optum.com/jenkins/docker_build_agents/tree/master/mixins
 * 3 - the env.FORTIFY_HOME is being set from the FORTIFY_VERSION in the Constants
 *
 * @param scarProjectVersion Project version in Scar. This is required to upload the fpr file to scar and can be found within the URL
 *  for your SCAR project and desired version. (e.g. https://scar.uhc.com/ssc/html/ssc/version/99999/fix?issueGrouping=ISSUE_FOLDER. 99999 is the version ID.)
 * @param fortifyBuildName The name of the fortify build.
 * @param scarUploadToken Token when uploading the fpr file to scar. This is for authentication. This can be used, or
 *  scarCredentialsId can be provided and a token will be retrieved.
 * @param scarCredentialsId ID to SCAR credentials for upload and/or download. This is required if downloading from SCAR.
 *  If uploading to SCAR, this can be used or scarUploadToken may be used.
 * @param fortifyTranslateExclusions To pass the list of exclusions to fortify so that can be excluded from the scan
 * @param fortifyMaxSizeMemory defaults to 1G
 * @param uploadToScar If true, uploads the fpr scar, defaults to false. Requires scarUploadToken or scarCredentialsId.
 * @param downloadFromScar If true, downloads previous scan FPR from SCAR, defaults to false. Requires scarCredentialsId.
 * @param criticalThreshold, fortify fails if the critical scan errors are greater than this threshold, defaults to 0
 * @param highThreshold, fortify fails if the high scan errors are greater than this threshold, defaults to 0
 * @param mediumThreshold, fortify fails if the medium scan errors are greater than this threshold
 * @param lowThreshold, fortify fails if the low scan errors are greater than this threshold
 * @param isDotNetCore If true, translates the project using .NET Core 1.10
 * @param javascript.additionalTranslateOptions Additional options to be tagged onto the translate command
 * @param javascript.source Source file or directory of the javascript files
 * @param isGenerateDevWorkbook Set to true to generate the developer workbook report.
 * @param onlyNewIssues Set to true to report only on new issues. This applies to the thresholds and the Developer
 *  Workbook (PDF Scan Summary will also report on all issues.) This is useful in branch builds, for example. Defaults
 *  to false.
 * @param additionalIssueFilters Additional filters to apply to the issue count. This applies to the thresholds and the
 *  Developer Workbook (PDF Scan Summary will also report on all issues.) See the Audit Workbench documentation
 *  for details. (Hint, use the search box in Audit Workbench below the issue list build and try out filters.)
 * @param source specify the source directory for non-javascript scans (see javascript.source for javascript scans.)
 *  *  Defaults to **\/*.
 */

def call(Map<String, Object> config){
  Fortify fortify = new Fortify(this)
  fortify.fortifyScanMaven(config)
}
