def call(String containerPlatformType, String artifactsName, String serverUrl, String projectName, String appName, String artifactPath, String credentials){
    switch (containerPlatformType){
        case "xld":
            buildAndDeployImageXLDeploy(artifactsName, serverUrl, projectName, appName, artifactPath, credentials);
            break;
        default:
            return containerPlatformType + "is currently not supported";
    }
}

def buildAndDeployImageXLDeploy(String artifactsName, String serverUrl, String projectName, String appName, String artifactPath, String credentials){
    unstash name: artifactsName

    //TODO: add XL Deploy pipeline stuff from Sam
}