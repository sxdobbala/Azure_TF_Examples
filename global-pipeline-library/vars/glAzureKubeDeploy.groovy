import com.optum.jenkins.pipeline.library.azure.Azure

/**
 * Deploy a Docker image in your Azure Container Registry to a Kubernetes cluster
 * Assumes you already have a Container service set up in a resource group on an Azure subscription, see this
 * Terraform module: https://github.optum.com/CommercialCloud-EAC/azure_aks
 *
 * @param resourceGroup String Name of the Azure resource group your Kubernetes cluster runs on
 * @param clusterName String Name of the Kubernetes cluster
 * @param deployConfig String The path to the yaml template in your source control/Jenkins workspace
 * @param appName String Name of your application to be deployed and updated in your Kubernetes cluster
 *          Used to check if a deployment exists so we can update your Kubernetes deployment, or create a deployment
 * @param loginServer String Login server name for the Azure Container Registry
 * @param azureClientId String Your Azure ARM Client ID, points to a StringBinding Jenkins credential
 * @param azureClientSecret String Your Azure ARM Client Secret, points to a StringBinding Jenkins credential
 * @param azureTenant String The tenant value is the Azure Active Directory tenant associated with the service principal,
 *          points to a StringBinding Jenkins credential
 * @param containerName String optional parameter for projects whose container name differs from the app name
 * @param imageName String optional parameter for projects whose image name differs from the app name
 * @param imageTag String optional parameter for projects using an image tag other than "latest"
 */

def call(Map<String, Object> config) {
  Azure azure = new Azure(this)
  azure.deploy(config)
}

