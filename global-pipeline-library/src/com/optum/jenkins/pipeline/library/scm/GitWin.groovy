#!/usr/bin/env groovy

package com.optum.jenkins.pipeline.library.scm

def gitWinCheckOut(Map<String,Object> params){
	def config = params
	git branch: config.tag, credentialsId: config.credentialsId, url: config.url

}
