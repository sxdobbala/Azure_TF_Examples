---
layout: main
title: Creating new Application Types and Subtypes
permalink: codegen/apptypes
---

If Cloud Scaffolding doesn't offer an Application Type that you are looking for or you need a different Application Subtype for one of our existing types and you'd like to add them in, then you're in the right place. Codegen's structure makes it easy to add a new Application Types and Subtypes. Let's walk through adding a new app type.

If you haven't done so already, please follow the developer guide to get Codegen setup for development on your machine. This will be the easiest method to get everything done, as you will need to commit the app type to Codegen.

## Adding the feature files
The first step to making your new Application Type available would be to build the feature files for it. These feature files let Codegen know which of our cloud platforms should support your application and what systems need to be made available for your app type to exist.

To add these files, using your system's file browser or an IDE, whichever you prefer, browse to core/src/resources/feature. If the type doesn't exist, create a folder in here with the name of the type, in all lowercase characters, that you wish to add.

### Adding the type
Inside of the type folder, you'll need to add a subtype, which typically is the type of application that will be generated. The most common subtype offered is a microservice.

### Adding platforms and features
Inside of the subtype folder, you'll need to create at least two text files. The first will be named after the cloud platform you're wanting to run it on, the option currently being openshift. In this file, you'll make a comma separated list of the features you want the app to have, such as Jenkins, Github, etc. See the [features documentation](../dtc/) for the current list of features available.

#### Important considerations for features
If your new application type/subtype needs...
 - to be able to deploy to the cloud, you'll need the following features: jenkins, dtr, and openshift.
 - to be built from source, you'll need github
 - to provide templated code, you'll need codegen
 - to push artifacts to Artifactory, you'll need artifactory 

### Adding facets
You'll then need to add a facets file, which will contain the versions of the main tools that the template will be based on. 

## Adding a template application (App Type only)
After making sure your feature files are properly added, you'll need to create a template application for your app type. To do so, create a folder in the core/src/resources/velocity directory with the same name you gave your type. Inside of this folder, make an init folder.

In the init folder, place the code template that the app type needs. Bearing in mind that this application type may be used by others, please ensure that there is no project specific information in the template. Here are some other notes to know when making the templates:

 - For Jenkins pipelines
	- There are a number of templatable pieces of information that are available to be inserted in the template when necessary.
	- If the ability to deploy is needed in the pipeline, in the step where you want a deployment to occur, add the template tag ${deploy}. This will require the dtr feature, as mentioned above.
	- We also have some tools that can be included in the jenkins pipeline with no extra setup from the user. You can find a list of these on the main Codegen page
 
## Testing this all out
Once this is added, your new app type should show up in Codegen, after building of course. There are some tests in that will fail if the file structure isn't correct. If tests pass, then check the following using Codegen's Swagger UI.

Begin testing by making a POST call to /api/v1/metadata/applications/types, which should have the resulting type in the list returned.

A POST call to /api/v1/metadata/applications/{applicationType}/subtypes, with your type name replacing {applicationType}, should show the subtype.     

A POST call to /api/v1/metadata/applications/{applicationType}/{applicationSubtype}/{paas}/features, with your type, subtype, and cloud platform specified respectively, will then show what features you've made available. If you've made things available for multiple platforms, make sure you test them all.

If all of these are correct, then you can try to generate your code body and make sure that it works. This can be done by using a POST call to /api/v1/codegen/generate with a body like:

```
{
  "application": {
    "applicationType": "EXAMPLE",
    "name": "name",
    "projectOwner": "msid",
    "subType": "MICROSERVICE",
    "team": "team"
  },
  "billingInformation": {
    "askGlobalId": "string",
    "tmdbNumber": "string"
  },
  "features": [
    {
      "name": "github",
      "uri": "https://github.optum.com/org/repo",
      "version": "v1",
      "organizationName": "org",
      "repositoryName": "repo",
      "scmType": "git",
      "managementGroup": "secure_group"
    }
  ],
  "version": "1"
}
```

If your Github repository is filled with the correct files, then you're good to make a pull request and get the new app type merged in! If you're running into problems, information for contacting maintainers can be found on the main page of the documentation.

