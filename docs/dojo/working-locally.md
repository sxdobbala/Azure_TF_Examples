---
title: Dojo Content - Working Locally
description: Setup your machine to allow for contributing content locally.
services: dojo
ms.service: dojo
ms.subservice: content
ms.topic: how-to
ms.reviewers: bashey,fcatacut,ksadinen
ms.author: bashey
author: bashey
ms.date: 06/11/2021
---

# Overview

For small, grammar or spelling level corrections the easiest process to contribute the change directly on the [GitHub repository](https://github.optum.com/dojo360/dojo-docs). However, for larger changes involving multiple content, hub, or landing pages, working locally can provide a substantial productivity boost. This guide will walk you through how to setup your local workstation to support contributing content back to the Dojo.

## Software Installs

The software installs below will greatly accelerate your editing process with the Dojo.

### 1. Sign-up for Optum GitHub

Using [Secure](https://atlas.uhg.com/article/KB0091422) add your MS user account to the **github_users** group.

### 2. Install Visual Studio Code

Using the [App Store](https://optum.service-now.com/euts_intake?id=euts_appstore_app_details&appKeyId=27134) install the latest version of Visual Studio Code (VSCode).

### 3. Install the Doc Authoring Pack Extension

Install [Microsoft's Doc Authoring Pack](https://marketplace.visualstudio.com/items?itemName=docsmsft.docs-authoring-pack) via the Extensions menu.

### 4. Install Git

If not already done so, please install [Git](https://optum.service-now.com/euts_intake?id=euts_appstore_app_details&appKeyId=24196).

> **Note:**
> If you have never used Git it is highly recommended that you complete the [beginner tutorials](https://guides.github.com/activities/hello-world/). The manual for Git is also available [online](https://git-scm.com/docs/gittutorial).

## Working Locally

### 1. Fork Dojo-Docs

1. In the GiHub user interface navigate to the [Dojo Docs](https://github.optum.com/dojo360/dojo-docs) repository.
1. At the top right corner click **Fork**.
1. In the light window that pops up select your **username**. Doing so will create a copy under your user in GitHub. GitHub should redirect you to the newly created repository once it completes the Fork.
1. In the new Fork click the **Clone or download** button and copy the HTTPS URL out of the pop-up window. You will use this URL in the next step.

### 2. Clone Locally

In a Command (Windows) or Terminal (Mac) window, do the following:

1. Type and execute (by pressing Enter)

    ```powershell
    mkdir c:\src
    ```
## powershell is not installed on my local terminal

2. Change to the new directory:

    ```powershell
    cd c:\src
    ```

3. Clone the Fork locally:

    ```powershell
    git clone <paste the URL from 1.4>
    ```
  
### 3. Open and Edit in VSCode

1. Now, open VSCode and click **Open Folder**.
1. Navigate to and Open the **src/dojo-docs** directory.
1. Proceed to edit the content as needed.

### 4. Commit and Push Local Changes

1. In VSCode, select **Terminal** from the Menu and click **New Terminal**.
1. In the terminal window type and enter:

    ```bash
    git add --all
    ```

3. Then enter the following:

    ```bash
    git commit -m "<enter an appropriate message documenting the change>"
    ```

4. Finally:

    ```bash
    git push
    ```

### 5. Submit a Pull Request

Navigate back to the [Dojo Docs](https://github.optum.com/dojo360/dojo-docs) repository in GitHub and issue a Pull Request using the fork under your user account as the merge base.

## Reference

- [Pull Request guide](https://docs.github.com/en/github/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request) from GitHub.
