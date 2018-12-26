# Azure Launchpad 1.3.1 (TBD)

FEATURES:
- Removes the use of the `EISContainerRead` custom role.
- Replaces the `eis_storage_read_only_sp` Service Principals with the `CC_AZU_EIS_READ_SP` Service Principals which will be assigned the `Reader` role. These service principals will be used in the [Azure Storage Container Scan](https://github.optum.com/CommercialCloud-Team/azure_storage_container_scan) process in place of the `eis_storage_read_only_sp` service principals.

# Azure Launchpad 1.3.0 (Dec 17, 2018)

FEATURES:

- Creates a custom role definition in each Launchpad subscription called `EISContainerRead`. This role has actions `Microsoft.Storage/storageAccounts/read` and `Microsoft.Storage/storageAccounts/blobServices/containers/read`.
- Assigns the `eis_storage_read_only_sp` Service Principal the custom `EISContainerRead` role mentioned above in every subscription running Launchpad. This Service Principal is used to automatate the process of checking if subscriptions have any public blog storage. The job that checks for public blob storage can be found [here](https://github.optum.com/CommercialCloud-Team/azure_storage_container_scan).

# Azure Launchpad 1.2.0 (Nov 14, 2018)

FEATURES:

- **Directory Structure:** Moved code that runs on individual subscriptions to a directory called `launchpad_subscription`. This better indicates which code is used for the central management subscription and which code is used for the individual launchpad subscriptions.
- **New policy umbrella initiative:** Created a new policy initiative that contains policies that are deemed more critical. This allows policies to be placed into the corresponding initiative based on their criticality.
- **New policies:** New policies for hitrust networking were added:
  - **Policy Umbrella Initiative:**
    - **Audit allow port ranges inbound:** Audits any inbound network security rules that allow port ranges, i.e. 3000-4000.
  - **Critical Policy Umbrella Initiative:**
    - **Audit allow rdp inbound:** Audits any inbound network security rules that allow rdp from the internet.
    - **Audit allow ssh inbound:** Audits any inbound network security rules that allow ssh from the internet.
    - **Audit allow sql ports inbound:** Audits any inbound network security rules that allow sql ports (currently 1433 and 3306) from the internet.
    - **Audit allow all ports inbound:** Audits any inbound network security rules that allow all ports (\*) from the internet.

# Azure Launchpad 1.1.2 (Nov 5, 2018)

FEATURES:

- **Policy Update :** Updated the description and display name for the `audit_storage_firewall` policy. It now states that this policy checks for a firewall on a storage account, which was the policy's original intent.

# Azure Launchpad 1.1.1 (Oct 25, 2018)

FEATURES:

- **Policy Update :** Replaced "subnets_require_nsg" policy with "non_gateway_subnets_require_nsg". The policy will now require all subnets to have an NSG attached excluding subnets with the name of "GatewaySubnet". Azure utilizes subnets with this naming convention for virtual network gateways and does not allow an NSG to be attached to them.
- **Directory Structure:** Moved central management resources (event hub for logs and splunk forwarder vm) under a single `central_management_subscription` directory. The Terraform source for these resources used to live in [this](https://github.optum.com/CommercialCloud-EAC/azure_tenant_launchpad_deprecated) repo but have been moved to the `central_management_subscription` directory.
- **Central Management module change :** Added locks on the resource groups for splunk and event hub. This still allows Terraform to manage the resources, but prevents accidental deletion via the portal. This does not currently prevent changes via the portal - just prevents deletion.

# Azure Launchpad 1.1.0 (May 10, 2018)

FEATURES:

- **Policy Update :** Added a policy to audit subnets to make sure that they have an NSG

# Azure Launchpad 1.0.2 (Apr 10 2018)

BUGFIX:

- AzureRM version set to a minimum instead of an absolute.

# Azure Launchpad 1.0.1 (Mar 6 2018)

FEATURES:

- **Jenkinsfile :** Added a Jenkinsfile that can be used to run azure_bootstrap through Jenkins.

# Azure Launchpad 1.0.0 (Feb 22 2018)

FEATURES:

- **Remote state file storeage :** Creates an storage account and container to be used for storing the Terraform state file remotely

Please see github commit and pull request history for more details.
