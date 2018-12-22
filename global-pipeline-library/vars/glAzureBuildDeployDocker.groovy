import com.optum.jenkins.pipeline.library.azure.Azure

/**
 * Build and deploy a Docker image to an Azure Container Registry (ACR)
 * This assumes that you already have an Azure Container Registry stood up in your subscription, see this Terraform module:
 * https://github.optum.com/CommercialCloud-EAC/azure_container_registry
 *
 * @param loginServer String Login server name for the Azure Container Registry
 * @param credentialsId String Credentials to push to your ACR, should point to Jenkins credentials id
 * @param image String Name of your docker image; An image name may contain lowercase and uppercase letters,
 *          digits, underscores, periods and dashes. An image name may not start with a period or a dash and may
 *          contain a maximum of 128 characters.
 * @param tag String The unique identifier for this instance of the image, defaults to Jenkins build number; same
 *          requirements as image name
 * @param baseDir String Directory of Dockerfile, defaults to the current directory
 * @param extraBuildOptions String Optional Push options
 * @param requirePull Boolean Pull the base image? False may be necessary when base image is
 *          private and is pulled separately
 *
 * Build usage : docker build [OPTIONS] PATH | URL | -
 * Tag usage   : docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]
 * Login usage : docker login [OPTIONS] [SERVER]
 * Push usage  : docker push [OPTIONS] NAME[:TAG]
 */

def call(Map<String, Object> config) {
  Azure azure = new Azure(this)
  azure.buildDeployDocker(config)
}
