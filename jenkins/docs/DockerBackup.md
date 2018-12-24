# Docker Backup example config:
```
jenkinsNode: 'docker-aws-slave'
project:
    gitUrl: 'https://github.optum.com/ct-instrumentation/sample_app_k8s.git'
    credentialsId: 'fb064600-b737-4b1c-8643-63357494dd55'
deploymentBranchSetting:
-   name: 'US'
    mavenBuild: true
    deployToArtifactory: false
    additionalMavenArgs: "-Dmaven.test.failure.ignore=true"
    docker:
       repo: 'admin/sampleapp'
       hub: '869720489587.dkr.ecr.us-east-2.amazonaws.com'
       credentialsId: 'd6b6e4a8-4f3d-43d5-80dc-9d42e92cd2bc'
       backupDtr: true
       backupDtrCredentialsId: 'Optum_DOCKER_CREDENTIAL'
```

The picture below shows how shared library will execute this config:

![dockerbackup](/images/DockerBackup.jpeg)