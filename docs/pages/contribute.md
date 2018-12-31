---
layout: main
title: Contribute to Cloud Scaffolding
permalink: contribute
---

The Cloud Scaffolding team welcomes contributions from the community, so long as it follows our [Contribution Guidelines](guidelines). The information below describes the workflow that can be used to make contributions to the ecosystem.

##  How to Setup a Local Developer Environment
See [Local Developer Environment Setup](devguide) guide for instructions.

## Git Workflow

All code for a particular service in Cloud Scaffolding is maintained in the Develop branch of the particular service. Any new developments are made on feature branches that originate off of the develop branch and, once complete, are merged back to the develop branch.

### Feature Branching

After a repository is cloned as described in a prior section, it will by default track the master branch. Since most active development efforts occur on the develop branch, it should be checked out and used for any new development.
Execute the following commands within the repository:
```
$ git fetch origin
$ git checkout -b develop origin/develop
```
Any development can now occur by creating a feature branch off of the develop branch. By default:
- for new features should use the convention `feature/<branch_name>`.
- to fix bugs should use the convention `fix/<branch_name>`

In either case, the branch name should reflect the feature being added or what is being fixed, like `feature/readme-update` for a README update or `fix/bad-urls` for a fix to replace non-working urls.

To create a new feature branch called feature/readme-update that would hypothetically update the README file of a repository, execute the following while currently on the develop branch:
```
$ git status

On branch develop
Your branch is up-to-date with 'origin/develop'.

$ git checkout -b feature/<feature-name>
```
### Testing Your Code

When introducing new features, be sure to add appropriate unit and/or integration tests that may be used to automate and confirm any modifications. Use the existing tests as a guide.

### Committing Code

When you are satisfied with your changes, you can commit the modifications to your local feature branch. First, add the files that you would like to include in your change:
`$ git add <file>`

Commit the code by also adding a meaningful message describing the change
`$ git commit -m "<message>"`

Commit as many times as you would like. If you have a large number of commits as part of a feature branch, is recommended that these be squashed in order to reduce the overall number of commits on the repository.

### Pushing Your Feature Branch

Once all of the commits to your feature branch are complete, you can then push the branch to the remote repository which will begin the process of incorporating it with the project. Execute the following command to push the feature branch to the remote repository:
`$ git push -u origin feature/<branch_name>`

On GitHub, you can verify the branch is now visible in the dropdown list of branches.

### Submit A Pull Request

Merge requests are a way of requesting changes to a repository. It gives repository maintainers an opportunity to review and incorporate all contributions.
- To create a Pull Request, navigate to the repository's web interface and find the branch that you have been working on. Then select Pull Request. Check to make sure that your changes are all there and give the request a name and description reflecting either what the pull will bring in or a GitHub issue that the pull is resolving.
- The next step is to those the branch to apply the changes to (base branch) and the source containing the changes (target branch). Use the first dropdown to select the develop branch and second dropdown for the name of the branch pushed in the previous section.
    - **Note: You may issue merge requests against any branch, but in most cases, you will request changes against the develop branch.**
- Once both branches are selected, click Compare and the web interface will provide a visualization of the differences along will all commits to include as part of the merge request. Once satisfied, select the New Merge Request button. On the resulting page, enter a title and description of the change being made. Hit Submit merge request to notify repository maintainers of the intention to contribute changes.
- During the review process, maintainers may request additional changes be made. Changes can be made against the same branch and pushes to the remote repository will update the content of the merge request.
- You will be notified if your proposed change was accepted or denied.

### Keeping Your Branches in Sync

As the remote repositories continue to be updated, you will need to also keep your repository in sync. Checkout the name of the branch that has been updated in the remote repository (typically master or develop) and pull in the latest changes:
```
$ git checkout develop
$ git pull origin develop
```

### Deleting Your Local Feature Branch

Once a proposed change has been introduced, keep your local repository clean by deleting the feature branch. Execute the following command to delete the feature branch.
`$ git branch -d feature/<branch_name>`

If your change was not integrated into the repository, you may receive a warning indicating such occurrence. To forcibly remove the branch replace the `-d` flag with `-D`.
