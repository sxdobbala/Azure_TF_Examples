# Examples

* Create_AppGateway: 
1) creates a vnet with one subnet allowing inbound http and https traffic.
2) Creates an application gateway with public ip as frontend ip.
3) Sample ssl certificates are uploaded into the app gateway to allow inbound https traffic.

**NOTE:** These examples assume terraform has been installed and Azure access keys have been configured.

#### Run the examples

```
> cd <example folder>

> az login --service-principal -u <YOUR APPLICATION_ID> -p <YOUR CLIENT SECRET> --tenant <YOUR TENANT ID>
> terraform init
> terraform plan
> terraform apply
```

Cleanup the example
```
> terraform destroy
