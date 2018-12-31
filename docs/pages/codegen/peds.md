---
layout: main
title: Product Engineering and Data Solutions (PEDS) App Type
permalink: codegen/peds
---

By specifying PEDS in the app type in BOM Lite, we are actually creating a Java Maven application with Spring Boot framework.
It also includes Hystrix framework library which helps to control the interaction between services by providing fault tolerance and latency tolerance. It improves overall resilience of the system by isolating the failing services and stopping the cascading effect of failures.

This app type has the following modules:
- API: This module can be used to define the structure (request-response schema) of the application.
- CORE: This module can be used to define all the functionality/Business logic  of the application.
- Database: This includes Liquibase, which is an open source, database-independent library for tracking, managing and applying database schema changes.
- WEB: It includes web controller and swagger configurations.

Apart from these modules , it also includes the following :
- DockerFile: This is used to create the Docker image .
- JenkinsFile: This is used to create the Jenkins pipeline which is helpful in building, deploying and testing (using Sonar, Fortify, Arachni) the application.
