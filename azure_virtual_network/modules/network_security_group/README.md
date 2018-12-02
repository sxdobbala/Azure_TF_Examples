# Network Security Group

This folder contains a [Terraform](https://www.terraform.io/) module to deploy a
[Network Security Group](https://docs.microsoft.com/en-us/azure/virtual-network/security-overview) in [Azure](https://azure.microsoft.com/en-us/). 

The module will automatically assign three security rules that block network traffic to each NSG to meet Optum security standards. The assigned rules are listed below:

1. **DenyAllOutboundTraffic :** Denies all outbound traffic. Priority 4096.
2. **DenyAllInboundTraffic :** Denies all inbound traffic. Priority 4095.
3. **AllowInboundAzureLoadBalancer :** Allow inbound access from Azure Load Balancer. Priority 1000.

New access can be added incrementally with security rules created by using the `azurerm_network_security_rule` Terraform Azure provider resource. See examples: [network_security_group](../../examples/network_security_group) [three_tiered_network](../../examples/three_tiered_network)

## How do you use this module?

This folder defines a [Terraform module](https://www.terraform.io/docs/modules/usage.html), which you can use in your
code by adding a `module` configuration and setting its `source` parameter to URL of this folder:

```hcl
module "network_security_group" {
  # TODO: update this to the final URL
  # Use version v2.0.0 of the network_security_group module
  source = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group//modules/network_security_group?ref=v2.0.0"

  # Specify the name of the NSG
  name = "my_nsg"

  # Specify the rg of the NSG
  resource_group_name   = "my_rg"

  # ... See variables.tf for the other parameters you can define for the network_security_group module
}
```

You can find the other parameters in [variables.tf](variables.tf).

Check out the [network_security_group example](../../examples/network_security_group) for fully-working sample code.