module "central_management_subscription_event_hub" {
  source = "./centralized_logging_event_hub"
  group_uuid          = "${var.group_uuid}"
  location            = "${var.location}"
}

module "central_management_subscription_splunk_forwarder" {
  source = "./centralized_logging_splunk"
  splunk_sas_token    = "${var.splunk_sas_token}"
  location            = "${var.location}"
}
