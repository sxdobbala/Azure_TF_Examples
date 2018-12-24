package com.optum.utils

import groovy.time.TimeCategory
import groovy.time.TimeDuration

def startTime = null
def endTime = null
TimeDuration totalTimeForTests = null;

def setTestStartTime () {
    startTime = new Date()
    println "StartTime: " + startTime
}

def setTestEndTime () {
    endTime = new Date()
    println "EndTime: " + endTime
    calculateTimeTakeForTests()
}

def calculateTimeTakeForTests () {
    println "StartTime: " + startTime
    println "EndTime: " + endTime
    totalTimeForTests = TimeCategory.minus(endTime, startTime)
    println("TIME TAKEN FOR TEST: " + totalTimeForTests.toMilliseconds()/1000 + "sec's")
    //println "TIME TAKEN FOR TEST: " + totalTimeForTests.days + "d:" + totalTimeForTests.hours + "h:" + totalTimeForTests.minutes + "m:" + totalTimeForTests.seconds + "s"
}

