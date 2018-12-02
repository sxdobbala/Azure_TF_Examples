# Subnet

This folder contains a [Terraform](https://www.terraform.io/) module to deploy a
[Subnet](https://docs.microsoft.com/en-us/azure/virtual-network) in [Azure](https://azure.microsoft.com/en-us/).

Subnets will automatically have an NSG created by the `network_security_group` module to meet Optum security standards. View the [network_security_group](../network_security_group) module to learn more about the attached default NSG.

## How do you use this module?

This folder defines a [Terraform module](https://www.terraform.io/docs/modules/usage.html), which you can use in your
code by adding a `module` configuration and setting its `source` parameter to URL of this folder:

```hcl
module "subnet" {
  # TODO: update this to the final URL
  # Use version v2.0.0 of the subnet module
  source = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group//modules/subnet?ref=v2.0.0"

  # Specify the name of the Subnet
  name = "my_subnet"

  # Specify the rg of the Subnet
  resource_group_name   = "my_rg"

  # Specify the vnet this subnet is created for
  virtual_network_name   = "my_vnet"

  # Specify the address prefix of the Subnet
  address_prefix   = "10.0.1.0/24"

  # Specify the default NSG created for the Subnet
  network_security_group_name   = "my_nsg"

  # ... See variables.tf for the other parameters you can define for the subnet module
}
```

You can find the other parameters in [variables.tf](variables.tf).

Check out the [virtual_network example](../../examples/virtual_network) for fully-working sample code.