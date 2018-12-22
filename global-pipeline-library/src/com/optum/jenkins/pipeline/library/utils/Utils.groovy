#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.utils

import com.optum.jenkins.pipeline.library.event.ApprovalEvent
import com.optum.jenkins.pipeline.library.event.EventStatus
import groovy.time.TimeCategory
import groovy.time.TimeDuration

class Utils implements Serializable {
  def jenkins

  Utils() throws Exception {
    throw new Exception('`this` must be passed when creating new class instance e.g. new Classname(this) instead of new Classname(). ' +
            'This enables access to jenkinsfile steps, global variables, functions and envvars.')
  }

  Utils(jenkins) {
    this.jenkins = jenkins
  }

  /**
   * Retries the passed in closure a set amount of times
   * @param times int optional - default 5 -  The string representing the id of the user/pass credential (this can be either a hash or a string you provide when creating a credential)
   * @param delay int optional - default 0 -  The number of seconds to delay between retries
   * @param exponential boolean optional - default false - An exponential backoff option.  Set to true will result in exponential growth in delay
   * @param errorHandler closure optional - default { e -> jenkins.echo e.message } -  Call the passed in block on error
   * */
  def retry(Map<String, Object> params, Closure body) {
    def defaults = [
            times           : 5,  // optional
            delay           : 0,  // optional
            exponential     : false,  // optional
            errorHandler    : { e -> jenkins.echo e.message }, //optional
    ]
    def config = defaults + params

    jenkins.echo "Retry arguments: $config"

    int retries = 0
    int sleepTime = config.delay
    def exceptions = []
    while (retries++ < config.times) {
      try {
        return body.call()
      } catch (e) {
        exceptions << e
        config.errorHandler.call(e)
        if (config.exponential) {
            sleepTime = config.delay ** retries
        }
        jenkins.sleep sleepTime
      }
    }
    throw new Exception("Failed after $config.times retries")
  }

  /**
   * A wrapper around the stage step to provide node and toggle features.  Takes named
   * parameters and then
   * @param name String required - The name of this stage
   * @param nodeLabel String optional - If specified, nodeLabel will be used as the label of a node block to wrap the stage in.
   * @param runStage boolean optional - If set to false, skip the stage
   *  Otherwise, none will be added
   * */
  def gStage(Map<String, Object> params, Closure body) {
    def defaults = [
            runStage   : true,  // optional
    ]
    def config = defaults + params

    jenkins.echo "Stage arguments: $config"

    if(!config.runStage) {
      jenkins.echo "Skipping stage"
      return
    }

    requireParams((String[])['name'], config)

    //Only spin up on a new node if one is passed in
    jenkins.stage(config.name) {
      if (config.nodeLabel) {
        return jenkins.node(config.nodeLabel) {
          body.call()
        }
      }
      else {
        return body.call()
      }
    }
  }

  // Returns the rounded number of seconds between passed in Date and current Date
  def long getDuration(Date processStart) {
    def endTime = new Date()
    TimeDuration duration = TimeCategory.minus (endTime, processStart)
    return Math.round(duration.toMilliseconds() / 1000)
  }

  def requireParams(String[] requiredParams, Map<String, Object> params)
  {
    def missingParams = []
    requiredParams.each { param ->
      if(!params.containsKey(param) || params.get(param) == null) {
        missingParams.add(param)
      }
    }
    if(missingParams.size > 0)
      jenkins.error("Required parameters missing: " + missingParams.toSorted().join(",") )
  }
}
