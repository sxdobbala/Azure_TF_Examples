## 2.0.0 (October 17, 2018)

IMPROVEMENTS:
* Restructured the modules to 2.0 format and standardized modules that don't necessarily have a root module
* Fixed unit tests and updated failure messages to be more meaningful
* Added integration test suites for the following examples: `add_user_to_existing_group`, `create_group_with_policies`, `create_group_with_user_membership`, `create_policy`, `create_role_with_inline_policy`, `create_role_with_managed_policy`, `create_role_with_service_principal`, `create_role_with_user_principal` and updated the test cases for `create_role_with_federated_principal`
* Added [AWS IAM explanatory diagram](https://www.lucidchart.com/documents/view/0692ff06-ee95-40ea-a4f1-0e85ec8acae5) (external - Lucidchart)
* Added Jenkins pipeline integration and automated unit and integration testing for PRs
* Added GitHub `pull request template`
* Added EIS requirements for endorsement

## 1.0.4 (August 10, 2018)

IMPROVEMENTS:
* Added a new feature, support for federated principals, for federated identities to be able to assume roles.

EXAMPLES UPDATE:
* **Create Role with Federated Principal Example:** Added this example to illustrate how a federated identity could assume a role and be able to make use of managed and inline policies attached to the role.

BUG FIXES:
* **Terraform Version Check Failing:** Updated Terraform version check. 

## 1.0.3 (Feb 5, 2018)

IMPROVEMENTS:
* Updated references to use new CommercialCloud-EAC Github Org

## 1.0.2 (Dec 31, 2017)

EXAMPLES UPDATE:
* **Add User to Existing Group Example:** Added example that illustrates how to add an IAM User to an already existing IAM Group

## 1.0.1 (Dec 7, 2017)

BUG FIXES:
* **IAM role:** Fixes "count cannot be computed" issue when calling this module from another module and adding inline policies to a role
* **IAM group:** Fixes "count cannot be computed" issue when calling this module from another module and adding inline policies to a group

## 1.0.0 (Oct 27, 2017)

FEATURES:
* **IAM user :** Creates AWS IAM users
* **IAM role :** Creates AWS IAM roles, allows users to assume the role, assigns policies to roles
* **IAM group :** Creates AWS IAM groups, assigns users to a group, assigns policies to groups
* **IAM policy :** Creates AWS IAM policies

Please see github commit and pull request history for more details.
