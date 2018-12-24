### The Concept of Custom Execution
- "preExecutionScript" and "postExecutionScript" blocks can be clubbed with docker, ose, aws, helm, sonar, lambda,UI to facilitate need of pre and post execution of any custom script
 
### Example Config with preExecutionScript and postExecutionScript

```yml
notificationEmail: ''
flowdock:
    org: ''
    flow: ''
    token: ''
jenkinsNode: ''
project:
    gitUrl: ''
    credentialsId: ''
    pomPathExtension: ''
    deploymentBranchSetting:
        - name: ''
          mavenBuild:
          additionalMavenArgs: ''
          gradleBuild:
          additionalGradleArgs: ''
          deployToArtifactory:
          docker:
              preExecutionScript:
                commandset:
                    -'oc login --server=https://ose-elr-core.optum.com -u ${OSE_USR_NAME} -p ${OSE_PW}'
                credentials:
                    -credentialsId: '0ba9571f-xxx-yyy-000-703b27534zzz'
                     type: 'usernamePassword'
                     usernameVariable: 'OSE_USR_NAME'
                     passwordVariable: 'OSE_PW'
              postExecutionScript:
                commandset:
                    -'sample kubeconfig command'
                    -'sample token command'
                credentials:
                    -credentialsId: '001023-c2ba-491f-80c9-zzzxxxyyy'
                     type: 'kubeconfig'
                     kubeConfigVariable: 'kubeconfig'
                    -credentialsId: '#####-c2ba-491f-80c9-zzzxxxyyy'
                     type: 'token'
                     tokenVariable: 'token'
              repo: 'sample-app'
              tagIdentifier: '0.0.1'
              credentialsId: '0ba9571f-xxx-yyy-000-703b27534zzz'
```

