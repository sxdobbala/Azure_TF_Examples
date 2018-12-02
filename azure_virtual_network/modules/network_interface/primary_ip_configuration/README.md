# Network Interface (Primary Ip Configuration)

This folder contains a [Terraform](https://www.terraform.io/) module to deploy a
[Network Interface](https://docs.microsoft.com/en-us/azure/virtual-network/virtual-network-network-interface) in [Azure](https://azure.microsoft.com/en-us/).

This module allows you to create NIC with a single primary ip configuration.

## How do you use this module?

This folder defines a [Terraform module](https://www.terraform.io/docs/modules/usage.html), which you can use in your
code by adding a `module` configuration and setting its `source` parameter to URL of this folder:

```hcl
module "network_interface" {
  # TODO: update this to the final URL
  # Use version v2.0.0 of the network_interface module
  source = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group//modules/network_interface/primary_ip_configuration?ref=v2.0.0"

  # Specify the name of the NIC
  name = "my_nic"

  # Specify the rg of the NIC
  resource_group_name   = "my_rg"

  # Specifiy the primary ip configuration
  ip_configuration_name                          = "primary_config"
  ip_configuration_subnet_id                     = "/subscription/123/..."
  ip_configuration_private_ip_address_allocation = "Dynamic"
  ip_configuration_public_ip_address_id          = "/subscription/123/..."

  # ... See variables.tf for the other parameters you can define for the network_interface module
}
```

You can find the other parameters in [variables.tf](../variables.tf).

Check out the [primary_ip_configuration example](../../../examples/network_interface_with_primary_ip_configuration) for fully-working sample code.