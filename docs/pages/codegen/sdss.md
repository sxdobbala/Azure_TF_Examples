---
layout: main
title: SDSS App Type
permalink: codegen/sdss
---

By specifying SDSS in the app type in BOM Lite, we are actually creating a Java Maven application with Spring Boot framework which has the following modules:
- API: This module can be used to define the structure (request-response schema) of the application.
- CORE: This module can be used to define all the functionality/Business logic of the application.
- Database: This includes Liquibase which is an open source, database-independent library for tracking, managing and applying database schema changes.
- WEB: It includes web controller and swagger configurations.

Apart from these modules, it also includes the following :
- Dockerfile: This is used to create the Docker image .
- JenkinsFile: This is used to create the Jenkins pipeline which is helpful in building, deploying and testing (Sonar, Fortify , Arachni) the application.
- Sdss-template (OpenShift template): We can leverage this template for creating the application on OpenShift environment.
