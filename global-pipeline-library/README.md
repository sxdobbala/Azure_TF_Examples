# Global Pipeline Library (GPL)

This repository is a collection of shared Groovy classes to extend the functionality of your Jenkins Pipeline by defining your Jenkins Pipeline in a simple Jenkinsfile.

## JPaC Initiatives: GPL, Pipelines and DevOps Metrics

### [GPL](./JPAC_CAPABILITIES.md)

Consumers can begin using the 'gl' methods in the [./vars](./vars) directory to build out the functionality that they need within their Jenkinsfile.  A great way to do this is to start with a mature working example and modify to suit your needs.

See the "Full Pipeline Examples" below in the "JPaC Learning Resources"
   section.

* **Owner**: [Eric Starr][es_email]

### [Pipeline Templates](./PIPELINE_TEMPLATES.md) (in development)

The goal of pipeline templates is enabling all of Optum to use the same, shared and proven CI/CD pipelines. By succeeding in this high re-usability, greenfield product engineering teams will start with full CI/CD on day 0 backed by production hardened pipeline code.

* **Owner**: [MG][mg_email]

### [DevOps Event Metrics](./DEVOPS_EVENTS.md) (in development)

Built into GPL, and therefore Pipeline Templates, DevOps Events are automatically being captured in JPaC and streamed through kafka to data stores for enterprise insights and dashboarding efforts.

* **Owner**: [Marcus Maday][mm_email]

## Prerequisites

* Jenkins 2.1+
* [Jenkins - Workflow CPS Global Lib Plugin](https://github.com/jenkinsci/workflow-cps-global-lib-plugin)

## JPaC Learning Resources

1. See the ['vars'](./vars) folder for documentation within the code
2. See the ['examples'](./examples) folder for example snippets
3. Full Pipeline Examples
  - [Spring Boot](https://github.optum.com/OPTUMSource/spring)
  - [Dot Net](https://github.optum.com/IDWS/idws-dotnet-poc)
4. Achieving CI/CD Maturity using the [JPaC Capabilities](./JPAC_CAPABILITIES.md)<br>

> This is an Opinionated view of how to achieve CI/CD maturity using the current JPaC Capabilities.  Your implementation may vary as needed from the model provided; however, please be careful to make sure that your implementation is as mature as it can possibly be.  You should make sure that your implementation takes into consideration things like Reliability, Availability, Scalability,
Security and Performance.

## JPaC Internal Articles and Resources

- [JPaC on HubConnect](https://hubconnect.uhg.com/docs/DOC-129470)
- [Maven Build with Sonar - Step by Step Guide](https://hubconnect.uhg.com/docs/DOC-126587)
- [JPaC for .NET Standard](https://hubconnect.uhg.com/docs/DOC-140546)
- [CI/CD Best Practices](https://github.optum.com/cicd-practices/cicdpractices)

## DevOps Events Explained

As we develop the various JPaC capabilities ('gl' methods) we are also adding the capability to record events
related to those 'gl' methods.  For example, if you use the glMavenBuild method, an event will get sent at
execution time to a datastore where we can track what is happenning.  Below you will find resources to help
you understand what we are doing and why.
- [DevOps Metrics Presentation](https://github.optum.com/pages/devops-engineering/presentation-devops-metrics/)
- [DevOps Events](./DEVOPS_EVENTS.md)

It is important to know that we are currently working on being able to show information related to these events
(e.g. CI Maturity) within Dashboards across Optum.  This is a WIP for 2018.

## Jenkinsfile Authoring Recommendations

- Define a specific version in your Jenkinsfile rather than using `master` to ensure that the Global Pipeline 
Library does not change out from under you. Your build and deployments could break if you rely upon `master` 
since `master` is constantly changing.  You can reference a specific released version of this Library by 
setting the version in the `@Library` statement within your Jenkinsfile like this: 
```
@Library("com.optum.jenkins.pipeline.library@v0.1.25")
```

- Use of [Declarative](https://www.blazemeter.com/blog/how-to-use-the-jenkins-declarative-pipeline) over [scripted](https://www.blazemeter.com/blog/how-to-use-the-jenkins-scripted-pipeline?utm_source=blog&utm_medium=BM_blog&utm_campaign=how-to-use-the-jenkins-declarative-pipeline).

- Use the JPaC global static public functions found in the [./vars](./vars). If you are unable to find what you need we ask that you consider [contributing](CONTRIBUTING.md) to JPaC.

### Jenkins Central Configurations

- By default the Jenkins Central instance loads the latest version of the [JPaC release](https://github.optum.com/jenkins-pipelines/global-pipeline-library/releases/). This setting can be changed using the @version tag within your Jenkinsfile to set the specific release version.

- To set the agent node type in Jenkins Central you can find the list of available options in the [Jenkins Central Configuration](https://jenkins.optum.com/central/configure-readonly/) by finding the *Docker Templates* section and finding the *Label* setting name.

### External Documentation

 - [Getting started with Jenkins Pipelines](https://jenkins.io/doc/book/pipeline/getting-started)
 - [Pipeline Syntax](https://jenkins.io/doc/book/pipeline/syntax/)

## Contributing

Please refer to [Contribution Guidelines](CONTRIBUTING.md) for guidance on contributing to this project.

## Maintainers
- [Marcus Maday][mm_email]
- [Eric Starr][es_email]
- [Scott Maciej][sm_email]
- [Cathal Ruddy][cr_email]
- [Rajitha Ramasayam][rr_email]
- [RJ Seibert][rs_email]
- [MG][mg_email]
- [Ryan Sites][sites_email]
- [Garret Ruh][gr_email]
- [Brandon Gilzean][bg_email]
- [Zach Becker][zb_email]
- [Anthony Khounlo][ak_email]

## License

This Optum InnerSource Project uses the [Optum InnerSource License](https://github.optum.com/OPTUMSource/OPTUMSource/blob/master/OPTUMLicense.md)

## Acknowledgements

Inspired by and aligned to [Jenkins - Workflow CPS Global Lib Plugin](https://github.com/jenkinsci/workflow-cps-global-lib-plugin)

[mm_email]: mailto:marcus_maday@optum.com
[es_email]: mailto:eric.starr@optum.com
[sm_email]: mailto:scott_maciej@optum.com
[cr_email]: mailto:cathal_ruddy@optum.com
[rr_email]: mailto:rajitha.ramasayam@optum.com
[rs_email]: mailto:richard_seibert@optum.com
[mg_email]: mailto:matthew.grose@optum.com
[gr_email]: mailto:garret.ruh@optum.com
[bg_email]: mailto:brandon.gilzean@optum.com
[ak_email]: mailto:anthony.khounlo@optum.com
[zb_email]: mailto:zachary.becker@optum.com
[sites_email]: mailto:ryan.sites@optum.com

