# DevOps Events

There is an initiative across Optum to track the DevOps Maturity of applications using the events that
naturally occur during the CI/CD Pipeline stages that are added to a Jenkinsfile.  By using Jenkins Pipeline as Code (JPaC) and the Global Pipeline Library, you can get Devops Event Metrics captured for your application for free.<br><br>
To view a presentation on the DevOps Maturity Metrics and how it works, you can go here: https://github.optum.com/pages/devops-engineering/presentation-devops-metrics/
<br><br>
<small>
Note: For every 'gl' method in the /vars folder, extra code will have to be added that will enable the sending of the event represented by the gl method.  It is for this reason that we have created a table below so that you can 
understand which methods have been DEVOPS EVENT enabled.
</small>

### Optumfile.yml
<b>Breaking Change!!!</b><br>
<i>It is now Required that all JPaC users create an Optumfile.yml at the top level of their source control.
If you do not then you will get an error.</i>

- askId: <br>If you do not know your ASKID, you can use 'poc' for now.  To look up your ASK ID, you can go here: http://ask.uhc.com      and enter the name of your application in the search box.  You will then see a field 'ASK Global Id' where you will see an ID that looks something like this <b>UHGWM110-006715</b><br>
- caAgileId: <br>If you do not know your caAgileId, you can use 'poc' for now.  If you do NOT use Rally at all then just use 'poc' for this field.  If you do use Rally to track the User Stories for your project then you can login here https://rally1.rallydev.com and navigate to your project.  The URL will look something like this 'https://rally1.rallydev.com/#/119066050240d/dashboard'.  The number part of the URL will be your CA Agile ID.  In the example above the caAgileId is <b>119066050240d</b><br>
- projectKey: <br>
  <div style="text-align:left;">
    This should be unique across Optum.  If you use Maven then this would be your groupId + colon + artifactId.  It is important that you use the same key that is used for your sonar scans.  Look at your project page in Sonar and find the "key". This is what you should use here.  It should look something like this <b>com.optum.electronicProducts:ecme</b>
  </div>
- projectFriendlyName: <br>Start with a short product Identifier, then add a dash, then add the name of this component.  In Maven, the component name would be equivalent to your artifactId in your POM.  Examples: <b>ECM-webapp</b> or <b>ECM-microservice</b> or <b>ECM-adminui</b>
- componentType: <br>
Options: <b>code</b>, <b>database</b>, <b>infrastructure</b>, <b>config</b>, <b>other</b><br>
- targetQG:  
Options: <b>GATE_00, GATE_01, GATE_02, GATE_03, GATE_04, GATE_05, GATE_06, GATE_07, GATE_08, GATE_09, GATE_EXEMPT</b><br>
This is the Sonar Quality Gate that your team has targeted to achieve by the end of the current year.  If you are unsure of what this should be you can use <b>GATE_00</b> for now.  See http://sonar.optum.com/quality_gates to review the Quality Gates in Sonar.  See https://github.optum.com/pages/devops-engineering/presentation_devops_sonar-quality-gates-2018/ to see a presentation on the 2018 Sonar Quality gates and the associated MBO for Code Quality)


```
apiVersion: v1
metadata:
  askId: poc
  caAgileId: poc
  projectKey: com.optum.myorg.myapp:component 
  projectFriendlyName: MyApp-component
  componentType: code 
  targetQG: GATE_00
```

### General Architectural Flow
JPaC -> Kafka -> ElasticSearch -> ... a Dashboard can be configured to consume this data. 

If you call one of the supported 'gl' methods, a corresponding event gets 
pushed to the Kafka streaming service.  ElasticSearch is configured to pull
this data from Kafka.  Dashboards can then be configured to consume the data
in ElasticSearch.

### Dashboards
- Executive Dashboard as part of the OPEAR Reporting Portal (Not Implemented Yet)
- Team Dashboards (Not Implemented Yet).  This will emerge from the InnerSource JPaC Global Pipeline Team.

#### To Enable DevOps Events for your Project
DevOps Events are enabled by default.  You can disable by setting an environment variable in your Jenkinsfile like this

```groovy
  environment {
    DEVOPS_METRICS_ENABLED = 'false'
  }
```



#### You Can View These Events in 2 Different Places
- Kafka (events stay in Kafka 24 hours): [http://metrics-topic-ui-devops01.ose-elr-core.optum.com/](http://metrics-topic-ui-devops01.ose-elr-core.optum.com/)
- Kibana (the viewer for ElasticSearch): [http://metrics-kibana-devops01.ose-elr-core.optum.com/app/kibana](http://metrics-kibana-devops01.ose-elr-core.optum.com/app/kibana)


###### Here are some resources to help understand how this all works:
- Slidedeck: [https://github.optum.com/pages/devops-engineering/presentation-devops-metrics/](https://github.optum.com/pages/devops-engineering/presentation-devops-metrics/)

###### Each 'gl' method is an Event Type
For every 'gl' method in the vars directory, support needs to be added to be able to capture the Devops Events.  The following table is used to capture the 

**TBD** = To Be Determined / Not Yet Implemented


|Category             | gl Method Name                        | Version Added  | Event Code (Kafka Topic)               |
|:--------------------|:--------------------------------------|---------------:|:-------------------------|
| **Angular**         | glAngularBuild                        |        ....TBD | (devops.build)           |
|                     | glAngularTest                         |        ....TBD | (devops.test)            |
|                     |                                       |                |                          |
| **Approval**        | glApproval                            |        ....TBD | (devops.approval)        |
|                     |                                       |                |                          |
| **Arachni**         | glArachniScan                         |        ....TBD | (devops.sec.pen)         |
|                     |                                       |                |                          |
| **Docker**          | glDockerImageBuild                    |        ....TBD | (devops.docker.build)    |
|                     | glDockerImageBuildPush                |        ....TBD | (devops.docker.build),   |
|                     |                                       |        ....TBD | (devops.docker.push)     |
|                     | glDockerImagePull                     |        ....TBD | (devops.docker.pull)     |
|                     | glDockerImagePush                     |        ....TBD | (devops.docker.push)     |
|                     | glDockerImageTag                      |        ....TBD | (devops.docker.tag)      |
|                     | glDockerRepoCreate                    |        ....TBD | (devops.docker.push)     |
|                     | glDockerTagDelete                     |        ....TBD |                          |
|                     |                                       |                |                          |
| **DotNetCore**      | glDotNetCoreBuild                     |        ....TBD | (devops.build)           |
|                     | glDotNetCorePublish                   |        ....TBD | (devops.deploy)          |
|                     | glDotNetCoreTest                      |        ....TBD | (devops.test)            |
|                     |                                       |                |                          |
| **Fortify**         | glFortifyScan                         |    **v0.1.21** | **devops.fortify.cloud** |
|                     | glFortifyScan                         |    **v0.1.21** | **devops.fortify.local** |
|                     |                                       |                |                          |
| **Git**             | glGitCheckout                         |        ....TBD | (devops.scm.co)          |
|                     | glGitCheckoutTag                      |        ....TBD | (devops.scm.tag)         |
|                     |                                       |                |                          |
| **Maven**           | glMavenArtifactoryDeploy              |    **v0.1.21** | **devops.artifact.store**|
|                     | glMavenBuild                          |    **v0.1.21** | **devops.build**         |
|                     |                                       |                |                          |
| **Openshift**       | glOpenshiftBuildAndRun                |        ....TBD |                          |
|                     | glOpenshiftDeleteServiceResources     |        ....TBD |                          |
|                     | glOpenshiftDeploy                     |        ....TBD |                          |
|                     |                                       |                |                          |
| **Retry**           | glRetry                               |        ....TBD | (devops.retry)           |
|                     |                                       |                |                          |
| **SendDevOpsEvent** | glSendDevOpsEvent                     | Helper Method  | NOT APPLICABLE           |
|                     |                                       |                |                          |
| **Sonar**           | glSonarGradleScan                     |    **v0.1.21** | **devops.sonar**         |
|                     | glSonarMavenScan                      |    **v0.1.21** | **devops.sonar**         |
|                     | glSonarNpmScan                        |    **v0.1.21** | **devops.sonar**         |
|                     | glSonarScan                           |    **v0.1.21** | **devops.sonar**         |
|                     | glSonarScanWithPropertiesFile         |    **v0.1.21** | **devops.sonar**         |
|                     |                                       |                |                          |
| **Svn**             | glSvnCheckout                         |        ....TBD | (devops.scm.co)          |
|                     |                                       |                |                          |
| **Terraform**       | glTerraformApply                      |    **v0.1.2x** | **devops.cloud.provision** |
|                     |                                       |                |                          |
| **Xamarin**         | glXamarinAndroidBuildApk              |        ....TBD |                          |
|                     | glXamarinAndroidNameUtilsMakeApkNames |        ....TBD |                          |
|                     | glXamarinAndroidNameUtilsSetEnv       |        ....TBD |                          |
|                     | glXamarinJarsign                      |        ....TBD |                          |
|                     | glXamarinMdtoolBuild                  |        ....TBD |                          |
|                     | glXamarinNugetRestore                 |        ....TBD |                          |
|                     | glXamarinXbuildClean                  |        ....TBD |                          |
|                     | glXamarinXbuildPackageForAndroid      |        ....TBD |                          |
|                     | glXamarinZipalign                     |        ....TBD |                          |





























