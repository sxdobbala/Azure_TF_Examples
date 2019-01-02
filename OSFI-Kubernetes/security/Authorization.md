
# Role Based Access Controls (RBAC)

Kubernetes role-based access control (RBAC) lets administrators to exercise fine-grained control over how end users access the Kubernetes API resources running on OSFI Kubernetes cluster. Administrators can use RBAC to dynamically configure permissions for different type of cluster's users and define the kinds of resources with which they can interact.

An administrator can create RBAC permissions that apply to the entire cluster, or to specific namespaces within the cluster. Cluster-wide permissions are useful for limiting certain users' access to specific API resources (such as security policies or Secrets). Namespace-specific permissions are useful if, for example, if there are multiple groups of users operating within a tenant respective namespaces. RBAC can help us ensure that users only have access to cluster resources within their own namespace.

## RBAC - Overview

RBAC permissions can be defined by creating objects from the rbac.authorization.k8s.io API group in your cluster. This can be done using the kubectl command-line interface, or programmatically. 

The RBAC API declares four top-level types of objects:
1.	A Role or ClusterRole object that defines what resource types and operations are allowed for a set of users.
2.	A RoleBinding or ClusterRoleBinding that associates the Role (or ClusterRole) with one or more specific users.

We can consider ClusterRole as global roles and Roles are local roles. A _Role_ can only be used to grant access to resources within a single namespace. A ClusterRole can be used to grant the same permissions as a Role, but because they are cluster-scoped, they can also be used to grant access to:
* cluster-scoped resources (like nodes)
* non-resource endpoints (like “/healthz”)
* namespaced resources (like pods) across all namespaces

RBAC permissions are purely additive--there are no "deny" rules. When structuring RBAC permissions, we should design it in terms of "granting" users access to cluster resources.

A _role binding_ grants the permissions defined in a role to a user or set of users. It holds a list of subjects (users, groups, or service accounts), and a reference to the role being granted. Permissions can be granted within a namespace with a RoleBinding, or cluster-wide with a ClusterRoleBinding.

For complete information on using the Kubernetes API to create the necessary Role, ClusterRole, RoleBinding, and ClusterRoleBinding objects for RBAC, see [Using Role-Based Access Control Authorization](https://kubernetes.io/docs/reference/access-authn-authz/rbac/#default-roles-and-role-bindings) in the Kubernetes documentation.

# OSFI Kubernetes Roles

All Kubernetes clusters install a default set of ClusterRoles, representing common buckets users can be placed in. When the default ClusterRoles aren’t enough, it’s possible to create new roles that define a custom set of permissions. Since ClusterRoles are just regular API resources, they can be expressed as YAML or JSON manifests and applied using kubectl.

## Default Cluster Roles

OSFI Kubernetes platform will be using defaul user-facing cluster roles provided automatically by Kubernetes. They include super-user roles (cluster-admin), roles intended to be granted cluster-wide using ClusterRoleBindings (cluster-status), and roles intended to be granted within particular namespaces using RoleBindings (admin, edit, view).

Default ClusterRole | Default ClusterRoleBinding | Description
------------------- | -------------------------- | -----------
cluster-admin | system:masters group | Allows super-user access to perform any action on any resource. When used in a ClusterRoleBinding, it gives full control over every resource in the cluster and in all namespaces. When used in a RoleBinding, it gives full control over every resource in the rolebinding's namespace, including the namespace itself.
admin | None | Allows admin access, intended to be granted within a namespace using a RoleBinding. If used in a RoleBinding, allows read/write access to most resources in a namespace, including the ability to create roles and rolebindings within the namespace. It does not allow write access to resource quota or to the namespace itself.
edit | None | Allows read/write access to most objects in a namespace. It does not allow viewing or modifying roles or rolebindings.
view | None | Allows read-only access to see most objects in a namespace. It does not allow viewing roles or rolebindings. It does not allow viewing secrets, since those are escalating

### Cluster Admin Role

This Role grand complete control over any resources in the cluster. This role will not be assigned to project admins, developers or regular users in the platform. Only cluster administrators are allowed to have this role.

Description: This will be assigned to groups of cluster administrators with no root permissions on cluster nodes. 

Best Practice: Small number of people that have responsibility to do PoCs, enable capabilities, implement platform architecture best practices across the cluster

### Admin Role

This role is to assign complete control over a namespace. Each tenant will be assigned to one or more namespaces and each namespace would have a default system account and custom service accounts. When a namespace request is placed via OSFI Portal or Cloud Scaffolding, the 'admin' role will be granted to the custom service account and tenant admin group (secure group).

Description: If used in a local binding, an admin user will have rights to view any resource in the project and modify any resource in the project except for quota.
These are the permissions granted by admin role:
* Delete, patch and update the project object (example - pods)
* Modify rolebindings/policybindings (maintain project permissions)
* Manage secrets

Best practice: Small number of people that have responsibility for maintaining project permissions and managing project members

### Edit Role

This role provides the ability to change anything in the project, aside from modifying permissions or deleting the project. Tenant admins or owner of the Tenant ServiceAccount can assign this role to individual ServiceAccounts or group of users

Description: A user that can modify most objects in a project, but does not have the power to view or modify roles or bindings.

Best Practice: Persons responsible for defining the structure and objects that make up the project. This role gives access to secrets, so grant this role only to small group of Developments leads

### View Role

This role provides read only access to most things in a project. 

Description: A user who cannot make any modifications, but can see most objects in a project including secrets. They cannot view or modify roles or bindings.

Best Practice: Use for team members that do not need to make changes to the project, but do need to be able to view pod logs, and the definition of project objects. This role gives access to read secrets, so assign this role to respective users

## Custom Cluster Roles

By providing explicit and auditable security mechanisms, Kubernetes RBAC reduces the security risk of a cluster. The Role and Cluster Role bindings provided out-of-the-box support for the cluster operator to apply [the principle of least privilege](https://en.wikipedia.org/wiki/Principle_of_least_privilege) to every Kubernetes clients. OSFI Kubernetes platform also follow the same best practice and grant only the minimum required access privileges for the task that a user or pod need to carry out.

The default out-of-box roles can be further customized to support specific project needs. The platform support few custom roles to meet specific common needs seen in Optum.

Custom Cluster Role | Custom ClusterRoleBinding | Description
------------------- | ------------------------- | -----------
projectdeploy  | None | Allows read/write access to 'deployment' objects in a namespace
developer | None | Allows read/write access to most objects in a namespace except secrets, roles or rolebindings

### Project Deploy Role

A custom role defined for Service Accounts that need permission to create deployments from CI/CD tools such as Jenkins.

Description: This custom role is defined from default 'edit' role, customize it to allow read/write access only to 'deployment' objects. Each tenant name spaces will be provisioned with a service account with 'projectdeploy' custom role. This service account can be used to configure build pipelines from CI/CD tools.

Best Practice: A service account with responsibility to manage application deployments

### Developer Role

The developer role addresses a concern with preventing access to secrets that are part of the project. There may be teams that desire to restrict access to secrets and control of service accounts to a smaller group, while still allowing developers on the team to be able to maintain most of the object types in the project. The developer role was derived from the edit role for this purpose.  It removed all rules related to secrets, preventing them from being created or viewed. It allows read-only access to service accounts, preventing someone in this role from being able to create new service accounts.
When a namespace request is placed via OSFI Portal or Cloud Scaffolding, the 'developer' role will be granted to the tenant developer group (secure group).

Description: A user that can modify most objects in a project, but does not have the power to create or modify serviceaccounts, roles, bindings and no access to secrets

Best Practice: Persons responsible for defining the structure and objects that make up the project. This is the standard role for most of the project members

# Summary

OSFI Kubernetes will be leveraging Kubernetes RBAC for end user authorization. When a namespace/tenancy request is placed via OSFI Portal or Cloud Scaffolding:

1. One service account will be provisioned with 'admin' cluster role. Owner of the service account is responsible to manage the respective namespaces, create/maintain additional service accounts as needed, add/modify roles and role bindings for namespace (RBAC for tenancy), and maintain secrets

2. Role bindings will be created for 'developer' role for Tenant developer group (secure group created by automated provisioning process) or regular users - this provide access to Kubernetes dashboard for regular users

3. Role binding will be created for 'admin' role for Tenant admins (secure group created by automated provisioning process) - this provide access to Kubernetes dashboard for admin users

Tenant admins or ServiceAccount owner is responsible to create additional service accounts, roles, role-bindings for the tenancy or namespace based on the project RBAC needs
