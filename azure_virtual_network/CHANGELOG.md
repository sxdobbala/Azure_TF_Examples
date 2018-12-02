## 2.0.0-beta2 (Aug 13, 2018)

* **NSG Default Diagnostic Logging :** NSGs will now be created with diagnostic logs enabled and pointed to a central event hub

## 2.0.0-beta1 (Jul 23, 2018)

FEATURES:
* **Module Version Tagging :** Added Terraform module version tagging to vnet, nic, nsg, public ip, route table modules.
* **Subnet Default NSGs :** Subnet module will now create a default NSG with deny all rules for each subnet.
* **Azure Load Balancer Security Rule :** NSGs will now be created with a default security rule to allow inbound access from Azure Load Balancer.

CHANGES:
* Updated Jenkinsfile to use new pipeline that will run python integration tests.
* Added integration tests for three tiered network example.
* Breaking Change: Removed ability to add security rules using the `network_security_group` module. The `azurerm_network_security_rule` resource will be used to create rules. This allows for ease of use of the security rule resources optional inputs.
* Breaking Change: Removed ability to create multiple subnets using the `subnet` module in order to create an individual NSG with each subnet.
* Breaking Change: Refactored `network_interface` module into two submodules.
* Breaking Change: Refactored `public_ip` module into four submodules.
* Breaking Change: Updated Module File Structure moving `terraform_module` to root module.
* Breaking Change: Updated variable naming conventions to match terraform provider.

## 1.0.9 (Jul 2, 2018)

Fix: Created default for internal_dns_name_label, as this was causing issues depending on what provider version was used.  Now it should be happy with any version as there will be a sane default string.

## 1.0.8 (May 1, 2018)

Fix: Update version number of assert equals so that windows users can utilize this module.

## 1.0.7 (Apr 26, 2018)

Fix: removed the `public_ip_zones` argument for the public_ip resource since it is still in preview and requires version 1.3.0 of the azurerm provider.

## 1.0.6 (Apr 18, 2018)

Features:

* **Public IP :** Adds the ability to create Public IPs.

Please see github commit and pull request history for more details.

## 1.0.5 (Apr 13, 2018)

Features:
* **Three Tier Network Example :** Updates the Three Tier Network example with additional security rules.
* **Network Security Groups :** Automatically adds rules to deny all inbound and outbound traffic. Add rules to allow access as necessary.

## 1.0.4 (Apr 12, 2018)

Features:
* **Route Tables :** Adds the ability to create Route Tables with routes in them.

## 1.0.3 (Apr 9, 2018)

Features:
* **Network Interface :** Adds the ability to create Network Interfaces.

Please see github commit and pull request history for more details.

## 1.0.2 (Mar 30, 2018)

FEATURES:
* **Network Security Groups :** Adds the ability to create Network Security Groups and create rules for these groups. A subnet can then choose which Network Security Group to be associated with.

Please see github commit and pull request history for more details.

## 1.0.1 (Mar 27, 2018)

FEATURES:
* **Subnets in Virtual_Network :** Adds the ability to create a subnet as part of the virtual network creation process
* **Subnet Sub-Module :** Adds the ability to create a subnet via a standalone unit of code 

Please see github commit and pull request history along with the examples folder for more details.

## 1.0.0 (Feb 15 2018)

FEATURES:
* **Virtual Network Creation :** Creates a virtual network to be used for isolating azure resources within the grature azure environment
Please see github commit and pull request history for more details.
