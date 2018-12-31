def call(String languageType, String binaryRepositoryUrl, String snapshotRepoName, String credentials){
    switch (languageType) {
        case "java":
            artifactoryJavaMaven(binaryRepositoryUrl, credentials, snapshotRepoName);
            break;
        default:
            return languageType + "is not currently supported";
    }
}

def artifactoryJavaMaven(String binaryRepositoryUrl, String credentials, String snapshotRepoName){
    sh '''
        . /etc/profile.d/jenkins.sh
        echo $MAVEN_HOME > m2home.txt
        echo $JAVA_HOME > javahome.txt
    '''

    // Set variables needed by Artifactory maven build
    env.MAVEN_HOME = readFile('m2home.txt').trim()
    env.JAVA_HOME = readFile('javahome.txt').trim()

    // Define Artifactory Parameters
    def artifactoryServer = Artifactory.newServer url: binaryRepositoryUrl, credentialsId: credentials
    def rtMaven = Artifactory.newMavenBuild()
    def buildInfo = Artifactory.newBuildInfo()

    rtMaven.deployer snapshotRepo: snapshotRepoName, server: artifactoryServer

    artifactoryServer.publishBuildInfo buildInfo
}