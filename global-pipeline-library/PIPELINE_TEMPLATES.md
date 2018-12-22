# Pipeline Templates

The goal of pipeline templates is enabling all of Optum to use the same, shared and proven CI/CD pipelines.  By succeeding in this high re-usability, greenfield product engineering teams will start with full CI/CD on day 0 backed by production hardened pipeline code.

## What are Pipeline Templates

Pipeline Templates are re-usable fully implemented pipelines.  Where the JPaC Global Pipeline Library enables developers to share functions to build pipelines, Pipeline Templates enable developers to share the built pipelines themselves.  By nature, Pipeline Templates are opinionated implementations derived from JPaC GPL best practice usage across one to many teams, gifted up in an easy to use package.

### Understanding Pipeline Templates

The Pipeline Template framework continues to build on the Global Pipeline Libary, there are 3 primary layers in play:

1. [Global Pipeline Library](#global-pipeline-library)
2. [Base Pipeline Template](#base-pipeline-template)
3. [Pipeline Template](#pipeline-template)

#### Global Pipeline Library

The Global Pipeline Library is what most developers are familiar with, it is the Shared Libary consistening of a high amount of re-usable functions.  

#### Base Pipeline Template

The Base Pipeline Template is the shared code across all Pipeline Templates.  This code is responsible for maintaining a similar core pipeline experience and YAML interface for each Pipeline Template.  This includes handling:

- Git Branching Strategy
- Environment Declarations
- Secret Declarations

#### Pipeline Template

The Pipelne Template is a specific pipeline implementation.  The first two Pipeline Template implementations will be:

- **Naftis**: Declarative Openshift/Kubernetes
- **Terraform**: Terraform

### How Pipeline Templates Work

> **NOTICE**: Pipeline Templates are in an incubating stage and example code is likely to change.

Pipeline Templates rely on two source controlled files in a code repository to work:

- Jenkinsfile
- Optumfile

#### Jenkinsfile

The Jenkinsfile is required to integrate with Jenkins and "import" the Global Pipeline Libary and Pipeline Templates

Example:

```groovy
#!/usr/bin/groovy
@Library('com.optum.jenkins.pipeline.library') _

PipelineTemplate()
```

> **IMPORTANT**:  To allow for an extremely high re-use of Jenkins Pipeline code,  Pipeline Templates emphasize the smallest amount of code in the Jenkinsfile as possible with a source controlled YAML `Optumfile` being the primary configurator.

#### Optumfile

The `Optumfile` has a reservered namespace for pipeline templates.  Pipeline Templates define their YAML interface within this namespace allowing for application develpers to configure their specific Pipeline Template usage.  

Base `Optumfile` Example:

```yaml
apiVersion: v2
askId: poc
caAgileId: poc
projectKey: jpac
projectFriendlyName: global-pipeline-library
componentType: code
targetQG: GATE_08
pipeline: # Reserved namespace for Pipeline Templates
  template: <template name, e.g. naftis, terraform, etc>
  config: # Reserved namespace for each Pipeline Template implementation's YAML interface (defined in Config.groovy)
```

Naftis `Optumfile` Example:

```yaml
apiVersion: v2
askId: poc
caAgileId: poc
projectKey: jpac
projectFriendlyName: global-pipeline-library
componentType: code
targetQG: GATE_08
pipeline: # Reserved namespace for Pipeline Templates
  template: <template name, e.g. naftis, terraform, etc>
  config:
    adapter: nodejs # adapter to use
    credentials: # credentials used by the pipeline
      dtr: DTR_AUTH # publishing to DTR
      ose: OSE_AUTH # creating resources in OSE
      newrelic: NR_AUTH
    deployment:
      clusters:
        - ose-ctc-dmz
      revisionHistoryLimit: 2
      vars: # Map of default variables injected into the template(s)
        logLevel: INFO
      environments: # Environments to deploy to for master branch pipeline
        pr: # Pull request configuration
         cluster: ose-ctc-dmz
         deployEnv: dev
         namespace: link-notifications-dev
         vars:
          replicas: 1
        test:
          namespace: notifications-test # OpenShift project name
          vars: # Map of environment-specific variables
            replicas: 1
        stage:
          namespace: notifications-stage
          vars:
            replicas: 3
        prod:
          namespace: notifications-prod
          vars:
            replicas: 3
```

### Scenario: Product Awesome

Take the scenario where a product portfolio, Product Awesome, is deploying 15 indepedent applications to OSFI openshift using the same pipeline logic.  Product Awesome is obviously using the JPaC Global Pipeline Library and therefore within their 15 different git repositiories have the same Jenkinsfile containing the same JPaC GPL methods with some varying configuration.  When the pipeline developers for Product Awesome make any changes to their pipeline, for example allowing pull requests to spin up an ephemeral environment for testing, the developers will need to make the same code change to their 15 different git repositories.  This is do-able however not an ideal situation for their pipeline developers.  

Now imagine if several other product portfolio's decided to follow the lead of Product Awesome and implemented the same pipelines for their hundreds of applications.  Now we're in need of updating hundreds of git repositories across several portfolios and many more teams to keep features in-sync.  This is no longer as feasible and inevitably certain teams will pull ahead while others fall behind.  Additionally, when new teams now want to follow the lead of Product Awesome there's no longer a single location for those teams to get started with the latest and greatest pipeline code.  In an ideal world the Jenkinsfile's pipeline code would be pulled from a common and versioned source code repository.  This would allow all teams to receive updates to the pipeline by simply updating their version accordingly.  

The Pipeline Template framework has been implemented to resolve this exact issue.  
