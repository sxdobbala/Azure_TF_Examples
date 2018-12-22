package com.optum.jenkins.pipeline.library.utils

import jenkins.model.Jenkins

class PluginValidator {

  /** This method checks for the existence of a jenkins plugin
    @param jenkins object
    @param plugin, jenkins plugin
   */
  static boolean pluginExists(Object jenkins, String plugin) {
    def plugins = Jenkins.instance.getPluginManager().getPlugins()
    Cache cache = Cache.getInstance()
    String cacheKey = "JENKINS_PLUGINS"
    Map jenkinsPlugins = cache.getValue(cacheKey)
    if (!jenkinsPlugins) {
      jenkinsPlugins = new HashMap()
      plugins.each {
        jenkinsPlugins.put(it.getShortName().toLowerCase(), it.getVersion().toLowerCase())
        jenkinsPlugins.put(it.getDisplayName().toLowerCase(), it.getVersion().toLowerCase())
      }
      cache.setValue(cacheKey, jenkinsPlugins)
    }
    return jenkinsPlugins.containsKey(plugin.toLowerCase())
  }

  /** This method returns the version of a jenkins plugin if exists
    @param jenkins object
    @param plugin, jenkins plugin
   */
  static String getPluginVersion(Object jenkins, String plugin) {
    Cache cache = Cache.getInstance()
    String cacheKey = "JENKINS_PLUGINS"
    Map jenkinsPlugins = cache.getValue(cacheKey)
    if (jenkinsPlugins) {
      if (jenkinsPlugins.containsKey(plugin.toLowerCase())) {
        return jenkinsPlugins.get(plugin.toLowerCase())
      } else {
        jenkins.error("Jenkins Plugin " + plugin + "not available. Check if the plugin name is passed correctly.")
      }
    } else {
      jenkinsPlugins = new HashMap()
      def plugins = Jenkins.instance.getPluginManager().getPlugins()
      plugins.each {
        jenkinsPlugins.put(it.getShortName().toLowerCase(), it.getVersion().toLowerCase())
        jenkinsPlugins.put(it.getDisplayName().toLowerCase(), it.getVersion().toLowerCase())
      }
      cache.setValue(cacheKey, jenkinsPlugins)
    }
    jenkinsPlugins.get(plugin.toLowerCase())
  }
}
