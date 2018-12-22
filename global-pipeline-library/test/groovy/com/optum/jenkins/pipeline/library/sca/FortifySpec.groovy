package com.optum.jenkins.pipeline.library.sca

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import com.optum.jenkins.pipeline.library.event.EventStatus
import com.optum.jenkins.pipeline.library.event.FortifyLocalEvent
import com.optum.jenkins.pipeline.library.utils.PropertyReader.BuildInfoReader
import jenkins.model.Jenkins
import spock.lang.Specification

class FortifySpec extends Specification {

  def "jenkins context is available"() {
    given: "Default jenkins context"
    def jenkins = [echo: 'hello']
    when: 'Creating class with jenkins context'
    def fortify = new Fortify(jenkins)
    then: "Jenkins context is available"
    fortify.getJenkins() == jenkins
  }

  def "error for missing jenkins context"() {
    when: 'Creating class without jenkins context'
    def fortify = new Fortify()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  def "fortifyScan base commands are structured correctly"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def jenkins = [
            env             : [FORTIFY_VERSION: '1'],
            echo            : {},
            error           : { msg -> throw new JenkinsErrorException(msg) },
            command         : { String cmd, boolean result=false -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
            archiveArtifacts: {},
            fileExists      : { Map map -> return true },
            readFile        : { Map map -> return getClass().getResource('Test-Build-Name.xml').getText() }
    ]
    def fortifyLocalEvent = GroovySpy(FortifyLocalEvent, global: true, useObjenesis: true)
    fortifyLocalEvent.send() >> "nop"
    when: "I run default fortify scan"
    def fortify = new Fortify(jenkins)
    def config = [
            fortifyBuildName          : "Test-Build-Name",
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
            fortifyMaxSizeMemory      : "-Xmx1G",
            fortifyJdkVersion         : "1.7",
            fortifyClassPath          : "",
    ]
    def fortifyRoot = 'tools/fortify/' + jenkins.env.FORTIFY_VERSION
    def fortifyVersionCommand = fortifyRoot + '/bin/sourceanalyzer -version'
    def fortifyCleanCmd = fortifyRoot + '/bin/sourceanalyzer -verbose -64 -b ' + config.fortifyBuildName + ' -clean -logfile fortify_results/fortify_clean.log'
    def fortifyTranslateCmd = fortifyRoot + '/bin/sourceanalyzer -verbose -64 ' + config.fortifyMaxSizeMemory + ' -source ' + config.fortifyJdkVersion + ' -b ' + config.fortifyBuildName + ' -cp ' + "'" + config.fortifyClassPath + "'" + ' ' + config.fortifyTranslateExclusions + ' ' + "'**/*'" + ' -logfile fortify_results/fortify_translation.log'
    def fortifyMakeMobile = fortifyRoot + '/bin/sourceanalyzer -b ' + config.fortifyBuildName + ' -make-mobile'
    def fortifyShowFiles = fortifyRoot + '/bin/sourceanalyzer -verbose -b ' + config.fortifyBuildName + ' -show-files'
    def fortifyShowBuildWarnings = fortifyRoot + '/bin/sourceanalyzer -verbose -b ' + config.fortifyBuildName + ' -show-build-warnings'
    fortify.fortifyScan(config)
    then: "The base set of fortify commands are structured correctly"
    allCommandsCalled.toString().contains(fortifyVersionCommand)
    allCommandsCalled.toString().contains(fortifyCleanCmd)
    allCommandsCalled.toString().contains(fortifyTranslateCmd)
    allCommandsCalled.toString().contains(fortifyMakeMobile)
    allCommandsCalled.toString().contains(fortifyShowFiles)
    allCommandsCalled.toString().contains(fortifyShowBuildWarnings)
  }

  def "fortifyScan with no upload command is structured correctly"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def jenkins = [
            env             : [FORTIFY_VERSION: '1'],
            echo            : {},
            error           : { msg -> throw new JenkinsErrorException(msg) },
            command         : { String cmd, boolean result = false -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
            archiveArtifacts: {},
            fileExists      : { Map map -> return true },
            readFile        : { Map map -> return getClass().getResource('Test-Build-Name.xml').getText() }
    ]
    def fortifyLocalEvent = GroovySpy(FortifyLocalEvent, global: true, useObjenesis: true)
    fortifyLocalEvent.send() >> 'nop'
    when: "I run fortify local scan with no upload"
    def fortify = new Fortify(jenkins)
    def config = [
            fortifyBuildName    : "Test-Build-Name",
            fortifyMaxSizeMemory: "-Xmx1G",
            uploadToScar        : false,
    ]
    def fortifyRoot = 'tools/fortify/' + jenkins.env.FORTIFY_VERSION
    def fortifyLocalScanCmd = fortifyRoot + '/bin/sourceanalyzer -verbose -64 ' + config.fortifyMaxSizeMemory + ' -b ' + config.fortifyBuildName + ' -scan -f fortify_results/' + config.fortifyBuildName + '.fpr -logfile fortify_results/fortify_scan.log'
    fortify.fortifyScan(config)
    then: "The command is structured correctly"
    allCommandsCalled.toString().contains(fortifyLocalScanCmd)
  }

  def "fortifyScan  with upload command is structured correctly"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def jenkins = [
            env             : [FORTIFY_VERSION: '1'],
            echo            : {},
            error           : { msg -> throw new JenkinsErrorException(msg) },
            command         : { String cmd, boolean result = false -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
            archiveArtifacts: {},
            fileExists      : { Map map -> return true },
            readFile        : { Map map -> return getClass().getResource('Test-Build-Name.xml').getText() },
            usernamePassword: { Map },
            withCredentials : { List list, Closure closure -> }
    ]
    def fortifyLocalEvent = GroovySpy(FortifyLocalEvent, global: true, useObjenesis: true)
    fortifyLocalEvent.send() >> 'nop'
    when: "I run fortify local scan with upload"
    def fortify = new Fortify(jenkins)
    def config = [
            fortifyBuildName    : "Test-Build-Name",
            fortifyMaxSizeMemory: "-Xmx1G",
            fortifyScarUrl      : "https://scar.uhc.com/ssc",
            uploadToScar        : true,
            scarUploadToken     : "1111111111111111111",
            scarProjectVersion  : "11111"
    ]
    def fortifyRoot = 'tools/fortify/' + jenkins.env.FORTIFY_VERSION
    def fortifyUploadFpr = fortifyRoot + '/bin/fortifyclient uploadFPR -f fortify_results/' + config.fortifyBuildName + '.fpr -applicationVersionID ' + config.scarProjectVersion + ' -url ' + config.fortifyScarUrl + ' -authtoken ' + config.scarUploadToken
    fortify.fortifyScan(config)
    then: "The command is structured correctly"
    allCommandsCalled.toString().contains(fortifyUploadFpr)
  }

  def "fortifyScan with upload with credential ID command is structured correctly"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def jenkins = [
      env             : [FORTIFY_VERSION: '1'],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd, result = false ->
        calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n'
        return "Authorization Token: 1111111111"
      },
      archiveArtifacts: {},
      fileExists      : { Map map -> return true },
      readFile        : { Map map -> return getClass().getResource('Test-Build-Name.xml').getText() },
      usernamePassword: { Map },
      withCredentials : { List creds, Closure closure -> closure.call()}
    ]
    def fortifyLocalEvent = GroovySpy(FortifyLocalEvent, global: true, useObjenesis: true)
    fortifyLocalEvent.send() >> 'nop'
    when: "I run fortify local scan with upload"
    def fortify = new Fortify(jenkins)
    def config = [
      fortifyBuildName    : "Test-Build-Name",
      fortifyMaxSizeMemory: "-Xmx1G",
      fortifyScarUrl      : "https://scar.uhc.com/ssc",
      uploadToScar        : true,
      scarCredentialsId   : "creds",
      scarProjectVersion  : "11111"
    ]
    def fortifyRoot = "tools/fortify/$jenkins.env.FORTIFY_VERSION"
    def fortifyTokenCmd = "$fortifyRoot/bin/fortifyclient -url $config.fortifyScarUrl -user \$SCAR_USER -password \$SCAR_PASS token -gettoken AnalysisUploadToken"
    def fortifyUploadCmd = "$fortifyRoot/bin/fortifyclient uploadFPR -f fortify_results/${config.fortifyBuildName}.fpr -applicationVersionID $config.scarProjectVersion -url $config.fortifyScarUrl -authtoken 1111111111"
    fortify.fortifyScan(config)
    then: "The command is structured correctly"
    allCommandsCalled.toString().contains(fortifyTokenCmd)
    allCommandsCalled.toString().contains(fortifyUploadCmd)
  }

  def "fortifyScan with download command is structured correctly"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def jenkins = [
      env             : [FORTIFY_VERSION: '1'],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd, result = false ->
        calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n'
        return "Authorization Token: 1111111111"
      },
      archiveArtifacts: {},
      fileExists      : { Map map -> return true },
      readFile        : { Map map -> return getClass().getResource('Test-Build-Name.xml').getText() },
      usernamePassword: { Map },
      withCredentials : { List creds, Closure closure -> closure.call()}
    ]
    def fortifyLocalEvent = GroovySpy(FortifyLocalEvent, global: true, useObjenesis: true)
    fortifyLocalEvent.send() >> 'nop'
    when: "I run fortify local scan with download"
    def fortify = new Fortify(jenkins)
    def config = [
      fortifyBuildName    : "Test-Build-Name",
      fortifyMaxSizeMemory: "-Xmx1G",
      fortifyScarUrl      : "https://scar.uhc.com/ssc",
      downloadFromScar    : true,
      scarCredentialsId   : "creds",
      scarProjectVersion  : "11111"
    ]
    def fortifyRoot = "tools/fortify/$jenkins.env.FORTIFY_VERSION"
    def fortifyTokenCmd = "$fortifyRoot/bin/fortifyclient -url $config.fortifyScarUrl -user \$SCAR_USER -password \$SCAR_PASS token -gettoken AnalysisDownloadToken"
    def fortifyDownloadCmd = "$fortifyRoot/bin/fortifyclient -url ${config.fortifyScarUrl} -authtoken 1111111111 downloadFPR -file fortify_results/${config.fortifyBuildName}.fpr -projectVersionID ${config.scarProjectVersion}"
    fortify.fortifyScan(config)
    then: "The command is structured correctly"
    allCommandsCalled.toString().contains(fortifyTokenCmd)
    allCommandsCalled.toString().contains(fortifyDownloadCmd)
  }

  def "fortifyScan result generation commands are structured correctly"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def jenkins = [
            env             : [FORTIFY_VERSION: '1'],
            echo            : {},
            error           : { msg -> throw new JenkinsErrorException(msg) },
            command         : { String cmd, boolean result = false -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
            archiveArtifacts: {},
            fileExists      : { Map map -> return true },
            readFile        : { Map map -> return getClass().getResource('Test-Build-Name.xml').getText() }
    ]
    def fortifyLocalEvent = GroovySpy(FortifyLocalEvent, global: true, useObjenesis: true)
    fortifyLocalEvent.send() >> 'nop'
    when: "I run local scan"
    def fortify = new Fortify(jenkins)
    def config = [
            fortifyBuildName    : "Test-Build-Name",
            fortifyMaxSizeMemory: "-Xmx1G",
            uploadToScar        : false,
    ]
    def fortifyRoot = "tools/fortify/$jenkins.env.FORTIFY_VERSION"
    String fortifyResultGeneratorPdf = "$fortifyRoot/bin/ReportGenerator $config.fortifyMaxSizeMemory -source fortify_results/${config.fortifyBuildName}.fpr -template ScanReport.xml -f fortify_results/${config.fortifyBuildName}.pdf -format pdf -verbose"
    String fortifyExtractCritical = "$fortifyRoot/bin/FPRUtility -information -project 'fortify_results/${config.fortifyBuildName}.fpr' -search -query '[analysis type]:SCA AND analysis:!Not an Issue AND [fortify priority order]:critical"
    String fortifyExtractHigh = "$fortifyRoot/bin/FPRUtility -information -project 'fortify_results/${config.fortifyBuildName}.fpr' -search -query '[analysis type]:SCA AND analysis:!Not an Issue AND [fortify priority order]:critical"
    fortify.fortifyScan(config)
    then: "The commands are structured correctly"
    allCommandsCalled.toString().contains(fortifyResultGeneratorPdf)
    allCommandsCalled.toString().contains(fortifyExtractCritical)
    allCommandsCalled.toString().contains(fortifyExtractHigh)
  }

  def "sendFortifyLocalEventWithScar no error for correct parameters"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            duration                  : '30',
            status                    : EventStatus.SUCCESS,
            fortifyBuildName          : 'somename1',
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
            scarProjectVersion        : "All"
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: 'sendFortifyLocalEventWithScar is called with correct parameters'
    def fortify = new Fortify(jenkins)
    fortify.sendFortifyLocalEventWithScar(jenkins, new Date(), config.status, config.fortifyBuildName, config.fortifyTranslateExclusions, config.scarProjectVersion, )
    then: 'No exception is thrown'
    noExceptionThrown()
  }

  def "sendFortifyLocalEvent no error for correct parameters"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            duration                  : '30',
            status                    : EventStatus.SUCCESS,
            fortifyBuildName          : 'somename1',
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
            criticalIssues : "10",
            highIssues : "15",
            mediumIssues : "20",
            lowIssues : "50"
    ]
    GroovyStub(BuildInfoReader, global: true)
    when: 'sendFortifyLocalEvent is called with correct parameters'
    def fortify = new Fortify(jenkins)
    fortify.sendFortifyLocalEvent(jenkins, new Date(), config.status, config.fortifyBuildName, config.fortifyTranslateExclusions)
    then: 'No exception is thrown'
    noExceptionThrown()
  }


  def "throw fortify exception when buildname is null"() {
    given:
    def jenkins = [
            env : [FORTIFY_VERSION: '1'],
            echo: {}
    ]
    Map config = [
            duration                  : '30',
            status                    : EventStatus.SUCCESS,
            fortifyBuildName          : '',
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'"
    ]
    when: 'fortifyScan is called with incorrect parameters'
    def fortify = new Fortify(jenkins)
    fortify.fortifyScan(config)
    then: 'Exception is thrown'
    def e = thrown(Exception)
    e.message.contains("Fortify Build Name is required in the input configuration, Please pass the build name in config.fortifyBuildName parameter.")
  }

  def "throw fortify parameter exception when buildname is not passed"() {
    given:
    def jenkins = [
            env : [FORTIFY_VERSION: '1'],
            echo: {}
    ]
    Map config = [
            duration                  : '30',
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'"
    ]
    when: 'fortifyScan is called with correct parameters'
    def fortify = new Fortify(jenkins)
    fortify.fortifyScan(config)
    then: 'Exception is thrown'
    def e = thrown(Exception)
    e.message.contains("Fortify Build Name is required in the input configuration, Please pass the build name in config.fortifyBuildName parameter.")
  }

  def "throw fortify parameter exception when auth token is not passed to upload the results"() {
    given:
    def jenkins = [
            env : [FORTIFY_VERSION: '1'],
            echo: {}
    ]
    Map config = [
            duration                  : '30',
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
            fortifyBuildName          : 'BuildName',
            uploadToScar              : true
    ]
    when: 'fortifyScan is called with incorrect parameters'
    def fortify = new Fortify(jenkins)
    fortify.fortifyScan(config)
    then: 'Exception is thrown'
    def e = thrown(FortifyInvalidParameterException)
    e.message.contains("Scar upload token(authentication token) or credentials ID is required to upload the scan results to scar, please pass the auth token in config.scarUploadToken parameter or the credential ID in config.scarCredentialsId parameter.")
  }

  def "throw fortify parameter exception when credentials ID is not passed to download previous results"() {
    given:
    def jenkins = [
      env : [FORTIFY_VERSION: '1'],
      echo: {}
    ]
    Map config = [
      duration                  : '30',
      fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
      fortifyBuildName          : 'BuildName',
      downloadFromScar          : true
    ]
    when: 'fortifyScan is called with incorrect parameters'
    def fortify = new Fortify(jenkins)
    fortify.fortifyScan(config)
    then: 'Exception is thrown'
    def e = thrown(FortifyInvalidParameterException)
    e.message.contains("Scar credentials ID is required to download the scan results from scar, please pass the " +
      "credential ID in config.scarCredentialsId parameter.")
  }

  def "throw fortify parameter exception when scar project version is not passed to upload the results"() {
    given:
    def jenkins = [
            env : [FORTIFY_VERSION: '1'],
            echo: {}
    ]
    Map config = [
            duration                  : '30',
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
            fortifyBuildName          : 'BuildName',
            uploadToScar              : true,
            scarUploadToken           : 'XXXXAAAABBBB'
    ]
    when: 'fortifyScan is called with incorrect parameters'
    def fortify = new Fortify(jenkins)
    fortify.fortifyScan(config)
    then: 'Exception is thrown'
    def e = thrown(FortifyInvalidParameterException)
    e.message.contains("Scar Project Version is required to upload the scan results to scar, please pass the project version in config.scarProjectVersion parameter.")
  }

  def "throw fortify threshold exception when the issues are greater than the threshold"() {
    given:
    def jenkins = [
            env : [FORTIFY_VERSION: '1'],
            echo: {}
    ]
    def issuesMap = [
            CriticalIssues: "3"
    ]
    when: 'checkIssues is called'
    def fortify = new Fortify(jenkins)
    fortify.checkIssues(jenkins, issuesMap, 2, "CriticalIssues")
    then: 'Exception is thrown'
    def e = thrown(FortifyThresholdFailedException)
    e.message.contains("Fortify scan failed with CriticalIssues : 3 greater than the threshold : 2")
  }

  def "throw exception if fortify cloudscan and cloudscanemail parameters are passed"() {
    given:
    def jenkins = [
            echo: {}
    ]
    Map config = [
            duration                  : '30',
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
            cloudScanOrLocal          : "cloud",
            fortifyBuildName          : 'BuildName',
            uploadToScar              : false,
            scarUploadToken           : 'XXXXAAAABBBB'
    ]
    when: 'fortifyScan is called with cloud parameters'
    def fortify = new Fortify(jenkins)
    fortify.validateBasicParameters(jenkins, config)
    then: 'Exception is thrown'
    def e = thrown(FortifyInvalidParameterException)
    e.message.contains("ERROR: Cloud scan is no more supported in pipeline library, 'cloudScanOrLocal' and 'fortifyCloudScanEmailTo' are not supported so please remove those parameters from your config.")
  }

  def "fortifyScan javascript commands are structured correctly"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def jenkins = [
            env             : [FORTIFY_VERSION: '1'],
            echo            : {},
            error           : { msg -> throw new JenkinsErrorException(msg) },
            command         : { String cmd, boolean result = false -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
            archiveArtifacts: {},
            fileExists      : { Map map -> return true },
            readFile        : { Map map -> return getClass().getResource('Test-Build-Name.xml').getText() }
    ]
    def fortifyLocalEvent = GroovySpy(FortifyLocalEvent, global: true, useObjenesis: true)
    fortifyLocalEvent.send() >> "nop"
    when: "I run javascript fortify scan"
    def fortify = new Fortify(jenkins)
    def config = [
            fortifyBuildName          : "Test-Build-Name",
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
            fortifyMaxSizeMemory      : "-Xmx1G",
            fortifyJdkVersion         : "1.7",
            javascript                : [source: 'fortify/uitk.js', additionalTranslateOptions: '-Dcom.fortify.sca.EnableDOMModeling=true']
    ]
    def fortifyRoot = 'tools/fortify/' + jenkins.env.FORTIFY_VERSION
    def fortifyTranslateCmd = fortifyRoot + "/bin/sourceanalyzer -verbose -64 ${config.fortifyMaxSizeMemory} -source ${config.fortifyJdkVersion} -b ${config.fortifyBuildName} '${config.javascript.source}' ${config.fortifyTranslateExclusions} -logfile fortify_results/fortify_translation.log ${config.javascript?.additionalTranslateOptions ? config.javascript?.additionalTranslateOptions : ""}"
    fortify.fortifyScan(config)
    then: "The javascript set of fortify commands are structured correctly"
    allCommandsCalled.toString().contains(fortifyTranslateCmd)
  }

  def "fortifyScan isGenerateDevWorkbook command is structured correctly"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def jenkins = [
            env             : [FORTIFY_VERSION: '1'],
            echo            : {},
            error           : { msg -> throw new JenkinsErrorException(msg) },
            command         : { String cmd, boolean result = false -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
            archiveArtifacts: {},
            fileExists      : { Map map -> return true },
            readFile        : { Map map -> return getClass().getResource('Test-Build-Name.xml').getText() }
    ]
    def fortifyLocalEvent = GroovySpy(FortifyLocalEvent, global: true, useObjenesis: true)
    fortifyLocalEvent.send() >> "nop"
    when: "I run this option"
    def fortify = new Fortify(jenkins)
    def config = [
            fortifyBuildName          : "Test-Build-Name",
            fortifyTranslateExclusions: "-exclude '**/src/test/**/*'",
            fortifyMaxSizeMemory      : "-Xmx1G",
            fortifyJdkVersion         : "1.7",
            isGenerateDevWorkbook     : true
    ]
    def fortifyRoot = 'tools/fortify/' + jenkins.env.FORTIFY_VERSION
    def fortifyResultGeneratorDevWorkbookPdf = fortifyRoot + "/bin/BIRTReportGenerator ${config.fortifyMaxSizeMemory} -source fortify_results/${config.fortifyBuildName}.fpr -template 'Developer Workbook' -output fortify_results/${config.fortifyBuildName}-developer-workbook.pdf -format PDF -verbose -searchQuery '[analysis type]:SCA AND analysis:!Not an Issue'"
    fortify.fortifyScan(config)
    then: "The developer workbook report command gets triggered"
    allCommandsCalled.toString().contains(fortifyResultGeneratorDevWorkbookPdf)
  }

  def "fortifyScanWithMaven commands are structured correctly"() {
    given:
    def calledJenkinsCommand
    def allCommandsCalled = ''
    def jenkins = [
      env             : [FORTIFY_VERSION: '1'],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd, boolean result = false -> calledJenkinsCommand = cmd; allCommandsCalled = allCommandsCalled + cmd + '\n' },
      archiveArtifacts: {},
      fileExists      : { Map map -> return true },
      readFile        : { Map map -> return getClass().getResource('Test-Build-Name.xml').getText() }
    ]
    def fortifyLocalEvent = GroovySpy(FortifyLocalEvent, global: true, useObjenesis: true)
    fortifyLocalEvent.send() >> "nop"
    when: "I run maven fortify scan"
    def fortify = new Fortify(jenkins)
    def config = [
      fortifyBuildName          : "Test-Build-Name"
    ]
    def mavenCommand = 'mvn package dependency:copy-dependencies -Dmaven.javadoc.skip=true -Dmaven.test.skip=true -DoutputDirectory=target/lib'
    fortify.fortifyScanMaven(config)
    then: "The maven command is structured correctly"
    allCommandsCalled.toString().contains(mavenCommand)
  }

  def 'createSearchQuery default'() {
    given:
    def newIssues = false
    def additionalFilters = ''
    when: 'I create the query'
    def resultQuery = Fortify.createSearchQuery(newIssues, additionalFilters)
    then: 'The query is correct'
    resultQuery == '[analysis type]:SCA AND analysis:!Not an Issue'
  }

  def 'createSearchQuery new issues'() {
    given:
    def newIssues = true
    def additionalFilters = ''
    when: 'I create the query'
    def resultQuery = Fortify.createSearchQuery(newIssues, additionalFilters)
    then: 'The query is correct'
    resultQuery == '[analysis type]:SCA AND analysis:!Not an Issue AND [issue age]:new'
  }

  def 'createSearchQuery additional filter'() {
    given:
    def newIssues = false
    def additionalFilters = '[some field]:value'
    when: 'I create the query'
    def resultQuery = Fortify.createSearchQuery(newIssues, additionalFilters)
    then: 'The query is correct'
    resultQuery == "[analysis type]:SCA AND analysis:!Not an Issue AND [some field]:value"
  }

  def 'createSearchQuery with severity'() {
    given:
    def newIssues = false
    def additionalFilters = null
    def severity = "low"
    when: 'I create the query'
    def resultQuery = Fortify.createSearchQuery(newIssues, additionalFilters, severity)
    then: 'The query is correct'
    resultQuery == "[analysis type]:SCA AND analysis:!Not an Issue AND [fortify priority order]:$severity"
  }

  def 'getIssuesCount returns counts'() {
    given:
    Jenkins jenkins = GroovyMock()
    jenkins.command({it.matches ".*critical.*"}, _) >> "No issues matched search query."
    jenkins.command({it.matches ".*high.*"}, _) >> "1 issues of 952 matched search query."
    jenkins.command({it.matches ".*medium.*"}, _) >> "2 issues of 952 matched search query."
    jenkins.command({it.matches ".*low.*"}, _) >> "34 issues of 952 matched search query."
    Fortify fortify = new Fortify(jenkins)
    when:
    def result = fortify.getIssueCount("", "", false)
    then:
    "0" == result.CriticalIssues
    "1" == result.HighIssues
    "2" == result.MediumIssues
    "34" == result.LowIssues
  }

}
