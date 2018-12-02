# azure_virtual_network 

## Contents

- [Overview](#overview)
- [Virtual Network Features](#virtual-network-features)
- [Terraform Modules](#terraform-modules)
  - [Virtual Network Inputs](#virtual-network-inputs)
  - [Virtual Network Outputs](#virtual-network-outputs)
  - [Subnet Inputs](#subnet-inputs)
  - [Subnet Outputs](#subnet-outputs)
  - [Network Security Group Inputs](#network-security-group-inputs)
  - [Network Security Group Outputs](#network-security-group-outputs)
  - [Public IP Inputs](#public-ip-inputs)
  - [Public IP Outputs](#public-ip-outputs)
  - [Route Table Inputs](#route-table-inputs)
  - [Route Table Outputs](#route-table-outputs)
  - [Network Interface Inputs](#network-interface-inputs)
  - [Network Interface Outputs](#network-interface-outputs)
- [Testing](#testing)
- [Examples](#examples)
- [Innersource Guidelines](#innersource-guidelines)
- [EIS Security Endorsement](#eis-security-endorsement)

## Overview

Azure Virtual Network (VNet) is a representation of your own network in the cloud. It is a logical isolation of the Azure cloud dedicated to your subscription. Please refer to [this](https://docs.microsoft.com/en-us/azure/virtual-network/virtual-networks-overview) article for more details.

This repository describes the setup and implementation of Azure Virtual Networks and related sub components using Terraform.

## Virtual Network Features

### Subnet `[/modules/subnet]`

Azure virtual networks can be segmented into one or more subnets and allocated a portion of the virtual network's address space.

### Network Security Group `[/modules/network_security_group]`

A Network Security Group (NSG) contains a list of security rules that allow or deny network traffic to resources connected to Azure Virtual Networks.

### Public IP `[/modules/public_ip/<dynamic_with_domain, dynamic_without_domain, static_with_domain, static_without_domain>]`

A public IP address is a dynamic or static IP address that you can assign to virtual machines, load balancers, and virtual network gateways to communicate with the Internet.

For an explanation of the multiple public ip modules, see the [Public IP Inputs](#public-ip-inputs) section below.

### Route Table `[/modules/route_table]`

A route table allows you to create your own routes for traffic between subnets within a virtual network.

### Network Interface `[/modules/network_interface/<primary_ip_configuration, primary_and_secondary_ip_configuration>]`

A network interface allows an Azure VM to communicate with Azure, on-prem resources, as well as the internet.

For an explanation of the multiple network interface modules, see the [Network Interface Inputs](#network-interface-inputs) section below.

## Terraform Modules

### Virtual Network Inputs

| Property | Description | Default |
| --- | --- | --- |
| name | The name of the virtual network. Changing this forces a new resource to be created. | `virtual_network` |
| namespace | Namespace for the terraform run. | |
| resource_group_name | The name of the resource group in which to create the virtual network. | |
| address_space | The address space that is used the virtual network. You can supply more than one address space. Changing this forces a new resource to be created. | `["10.0.0.0/16"]` |
| location | The location/region where the virtual network is created. Changing this forces a new resource to be created. | `centralus` |
| dns_servers | List of IP addresses of DNS servers. | |
| tags | A mapping of tags to assign to the resource. | |
| global_tags | Map of tags to apply to all resources that have tags parameters. | |

### Virtual Network Outputs

| Output | Description |
| --- | --- |
| id | The virtual NetworkConfiguration ID. | 
| name | The name of the virtual network. |
| resource_group_name | The name of the resource group in which to create the virtual network. |
| location | The location/region where the virtual network is created. |
| address_space | The address space that is used the virtual network. |

### Subnet Inputs 

**NOTE:** All subnet's will have a Network Security Group attached by default to meet security policy standards. If a Network Security Group is not associated to a subnet or network interface, all traffic is allowed inbound to or outbound from the subnet or network interface, as mentioned [here](https://docs.microsoft.com/en-us/azure/virtual-network/security-overview#default-security-rules). To see more details about the default NSG, view the [Network Security Group Inputs](#network-security-group-inputs) section.

| Property | Description | Default |
| --- | --- | --- |
| name | The name of the subnet. Changing this forces a new resource to be created. | `subnet` |
| namespace | Namespace for the terraform run. | |
| resource_group_name | The name of the resource group in which to create the subnet. Changing this forces a new resource to be created. | |
| virtual_network_name | The name of the virtual network to which to attach the subnet. Changing this forces a new resource to be created. | |
| address_prefix | The address prefix to use for the subnet. | |
| route_table_id | The ID of the Route Table to associate with the subnet. | |
| service_endpoints | The list of Service endpoints to associate with the subnet. Possible values can be viewed [here](https://docs.microsoft.com/en-us/azure/virtual-network/virtual-network-service-endpoints-overview) | |
| network_security_group_name | The name of the network security group that is attached to the subnet. | |
| network_security_group_location | Specifies the supported Azure location where the subnet's network security group exists. Changing this forces a new resource to be created. | `centralus` |

### Subnet Outputs

| Output | Description |
| --- | --- |
| id | The subnet ID. |
| ip_configurations | The collection of IP Configurations with IPs within this subnet. |
| name | The name of the subnet. |
| resource_group_name | The name of the resource group in which the subnet is created in. |
| virtual_network_name | The name of the virtual network in which the subnet is created in. |
| address_prefix | The address prefix for the subnet. |
| network_security_group_id | The subnet's Network Security Group ID. |
| network_security_group_name | The subnet's Network Security Group name. |
| event_hub_namespace_id | The default event hub namespace for diagnostic logging. |

### Network Security Group Inputs

**NOTE:** All Network Security Groups created will automatically have three security rules listed below

1. **DenyAllOutboundTraffic :** Denies all outbound traffic. Priority 4096.
2. **DenyAllInboundTraffic :** Denies all inbound traffic. Priority 4095.
3. **AllowInboundAzureLoadBalancer :** Allow inbound access from Azure Load Balancer. Priority 1000.

 These rules have the lowest priorities so users can incrementally allow access as needed. New security rules can be added by using the `azurerm_network_security_rule` Terraform Azure provider resource. See examples: [network_security_group](./examples/network_security_group) [three_tiered_network](./examples/three_tiered_network)

**NOTE:** The NSG will also have NetworkSecurityGroupEvent and NetworkSecurityGroupRuleCounter diagnostic logs enabled and setup to stream to a event hub living on a central subscription in the user's tenant. Currently, we only support Central US location and an error will occur when attempting to setup NSGs in other regions where diagnostic logs will not be enabled.

To set up diagnositc logging on NSG's, permissions are required to your tenant's central subscription's event hub. Therefore, we reccomend provisioning your resources with the service principal that was provided to launchpad.

| Property | Description | Default |
| --- | --- | --- |
| name | Specifies the name of the network security group. Changing this forces a new resource to be created. | |
| namespace | Namespace for the terraform run. | |
| resource_group_name | The name of the resource group in which to create the network security group. Changing this forces a new resource to be created. | | 
| location | Specifies the supported Azure location where the resource exists. Changing this forces a new resource to be created. | `centralus` |
| tags | A mapping of tags to assign to the resource. | |
| global_tags | Map of tags to apply to all resources that have tags parameters. | | 

### Network Security Group Outputs

| Output | Description |
| --- | --- |
| id | The Network Security Group ID. |
| name | The Network Security Group name. |
| event_hub_namespace_id | The default event hub namespace for diagnostic logging. |

### Public IP Inputs

**NOTE:** There are multiple versions of the Public IP module due to how Azure handles public ips. If using dynamic address allocation, the Azure API does not return an `ip_address` field since dynamic public ip addresses are not allocated until they are assigned to a resource and that resource has started (i.e. VM). Therefore, Terraform cannot retrieve this output so separate modules have to be created to properly retrieve that output. There are also separate modules for public ips with domain name labels since that [property](https://www.terraform.io/docs/providers/azurerm/r/public_ip.html#domain_name_label) cannot be an empty string.

**NOTE:** The `Standard` sku is limited to static ip address allocation. See [this](https://docs.microsoft.com/en-us/azure/virtual-network/virtual-network-ip-addresses-overview-arm#sku) article for more details.

**The following inputs are common to all Public IP modules.**

| Property | Description | Default |
| --- | --- | --- |
| name | Specifies the name of the Public IP resource . Changing this forces a new resource to be created. | `public_ip` |
| namespace | Namespace for the terraform run. | |
| resource_group_name | The name of the resource group in which to create the public ip. | |
| location | Location defines which availability zone the resource should be created in. | `centralus` |
| sku | The SKU of the Public IP. Accepted values are Basic and Standard. | `Basic` |
| idle_timeout_in_minutes | Specifies the timeout for the TCP idle connection. The value can be set between 4 and 30 minutes. | `4` |
| reverse_fqdn | A fully qualified domain name that resolves to this public IP address. If the reverseFqdn is specified, then a PTR DNS record is created pointing from the IP address in the in-addr.arpa domain to the reverse FQDN. | |
| tags | A mapping of tags to assign to the resource. | |
| global_tags | Map of tags to apply to all resources that have tags parameters. | |

**Following are the inputs specific to the modules.**

### dynamic_with_domain and static_with_domain

| Property | Description | Default |
| --- | --- | --- |
| domain_name_label | Label for the Domain Name. Will be used to make up the FQDN. If a domain name label is specified, a DNS record is created for the public IP in the Microsoft Azure DNS system. | | 

### Public IP Outputs

**The following outputs are common to all Public IP modules.**

| Output | Description |
| --- | --- |
| id | The Public IP ID. |
| name | The Public IP name. |

**The following are the outputs specific to the modules.**

### dynamic_with_domain and static_with_domain 

| Output | Description |
| --- | --- |
| fqdn | Fully qualified domain name of the A DNS record associated with the public IP. This is the concatenation of the domainNameLabel and the regionalized DNS zone. |

### static_with_domain and static_without_domain 

| Output | Description |
| --- | --- |
| ip_address | The IP address value that was allocated. |

### Route Table Inputs

**NOTE:** The route_list inputs accepts a list of maps containing the name, address_prefix, and next_hop_type of each route. (See [example](/examples/route_table/main.tf) for usage)

| Property | Description | Default |
| --- | --- | --- |
| name | Name of the route table to be created. | |
| namespace | Name space for all Azure route table resources. | |
| resource_group_name | The name of the resource group that this route table should be a part of. | |
| location | The location / zone in which this route table will be located. | `Central US` |
| route_count | Number of routes to create. This is in place to circumvent a terraform shortcoming with passing counts to modules. | `0` |
| route_list | List of user defined routes that will be contained in the route table. | |
| tags | Map of tags to apply to this route table. | |
| global_tags | Map of tags to apply to all resources that have tags parameters. | |

### Route Table Outputs

| Output | Description |
| --- | --- |
| id | The Route Table ID. |
| name | The Route Table name. |
| subnets | The collection of Subnets associated with this route table. |
| route_ids | The collection of ids associated with the routes that were created. |

### Network Interface Inputs

**NOTE:** Network Interfaces support one or more ip configurations. We provide modules with the functionality to create a network interface with just one ip configuration or setup both a primary and a secondary ip configuration.

**The following inputs are common to all Network Interface modules.**

| Property | Description | Default |
| --- | --- | --- |
| name | The name of the network interface. Changing this forces a new resource to be created. | `default` |
| namespace | Name space for all azurerm_network_interface resources. | |
| resource_group_name | The name of the resource group that this network interface should be a part of. | |
| location | Location defines which availability zone the resource should be created in. | `centralus` |
| network_security_group_id | The ID of the Network Security Group to associate with the network interface. | |
| internal_dns_name_label | Relative DNS name for this NIC used for internal communications between VMs in the same VNet. When using default value, a random name with the format of "%s-%s" will be generated. | `default` |
| enable_ip_forwarding | Enables IP Forwarding on the NIC. | `false` |
| enable_accelerated_networking | Enables Azure Accelerated Networking using SR-IOV. Only certain VM instance sizes are supported. | `false` |
| dns_servers | List of DNS servers IP addresses to use for this NIC, overrides the VNet-level server list. | |
| tags | Tags for the network interface. | |
| ip_configuration_name | User-defined name of the IP. | |
| ip_configuration_subnet_id | Reference to a subnet in which this NIC has been created. | |
| ip_configuration_private_ip_address | Static IP Address. | |
| ip_configuration_private_ip_address_allocation | Defines how a private IP address is assigned. | |
| ip_configuration_public_ip_address_id | Reference to a Public IP Address to associate with this NIC. | |
| ip_configuration_load_balancer_backend_address_pools_ids | List of Load Balancer Backend Address Pool IDs references to which this NIC belongs. | |
| ip_configuration_load_balancer_inbound_nat_rules_ids | List of Load Balancer Inbound Nat Rules IDs involving this NIC. | |

**The following are the inputs specific to the modules.**

### primary_and_secondary_ip_configuration

| Property | Description | Default |
| --- | --- | --- |
| secondary_ip_configuration_name | User-defined name of the IP. | |
| secondary_ip_configuration_subnet_id | Reference to a subnet in which this NIC has been created. | |
| secondary_ip_configuration_private_ip_address | Static IP Address. | |
| secondary_ip_configuration_private_ip_address_allocation | Defines how a private IP address is assigned. | |
| secondary_ip_configuration_public_ip_address_id | Reference to a Public IP Address to associate with this NIC. | |
| secondary_ip_configuration_load_balancer_backend_address_pools_ids | List of Load Balancer Backend Address Pool IDs references to which this NIC belongs. | |
| secondary_ip_configuration_load_balancer_inbound_nat_rules_ids | List of Load Balancer Inbound Nat Rules IDs involving this NIC. | |

### Network Interface Outputs

**The following outputs are common to all Network Interface modules.**

| Output | Description |
| --- | --- |
| id | The Virtual Network Interface ID. |
| private_ip_address | The private ip address of the network interface. |

## Testing

For unit tests, ensure that ruby and bundler have been installed, install the required gems and run the tests:

```
> ruby --version
> bundle install
> rspec
```

All tests should pass.

## Integration tests

Integration tests use pytest. See the [README](/tests/README.md) in the `tests/` directory for more details.

## Examples

Examples are included in this repository, for more information see the examples folder.

Before running the examples, ensure the following environment variables from creating your service principal are exported:

```
export ARM_SUBSCRIPTION_ID="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
export ARM_CLIENT_ID="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
export ARM_CLIENT_SECRET="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
export ARM_TENANT_ID="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
```

**NOTE:** If using Windows, please refer to this [Microsoft documentation page](https://msdn.microsoft.com/en-us/library/windows/desktop/ms682653(v=vs.85).aspx) to setup your environment variables.

## Innersource Guidelines

All contributions to the CommercialCloud repositories must follow the guidelines outlined in the following guides:
* [Commercial Cloud Terraform Developers Guide](https://github.optum.com/CommercialCloud-EAC/welcome/tree/master/DEVELOPER_GUIDE.md)
* [Contributing](https://github.optum.com/CommercialCloud-EAC/welcome/tree/master/CONTRIBUTING.md)
* [Contributor Code of Conduct](https://github.optum.com/CommercialCloud-EAC/welcome/tree/master/CODE_OF_CONDUCT.md)

## EIS Security Endorsement

This module helps to enforce the following EIS mandates:

| Req ID | Requirement | How the module addresses the requirement |
| --- | --- | --- |
| **3.1** | *Information System Boundaries*: A native collection of solutions shall be selected to provide information system boundaries for information services, users, and information systems to prevent information leakage and unauthorized access. | All traffic to a subnet is restricted, access is incrementally added with network security rules (equivalent of firewall rules) to the subnet's network security group |
|**8.1** | *Platform and Software Levels*: Cloud native functionality must be enabled to capture security events. | Network security groups automatically have diagnostic logging enabled. (Note: Currently, automatic diagnostic logging is only supported for Central US and an error will occur when attempting to setup NSGs in other regions where diagnostic logs will not be enabled.)|