Deployment Process for MM, DTC, and Codegen

All new deployments should be done through the multi-branch pipelines:

https://jenkins.optum.com/cloudscaffolding/job/mix-master/job/multi-branch-scan-mm/

https://jenkins.optum.com/cloudscaffolding/job/dtc/job/multi-branch-scan-dtc/

https://jenkins.optum.com/cloudscaffolding/job/codegen/job/multi-branch-scan-codegen/

 

In order to make a new deployment, select the develop branch (should be at the top of the list), select "Build with Parameters" from the left pane, and click build to use the default values.

To promote the build to production, simply wait for the build to reach the approval phase, hover over the box for that step, and click "Proceed"

WARNING: This probably goes without saying, but don't promote to production unless you're absolutely certain you know what you're doing.



How To Build Branches in Jenkins
There are two ways to build branches in the multi-branch pipeline:

1: Submit a new PR
PRs are automatically built in Jenkins whenever they are submitted or updated with a new commit. A message will appear in your PR saying that checks are pending, completed or failed. If you click the details button on the check, it will bring you to the jenkins build.

Alternatively, you can navigate to the build manually in the folder. The build's name will be something like PR-123, where the 123 is the number of your PR in github.

While builds on new commits are executed automatically, you can also manually trigger new builds by following the same steps you would to build develop. Do not change the branch parameter from the default develop.

Note that builds in PR branches will not ask for approval, as there is no reason for anyone to be pushing any branch other than develop to production.

2: Build through Develop
This is how building branches was done before multi-branch was introduced. The steps are the same as deploying develop, but when in the "build with parameters" step, replace the default branch name "develop" with the name of your branch in github.

This is not the recommended method to build a branch. Functionally, it is the same as building through a PR, but it leaves a messier trail in the develop branch.
