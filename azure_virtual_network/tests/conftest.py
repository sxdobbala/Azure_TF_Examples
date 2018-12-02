import os
import logging
import pytest
from azure.common.credentials import ServicePrincipalCredentials
from azure.mgmt.resource import ResourceManagementClient
from azure.mgmt.network import NetworkManagementClient
from azure.mgmt.monitor import MonitorManagementClient
from azure.common.client_factory import get_client_from_cli_profile
#did we get an AzLogin with a value other than no?
AzLoginStr = "--AzLogin"

def pytest_addoption(parser):
    parser.addoption(AzLoginStr, action="store_true")

# File for pytest fixtures.
# Details about fixtures here: https://docs.pytest.org/en/latest/fixture.html

LOGGER = logging.getLogger()


@pytest.fixture
def service_principal_credentials():
    """Fixture for authenticating with a Service Principal"""
    try:
        return ServicePrincipalCredentials(
            client_id=os.environ["ARM_CLIENT_ID"],
            secret=os.environ["ARM_CLIENT_SECRET"],
            tenant=os.environ["ARM_TENANT_ID"]
        )
    except Exception as exception:
        LOGGER.error("Failed to authenticate with Azure: %s", str(exception))


@pytest.fixture
def resource_management_client():
    """Fixture that provides operations for working with resources and resource groups."""
    try:
        if( pytest.config.getoption( AzLoginStr ) ) :
            return get_client_from_cli_profile( ResourceManagementClient )
        else:
            return ResourceManagementClient(
                service_principal_credentials(),
                os.environ["ARM_SUBSCRIPTION_ID"]
            )
    except Exception as exception:
        LOGGER.error(
            "Failed to create Resource Management Client: %s", str(exception))


@pytest.fixture
def network_management_client():
    """Fixture that provides operations for working with Azure networking."""
    try:
        if( pytest.config.getoption( AzLoginStr)) :
            return get_client_from_cli_profile( NetworkManagementClient )
        else:
            return NetworkManagementClient(
                service_principal_credentials(),
                os.environ["ARM_SUBSCRIPTION_ID"]
            )

    except Exception as exception:
        LOGGER.error(
            "Failed to create Network Management Client: %s", str(exception))


@pytest.fixture
def monitor_management_client():
    """Fixture that provides operations for working with Azure monitoring."""
    try:
        if( pytest.config.getoption( AzLoginStr ) ) :
            return get_client_from_cli_profile( MonitorManagementClient )
        else:
            return MonitorManagementClient(
                service_principal_credentials(),
                os.environ["ARM_SUBSCRIPTION_ID"]
            )
    except Exception as exception:
        LOGGER.error(
            "Failed to create Monitor Management Client: %s", str(exception))