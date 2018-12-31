def call(String languageType, String repositoryUrl, String branch, String artifactsName, String snapshotRepo, String artifactoryUrl, String credentials){
    switch (languageType) {
        case "java":
            println("in java")
            javaMavenBuildUnitTestPublish(repositoryUrl, branch, artifactsName, snapshotRepo, artifactoryUrl, credentials);
            break;
        default:
            println("in default")
            return languageType + "is not currently supported";
    }
}

def javaMavenBuildUnitTestPublish(String repositoryUrl, String branch, String artifactsName, String snapshotRepo, String artifactoryUrl, String credentials) {
    sh '''
        . /etc/profile.d/jenkins.sh
        echo $MAVEN_HOME > m2home.txt
        echo $JAVA_HOME > javahome.txt
    '''

    // Set variables needed by Artifactory maven build
    env.MAVEN_HOME = readFile('m2home.txt').trim()
    env.JAVA_HOME = readFile('javahome.txt').trim()

    // Define Artifactory Parameters
    def rtMaven = Artifactory.newMavenBuild()
    def buildInfo = Artifactory.newBuildInfo()

    git branch: branch, credentialsId: credentials, url: repositoryUrl

    sh 'git rev-parse HEAD > commit'
    COMMIT_HASH = readFile('commit').trim()
    echo 'commit hash: ' + COMMIT_HASH

    rtMaven.deployer snapshotRepo: snapshotRepo, server: artifactoryServer

    rtMaven.run pom: 'pom.xml', goals: '-U clean install', buildInfo: buildInfo

    step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

    artifactoryServer.publishBuildInfo buildInfo

    stash name: artifactsName
}