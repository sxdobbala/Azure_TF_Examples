package com.optum.jenkins.pipeline.library.sonar

import com.optum.jenkins.pipeline.library.utils.PropertyReader.OptumFileReader
import spock.lang.Specification
import com.optum.jenkins.pipeline.library.scm.Git

class SonarMetricsSpec extends Specification {

  def "TC001-Get sonar metrics for correct parameters"() {
    given: 'Jenkins mocked to get sonar metrics correctly'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      error: { msg -> print "\nUnitTest:mocked: error" },
      sh   : { Map map -> return '{"component":{"id":"AVai3Zy-OJ_mgqaQbTWv","key":"ClaimMiner.TransferWorkflowService:trunk","name":"ClaimMiner-TransferWorkflowService trunk","qualifier":"TRK","measures":[{"metric":"major_violations","value":"91","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"new_critical_violations","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"critical_violations","value":"18","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"test_errors","value":"0","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"new_blocker_violations","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"test_failures","value":"0","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"new_major_violations","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"blocker_violations","value":"0","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"skipped_tests","value":"0","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"tests","value":"16","periods":[{"index":1,"value":"168"}]},{"metric":"ncloc","value":"8521","periods":[{"index":1,"value":"8331"}]}]}}' }
    ]
    def expectedFromSonarMetrics = "[blocker_violations:0, critical_violations:18, major_violations:91, new_blocker_violations:0, new_critical_violations:0, new_major_violations:0, coverage:-1, new_coverage:-1, skipped_tests:0, test_errors:0, test_failures:0, tests:16, ncloc:8521]"

    when: "I try to get sonar metrics using correct parameters"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'ClaimMiner.TransferWorkflowService:trunk'
    def sonar = new Sonar(jenkins)
    def sonarMetricConfig = [
      sonarMetricsApiBaseURL: "http://sonar-stg.optum.com/api/measures/component",
      additionalMetrics     : ""
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "testBranch"
    def sonarMetricsMap = sonar.getSonarMetrics(sonarMetricConfig)

    then: "correct sonar metrics map is returned"
    assert sonarMetricsMap.toString() == expectedFromSonarMetrics
  }

  def "TC002-Get sonar metrics return error for missing projectKey"() {
    given: 'Jenkins mocked for needed functions'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      sh : {},
      error: { msg -> throw new IllegalArgumentException(msg) },
    ]

    when: "I try to get sonar metrics without projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getOptumFile(_) >> [
      metadata:[projectKey: '']
    ]


    def sonar = new Sonar(jenkins)
    def sonarMetricConfig = [
      sonarMetricsApiBaseURL: "http://sonar.optum.com/api/measures/component"
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "testBranch"
    def sonarMetricsMap = sonar.getSonarMetrics(sonarMetricConfig)

    then: "Exception is thrown - missing project key error"
    def ex = thrown(RuntimeException)
    //assert ex.message.contains("Error...Project key is required")
    assert ex.message.contains("Error...JSON parsing/ no metric data")
  }

  def "TC003-Get sonar metrics return error in json for wrong project key (at sonar.optum)"() {
    given: 'Jenkins mocked for json error from sonar API'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      error: { msg -> throw new RuntimeException(msg) },
      sh   : { Map map -> return '{"errors":[{"msg":"Component key \'ClaimMiner_TransferWorkflowService:trunk\' not found"}]}' },
    ]

    when: "I try to get sonar metrics using wrong projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'fakeProjectKey'
    def sonar = new Sonar(jenkins)
    def sonarMetricConfig = [
      sonarMetricsApiBaseURL: "http://sonar.optum.com/api/measures/component"
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "testBranch"
    def sonarMetricsMap = sonar.getSonarMetrics(sonarMetricConfig)

    then: "Exception is thrown - json parsing/ no metric data"
    def ex = thrown(RuntimeException)
    assert ex.message.contains("Error...JSON parsing/ no metric data:")
  }

  def "TC004-Get sonar metrics gets html error from sonar site for wrong project key (at sonar-stg.optum"() {
    given: 'Jenkins mocked to return html error for sonar metrics'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      error: { msg -> throw new RuntimeException(msg) },
      sh   : { Map map -> return 'Not Found\n' +
        '\n' +
        'The requested URL /api/measures/component was not found on this server.\n' +
        '\n' +
        'Apache/2.0.65 (Win32) Server at sonar-stg.optum.com Port 80' },
    ]
    // Assignment not used
    // def expectedFromSonarMetrics = "[blocker_violations:-1, critical_violations:-1, major_violations:-1, new_blocker_violations:-1, new_critical_violations:-1, new_major_violations:-1, coverage:-1, new_coverage:-1, skipped_tests:-1, test_errors:-1, test_failures:-1]"

    when: "I try to get sonar metrics using wrong projectKey (on sonar-stg.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'fakeProjectKey'
    def sonar = new Sonar(jenkins)
    def sonarMetricConfig = [
      sonarMetricsApiBaseURL: "http://sonar-stg.optum.com/api/measures/component"
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "testBranch"
    def sonarMetricsMap = sonar.getSonarMetrics(sonarMetricConfig)

    then: "Exception is thrown - json parsing/ no metric data"
    def ex = thrown(RuntimeException)
    assert ex.message.contains("Error...JSON parsing/ no metric data:")
  }

  def "TC005-Get sonar metrics return correct result with additional metric parameter"() {
    given: 'Jenkins mocked for correct result from sonar API for additional metric'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      error: { msg -> print "\nUnitTest:mocked: error" },
      sh   : { Map map -> return '{"component":{"id":"AVai3Zy-OJ_mgqaQbTWv","key":"ClaimMiner.TransferWorkflowService:trunk","name":"ClaimMiner-TransferWorkflowService trunk","qualifier":"TRK","measures":[{"metric":"major_violations","value":"91","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"new_critical_violations","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"critical_violations","value":"18","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"test_errors","value":"0","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"new_blocker_violations","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"test_failures","value":"0","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"new_major_violations","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"blocker_violations","value":"0","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"skipped_tests","value":"0","periods":[{"index":1,"value":"0"},{"index":2,"value":"0"},{"index":3,"value":"0"}]},{"metric":"tests","value":"168","periods":[{"index":1,"value":"168"}]},{"metric":"ncloc","value":"8521","periods":[{"index":1,"value":"8331"}]}]}}' }
    ]
    def expectedFromSonarMetrics = "[blocker_violations:0, critical_violations:18, major_violations:91, new_blocker_violations:0, new_critical_violations:0, new_major_violations:0, coverage:-1, new_coverage:-1, skipped_tests:0, test_errors:0, test_failures:0, tests:168, ncloc:8521, duplicated_lines:-1]"

    when: "I try to get sonar metrics using correct projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'ClaimMiner.TransferWorkflowService:trunk'
    def sonar = new Sonar(jenkins)
    def sonarMetricConfig = [
      sonarMetricsApiBaseURL: "http://sonar.optum.com/api/measures/component",
      additionalMetrics     : "duplicated_lines"
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "testBranch"
    def sonarMetricsMap = sonar.getSonarMetrics(sonarMetricConfig)

    then: "correct sonar metrics map is returned"
    assert sonarMetricsMap.toString() == expectedFromSonarMetrics
  }

  def "TC006-Get sonar metrics return error for wrong additional metric parameter"() {
    given: 'Jenkins mocked for json error from sonar API'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      error: { msg -> throw new RuntimeException(msg) },
      sh   : { Map map -> return '{"errors":[{"msg":"The following metric keys are not found: abcd"}]}' },
    ]

    when: "I try to get sonar metrics using wrong projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'fakeProjectKey'
    def sonar = new Sonar(jenkins)
    def sonarMetricConfig = [
      sonarMetricsApiBaseURL: "http://sonar.optum.com/api/measures/component",
      additionalMetrics     : "abcd"
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "testBranch"
    def sonarMetricsMap = sonar.getSonarMetrics(sonarMetricConfig)

    then: "Exception is thrown - wrong additional metric parameter error"
    def ex = thrown(RuntimeException)
    assert ex.message.contains("Error...JSON parsing/ no metric data:")
  }

  def "TC007-Get sonar metrics return error for wrong regEx additional metric parameter format"() {
    given: 'Jenkins mocked for json error from sonar API'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      error: { msg -> throw new RuntimeException(msg) }
    ]

    when: "I try to get sonar metrics using wrong projectKey (on sonar.optum)"
    def sonar = new Sonar(jenkins)
    def sonarMetricConfig = [
      projectKey            : "ClaimMiner.TransferWorkflowService:trunk",
      sonarMetricsApiBaseURL: "http://sonar.optum.com/api/measures/component",
      additionalMetrics     : "abcd0,"
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "testBranch"
    def sonarMetricsMap = sonar.getSonarMetrics(sonarMetricConfig)

    then: "Exception is thrown - additional parameters format error"
    def ex = thrown(RuntimeException)
    assert ex.message.contains("Error...additionalMetrics can only contain")
  }

  def "TC008-Get sonar metrics return error for no metric data"() {
    given: 'Jenkins mocked empty metric return data'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      error: { msg -> throw new RuntimeException(msg) },
      sh   : { Map map -> return '{"component":[]}' },
    ]

    when: "I try to get sonar metrics using wrong projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'fakeProjectKey'

    def sonar = new Sonar(jenkins)
    def sonarMetricConfig = [
      sonarMetricsApiBaseURL: "http://sonar.optum.com/api/measures/component"
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "testBranch"
    def sonarMetricsMap = sonar.getSonarMetrics(sonarMetricConfig)

    then: "Exception is thrown - Metrics data not found for"
    def ex = thrown(RuntimeException)
    assert ex.message.contains("Error...Metrics data not found for")
  }


  def "TC009-Convert metrics map in html"() {
    given: 'sonar metrics map'
    def sonarMetricsMap = [blocker_violations:0, critical_violations:18, major_violations:91, new_blocker_violations:0, new_critical_violations:0, new_major_violations:0, coverage:"-1", new_coverage:"-1", skipped_tests:0, test_errors:0, test_failures:0]
    def expectedSonarMetricsHTML = "<html><head><style type='text/css'>.header { margin: 30px;padding: 5px;background-color: #d9d9d9} .row {margin: 30px;padding: 5px;background-color:  #b3f0ff}</style></head><body><h2>sonar quality metrics</h2><table><tr><th class='header'>metric</th><th class='header'>value</th></tr><tr><td>blocker_violations</td><td>0</td></tr><tr><td>critical_violations</td><td>18</td></tr><tr><td>major_violations</td><td>91</td></tr><tr><td>new_blocker_violations</td><td>0</td></tr><tr><td>new_critical_violations</td><td>0</td></tr><tr><td>new_major_violations</td><td>0</td></tr><tr><td>coverage</td><td>-1</td></tr><tr><td>new_coverage</td><td>-1</td></tr><tr><td>skipped_tests</td><td>0</td></tr><tr><td>test_errors</td><td>0</td></tr><tr><td>test_failures</td><td>0</td></tr></table></body></html>"
    def jenkins = []

    when: "I convert map to html"
    def sonar = new Sonar(jenkins)
    def sonarMetricsHTML = sonar.convertSonarMetricsMapToHTML(sonarMetricsMap)

    then: "Error is printed and missing metrics returned"
    assert sonarMetricsHTML  == expectedSonarMetricsHTML
  }

  def "TC010-Throw Invalid parameters exception when projectkey is passed"() {
    given: 'Jenkins mocked empty metric return data'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {}
    ]

    when: "I try to get sonar metrics using wrong projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'fakeProjectKey'
    def params = [
      projectKey: "key1"
    ]
    def sonar = new Sonar(jenkins)
    sonar.scanWithMaven(params)
    then: "SonarInvalidParameterException is thrown"
    def ex = thrown(SonarInvalidParameterException)
    ex.message.contains("ERROR: Do not pass projectKey or projectName in the config parameters.")
  }

  def "TC011-Throw Invalid parameters exception when projectname is passed"() {
    given: 'Jenkins mocked empty metric return data'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {}
    ]

    when: "I try to get sonar metrics using wrong projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'fakeProjectKey'
    def params = [
      projectName: "name1"
    ]
    def sonar = new Sonar(jenkins)
    sonar.scanWithMaven(params)
    then: "SonarInvalidParameterException is thrown"
    def ex = thrown(SonarInvalidParameterException)
    ex.message.contains("ERROR: Do not pass projectKey or projectName in the config parameters.")
  }

  def "TC012-Get sonar quality gate return correct result"() {
    given: 'Jenkins mocked for correct result from sonar API'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      error: { msg -> print "\nUnitTest:mocked: error" },
      sh   : { Map map -> return '{"qualityGate":{"id":"37","name":"GATE_07","default":false}}' },
    ]
    def expectedFromSonarQG = "[qualityGate:7]"

    when: "I try to get sonar metrics using correct projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'com.optum.devops:simpleMavenApp'
    def sonar = new Sonar(jenkins)
    def sonarQGConfig = [
      sonarMetricsApiBaseURL: "http://sonar.optum.com/api/measures/component",
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "master"
    def sonarQG = sonar.getSonarQualityGate(sonarQGConfig)

    then: "correct sonar quality gate map is returned"
    assert sonarQG.toString() == expectedFromSonarQG
  }

  def "TC013-Get sonar quality gate return with wrong projectKey"() {
    given: 'Jenkins mocked for correct result from sonar API'
    def jenkins = [
      env  : [JOB_NAME : "TESTJOB"],
      echo : {},
      error: { msg -> print "\nUnitTest:mocked: error" },
      sh   : { Map map -> return '{"errors":[{"msg":"Component key \'com.optum.devops:simpleMavenAppWrong\' not found"}]}' },
    ]
    def expectedFromSonarQG = "[qualityGate:-9]"  // error message project not found obtain default value

    when: "I try to get sonar metrics using correct projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'com.optum.devops:simpleMavenAppWrong'
    def sonar = new Sonar(jenkins)
    def sonarQGConfig = [
      sonarMetricsApiBaseURL: "http://sonar.optum.com/api/measures/component",
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "master"
    def sonarQG = sonar.getSonarQualityGate(sonarQGConfig)

    then: "correct sonar quality gate map is returned"
    assert sonarQG.toString() == expectedFromSonarQG
  }

  def "TC014-Get sonar quality gate return for preview mode"() {
    given: 'Jenkins mocked for correct result from sonar API'
    def jenkins = [
      env  : [JOB_NAME : "PR-Test"],
      echo : {},
      error: { msg -> print "\nUnitTest:mocked: error" },
      sh   : {},
    ]
    def expectedFromSonarQG = "[qualityGate:-4]"  // preview mode

    when: "I try to get sonar metrics using correct projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'com.optum.devops:simpleMavenApp'
    def sonar = new Sonar(jenkins)
    def sonarQGConfig = [
      sonarQualityGateApiBaseURL: '/api/qualitygates/get_by_project',
      isPreview                 : true
    ]
    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "PR-Test"
    def sonarQG = sonar.getSonarQualityGate(sonarQGConfig)

    then: "correct sonar quality gate map is returned"
    assert sonarQG.toString() == expectedFromSonarQG
  }

  def "TC015-Get sonar metrics return for preview mode"() {
    given: 'Jenkins mocked for correct result from sonar API'
    def jenkins = [
      env  : [JOB_NAME : "PR-346"],
      echo : {},
      error: { msg -> print "\nUnitTest:mocked: error" },
      sh   : {},
    ]

    when: "I try to get sonar metrics using correct projectKey (on sonar.optum)"
    GroovyMock(OptumFileReader, global: true)
    OptumFileReader.getProjectKey(_) >> 'com.optum.devops:simpleMavenApp'
    def sonar = new Sonar(jenkins)
    def sonarMetricConfig = [
      projectKey            : "ClaimMiner.TransferWorkflowService",
      sonarMetricsApiBaseURL: "http://sonar.optum.com/api/measures/component",
      additionalMetrics     : "abcd0,",
      isPreview             : true
    ]

    Git gitMock = Mock()
    GroovyMock(Git, global: true)
    new Git(jenkins) >> gitMock
    gitMock.getBranch() >> "PR-346"
    def sonarMetrics = sonar.getSonarMetrics(sonarMetricConfig)

    then: "correct no Metrics value returned"
    assert sonarMetrics.isEmpty()
  }
}
