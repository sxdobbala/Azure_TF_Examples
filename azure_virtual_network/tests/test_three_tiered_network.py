import sys
import utils


class TestThreeTieredNetwork():
    """Integration tests for examples/three_tiered_network"""

    def setup_class(self):
        """This runs before all tests"""
        try:
            self.tfstate = utils.parse_tfstate("examples/three_tiered_network/terraform.tfstate")
            self.resource_group_name = self.tfstate["resource_group"]["name"]
            self.virtual_network_name = self.tfstate["virtual_network"]["name"]
            self.virtual_network_address_space = self.tfstate["virtual_network"]["address_space"]
            self.web_tier_subnet_id = self.tfstate["web_tier_subnet"]["id"]
            self.web_tier_subnet_name = self.tfstate["web_tier_subnet"]["name"]
            self.web_tier_subnet_address_prefix = self.tfstate["web_tier_subnet"]["address_prefix"]
            self.web_tier_nsg_id = self.tfstate["web_tier_subnet"]["network_security_group_id"]
            self.web_tier_nsg_name = self.tfstate["web_tier_subnet"]["network_security_group_name"]
            self.web_tier_event_hub_namespace_id = self.tfstate["web_tier_subnet"]["event_hub_namespace_id"]
            self.business_tier_subnet_id = self.tfstate["business_tier_subnet"]["id"]
            self.business_tier_subnet_name = self.tfstate["business_tier_subnet"]["name"]
            self.business_tier_subnet_address_prefix = self.tfstate["business_tier_subnet"]["address_prefix"]
            self.business_tier_nsg_id = self.tfstate["business_tier_subnet"]["network_security_group_id"]
            self.business_tier_nsg_name = self.tfstate["business_tier_subnet"]["network_security_group_name"]
            self.business_tier_event_hub_namespace_id = self.tfstate["business_tier_subnet"]["event_hub_namespace_id"]
            self.data_tier_subnet_id = self.tfstate["data_tier_subnet"]["id"]
            self.data_tier_subnet_name = self.tfstate["data_tier_subnet"]["name"]
            self.data_tier_subnet_address_prefix = self.tfstate["data_tier_subnet"]["address_prefix"]
            self.data_tier_nsg_id = self.tfstate["data_tier_subnet"]["network_security_group_id"]
            self.data_tier_nsg_name = self.tfstate["data_tier_subnet"]["network_security_group_name"]
            self.data_tier_event_hub_namespace_id = self.tfstate["data_tier_subnet"]["event_hub_namespace_id"]
            self.provisioning_state_succeded = "Succeeded"
            self.expected_location = "centralus"
            self.vnet_module_tags = {"cc-eac_azure_virtual_network" : "v2.0.0"}
            self.default_diagnostic_log_name = "default_diagnostic_logs"
            self.deny_all_outbound_rule_name = "DenyAllOutboundTraffic"
            self.deny_all_inbound_rule_name = "DenyAllInboundTraffic"
            self.allow_inbound_load_balancer_rule_name = "AllowInboundAzureLoadBalancer"
        except Exception:
            pass
            
    def test_resource_group_deployed(self, resource_management_client):
        """Test the deployed resource group"""
        resource_group = resource_management_client.resource_groups.get(self.resource_group_name)
        assert resource_group != None
        assert resource_group.name == self.resource_group_name
        assert resource_group.location == self.expected_location
        assert resource_group.tags == {'cc-eac_azure_resource_group': 'v2.0.0'}
        assert resource_group.properties.provisioning_state == self.provisioning_state_succeded

    def test_virtual_network_deployed(self, network_management_client):
        """Test the deployed virtual network"""
        virtual_network = network_management_client.virtual_networks.get(self.resource_group_name, self.virtual_network_name)
        assert virtual_network != None
        assert virtual_network.name == self.virtual_network_name
        assert virtual_network.location == self.expected_location
        assert virtual_network.address_space.address_prefixes == self.virtual_network_address_space
        assert virtual_network.tags == self.vnet_module_tags
        assert virtual_network.provisioning_state == self.provisioning_state_succeded

    def test_web_tier_subnet_deployed(self, network_management_client):
        """Test the deployed web tier subnet"""
        web_tier_subnet = network_management_client.subnets.get(self.resource_group_name, self.virtual_network_name, self.web_tier_subnet_name)
        self.assert_subnet_deployed(web_tier_subnet, self.web_tier_subnet_name, self.web_tier_subnet_address_prefix, self.web_tier_nsg_id, self.provisioning_state_succeded)
        
    def test_web_tier_nsg_deployed(self, network_management_client):
        """Test the deployed web tier nsg"""
        web_tier_nsg = network_management_client.network_security_groups.get(self.resource_group_name, self.web_tier_nsg_name)
        self.assert_nsg_deployed(web_tier_nsg, self.web_tier_nsg_name, self.expected_location, self.web_tier_subnet_id, self.vnet_module_tags, self.provisioning_state_succeded)

    def test_web_tier_nsg_deny_all_outbound_rule_deployed(self, network_management_client):
        """Test the deployed web tier nsg for DenyAllOutboundTraffic rule"""
        deny_all_outbound_rule = network_management_client.security_rules.get(self.resource_group_name, self.web_tier_nsg_name, self.deny_all_outbound_rule_name)
        self.assert_nsg_rule_deployed(deny_all_outbound_rule, self.deny_all_outbound_rule_name, "*", "*", "*","*", "*", "Deny", 4096, "Outbound", self.provisioning_state_succeded)

    def test_web_tier_nsg_diagnostic_logs_deployed(self, monitor_management_client):
        """Test the deployed web tier nsg diagnostic log settings"""
        web_tier_nsg_diagnostic_logs = monitor_management_client.diagnostic_settings.get(self.web_tier_nsg_id, self.default_diagnostic_log_name)
        self.assert_nsg_diagnostic_logs_deployed(web_tier_nsg_diagnostic_logs, self.default_diagnostic_log_name, self.web_tier_event_hub_namespace_id)

    def test_web_tier_nsg_deny_all_inbound_rule_deployed(self, network_management_client):
        """Test the deployed web tier nsg for DenyAllInboundTraffic rule"""
        deny_all_inbound_rule = network_management_client.security_rules.get(self.resource_group_name, self.web_tier_nsg_name, self.deny_all_inbound_rule_name)
        self.assert_nsg_rule_deployed(deny_all_inbound_rule, self.deny_all_inbound_rule_name, "*", "*", "*","*", "*", "Deny", 4095, "Inbound", self.provisioning_state_succeded)

    def test_web_tier_nsg_allow_inbound_load_balancer_rule_deployed(self, network_management_client):
        """Test the deployed web tier nsg for AllowInboundAzureLoadBalancer rule"""
        allow_inbound_load_balancer_rule = network_management_client.security_rules.get(self.resource_group_name, self.web_tier_nsg_name, self.allow_inbound_load_balancer_rule_name)
        self.assert_nsg_rule_deployed(allow_inbound_load_balancer_rule, self.allow_inbound_load_balancer_rule_name, "*", "*", "*","AzureLoadBalancer", "*", "Allow", 1000, "Inbound", self.provisioning_state_succeded)

    def test_web_tier_nsg_allow_inbound_https_rule_deployed(self, network_management_client):
        """Test the deployed web tier nsg for AllowInboundHttps rule"""
        allow_inbound_https_rule_name = "AllowInboundHttps"
        allow_inbound_https_rule = network_management_client.security_rules.get(self.resource_group_name, self.web_tier_nsg_name, allow_inbound_https_rule_name)
        self.assert_nsg_rule_deployed(allow_inbound_https_rule, allow_inbound_https_rule_name, "tcp", "*", "443","Internet", self.web_tier_subnet_address_prefix, "Allow", 100, "Inbound", self.provisioning_state_succeded)

    def test_web_tier_nsg_allow_outbound_to_business_tier_rule_deployed(self, network_management_client):
        """Test the deployed web tier nsg for AllowOutboundToBusinessTier rule"""
        allow_outbound_to_business_tier_rule_name = "AllowOutboundToBusinessTier"
        allow_outbound_to_business_tier_rule = network_management_client.security_rules.get(self.resource_group_name, self.web_tier_nsg_name, allow_outbound_to_business_tier_rule_name)
        self.assert_nsg_rule_deployed(allow_outbound_to_business_tier_rule, allow_outbound_to_business_tier_rule_name, "tcp", "*", "8080",self.web_tier_subnet_address_prefix, self.business_tier_subnet_address_prefix, "Allow", 200, "Outbound", self.provisioning_state_succeded)

    def test_business_tier_subnet_deployed(self, network_management_client):
        """Test the deployed business tier subnet"""
        business_tier_subnet = network_management_client.subnets.get(self.resource_group_name, self.virtual_network_name, self.business_tier_subnet_name)
        self.assert_subnet_deployed(business_tier_subnet, self.business_tier_subnet_name, self.business_tier_subnet_address_prefix, self.business_tier_nsg_id, self.provisioning_state_succeded)

    def test_business_tier_nsg_deployed(self, network_management_client):
        """Test the deployed business tier nsg"""
        business_tier_nsg = network_management_client.network_security_groups.get(self.resource_group_name, self.business_tier_nsg_name)
        self.assert_nsg_deployed(business_tier_nsg, self.business_tier_nsg_name, self.expected_location, self.business_tier_subnet_id, self.vnet_module_tags, self.provisioning_state_succeded)

    def test_business_tier_nsg_diagnostic_logs_deployed(self, monitor_management_client):
        """Test the deployed business tier nsg diagnostic log settings"""
        business_tier_nsg_diagnostic_logs = monitor_management_client.diagnostic_settings.get(self.business_tier_nsg_id, self.default_diagnostic_log_name)
        self.assert_nsg_diagnostic_logs_deployed(business_tier_nsg_diagnostic_logs, self.default_diagnostic_log_name, self.business_tier_event_hub_namespace_id)

    def test_business_tier_nsg_deny_all_outbound_rule_deployed(self, network_management_client):
        """Test the deployed business tier nsg for DenyAllOutboundTraffic rule"""
        deny_all_outbound_rule = network_management_client.security_rules.get(self.resource_group_name, self.business_tier_nsg_name, self.deny_all_outbound_rule_name)
        self.assert_nsg_rule_deployed(deny_all_outbound_rule, self.deny_all_outbound_rule_name, "*", "*", "*","*", "*", "Deny", 4096, "Outbound", self.provisioning_state_succeded)

    def test_business_tier_nsg_deny_all_inbound_rule_deployed(self, network_management_client):
        """Test the deployed business tier nsg for DenyAllInboundTraffic rule"""
        deny_all_inbound_rule = network_management_client.security_rules.get(self.resource_group_name, self.business_tier_nsg_name, self.deny_all_inbound_rule_name)
        self.assert_nsg_rule_deployed(deny_all_inbound_rule, self.deny_all_inbound_rule_name, "*", "*", "*","*", "*", "Deny", 4095, "Inbound", self.provisioning_state_succeded)

    def test_business_tier_nsg_allow_inbound_load_balancer_rule_deployed(self, network_management_client):
        """Test the deployed business tier nsg for AllowInboundAzureLoadBalancer rule"""
        allow_inbound_load_balancer_rule = network_management_client.security_rules.get(self.resource_group_name, self.business_tier_nsg_name, self.allow_inbound_load_balancer_rule_name)
        self.assert_nsg_rule_deployed(allow_inbound_load_balancer_rule, self.allow_inbound_load_balancer_rule_name, "*", "*", "*","AzureLoadBalancer", "*", "Allow", 1000, "Inbound", self.provisioning_state_succeded)

    def test_business_tier_nsg_allow_inbound_from_web_tier_rule_deployed(self, network_management_client):
        """Test the deployed business tier nsg for AllowInboundFromWebTier rule"""
        allow_inbound_from_web_tier_rule_name = "AllowInboundFromWebTier"
        allow_inbound_from_web_tier_rule = network_management_client.security_rules.get(self.resource_group_name, self.business_tier_nsg_name, allow_inbound_from_web_tier_rule_name)
        self.assert_nsg_rule_deployed(allow_inbound_from_web_tier_rule, allow_inbound_from_web_tier_rule_name, "tcp", "*", "8080",self.web_tier_subnet_address_prefix, self.business_tier_subnet_address_prefix, "Allow", 100, "Inbound", self.provisioning_state_succeded)

    def test_business_tier_nsg_allow_outbound_to_data_tier_rule_deployed(self, network_management_client):
        """Test the deployed business tier nsg for AllowOutboundToDataTier rule"""
        allow_outbound_to_data_tier_rule_name = "AllowOutboundToDataTier"
        allow_outbound_to_data_tier_rule = network_management_client.security_rules.get(self.resource_group_name, self.business_tier_nsg_name, allow_outbound_to_data_tier_rule_name)
        self.assert_nsg_rule_deployed(allow_outbound_to_data_tier_rule, allow_outbound_to_data_tier_rule_name, "tcp", "*", "3306",self.business_tier_subnet_address_prefix, self.data_tier_subnet_address_prefix, "Allow", 200, "Outbound", self.provisioning_state_succeded)

    def test_data_tier_subnet_deployed(self, network_management_client):
        """Test the deployed data tier subnet"""
        data_tier_subnet = network_management_client.subnets.get(self.resource_group_name, self.virtual_network_name, self.data_tier_subnet_name)
        self.assert_subnet_deployed(data_tier_subnet, self.data_tier_subnet_name, self.data_tier_subnet_address_prefix, self.data_tier_nsg_id, self.provisioning_state_succeded)

    def test_data_tier_nsg_deployed(self, network_management_client):
        """Test the deployed data tier nsg"""
        data_tier_nsg = network_management_client.network_security_groups.get(self.resource_group_name, self.data_tier_nsg_name)
        self.assert_nsg_deployed(data_tier_nsg, self.data_tier_nsg_name, self.expected_location, self.data_tier_subnet_id, self.vnet_module_tags, self.provisioning_state_succeded)

    def test_data_tier_nsg_diagnostic_logs_deployed(self, monitor_management_client):
        """Test the deployed data tier nsg diagnostic log settings"""
        data_tier_nsg_diagnostic_logs = monitor_management_client.diagnostic_settings.get(self.data_tier_nsg_id, self.default_diagnostic_log_name)
        self.assert_nsg_diagnostic_logs_deployed(data_tier_nsg_diagnostic_logs, self.default_diagnostic_log_name, self.data_tier_event_hub_namespace_id)

    def test_data_tier_nsg_deny_all_outbound_rule_deployed(self, network_management_client):
        """Test the deployed data tier nsg for DenyAllOutboundTraffic rule"""
        deny_all_outbound_rule = network_management_client.security_rules.get(self.resource_group_name, self.data_tier_nsg_name, self.deny_all_outbound_rule_name)
        self.assert_nsg_rule_deployed(deny_all_outbound_rule, self.deny_all_outbound_rule_name, "*", "*", "*","*", "*", "Deny", 4096, "Outbound", self.provisioning_state_succeded)

    def test_data_tier_nsg_deny_all_inbound_rule_deployed(self, network_management_client):
        """Test the deployed data tier nsg for DenyAllInboundTraffic rule"""   
        deny_all_inbound_rule =  network_management_client.security_rules.get(self.resource_group_name, self.data_tier_nsg_name, self.deny_all_inbound_rule_name)
        self.assert_nsg_rule_deployed(deny_all_inbound_rule, self.deny_all_inbound_rule_name, "*", "*", "*","*", "*", "Deny", 4095, "Inbound", self.provisioning_state_succeded)

    def test_data_tier_nsg_allow_inbound_load_balancer_rule_deployed(self, network_management_client):
        """Test the deployed data tier nsg for AllowInboundAzureLoadBalancer rule"""
        allow_inbound_load_balancer_rule = network_management_client.security_rules.get(self.resource_group_name, self.data_tier_nsg_name, self.allow_inbound_load_balancer_rule_name)
        self.assert_nsg_rule_deployed(allow_inbound_load_balancer_rule, self.allow_inbound_load_balancer_rule_name, "*", "*", "*","AzureLoadBalancer", "*", "Allow", 1000, "Inbound", self.provisioning_state_succeded)

    def test_data_tier_nsg_allow_inbound_from_business_tier_rule_deployed(self, network_management_client):
        """Test the deployed data tier nsg for AllowInboundFromBusinessTier rule"""
        allow_inbound_from_business_tier_rule_name = "AllowInboundFromBusinessTier"
        allow_inbound_from_business_tier_rule = network_management_client.security_rules.get(self.resource_group_name, self.data_tier_nsg_name, allow_inbound_from_business_tier_rule_name)
        self.assert_nsg_rule_deployed(allow_inbound_from_business_tier_rule, allow_inbound_from_business_tier_rule_name, "tcp", "*", "3306",self.business_tier_subnet_address_prefix, self.data_tier_subnet_address_prefix, "Allow", 100, "Inbound", self.provisioning_state_succeded)

    def assert_subnet_deployed(self, subnet, name, address_prefix, network_security_group_id, provisioning_state):
        """Test a deployed subnet"""
        parent = sys._getframe(1).f_code.co_name
        assert (subnet != None) and parent
        assert (subnet.name == name) and parent
        assert (subnet.address_prefix == address_prefix) and parent
        assert (subnet.network_security_group != None) and parent
        assert (subnet.network_security_group.id == network_security_group_id) and parent
        assert (subnet.provisioning_state == provisioning_state) and parent

    def assert_nsg_deployed(self, nsg, name, location, subnet_id, tags, provisioning_state):
        """Test a deployed nsg"""
        parent = sys._getframe(1).f_code.co_name
        assert (nsg != None) and parent
        assert (nsg.name == name) and parent
        assert (nsg.location == location) and parent
        assert (len(nsg.subnets) == 1) and parent
        assert (nsg.subnets[0].id == subnet_id) and parent
        assert (nsg.tags == tags) and parent
        assert (nsg.provisioning_state == provisioning_state) and parent

    def assert_nsg_diagnostic_logs_deployed(self, nsg_diagnostic_logs, name, event_hub_namespace_id):
        """Test a deployed nsg diagnostic logs settings"""
        parent = sys._getframe(1).f_code.co_name
        assert (nsg_diagnostic_logs != None) and parent
        assert (nsg_diagnostic_logs.name == name) and parent
        assert (nsg_diagnostic_logs.event_hub_name == event_hub_namespace_id) and parent
        assert (len(nsg_diagnostic_logs.logs) == 2) and parent
        assert (nsg_diagnostic_logs.logs[0].category == "NetworkSecurityGroupRuleCounter") and parent
        assert (nsg_diagnostic_logs.logs[0].enabled == True) and parent
        assert (nsg_diagnostic_logs.logs[1].category == "NetworkSecurityGroupEvent") and parent
        assert (nsg_diagnostic_logs.logs[1].enabled) and parent

    def assert_nsg_rule_deployed( self, rule, name, protocol, source_port_range, desitination_port_range, source_address_prefix, destination_address_prefix, access, priority, direction, provisioning_state):
        """Test a deployed nsg rule"""
        parent = sys._getframe(1).f_code.co_name
        assert (rule != None) and parent
        assert (rule.protocol == protocol) and parent
        assert (rule.source_port_range == source_port_range) and parent
        assert (rule.destination_port_range == desitination_port_range) and parent
        assert (rule.source_address_prefix == source_address_prefix) and parent
        assert (rule.destination_address_prefix == destination_address_prefix) and parent
        assert (rule.access == access) and parent
        assert (rule.priority == priority) and parent
        assert (rule.direction == direction) and parent
        assert (rule.provisioning_state == provisioning_state) and parent