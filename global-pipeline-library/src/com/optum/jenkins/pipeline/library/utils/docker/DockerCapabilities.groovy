package com.optum.jenkins.pipeline.library.utils.docker

class DockerCapabilities {
  /**
   * Supporting methoding - returns both major & minor docker version as a numeric value
   * @param dockerVersion String Required The string for the docker version used
  * */  
  static def getVersionNumbers(String dockerVersion) {
    def majorVersionValue = 0, minorVersionValue = 0

    // if docker version was provided
    if (dockerVersion && !dockerVersion.equals("") && !dockerVersion.equalsIgnoreCase("null")) {
      def (dockerMajorVersion, dockerMinorVersion) = dockerVersion.tokenize(".")

      // If a major version is provided & is an actual number, save it
      if (dockerMajorVersion && dockerMajorVersion.isNumber()) {
        majorVersionValue = Integer.parseInt(dockerMajorVersion)
      }

      // If a minor version is provided & is an actual number, save it
      if (dockerMinorVersion && dockerMinorVersion.isNumber()) {
        minorVersionValue = Integer.parseInt(dockerMinorVersion)
      }
    }

    return [majorVersionValue, minorVersionValue]
  } // getVersionNumber

  /**
   * Returns true if docker version supports the --password-stdin parameters.
   * @param dockerVersion String Required The string for the docker version used
   *
   * NOTE: 
   * (1) --password-stdin supported when docker version is version is v17.09+
   * (2) Docker best practices note that using --password (-p) via the CLI is insecure.
   * Reference: https://docs.docker.com/v17.09/engine/reference/commandline/login/
  * */
  static public boolean supportsPasswordStdin(String dockerVersion) {
    def (majorVersionValue, minorVersionValue) = DockerCapabilities.getVersionNumbers(dockerVersion)

    return majorVersionValue >= 18 || (majorVersionValue >= 17 && minorVersionValue >= 9)
  }    
}