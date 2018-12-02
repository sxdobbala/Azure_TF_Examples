## MySQL SaaS, Apache, Python voting app

This example builds an Ubuntu VM with apache that offloads SSL, in front of a simple Python/Flask voting app, backed by MySQL (instead of the usual Redis). It also builds the MySQL SaaS piece, network, and all network rules.

**Note:** Make sure to update ```terraform.tfvars``` with your proper ```resource_group_name``` and ```email_address```.
