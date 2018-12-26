import os
import pytest

from azure.common.credentials import ServicePrincipalCredentials
from azure.mgmt.authorization import AuthorizationManagementClient
from azure.mgmt.monitor import MonitorManagementClient
from azure.mgmt.resource import PolicyClient


def pytest_addoption(parser):
    parser.addoption("--centralManagementRG", action="store", default="")
    parser.addoption("--eventHubKeyVault", action="store", default="")
    parser.addoption("--storageAccount", action="store", default="")
    parser.addoption("--storageContainer", action="store", default="")
    parser.addoption("--hubRole", action="store", default="")
    parser.addoption("--exportActivityLog", action="store", default="")
    parser.addoption("--eventHubNamespace", action="store", default="")
    parser.addoption("--eventHubResourceGroup", action="store", default="")
    parser.addoption("--eventHubKeyVaultSecret", action="store", default="")
    # parser.addoption("--storageAccessKey", action="store", default="")


# Pulls command line options, checks for validity, and passes to test
def pytest_generate_tests(metafunc):
    centralManagementRG = metafunc.config.option.centralManagementRG
    eventHubKeyVault = metafunc.config.option.eventHubKeyVault
    storageAccount = metafunc.config.option.storageAccount
    storageContainer = metafunc.config.option.storageContainer
    hubRole = metafunc.config.option.hubRole
    exportActivityLog = metafunc.config.option.exportActivityLog
    eventHubNamespace = metafunc.config.option.eventHubNamespace
    eventHubResourceGroup = metafunc.config.option.eventHubResourceGroup
    eventHubKeyVaultSecret = metafunc.config.option.eventHubKeyVaultSecret
    # storageAccessKey = metafunc.config.option.storageAccessKey
    if 'centralManagementRG' in metafunc.fixturenames and centralManagementRG is not None:
        metafunc.parametrize("centralManagementRG", [centralManagementRG])
    if 'eventHubKeyVault' in metafunc.fixturenames and eventHubKeyVault is not None:
        metafunc.parametrize("eventHubKeyVault", [eventHubKeyVault])
    if 'storageAccount' in metafunc.fixturenames and storageAccount is not None:
        metafunc.parametrize("storageAccount", [storageAccount])
    if 'storageContainer' in metafunc.fixturenames and storageContainer is not None:
        metafunc.parametrize("storageContainer", [storageContainer])
    if 'hubRole' in metafunc.fixturenames and hubRole is not None:
        metafunc.parametrize("hubRole", [hubRole])
    if 'exportActivityLog' in metafunc.fixturenames and exportActivityLog is not None:
        metafunc.parametrize("exportActivityLog", [exportActivityLog])
    if 'eventHubNamespace' in metafunc.fixturenames and eventHubNamespace is not None:
        metafunc.parametrize("eventHubNamespace", [eventHubNamespace])
    if 'eventHubResourceGroup' in metafunc.fixturenames and eventHubResourceGroup is not None:
        metafunc.parametrize("eventHubResourceGroup", [eventHubResourceGroup])
    if 'eventHubKeyVaultSecret' in metafunc.fixturenames and eventHubKeyVaultSecret is not None:
        metafunc.parametrize("eventHubKeyVaultSecret",
                             [eventHubKeyVaultSecret])
    # if 'storageAccessKey' in metafunc.fixturenames and storageAccessKey is not None:
    #     metafunc.parametrize("storageAccessKey", [storageAccessKey])


@pytest.fixture
def service_principal_credentials():
    """Fixture for authenticating with a Service Principal"""

    return ServicePrincipalCredentials(
        client_id=os.environ["ARM_CLIENT_ID"],
        secret=os.environ["ARM_CLIENT_SECRET"],
        tenant=os.environ["ARM_TENANT_ID"]
    )


@pytest.fixture
def arm_subscription_id():
    """Fixture that returns the subscription ID that these test are run in"""

    return os.environ["ARM_SUBSCRIPTION_ID"]


@pytest.fixture
def authorization_management_client(service_principal_credentials, arm_subscription_id):
    """Fixture for AuthorizationManagementClient"""

    return AuthorizationManagementClient(
        service_principal_credentials, arm_subscription_id
    )


@pytest.fixture
def monitor_management_client(service_principal_credentials, arm_subscription_id):
    return MonitorManagementClient(
        service_principal_credentials, arm_subscription_id
    )


@pytest.fixture
def policy_client(service_principal_credentials, arm_subscription_id):
    return PolicyClient(
        service_principal_credentials, arm_subscription_id
    )
