package com.optum.jenkins.pipeline.library.utils

@Singleton
class Cache implements Serializable {
  Map values = new HashMap()

  def getValue(key){
    return values.get(key)
  }

  def setValue(key, value) {
    values.put(key, value)
  }

  def clear(){
    values = [:]
  }
}
