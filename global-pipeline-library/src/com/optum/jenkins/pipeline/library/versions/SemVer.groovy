#!/usr/bin/env groovy
package com.optum.jenkins.pipeline.library.versions

/**
 * Object Model for semantic version
 * @see <a href="https://semver.org/">https://semver.org/</a>
 *
 */
class SemVer implements Serializable {

  private final int major, minor, patch

  enum PatchLevel {
    MAJOR('MAJOR'),
    MINOR('MINOR'),
    PATCH('PATCH')

    private final String patchlevel

    PatchLevel(String patchlevel) {
      this.patchlevel = patchlevel
    }

    String level() {
      return this.patchlevel
    }
  }

  SemVer(String version) {
    def versionParts = version.tokenize('.')
    if (versionParts.size() != 3) {
      throw new IllegalArgumentException("Wrong version format - expected MAJOR.MINOR.PATCH - got ${version}")
    }
    this.major = versionParts[0].toInteger()
    this.minor = versionParts[1].toInteger()
    try{
      this.patch = versionParts[2].toInteger()
    }
    catch (NumberFormatException ex){
      throw new IllegalArgumentException("Extra version classifier is not currently supported - expected MAJOR.MINOR.PATCH - got ${version}")
    }
  }

  SemVer(int major, int minor, int patch) {
    this.major = major
    this.minor = minor
    this.patch = patch
  }

  SemVer bump(String patchLevel) {
    switch (patchLevel?.toUpperCase()) {
      case PatchLevel.MAJOR.level():
        return new SemVer((major + 1), 0, 0)
      case PatchLevel.MINOR.level():
        return new SemVer(major, (minor + 1), 0)
    }
    return new SemVer(major, minor, (patch + 1))
  }

  String toString() {
    return "${major}.${minor}.${patch}"
  }
}

