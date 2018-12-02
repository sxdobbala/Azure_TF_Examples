# Examples

## Directions

Ensure the required environment variables are exported as specified in the README.

Then run the following commands:

```
> cd <example folder>
> terraform init
> terraform plan
> terraform apply
```

The resources can then be destroyed by running:

```
> terraform destroy
```

## Descriptions

| Example Name | Description |
| --- | --- |
| network_interface_with_primary_and_secondary_ip_configuration | Creates a network interface with a primary and secondary ip configuration. |
| network_interface_with_primary_ip_configuration | Creates a simple network interface with one primary ip configuration. |
| network_security_group | Creates a simple network security group. Adds a single network security rule to the network security group. |
| public_ip | Creates a public ip with dynamic address allocation with a label for the domain name |
| route_table | Creates a route table with two routes. |
| three_tiered_network | Creates a virtual network with a web, business, and data tier. |
| virtual_network | Creates a simple virtual network with one subnet. Adds a single network security rule to the subnet's network security group. |

