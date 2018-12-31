---
title: Mix-Master
layout: main
permalink: mm/
---

Mix-Master is the orchestration layer that communicates with DTC to automate the creation of resources, enabling developers to be productive faster.

## Available Application types

For an up-to-date list of the application types that Cloud Scaffolding has available, check out our [Codegen](../codegen) documentation.

## Common Errors
Information on the errors that Mix-Master can commonly produce and how they may be displayed is available [here](errors)

## Running Orchestrations in Debug module
If you would like to see what the Manifest output of trying to provision will be without actually provisioning anything, Mix-Master's debug field will become useful, as setting it to true will only generate a Manifest for any Manifest or BOMLite you give it.

## Bill-Of-Materials Lite:
Below is an example Bill-Of-Materials Lite, or BOMLite, that is used to generate Greenfield applications, complete with field constraints:
```
{
  "version": "v1" (not null & not empty),
  "paas": "string", (openshift, optional)
  "application": {
    "name": "APPLICATION_NAME_VALUE" (not null & not empty, numbers or dashes, 3-21 characters),
    "team": "TEAM_NAME_VALUE" (not null & not empty, lowercase letters, numbers or dashes, 6-20 characters),
    "projectOwner": "MSID_OF_AUTHENTICATED_USER (also LDAP 'CN' value from ‘who is this service for' in PMG RSO)" (not null),
    "applicationType": "SDSS" (not null, see 1 below),
    "subType": "MICROSERVICE" (not null, only MICROSERVICE supported)
  },
  "billingInformation": {
    "tmdbNumber": "TMDB_SEARCH_CODE" (either this is non null or askGlobalId is non null),
    "askGlobalId": "ASK_GLOBAL_ID" (either this is non null or tmdbNumber is non null)
  },
  "features": [
  ]
}
```
\[1\] - Full Application Type list is available [here](https://jumpstart-codegen.optum.com/api/v1/metadata/applications/types) (Auth needed).

## Full Manifest:
Below is an example manifest that will be returned from Mix-Master when using a BOMLite to orchestrate and can also be used for orchestration if a user only needs a certain set of features or already has some of the features. Features can be removed if not needed.

```
{
  "version": "v1" (not null & not empty),
  "paas": "string", (openshift)
  "application": {
    "name": "APPLICATION_NAME_VALUE" (not null & not empty, numbers or dashes, 2-24 characters),
    "team": "TEAM_NAME_VALUE" (not null & not empty, lowercase letters, numbers or dashes, 6-63 characters),
    "projectOwner": "MSID_OF_AUTHENTICATED_USER (also LDAP 'CN' value from ‘who is this service for' in PMG RSO)" (not null),
    "applicationType": "SDSS" (not null, see 1 above),
    "subType": "MICROSERVICE" (not null, only MICROSERVICE supported)
  },
  "billingInformation": {
    "tmdbNumber": "TMDB_SEARCH_CODE" (either this is non null or askGlobalId is non null),
    "askGlobalId": "ASK_GLOBAL_ID" (either this is non null or tmdbNumber is non null)
  },
  "features": [
    {
      "name": "jenkins",
      "uri": "https://jenkins.optum.com/central/job/projectname/job/demoapp",
      "version": "v1",
      "managementGroup": "YourADGroup",
      "description": "Your jenkins project",
      "folderName": "projectname" (not null & not empty),
      "jobName": "demoapp" (not null & not empty),
      "dedicated": false (always false, no dedicated provisioning in play right now),
      "tools": ["sonar", "arachni", "fortify"] (see 3 below for current available tools, remove from list to take out of generated pipeline)
    },
    {
      "name": "artifactory",
      "uri": "http://repo1.uhc.com",
      "version": "v1",
      "groupId": "projectname" (not null & not empty, must be longer than 1 character. *this is also known as permission target name),
      "serviceAccount": "you" (not null & not empty)
    },
    {
      "name": "github",
      "uri": "https://github.optum.com/projectname/demoapp",
      "version": "v1",
      "organizationName": "projectname" (not null, beteween 3-50 alphanumeric with no spaces or special characters, underscores allowed),
      "repositoryName": "demoapp" (not null, beteween 3-100 lowercase alphanumeric with no spaces or special characters, underscores allowed)
    },
    {
      "name": "openshift",
      "uri": "https://ocp-elr-core-nonprod.optum.com" (or "https://ocp-elr-core.optum.com"),
      "version": "v1",
      "projectAdministrator": "you",
      "cpu": "1" (not null & not empty, must be a number),
      "ram": "2" (not null & not empty, must be a number),
      "projectName": "projectname" (not null & not empty, lowercase letters, numbers or dashes, 6-63 characters),
      "description": "This is your project description",
      "applicationName": "demo-app",

      "platform": "nonprod-origin", (prod-origin or nonprod-origin)
      "dataCenter": "elr" (elr or ctc),
      "environmentZone": "nonprod" (prod or nonprod),
      "networkZone": "core" (core or dmz)
    },
    {
      "name": "codegen",
      "uri": "https://jumpstart-codegen-dev.optum.com/",
      "version": "v1",
      "type": "SDSS",
      "facets": [
        {
          "version": "5.0.0",
          "name": "cloudsdk"
        },
        {
          "version": "1.0.0 ",
          "name": "junit"
        }
      ]
    }
  ]
}
```
\[3\] - The latest list of includable tools can be found [here](../codegen)
