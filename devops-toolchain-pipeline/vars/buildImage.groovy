def call(String containerPlatformType, String artifactsName, String serverUrl, String projectName, String appName, String artifactPath, String credentials){
    switch (containerPlatformType){
        case "ocp":
            buildImageOpenshift(artifactsName, serverUrl, projectName, appName, artifactPath, credentials);
            break;
        case "azure":
            return "azure is not currently supported";
        case "docker":
            return "docker is not currently supported";
        default:
            return containerPlatformType + "is currently not supported";
    }
}

def buildImageOpenshift(String artifactsName, String serverUrl, String projectName, String appName, String artifactPath, String credentials){
    unstash name: artifactsName

    env.OCP_SERVER = serverUrl
    env.OCP_PROJECT = projectName
    env.OCP_APP = appName
    env.JAR_PATH = artifactPath

    echo 'Make deployments directory, copy in artifacts, and starting OCP Binary Build'
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentials,
                      usernameVariable: 'OC_USER', passwordVariable: 'OC_PASS']]) {
        sh '''
            . /etc/profile.d/jenkins.sh
            [ -z $JAR_PATH ] && JAR_PATH='target'
            EXTENSIONS=(jar war ear)
            mkdir -p ./ocp/deployments/
            if [ -d $JAR_PATH ]; then
                for i in ${EXTENSIONS[@]}; do
                    if [ -f ./$JAR_PATH/*.$i ]; then
                        cp ./$JAR_PATH/*.$i ./ocp/deployments
                    fi
                done
            elif [ -f $JAR_PATH ]; then
                cp ./$JAR_PATH ./ocp/deployments/
            fi

            if [ -f .s2i/bin/assemble.sh ]; then
                mkdir -p ./ocp/.s2i/bin
                mv .s2i/bin/assemble.sh ./ocp/.s2i/bin/assemble
                mv .s2i/bin/*.sh ./ocp/.s2i/bin/
            fi

            cd ./ocp
            oc login $OCP_SERVER -u ${OC_USER} -p ${OC_PASS} --insecure-skip-tls-verify=true
            oc project $OCP_PROJECT
            oc start-build $OCP_APP --from-dir=. --follow=true
        '''
    }
}