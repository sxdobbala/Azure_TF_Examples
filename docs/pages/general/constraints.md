---
layout: main
title: Team and App Name Constraints
permalink: /general/constraints
---
These are constraints that are imposed on Team and App names by the Scaffolding API to meet the requirements of systems that are interacted with:

||Team Name|App Name|
|:---|:---:|:---:|
|Starting with hyphen|N|N|
|Ending with hyphen|N|N|
|Starting with number|Y|N|
|Ending with number|Y|Y|
|Hyphen in between|Y|Y|
|Number in between|Y|Y|
|Hyphen placed consecutively|N|N|
|Numbers placed consecutively|Y|Y|

## Application Name Criteria

The Application Name must meet the following criteria:
- No less than 3 and no more than 21 characters.
- No spaces.
- Cannot start or end with hyphen.
- Cannot start with number
- Can contain a mix of letters, numbers and hyphen, but no special characters (ex. !, $, &) or consecutive hyphen.

## Team Name Criteria

The Team Name must meet the following criteria:
- No less than 6 and no more than 20 characters.
- Name must be in all lowercase characters.
- No spaces.
- Cannot start or end with hyphen.
- Can contain a mix of letters, numbers and hyphen, but no special characters (ex. !, $, &) or consecutive hyphen.
