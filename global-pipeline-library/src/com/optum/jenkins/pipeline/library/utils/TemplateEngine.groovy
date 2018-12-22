#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.utils

import groovy.text.StreamingTemplateEngine
import com.cloudbees.groovy.cps.NonCPS

/**
* TemplateEngine class for general template parsing
* <p>
* Current implementation use groovy.text.StreamingTemplateEngine
* potentially can be enhanced with other template engines implementation
* @see <a href="http://docs.groovy-lang.org/docs/next/html/documentation/template-engines.html#_streamingtemplateengine">StreamingTemplateEngine</a>
* @author jesen.surjadi@optum.com
*/
class TemplateEngine implements Serializable {
  def jenkins

  TemplateEngine() throws Exception {
    throw new Exception('"this" must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
            'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  TemplateEngine(jenkins) {
    this.jenkins = jenkins
  }
  /**
   * Parse a template text values using groovy.text.StreamingTemplateEngine
   * @see <a href="http://docs.groovy-lang.org/docs/next/html/documentation/template-engines.html#_streamingtemplateengine">StreamingTemplateEngine</a>
   * @author jesen.surjadi@optum.com
   * @param binding Map optional - default empty map - the bindings to be used to replace templated key.
   * @param templateText String optional - default empty string -  the template text to be parsed
   * @param debugPrint boolean optional - default false - flag to print out parsed template result for debugging
   * @throws Exception
   * @return String - parsed template string
   * USE debugPrint WITH CAUTION AS IT WILL PRINT OUT ALL THE PARAMETERS, INCLUDING YOUR TEMPLATE TEXT
   * all params are optional, and the method will return empty string, if you don't feed in the right template/binding
   * this will be simple string template for now
   * */
  def parseTemplate(Map<String, Object> params) {
    def defaults = [
            binding           : [:],  // optional
            templateText      : '',  // optional
            debugPrint        : false, // optional
    ]
    def config = defaults + params
    if ( config.debugPrint ) {
      jenkins.echo 'parseTemplate arguments: ' + config
    }
    return stringFromTemplate(config.binding, config.templateText)
  }
  //for usage in jenkins, need to have @NonCPS otherwise there will be exception
  //java.io.NotSerializableException: groovy.text.StreamingTemplateEngine$StreamingTemplate
  //method return string object, either parsed text, unparsed text if no binding found
  //or exception will be rethrown
  @NonCPS
  private stringFromTemplate(Map<String, String> binding, String templateText) throws Exception {
    def template = new StreamingTemplateEngine().createTemplate(templateText)
    def result = binding ? template.make(binding).toString() : templateText
    return result
  }
}
