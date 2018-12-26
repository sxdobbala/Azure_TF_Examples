class TestPolicies():

    """Tests Launchpad policies on a subscription."""

    critical_policy_umbrella_assignment_name = "critical_umbrella_policy_initiative_assignment"
    critical_policy_umbella_definition_name = "critical_umbrella_policy_initiative"
    policy_umbrella_assignment_name = "umbrella_policy_initiative_assignment"
    policy_umbella_definition_name = "umbrella_policy_initiative"

    def test_critical_policy_umbrella_should_be_assigned(self, arm_subscription_id, policy_client):
        """Tests the critical policy umbrella."""

        policy_assignment_found = False
        policy_definition_found = False
        try:
            assignment = policy_client.policy_assignments.get(
                scope="/subscriptions/{}".format(arm_subscription_id), policy_assignment_name=self.critical_policy_umbrella_assignment_name)
            policy_assignment_found = True
        except:
            pass
        assert policy_assignment_found

        try:
            definition = policy_client.policy_set_definitions.get(
                policy_set_definition_name=self.critical_policy_umbella_definition_name
            )
            policy_definition_found = True

            assert definition.policy_type == "BuiltIn"
            assert len(definition.policy_definitions) == 4
        except:
            pass
        assert policy_definition_found

    def test_policy_umbrella_should_be_assigned(self, arm_subscription_id, policy_client):
        """Tests the policy umbrella."""

        policy_assignment_found = False
        try:
            assignment = policy_client.policy_assignments.get(
                scope="/subscriptions/{}".format(arm_subscription_id), policy_assignment_name=self.policy_umbrella_assignment_name)
            policy_assignment_found = True
        except:
            pass
        assert policy_assignment_found

        try:
            definition = policy_client.policy_set_definitions.get(
                policy_set_definition_name=self.policy_umbella_definition_name
            )
            policy_definition_found = True

            assert definition.policy_type == "BuiltIn"
            assert len(definition.policy_definitions) == 19
        except:
            pass
        assert policy_definition_found
