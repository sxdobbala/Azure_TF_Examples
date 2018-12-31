---
layout: main
title: Adding Selectable Toolsets to Codegen
permalink: codegen/toolsets
---

If there is a tool that you'd like to include in a build pipeline and would like to let users decide if they want it or not, Codegen can help with that. Follow the guide below to get your tool included.

If you haven't done so already, please follow the developer guide to get Codegen setup for development on your machine. This will be the easiest method to get everything done, as you will need to commit the app type to Codegen.

## Basic Setup
To add a selectable tool, make a new directory inside of core/src/resources/velocity/includes with the name you want the tool to be included as. For instance, if you want a tool called "Super Cool Code Reviewer", you might make a folder called "sccr". So long as it's easy to tell what tool it is.

What you put into this folder depends on a few things, listed below. 

### Tool is platform-independent
If the tool you want to add uses the same Jenkins configuration for any platform, as in the same Jenkins commands work for any cloud platform/service, then all you need to do is put the section of Jenkinsfile that needs to be added into a file called include.txt, which is what Codegen will look for when finding selectable tools.

### Tool has different configs for each platform
If the tool you want to add needs different Jenkins commands for different platforms, add a blank text file called paas. This tells Codegen to be aware of different cloud platforms being present.

Once this is added, then you will need to add a folder for all of the platforms we support, which right now is openshift and mesos-marathon. Even if your tool doesn't support one or the other, it needs to be added so that Codegen will handle both cases correctly. 

Inside each of these folders, place a include.txt file with the appropriate Jenkins commands you need for that platform. If the platform isn't supported for that tool, just leave the file blank.

### Tool has files that need including
If you need to include some files in the generated code, that's also possible. Depending on which setup you followed above, make a folder next to the appropriate include.txt named copyFiles. Inside of this directory, place the files you need copied to the generated code body. Make sure that you make the appropriate directory structures for these includes, as they will be copied exactly as displayed in the copyFiles folder.

For example, if you need "config" in the folder tool, then you'd make a folder in copyFiles called tool and put config in there and it'll end up in tool/config in the generated code.

## Including tools in templates
Once the tool has been added to the includes folder properly, you will need to place markers inside of the Jenkinsfile templates in order to have the tool added during generation. 

Find the appropriate template that you want to modify and then open the Jenkinsfile.vm file. Locate where the tool should be added in at and then put in a ${toolname} tag, where toolname will be the name of the tool you just added. 

For example, after adding "Super Cool Code Reviewer" to sccr, then you'd add ${sccr} into the Jenkinsfile.vm and then you can generate a project with it in it. 
