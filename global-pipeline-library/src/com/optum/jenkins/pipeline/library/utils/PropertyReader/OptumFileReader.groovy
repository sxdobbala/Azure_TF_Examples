package com.optum.jenkins.pipeline.library.utils.PropertyReader

import com.optum.jenkins.pipeline.library.utils.JenkinsPlugins.PipelineUtilitySteps
import com.optum.jenkins.pipeline.library.utils.JenkinsPlugins.WorkflowCpsGlobalLib

class OptumFileReader {

  static enum ComponentType {
    DATABASE, CODE, INFRASTRUCTURE, CONFIG, OTHER
  }

  //hardcoding package location because getClass() returns java.lang.Object when executing this withing jenkins
  static final String OPTUMFILEREADER_PATH = 'com/optum/jenkins/pipeline/library/utils/PropertyReader/'
  static final String OPTUMFILE_NAME = 'Optumfile.yml'

  static getApiVersion(jenkins) {
    if (jenkins.env.apiVersion != null && jenkins.env.apiVersion != "") {
      return jenkins.env.apiVersion
    }
    Object optumfile = getOptumFile(jenkins)
    if (optumfile.apiVersion == null) {
      jenkins.error getErrorMessage('apiVersion:')
    } else {
      jenkins.env.apiVersion = optumfile.apiVersion
      return optumfile.apiVersion
    }
  }

  static getAskId(jenkins) {
    if (jenkins.env.askId != null && jenkins.env.askId != "") {
      return jenkins.env.askId
    }
    Object optumfile = getOptumFile(jenkins)
    if (optumfile.metadata == null || optumfile.metadata.askId == null) {
      jenkins.error getErrorMessage('metadata:\n  askId:')
    } else {
      jenkins.env.askId = optumfile.metadata.askId
      return optumfile.metadata.askId
    }
  }

  static getCAAgileId(jenkins) {
    if (jenkins.env.caAgileId != null && jenkins.env.caAgileId != "") {
      return jenkins.env.caAgileId
    }
    Object optumfile = getOptumFile(jenkins)
    if (optumfile.metadata == null || optumfile.metadata.caAgileId == null) {
      jenkins.error getErrorMessage('metadata:\n  caAgileId:')
    } else {
      jenkins.env.caAgileId = optumfile.metadata.caAgileId
      return optumfile.metadata.caAgileId
    }
  }

  static getProjectFriendlyName(jenkins) {
    if (jenkins.env.projectFriendlyName != null && jenkins.env.projectFriendlyName != "") {
      return jenkins.env.projectFriendlyName
    }
    Object optumfile = getOptumFile(jenkins)
    if (optumfile.metadata == null || optumfile.metadata.projectFriendlyName == null) {
      jenkins.error getErrorMessage('metadata:\n  projectFriendlyName:')
    } else {
      jenkins.env.projectFriendlyName = optumfile.metadata.projectFriendlyName
      return optumfile.metadata.projectFriendlyName
    }
  }

  static getProjectKey(jenkins) {
    if (jenkins.env.projectKey != null && jenkins.env.projectKey != "") {
      return jenkins.env.projectKey
    }
    Object optumfile = getOptumFile(jenkins)
    if (optumfile.metadata == null || optumfile.metadata.projectKey == null) {
      jenkins.error getErrorMessage('metadata:\n  projectKey:')
    } else {
      jenkins.env.projectKey = optumfile.metadata.projectKey
      return optumfile.metadata.projectKey
    }
  }

  static getProjectVersion(jenkins) {
    if (jenkins.env.projectVersion != null && jenkins.env.projectVersion != "") {
      return jenkins.env.projectVersion
    }
    Object optumfile = getOptumFile(jenkins)
    if (optumfile.metadata == null || optumfile.metadata.projectVersion == null) {
      jenkins.error getErrorMessage('metadata:\n  projectVersion:')
    } else {
      jenkins.env.projectVersion = optumfile.metadata.projectVersion
      return optumfile.metadata.projectVersion
    }
  }


  static ComponentType getComponentType(jenkins) {
    if (jenkins.env.componentType != "" && jenkins.env.componentType != null) {
      return jenkins.env.componentType
    }
    Object optumfile = getOptumFile(jenkins)
    if (optumfile.metadata == null || optumfile.metadata.componentType == null) {
      jenkins.error getErrorMessage('metadata:\n  componentType:')
    } else {
      ComponentType type = optumfile.metadata.componentType.toUpperCase()
      jenkins.env.componentType = type
      return type
    }
  }

  //Create object fron Optumfile.yml if available, or alternatively return the jenkins environment variable OPTUMFILE
  static getOptumFile(jenkins) {
    Object optumfile = PipelineUtilitySteps.readYaml(jenkins, [file:OPTUMFILE_NAME])
    if (optumfile != null) {
      jenkins.echo "Info: Reading configuration from optum file"
      return optumfile
    } else {
      jenkins.error getOptumFileHelpMessage(jenkins, 'latest')
    }
  }

  static String getOptumFileHelpMessage(jenkins, apiVersion) {
    jenkins.echo('get optumfile help intro message')
    String introMessage = getOptumFileReaderConfig(jenkins).helpMessage
    jenkins.echo('get optumfile template message')
    String apiTemplate = getOptumFileTemplate(jenkins, apiVersion)
    return "$introMessage\n----------- SNIPPET $OPTUMFILE_NAME -----------\n$apiTemplate\n----------- SNIPPET END -----------"
  }

  private static getErrorMessage(String propertyPath){
    return "Unable to find Optumfile property:\n $propertyPath \nPlease check your Optumfile!"
  }

  static String getOptumFileTemplate(jenkins, apiVersion = 'latest') {
    ConfigObject config = getOptumFileReaderConfig(jenkins)
    def template = config.toProperties().getProperty('apiTemplates.' + apiVersion)
    String apiTemplate = WorkflowCpsGlobalLib.libraryResource(jenkins, OPTUMFILEREADER_PATH + template)
    return apiTemplate
  }

  static ConfigObject getOptumFileReaderConfig(jenkins) {
    String configText = WorkflowCpsGlobalLib.libraryResource(jenkins,
            OPTUMFILEREADER_PATH + 'OptumFileReaderConfig.groovy' )
    return new ConfigSlurper().parse(configText)
  }

  static getTargetQG(jenkins) {
    if (jenkins.env.targetQG != "" && jenkins.env.targetQG != null) {
      return jenkins.env.targetQG
    }
    Object optumfile = getOptumFile(jenkins)
    if (optumfile.metadata == null || optumfile.metadata.targetQG == null) {
      jenkins.error getErrorMessage('metadata:\n  targetQG:')
    } else {
      def resultQualityGate
      switch (optumfile.metadata.targetQG) {
        case 'GATE_00':
          resultQualityGate = "0"
          break
        case 'GATE_01':
          resultQualityGate = "1"
          break
        case 'GATE_02':
          resultQualityGate = "2"
          break
        case 'GATE_03':
          resultQualityGate = "3"
          break
        case 'GATE_04':
          resultQualityGate = "4"
          break
        case 'GATE_05':
          resultQualityGate = "5"
          break
        case 'GATE_06':
          resultQualityGate = "6"
          break
        case 'GATE_07':
          resultQualityGate = "7"
          break
        case 'GATE_08':
          resultQualityGate = "8"
          break
        case 'GATE_09':
          resultQualityGate = "9"
          break
        case 'ADOPTION':
          resultQualityGate = "-1"
          break
        case 'GATE_EXEMPT':
          resultQualityGate = "-2"
          break
        case 'GATE_REDZONE':
          resultQualityGate = "-3"
          break
        default:
          resultQualityGate = "-9"
          jenkins.echo "Unknown Target Gate... Please check http://sonar.optum.com/quality_gates for a valid gate value"
          break
      }//switch
      jenkins.env.targetQG = resultQualityGate
      return resultQualityGate
    }
  }
}

