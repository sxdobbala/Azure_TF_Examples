---
layout: main
title: Developer Guide
permalink: devguide
---

## Access
A portion of the assets related to Cloud Scaffolding require additional access by granted to a user's account.

- Source Code Management (GitHub)
    - Request access to the following group in Secure
        - github_users
    - Request access to Cloud_Scaffolding organization
        - Once access has been granted to GitHub, access must also be granted to the cloud_scaffolding organization for which the source code is contained within. Navigate to the  [GitHub](https://github.optum.com/cloudscaffolding) repository where you will be given the opportunity to request access to the organization.
- OpenShift v3
    - Request access to the following group in Secure
        - OSE3
    - Have project admin add you to the projects:
        - pbi-devops
        - pbi-devops-dev
        - pbi-devops-test
- Docker Trusted Registry (DTR)
    - Request access to the following groups in Secure
        - dtr_users
        - pbi_jumpstart


## Required Tools/Software
The following software are required on your machine and can be installed through the AppStore.
- Java Development Kit (JDK) 8
- Apache Maven
- Git
- Integrated Development Environment (such as IntelliJ [Recommended] or Red Hat JBoss Developer Studio)

## Local Development Environment Setup

This section describes the steps necessary to configure a developer's machine for Cloud Scaffolding development.

**Note: If you're using a Mac and run into trouble with SSL certs at any point, you can try using the fix here:** [Mac SSL Fix](macssl)

### Maven
Maven is a software project management tool. To support the resolution of dependencies from the enterprise artifact repository (Artifactory), a custom settings file must be configured within a user's' profile directory.
Add the following example file in
 - Windows `C:\Users\<username>\.m2\`
 - Mac `/Users/<username>/.m2/`

#### Sample XML - settings.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
   <localRepository>C:\Users\<YOUR_USER>\.m2\repository</localRepository>
   <servers>
      <server>
         <username><YOUR_USER></username>
         <password><YOUR_ENCRYPTED_PASSWORD></password>
         <id>artifactory</id>
      </server>
   </servers>
   <mirrors xmlns="http://maven.apache.org/SETTINGS/1.1.0">   
      <mirror>     
         <mirrorOf>*</mirrorOf>     
         <name>Artifactory</name>
         <url>http://repo1.uhc.com/artifactory/repo</url>     
         <id>artifactory</id>   
      </mirror>
   </mirrors>
   <profiles>
     <profile>
     <id>artifactory</id>
     <repositories>
       <repository>
       <id>central</id>
      <name>libs-releases</name>
      <url>
          http://repo1.uhc.com/artifactory/libs-releases
      </url>
      <releases>
        <enabled>true</enabled>
        <updatePolicy>never</updatePolicy>
        <checksumPolicy>warn</checksumPolicy>
      </releases>
    <snapshots>
    <enabled>false</enabled>
  </snapshots>
</repository>
<repository>
   <id>internal</id>
   <name>optum-releases</name>
   <url>
      http://repo1.uhc.com/artifactory/UHG-Releases
    </url>
    <releases>
      <enabled>true</enabled>
      <updatePolicy>never</updatePolicy>
    </releases>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>
  <repository>
    <id>snapshots</id>
    <name>libs-snapshots</name>
    <url>
       http://repo1.uhc.com/artifactory/libs-snapshots
    </url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
      <checksumPolicy>warn</checksumPolicy>
    </snapshots>
   </repository>
</repositories>

<pluginRepositories>
   <pluginRepository>
     <id>central</id>
     <name>libs-releases</name>
     <url>
       http://repo1.uhc.com/artifactory/libs-releases
     </url>
     <releases>
       <enabled>true</enabled>
       <updatePolicy>never</updatePolicy>
       <checksumPolicy>warn</checksumPolicy>
     </releases>
     <snapshots>
       <enabled>false</enabled>
     </snapshots>
  </pluginRepository>
  <pluginRepository>
    <id>snapshots</id>
    <name>libs-snapshots</name>
    <url>
http://repo1.uhc.com/artifactory/libs-snapshots
    </url>
    <releases>
       <enabled>false</enabled>
    </releases>
    <snapshots>
       <enabled>true</enabled>
       <updatePolicy>always</updatePolicy>
       <checksumPolicy>warn</checksumPolicy>
    </snapshots>
  </pluginRepository>
</pluginRepositories>

</profile>
</profiles>
<activeProfiles>
<activeProfile>artifactory</activeProfile>
</activeProfiles>
</settings>
```

Validation that maven is installed can be achieved by executing the following command:
> $ mvn --version

### Source Code Retrieval

For Mix-Master:
> $ git clone git@github.optum.com:cloud-scaffolding/mix-master.git

For Devops Toolchain:
> $ git clone git@github.optum.com:cloud-scaffolding/devops-toolchain-service.git

For Codegen:
> $ git clone git@github.optum.com:cloud-scaffolding/codegen.git

### Managing Encrypted Values

To managed secure values within several of the primary applications, the [Spring Boot framework](https://projects.spring.io/spring-boot/) along with a encryption library [jasypt](http://www.jasypt.org/) are utilized.

These values are secured with AES256 strength encryption. By default, the Java JDK does not provide this level of encryption due to export laws.
- An additional library, the [Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy File](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html), may need to be configured before the application can be launched, otherwise a decryption error will occur at application startup.

#### JCE Setup
If the issue occurs, Download the [(JCE) Unlimited Strength Jurisdiction Policy File](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
- Copy the downloaded file to the jre/lib/security folder within the directory containing the Java JDK (Java home) overwriting the existing file.
- You can find the location of the JDK directory by running the mvn --version command.

#### Jasypt Setup
1. In the Maven project resources folder (src/main/resources which also contains the application.yml) create a new file called **application-secrets.yml**.
2. In the new file, add a line:
> ```jasypt.encryptor.password: <PASSWORD>```

The <PASSWORD> string should be replaced with the actual password which the project owner can give you on request. This file is tracked out of source code so that the secrets are not published in the source code repository.

### LDAP Certificate
Exposed cloud scaffolding services are secured using LDAP credentials and connect to backend resources in order to validate proper authentication has been provided.

Since the LDAP server is secured using secure LDAP (LDAPS), the certificate of the server must be configured inside the default JDK keystore ($JAVA_HOME/jre/lib/security/cacerts).

Since the base images that are used to run Cloud Scaffolding components need to also include this certificate at runtime, you can obtain the necessary certificate from within the [devops-toolchain-container](https://github.optum.com/cloud-scaffolding/devops-toolchain-container/tree/master/images/webserver30-tomcat8-openshift-oc) repository.

Download the file or clone the repository locally, copy the file to $JAVA_HOME/jre/lib/security/ldapca.crt, and then add the ldapca.crt file to the default JDK keystore by executing the following command from the directory where the certificate was copied to.
> ```keytool -import -alias ldapca_self_sign -keystore cacerts -storepass changeit -file ldapca.crt```

You can also turn off authentication (and then won't need the LDAP cert) by adding the following to web/src/main/resources/application-secrets.yml:

> ```com.optum.pbi.auth.enabled: false```

Be sure to restart any running Java applications that may be running as it would need to recognize the newly updated keystore.

### Lombok Support in IDE
It is recommended to use an integrated development environment (IDE) for local development, as they greatly improve the development experience.

To support the Lombok annotations our project uses, it will be important that the IDE you choose has the necessary plugin installed.
- For Intellij there is a supported plugin available through the editor in plugin management settings or from the Jet Brains site.
- For JBoss Developer Studio, you will need to download the installer from the downloads page on the Lombok site.
- For Mac users, you will need to check your security settings to verify that your system allows downloaded programs to run in order to run the installer.
- For Windows users, when the installer completes it will register the lombok agent with your editor's jvm and put the jar in the correct folder but due to a bug in the installer it will not work correctly out of the box. To fix this issue you will need to go into your launcher ini file and update the line that specifies the lombok agent, such that the full file path is specified.

### Validating Building and Testing

To validate the local development environment has been configured properly, you can perform an application build. By default, when executing a Maven build, the JUnit based unit tests will also execute.
>$ mvn clean install

If the build completes successfully, your machine has been successfully configured for Cloud Scaffolding development

### Integration Testing
A suite of integration tests are maintained in a module within both the Mix-Master and Devops-Toolchain projects.

These target the deployed services and are used to validate the behavior of the live system. They can also be run against a locally deployed service to prevent regression and test new functionality.

In order to run the tests against your local service, you need to make sure your local service is up and running, and make note of the port that its running on. Services default to running on port 8080, but you can set an alternative value by defining the environment variables MM_SERVER_PORT and DTC_SERVER_PORT. You will need to set alternative ports if you plan to run more than one service at a time.

Next, in the src/main/java folder of the integration-tests module, there is a class BaseAdaptor; at the top of this class update the default host to be {+}http://localhost+:<port> so that it's pointing back to the local service.  For Mix-Master, the first time you run them locally you will also need to setup an application-secrets.yml file in integration-tests/src/test/resources.

For Mix-Master, the first time you run them locally you will also need to setup an *application-secrets.yml* file in integration-tests/src/test/resources.

In that file you'll need to specify the following values:
```
com.optum.pbi.admin.username: <username>
com.optum.pbi.admin.password: <password>
com.optum.pbi.github.username: <username>
com.optum.pbi.github.password: <password>
com.optum.pbi.real.username: <yourMSID>
com.optum.pbi.real.password: <yourMSpassword>
com.optum.pbi.artifactory.username: <username>
com.optum.pbi.artifactory.password: <password>
```

To run Devops Toolchain integration tests, the same directory should have the following values:
```
com.optum.pbi.admin.username: <username>
com.optum.pbi.admin.password: <password>
com.optum.pbi.real.username: <yourMSID>
com.optum.pbi.real.password: <yourMSpassword>
com.optum.pbi.ask.password: <askPassword>
com.optum.pbi.artifactory.username: <username>
com.optum.pbi.artifactory.password: <password>
com.optum.pbi.jenkins.username: <username>
com.optum.pbi.jenkins.password: <password>
com.optum.pbi.openshift.username: <username>
com.optum.pbi.openshift.password: <password>
```

There are several ways to run the tests. In a CLI, you can simply navigate to the integration-tests module (e.g. mix-master/integration-tests) and enter the following command to run all tests.
>$ mvn clean install

In an IDE such as Eclipse or IntelliJ, you may need to import the integration-tests module as a separate project, or add the module to the parent pom (note that if you make this change to the pom, you should be careful not to commit the change to the repo).

After doing this, you should have the option to run tests when you right-click on the package or individual test classes.

### Launching Applications

Spring boot applications can be launched in a number of ways. During the development phase, the recommended approach is to use the Spring Boot Maven Plugin. Most Cloud Scaffolding applications are comprised of a multi-module maven project with the spring boot module contained in a folder called "web".

Navigate to the web folder and execute the following command to launch the application.
>$ cd web
>$ mvn clean spring-boot:run

Inspect the server logs to confirm Camel started successfully.
If a line similar to the following appears, the application is most likely in a healthy state
> ```23:43:50.785 [CamelMainRunController] INFO SpringCamelContext::start - Apache Camel 2.18.1.redhat-000012 (CamelContext: camel-1) started in 4.987 seconds```

Each application also exposes a health endpoint located at /health that can be used to validate the state of the application.
To shut down the application, hit the CTRL+C keys.

### Application Profiles

To provide support for targeting specific environments at runtime, the concept Spring Boot Profiles is being utilized.

Spring Boot, by default, loads application properties from a file called application.yml located in the src/main/resources folder. This file would contain all of the properties to target a production environment. To provide support for defining values targeting other environments, such as development, a separate file can be created in the src/main/resources folder with the respective values. Spring Boot uses a specific naming convention by appending -<environment> to the end of the file.

For example, to define properties targeting a development environment in a profile called dev, the name of the file would be application-dev.yml.

To active the profile at runtime, the SPRING_PROFILES_ACTIVE environment variable can be set. For the example of a dev profile, setting SPRING_PROFILES_ACTIVE=dev would active the profile.
Alternatively, specifying the command line argument --spring.profiles.active=dev at runtime will also activate the profile.
For deployment into OpenShift, the SPRING_PROFILES_ACTIVE parameter can be specified when instantiating the tomcat8-oc-binary-template template or by executing the following command to add the environment variable to the DeploymentConfig associated with the application:
`oc env dc/<name> SPRING_PROFILES_ACTIVE=<profile_name>`

## Contributing Changes You've made

Visit our [Contributing to Cloud Scaffolding](contribute) for more information on putting changes in.
