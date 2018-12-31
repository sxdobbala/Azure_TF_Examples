def call(String containerPlatformType, String artifactsName, String serverUrl, String projectName, String artifactPath, String credentials){
    switch (containerPlatformType){
        case "ocp":
            deployImageOpenshift(artifactsName, serverUrl, projectName, artifactsName, artifactPath, credentials);
            break;
        case "xld":
            deployImageXLDeploy(artifactsName, serverUrl, projectName, artifactsName, artifactPath, credentials);
            break;
        default:
            return containerPlatformType + "is currently not supported";
    }
}

def deployImageOpenshift(String artifactsName, String serverUrl, String projectName, String appName, String artifactPath, String credentials){
    unstash name: artifactsName

    env.OCP_SERVER = serverUrl
    env.OCP_PROJECT = projectName
    env.OCP_APP = appName
    env.JAR_PATH = artifactPath

    echo 'Make deployments directory, copy in artifacts, and starting OCP Binary Build'
    withCredentials([[$class          : 'UsernamePasswordMultiBinding', credentialsId: credentials,
                      usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {
    openshiftDeploy apiURL: env.OCP_SERVER,
            authToken: ADMIN_AUTH_TOKEN,
            depCfg: env.OCP_APP,
            namespace: env.OCP_PROJECT,
            verbose: 'false'
    }
}

def deployImageXLDeploy(String artifactsName, String serverUrl, String projectName, String appName, String artifactPath, String credentials){
    unstash name: artifactsName

    //TODO: add XL Deploy pipeline stuff from Sam
}