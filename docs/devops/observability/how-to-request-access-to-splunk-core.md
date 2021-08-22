---
title: How to Request Access to Splunk Core
description: Learn how to gain access to Splunk Core.
ms.assetid: 8cb10d88-d57b-4ee9-921c-ec383ebff1a4
ms.topic: how-to
ms.date: 06/11/2021
ms.custom: ""
ms.services: "observability"
ms.author: "mnegron"
---

# Overview

Splunk is accessible via UHG's Single-Sign-On (SSO) function. In order to login to Splunk you must be a member of an Active Directory Global Group specific to the product(s) you need access to.

### Steps to request AD Group Membership

1. Navigate to [Secure](https://secure.uhc.com).
1. Select "Add Group Membership".
1. Enter the AD Group into the "Search groups by group name:" field.
1. Select the group from the "Available Groups" list and press the Green Arrow to move the selected group to the "Selected Groups" list. Repeat this action for each AD Group you need access to in Splunk.
1. Select "Next".
1. Select "Permanent" in the "Access Expires" field.
1. Add a reason for the requested access in the "Description of why access is required:" field.
1. Select "Submit".

## Role Types in Splunk Core

*What is the difference between "privilege" roles (i.e., user, power, admin) versus "index access" roles (i.e., products, such as ecac, efr, iedi, etc.)?*

Users are assigned to roles. A role contains a set of capabilities. Capabilities specify what actions are available to roles. Splunk Enterprise associates capabilities with specific roles. The "authorize.conf" configuration file provides a list of the user actions that you can use to configure roles. For example, capabilities determine whether someone with a particular role is allowed to add inputs or edit saved searches. The various capabilities are listed in "About defining roles with capabilities" in the Securing Splunk Enterprise manual. By default, Splunk Enterprise comes with the following roles predefined:

**admin** - this role has the most capabilities assigned to it.

**power** - this role can edit all shared objects (saved searches, etc.) and alerts, tag events, and other similar tasks.

**user** - this role can create and edit its own saved searches, run searches, edit its own preferences, create and edit event types, and other similar tasks.

**can_delete** - This role allows the user to delete by keyword. This capability is necessary when using the delete search operator.

**Note** Do not edit the predefined roles. Instead, create custom roles that inherit from the built-in roles, and modify the custom roles as required.

For detailed information on roles and how to assign users to roles, see the chapter "Users and role-based access control" in the Securing Splunk Enterprise manual. [About roles](https://docs.splunk.com/Documentation/Splunk/7.3.2/Admin/Aboutusersandroles).

## Active Directory Groups in Splunk Core

AD Groups are managed in the "authentication.conf" file located here: [_modules/search-head/files/configure_sh/system/local/authentication.conf](https://github.optum.com/O360Delivery/splunk-search-head-configuration/blob/master/_modules/search-head/files/configure_sh/system/local/authentication.conf).

HEC Token Stanza Template:

```bash
[http://TOKEN_NAME_FROM_REQUEST]
disabled = 0
index = [INDEX_NAME_FROM_REQUEST]
indexes = [list of INDEX_NAME_FROM_REQUEST]
sourcetype = [SOURCE_TYPE_FROM_REQUEST]
token = [GENERATED]
```

## HEC Token Definitions

**\[http://TOKEN_NAME_FROM_REQUEST]** - The name to be given to the index, supplied by the requester. Best practice is that this token name matches the index name.

**disabled** - The on/off status of this HEC token. Unless purposefully disabling a no longer used HEC Token this should be set to 1.

**index** - The name of the main index that this HEC Token will be related to, supplied by the requester.

**indexes** - A list of indexes that this data may be sent to if more than one is provided, supplied by the requester (not required)

**sourcetype** - the format of data being transmitted to the HEC Token and index (e.g. _json, cisco:ios, iis, log4j, log4Net, etc.), supplied by the requester (not required)

**token** - a generated GUID, to generate this GUID use the GUID plugin for Visual Studio Code

## How Roles are associated to Indexes in Splunk

Roles are managed in the "authorize.conf" file located here: [_modules/search-head/files/configure_sh/system/local/authorize.conf](https://github.optum.com/O360Delivery/splunk-search-head-configuration/blob/master/_modules/search-head/files/configure_sh/system/local/authorize.conf).

### Role Authorization Stanza Template

```bash
[role_<roleName>]
importRoles = user
srchIndexesAllowed = <semicolon-separated list>
srchIndexesDefault = <semicolon-separated list>
srchMaxTime = 0
```

### Role Authorization Definitions

The **&lt;roleName&gt;** in the stanza header is the name you want to give your role. For example: security, compliance, ninja. ***Role names must use lowercase characters only. They cannot contain spaces, colons, semicolons, or forward slashes.***

**importRoles** = A list of other roles and their associated capabilities that Splunk software should import. Importing other roles also imports the other aspects of that role, such as allowed indexes to search. By default, a role imports no other roles. Default for our implementation is "user".

**srchIndexesAllowed** = A list of indexes that this role is allowed to search. Follows the same wildcarding semantics as the 'srchIndexesDefault' setting. If you make any changes in the "Indexes" Settings panel for a role in Splunk Web, those values take precedence, and any wildcards you specify in this setting are lost. No default.

**srchIndexesDefault** = A list of indexes to search when no index is specified. These indexes can be wild-carded ("*"), with the exception that "*" does not match internal indexes. To match internal indexes, start with an underscore ("_"). All internal indexes are represented by "_*". The wildcard character "*" is limited to match either all the non-internal indexes or all the internal indexes, but not both at once. If you make any changes in the "Indexes searched by default" Settings panel for a role in Splunk Web, those values take precedence, and any wildcards you specify in this setting are lost. No default.

**srchMaxTime** = The maximum amount of time that search jobs from specific users with this role are allowed to run.  After a search runs for this amount of time, it auto-finalizes. If the role inherits from other roles, the value of the 'srchMaxTime' setting is specified in the included roles. This maximum value does not apply to real-time searches. Examples: 1h, 10m, 2hours, 2h, 2hrs, 100s. Default: 100days, but for our implementation we set the value to "0" (zero).

### Current Role Mapping (AD Groups)

The Role mapping is a key pair of &lt;roleName&gt; to &lt;AD Group&gt;.

```bash
[roleMap_SAML]
admin = optum_provider_splunk_admin
caseadviser = optum_provider_splunk_caseadviser
ecac = optum_provider_splunk_ecac
enlp = optum_provider_splunk_enlp
efr = optum_provider_splunk_efr
iedi = optum_provider_splunk_iedi
occ = optum_provider_splunk_occ
odx = optum_provider_splunk_odx
power = optum_provider_splunk_power
rws = optum_provider_splunk_rws
user = optum_provider_splunk_user
```
