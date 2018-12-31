---
layout: main
title: About Cloud Scaffolding
permalink: /about
---

Cloud Scaffolding has a few primary goals:
- Reduce ramp up time and enable productivity
- Simplify user experience
- Automate best practices across the development and DevOps spectrum

# Value Proposition
As a rough demonstration of the way these goals drive cost/quality benefits to the organization, let's sketch out some typical scenarios.

As a delivery team, we are often asked to provision new application infrastructure.  For example, I might need to stand up a the "NextGen" app in a Java ecosystem.  There are a number of hurdles to getting this done.
1. What is the current best practice?  Do we know the right tools and technologies to use today?  Or do we fall back to what we currently have?
2. After we figure out #1, how do we request the assets?  Is it via service catalog?  Do I need an infrastructure engineer?
3. How would I build a modern app?  I might know that we are using some pretty legacy patterns/frameworks for our existing platform, but how to I build the application the right way?

## Infrastructure Value Proposition
There are a number of other questions, but at a basic level, the above are the challenges a team faces.  So this leads to the following common scenario for new teams:
1. 2+ weeks determining what is the current technology stack we can/should leverage.
2. With some assumptions, provisioning for the following would take (in calendar duration):
  - OpenShift - a day
  - OpenShift project model - 4 weeks
  - Artifactory - 1 day
  - Jenkins - 1 day
  - Jenkins build pipeline or jobs - 4 weeks
  - GitHub - 1 day
  - SECURE
        - Provisioning groups and accounts -  4 weeks
  - Creating a modern app shell to run on both OpenShift and the local desktop (old OS without Docker support) 8 weeks

All total, project teams might struggle for nearly half a year to stand up a modern infrastructure and app.  That doesn't even include the coding.

## How does Cloud Scaffolding help?  
We build the infrastructure in less than 5 minutes, so you can just starting writing the logic.

Assuming a single resource for 25% of their time performs these activities, we can save the team ~200 hours of effort and produce a much more robust and consistent infrastructure.  That is a savings of about $20,000 without even considering the other benefits.

## Development Value Proposition
But we all know the infrastructure is only part of the picture.  There's also time writing code.  And there's a lot of either bad code, or boilerplate code.  This can lead to poor performance, and at a minimum is time wasted manually creating code files.  For instance, most modern apps use an interface and an implementation class for each and every class.  But the implementation directly derives from the interface.  Why should a developer type both twice?

- CodeGen can automatically create a REST endpoint.
  - The REST endpoint automatically uses semantic versioning to support long-term maintenance.
  - The API contract is automatically generated and a multi-layered application pattern is generated that backs it.
  - Resilience patterns and monitoring are baked into the generated code.
  - The versioned database changelog is created with automated CI/CD for the database.

Since the code is generated, there are no issues with fat-fingering, missing DI registration, naming errors or other typical development mistakes. This can easily shave 25% off the work of actually developing the code.  For a team with five developers for 6 sprints, you are talking about ~500 hours of development time saved.  At current billing, that is  ~$50,000 of savings.  For one Program Increment!
