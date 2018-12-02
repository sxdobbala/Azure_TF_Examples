# Route Table

This folder contains a [Terraform](https://www.terraform.io/) module to deploy a
[Route Table](https://docs.microsoft.com/en-us/azure/virtual-network/tutorial-create-route-table-portal) in [Azure](https://azure.microsoft.com/en-us/).

This module allows users to pass in multiple routes as a list to setup a route table. The `route_list` input accepts a list of maps containing the `name`, `address_prefix`, and `next_hop_type` of each route. (See [example](../../examples/route_table/main.tf) for usage)

## How do you use this module?

This folder defines a [Terraform module](https://www.terraform.io/docs/modules/usage.html), which you can use in your
code by adding a `module` configuration and setting its `source` parameter to URL of this folder:

```hcl
module "route_table" {
  # TODO: update this to the final URL
  # Use version v2.0.0 of the route_table module
  source = "git::https://github.optum.com/CommercialCloud-EAC/azure_resource_group//modules/route_table?ref=v2.0.0"

  # Specify the name of the route table
  name = "my_rt"

  # Specify the rg of the route table
  resource_group_name   = "my_rg"

  # Specify the number of routes in the route_list
  route_count         = 2

  # Specifiy the routes of the route table
  route_list = [
    {
      name           = "route1"
      address_prefix = "10.1.0.0/16"
      next_hop_type  = "vnetlocal"
    },
    {
      name           = "route2"
      address_prefix = "10.3.0.0/16"
      next_hop_type  = "vnetlocal"
    },
  ]

  # ... See variables.tf for the other parameters you can define for the route_table module
}
```

You can find the other parameters in [variables.tf](variables.tf).

Check out the [route_table example](../../examples/route_table) for fully-working sample code.