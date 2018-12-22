# Azure
Initial reusuable Azure functionality for [`global-pipeline-library`](https://github.optum.com/jenkins-pipelines/global-pipeline-library)

## Features
| `/var` method  | Description  |
| :-------------: |:-------------|
| `glAzureBuildDeployDocker.groovy`| Docker build, tag, push to Azure Container Registry |
| `glAzureKubeDeploy.groovy` | Deploy an image on ACR to a Kubernetes cluster on Azure |

### Azure Container Registry
The `glAzureBuildDeployDocker` method assumes that you already have an Azure Container Registry (ACR) stood up in a subscription in Azure  

| Parameter  | Description  |
| :-------------: |:-------------|
| `loginServer` | Login server name for the Azure Container Registry |
| `credentialsId` | Username and password credentials in Jenkins that can push to your ACR |
| `image` | Name of your image to be pushed |

### Container Service 
The `glAzureKubeDeploy` method assumes that you already have a Kubernetes Container Service stood up in a subscription in Azure  

| Parameter  | Description  |
| :-------------: |:-------------|
| `resourceGroup` |  The name of your Azure resource group that your Kubernetes cluster is hosted on |
| `clusterName` | Your Kubernetes cluster name |
| `deployConfig` | Path/name of your YAML deployment config file |
| `appName` | Name of your application to be deployed and updated in your Kubernetes cluster. Used to check if a deployment exists so we can update your Kubernetes deployment, or create a deployment |
| `loginServer` | Login server name for the Azure Container Registry, so the image can be pulled and deployed |
| `azureClientId` | Azure client id, should point to Jenkins secret text |
| `azureClientSecret` | Azure client secret, should point to Jenkins secret text |
| `azureTenant` | Azure tenant id, should point to Jenkins secret text |

### Kubernetes
Why we use the `kubectl` cli

> Use `kubectl apply -f <directory>` or `kubectl create -f <directory>`. This looks for Kubernetes configuration in all `.yaml`, `.yml`, and `.json` files in `<directory>` and passes it to apply or create.

### What these methods do
#### Deploy an application
cli: `kubectl apply -f config.yaml`  

Output: 
```
service "app" created
deployment "app" created
```

#### Update an application
cli: `kubectl set image deployment app app=<acrLoginServer>/app:latest`

### Example app  
See [this repo](https://github.optum.com/eric/gpl-azure) for an example that uses these methods
