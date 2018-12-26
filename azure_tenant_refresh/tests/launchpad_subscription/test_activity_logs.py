class TestActivityLogs():

    """Tests activity logs on a Launchpad subscription."""

    # Expected actions for the default log profile
    expected_default_profile_actions = ["Delete", "Write", "Action"]

    # List of regions for which Activity Log events should be stored or streamed.
    expected_default_profile_locations = [
        "australiacentral", "australiacentral2", "australiaeast", "australiasoutheast", "brazilsouth",
        "canadacentral", "canadaeast", "centralindia", "centralus", "eastasia", "eastus", "eastus2",
        "francecentral", "francesouth", "japaneast", "japanwest", "koreacentral", "koreasouth",
        "northcentralus", "northeurope", "southcentralus", "southindia", "southeastasia", "uksouth", "ukwest",
        "westcentralus", "westeurope", "westindia", "westus", "westus2", "global"
    ]

    def test_activity_logs_should_be_exported_to_the_central_event_hub(self, monitor_management_client, arm_subscription_id, eventHubResourceGroup, eventHubNamespace):
        """The subscription's activity logs should be exported to the central event hub."""

        default_profile = False
        log_profiles = list(monitor_management_client.log_profiles.list())
        assert log_profiles
        for profile in log_profiles:
            if "default" in profile.id:
                default_profile = True

                # Should be exported to the central event hub
                assert profile.service_bus_rule_id == "/subscriptions/{0}/resourceGroups/{1}/providers/Microsoft.EventHub/namespaces/{2}/authorizationrules/RootManageSharedAccessKey".format(
                    arm_subscription_id, eventHubResourceGroup, eventHubNamespace)

                # Log profile should have the expected actions
                for action in self.expected_default_profile_actions:
                    assert action in profile.categories

                # Log profile should have the expected locations
                for location in self.expected_default_profile_locations:
                    assert location in profile.locations

        assert default_profile
