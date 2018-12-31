---
layout: main
title: OPI App Type
permalink: codegen/opi
---

# Getting Started
For all information about the Catalog, see [this repo](https://github.optum.com/opi-api/welcome).

## Creating Your Application
It is recommended that you use the [Catalog site](http://api-catalog.optum.com/) to create an OPI API. The Catalog uses Cloud Scaffolding DTC APIs to create a GitHub repository and push the Codegen App Type to that repo. This process will automate a few other prerequisites needed to register your API with the Catalog.  

### Using jumpstart-codegen.optum.com to create a new API
If you would just rather create the API to see what kind of code is created, or you want to spin up another API without registering it with the Catalog, you can still use Codegen manually.  

Use the `POST /api/v1/codegen/generate` endpoint once you login to [this page](jumpstart-codegen.optum.com)

#### Example request body
```json
{
  "version" : "1",
  "paas" : "openshift",
  "application" : {
    "name" : "APP_NAME",
    "team" : "TEAM",
    "projectOwner" : "OWNER_MSID",
    "applicationType" : "OPI",
    "subType" : "MICROSERVICE"
  },
  "billingInformation" : {
    "tmdbNumber" : "TMDB-#######",
    "askGlobalId" : "UHGWM###-######"
  },
  "features" : [ {
    "name" : "github",
    "uri" : "https://github.optum.com/TEAM/APP_NAME",
    "version" : "v1",
    "organizationName" : "TEAM",
    "repositoryName" : "APP_NAME"
  } ]
}
```
