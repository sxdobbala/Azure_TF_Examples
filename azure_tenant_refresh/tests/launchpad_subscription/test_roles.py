import time


class TestRoles():

    """Test role definitions/assignments in a Launchpad subscription."""

    # The object ID of the SP (CC_AZU_EIS_READ_SP) that is given
    # access to each subscription.
    cc_azu_eis_read_sp_object_id = "7bd64576-cbfe-4cd0-af06-a946df9c5f5b"

    def test_cc_azu_eis_read_sp_object_id_should_be_assigned_the_reader_role(self, arm_subscription_id, authorization_management_client):
        """The CC_AZU_EIS_READ_SP SP should be assigned the Reader role."""

        assignments = list(authorization_management_client.role_assignments.list_for_scope(
            scope="/subscriptions/{}".format(arm_subscription_id), filter="assignedTo('{}')".format(self.cc_azu_eis_read_sp_object_id)))

        assert assignments
        assert len(assignments) == 1
        assert assignments[0].properties.principal_id == self.cc_azu_eis_read_sp_object_id

        role_definition_id = assignments[0].properties.role_definition_id
        role_definition_name = authorization_management_client.role_definitions.get_by_id(
            role_definition_id).properties.role_name
        assert role_definition_name == "Reader"
