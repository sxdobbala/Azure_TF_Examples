package com.optum.jenkins.pipeline.library.utils

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import com.optum.jenkins.pipeline.library.utils.TemplateEngine
import groovy.lang.MissingPropertyException
import groovy.text.TemplateExecutionException
import spock.lang.Specification

class TemplateEngineSpec extends Specification {

  def "jenkins context is available"(){
    given: "Default jenkins context"
    def jenkins = [echo : 'hello']
    when: 'Creating class with jenkins context'
    def testObject = new TemplateEngine(jenkins)
    then: "Jenkins context is available"
    testObject.getJenkins() == jenkins
  }

  def "error for missing jenkins context"(){
    when: 'Creating class without jenkins context'
    def testObject = new TemplateEngine()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('"this" must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
            'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }
  
  def "parseTemplate test for empty template string, should return empty string"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' }
    ]
    when: "I try to parse text using minimum required parameters"
    def testObject = new TemplateEngine(jenkins)
    
    def templateBinding = [value1: "this is value1", 
                           value2: "this is value2",
                           value3: "this is value3"]
    def text = ''
    
    def config = [binding: templateBinding,
                  templateText : text]
                  
    def parsedTemplate = testObject.parseTemplate config
    then: "return empty string"
    parsedTemplate == ''
  }
  
  def "parseTemplate test for empty binding, should return input template string as is"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' }
    ]
    when: "I try to parse text using templates parameters"
    def testObject = new TemplateEngine(jenkins)
    def templateBinding = [:]
    def text = '''\
                template value1 is <% out.print "value: " + value1 %> \n \
                template value2 is <% out.print "value: " + value2 %> \n \
                template value3 is <% out.print "value: " + value3 %> \n \
                '''
    
    def expectedResult = text
    
    def config = [binding: templateBinding,
                  templateText : text,
                  debugPrint : true]
                  
    def parsedTemplate = testObject.parseTemplate config
    then: "The template string command is returned as is correctly"
    parsedTemplate == expectedResult
  }
  
  def "parseTemplate test for incorrect binding with correct JSP input template string, should throw exception"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' }
    ]
    when: "I try to parse text using templates parameters"
    def testObject = new TemplateEngine(jenkins)
    def templateBinding = [value100: "this is value100", 
                           value200: "this is value200",
                           value300: "this is value300"]
    def text = '''\
                template value1 is <% out.print "value: " + value1 %> \n \
                template value2 is <% out.print "value: " + value2 %> \n \
                template value3 is <% out.print "value: " + value3 %> \n \
                '''
    
    def expectedResult = ''
    
    def config = [binding: templateBinding,
                  templateText : text]
                  
    def parsedTemplate = testObject.parseTemplate config
    then: "Exception is thrown"
    def e = thrown(Exception)
    e instanceof TemplateExecutionException
  }
  
  def "parseTemplate test for incorrect binding with correct GString input template string, should throw exception"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' }
    ]
    when: "I try to parse text using templates parameters"
    def testObject = new TemplateEngine(jenkins)
    def templateBinding = [value100: "this is value100", 
                           value200: "this is value200",
                           value300: "this is value300"]
    def text = '''\
                template value1 is value: \${value1} \n \
                template value2 is value: \${value2} \n \
                template value3 is value: \${value3} \n \
                '''
    
    def expectedResult = ''
    
    def config = [binding: templateBinding,
                  templateText : text]
                  
    def parsedTemplate = testObject.parseTemplate config
    then: "Exception is thrown"
    def e = thrown(Exception)
    e instanceof TemplateExecutionException
  }
  
  def "parseTemplate test for template string with JSP Syntax, should be parsed successfully"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' }
    ]
    when: "I try to parse text using templates parameters"
    def testObject = new TemplateEngine(jenkins)
    def templateBinding = [value1: "this is value1", 
                           value2: "this is value2",
                           value3: "this is value3"]
    def text = '''\
                template value1 is <% out.print "value: " + value1 %> \n \
                template value2 is <% out.print "value: " + value2 %> \n \
                template value3 is <% out.print "value: " + value3 %> \n \
                '''
    
    def expectedResult = '''\
                template value1 is value: this is value1 \n \
                template value2 is value: this is value2 \n \
                template value3 is value: this is value3 \n \
                '''
    
    def config = [binding: templateBinding,
                  templateText : text,
                  debugPrint : true]
                  
    def parsedTemplate = testObject.parseTemplate config
    then: "The template string command is parsed correctly"
    parsedTemplate == expectedResult
  }
  
  def "parseTemplate test for template string with GString Syntax, should be parsed successfully"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' }
    ]
    when: "I try to parse text using templates parameters with GString Syntax"
    def testObject = new TemplateEngine(jenkins)
    def templateBinding = [value1: "this is value1", 
                           value2: "this is value2",
                           value3: "this is value3"]
    def text = '''\
                template value1 is value: \${value1} \n \
                template value2 is value: \${value2} \n \
                template value3 is value: \${value3} \n \
                '''
    
    def expectedResult = '''\
                template value1 is value: this is value1 \n \
                template value2 is value: this is value2 \n \
                template value3 is value: this is value3 \n \
                '''
    
    def config = [binding: templateBinding,
                  templateText : text,
                  debugPrint : true]
                  
    def parsedTemplate = testObject.parseTemplate config
    
    then: "The template string command is parsed correctly"
    parsedTemplate == expectedResult
  }
}
