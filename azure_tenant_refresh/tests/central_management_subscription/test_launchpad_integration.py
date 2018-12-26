import subprocess
import json
import pytest
import os


class TestAzureLaunchpad():

    # Helper function used to run commands
    def run_command(self, args):
        # check=True will cause an exception if the return code is non zero
        return subprocess.run(args, stdout=subprocess.PIPE, check=True)

    # Get env vars
    def get_env_vars(self):
        env_vars = {}
        try:
            env_vars["subscription_id"] = os.environ["ARM_SUBSCRIPTION_ID"]
            env_vars["tenant_id"] = os.environ["ARM_TENANT_ID"]
            env_vars["client_id"] = os.environ["ARM_CLIENT_ID"]
            env_vars["client_secret"] = os.environ["ARM_CLIENT_SECRET"]
        except KeyError:
            sys.exit("Error: Environment variables not set. Expected 'ARM_SUBSCRIPTION_ID', 'ARM_TENANT_ID', 'ARM_CLIENT_ID', and 'ARM_CLIENT_SECRET'")
        return env_vars

    # Run before all tests. Do any needed setup here
    @pytest.fixture(scope="session", autouse=True)
    def setup(self, request):
        env_vars = self.get_env_vars()
        login_args = ["az", "login", "--service-principal", "-u", env_vars["client_id"],
                      "-p", env_vars["client_secret"], "--tenant", env_vars["tenant_id"]]
        login_result = self.run_command(login_args)
        set_args = ["az", "account", "set",
                    "--subscription", env_vars["subscription_id"]]
        set_result = self.run_command(set_args)

    # Test the deployment of the central management subscription
    def test_central_management_subscription_resource_group_deployed(self, centralManagementRG):
        args = ["az", "group", "show", "--name", centralManagementRG]
        result = self.run_command(args)
        result_string = result.stdout.decode("utf-8")
        assert result_string != ""
        if result_string != "":
            parsed = json.loads(result_string)
            assert parsed["name"] == centralManagementRG
            assert parsed["properties"]["provisioningState"] == "Succeeded"

    # Test the creation of the storage account
    def test_central_management_storage_account_created(self, storageAccount, centralManagementRG):
        args = ["az", "storage", "account", "show", "--name",
                storageAccount, "--resource-group", centralManagementRG]
        result = self.run_command(args)
        result_string = result.stdout.decode("utf-8")
        assert result_string != ""
        if result_string != "":
            parsed = json.loads(result_string)
            assert parsed["name"] == storageAccount
            assert parsed["provisioningState"] == "Succeeded"

    # Test the key vault secret
    def test_event_hub_key_vault_secret(self, eventHubKeyVault, eventHubKeyVaultSecret):
        args = ["az", "keyvault", "secret", "show", "--name",
                eventHubKeyVaultSecret, "--vault-name", eventHubKeyVault]
        result = self.run_command(args)
        result_string = result.stdout.decode("utf-8")
        assert result_string != ""
        if result_string != "":
            parsed = json.loads(result_string)
            assert parsed["attributes"]["enabled"] is True

    # Test the creation of the event hub role definition
    def test_event_hub_role_created(self, hubRole):
        args = ["az", "role", "definition", "list", "--name", hubRole]
        result = self.run_command(args)
        result_string = result.stdout.decode("utf-8")
        assert result_string != ""
        if result_string != "":
            parsed = json.loads(result_string)
            assert parsed[0]["roleName"] == hubRole
            assert parsed[0]["permissions"][0]["actions"][0] == "Microsoft.ServiceBus/namespaces/authorizationrules/listkeys/action"

    # Test the assignment of the event hub role
    def test_event_hub_role_assigned(self, hubRole):
        args = ["az", "role", "assignment", "list", "--role", hubRole]
        result = self.run_command(args)
        result_string = result.stdout.decode("utf-8")
        assert result_string != ""
        if result_string != "":
            parsed = json.loads(result_string)
            assert len(parsed) > 0

    # Test the configuration of the activity Logs
    def test_activity_role_export(self, exportActivityLog):
        priveleges = ["Delete", "Write", "Action"]
        args = ["az", "monitor", "log-profiles",
                "show", "--name", exportActivityLog]
        result = self.run_command(args)
        result_string = result.stdout.decode("utf-8")
        assert result_string != ""
        if result_string != "":
            parsed = json.loads(result_string)
            assert parsed["name"] == exportActivityLog
            assert parsed["retentionPolicy"]["enabled"] is False
            for rights in parsed["categories"]:
                assert rights in priveleges

    # Test the creation of the event hub resource group
    def test_event_hub_resource_group(self, eventHubResourceGroup):
        args = ["az", "group", "exists", "--name", eventHubResourceGroup]
        result = self.run_command(args)
        result_string = result.stdout.decode("utf-8")
        assert result_string == "true\n"

    # Test the existence of the event hub namespace
    def test_event_hub_namespace_exists(self, eventHubNamespace, eventHubResourceGroup):
        args = ["az", "eventhubs", "namespace", "show", "--name",
                eventHubNamespace, "--resource-group", eventHubResourceGroup]
        result = self.run_command(args)
        result_string = result.stdout.decode("utf-8")
        assert result_string != ""
        if result_string != "":
            parsed = json.loads(result_string)
            assert parsed["name"] == eventHubNamespace
            assert parsed["resourceGroup"] == eventHubResourceGroup
            assert parsed["provisioningState"] == "Succeeded"

    # Test the creation of the policy initiative
    def test_azure_policy(self):
        required_policies = ['06a78e20-9358-41c9-923c-fb736d382a4d', '0961003e-5a0a-4549-abde-af6a37f2724d', '17k78e20-9358-41c9-923c-fb736d382a12', '201ea587-7c90-41c3-910f-c280ae01cfd6', '44452482-524f-4bf4-b852-0bff7cc4a3ed',
                             '47a6b606-51aa-4496-8bb7-64b11cf66adc', '760a85ff-6162-42b3-8d70-698e268f648c', 'a7ff3161-0087-490a-9ad9-ad6217f4f43a', 'abcc6037-1fc4-47f6-aac5-89706589be24', 'af6cd1bd-1635-48cb-bde7-5b15693900b9',
                             'af8051bf-258b-44e2-a2bf-165330459f9d', 'e1e5fd5d-3e4c-4ce1-8661-7d1873ae6b15', 'e56962a6-4747-49cd-b67b-bf8b01975c4c']
        unallowed_locations = ['canadaeast', 'canadacentral', 'brazilsouth', 'northeurope', 'westeurope', 'francecentral', 'francesouth', 'germanynon-regional', 'germanycentral', 'germanynortheast', 'ukwest', 'uksouth',
                               'southeastasia', 'eastasia', 'australiacentral', 'australiacentral2', 'australiaeast', 'australiasoutheast', 'centralindia', 'westindia', 'southindia', 'japaneast', 'japanwest', 'koreacentral', 'koreasouth', 'global']
        policies = []
        args = ["az", "policy", "set-definition", "show",
                "--name", "umbrella_policy_initiative"]
        result = self.run_command(args)
        result_string = result.stdout.decode("utf-8")
        assert result_string != ""
        if result_string != "":
            parsed = json.loads(result_string)
            assert parsed["name"] == "umbrella_policy_initiative"
            for policy in parsed["policyDefinitions"]:
                policies.append(policy["policyDefinitionId"][-36:])
                if "e56962a6-4747-49cd-b67b-bf8b01975c4c" == policy["policyDefinitionId"][-36:]:
                    for locations in policy["parameters"]["listOfAllowedLocations"]["value"]:
                        assert locations not in unallowed_locations
            # Custom policies not checked
            for policy in required_policies:
                assert policy in policies
