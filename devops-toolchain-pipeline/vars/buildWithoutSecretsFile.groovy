def call(String languageType, String repositoryUrl, String branch, String artifactsName, String credentials){
    switch (languageType) {
        case "java":
            println("in java")
            javaMavenBuild(repositoryUrl, branch, artifactsName, credentials);
            break;
        case "javaFull":
            println("in javaFull")
            javaMavenBuildUnitTestPublish(repositoryUrl, branch, artifactsName, credentials, "UHG-Snapshots", "http://repo1test.uhc.com/artifactory");
            break;
        default:
            println("in default")
            return languageType + "is not currently supported";
    }
}

def call(String languageType, String repositoryUrl, String branch, String artifactsName, String credentials,
         String snapshotRepo, String artifactoryUrl){
    switch (languageType) {
        case "java":
            println("in java")
            javaMavenBuildUnitTestPublish(repositoryUrl, branch, artifactsName, credentials, snapshotRepo, artifactoryUrl);
            break;
        default:
            println("in default")
            return languageType + "is not currently supported";
    }
}

def javaMavenBuild(String repositoryUrl, String branch, String artifactsName, String credentials){
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

    rtMaven.run pom: 'pom.xml', goals: '-U clean install -DskipTests', buildInfo: buildInfo

    stash name: artifactsName
}

def javaMavenBuildUnitTestPublish(String repositoryUrl, String branch, String artifactsName, String credentials, String snapshotRepo, String artifactoryUrl){
    sh '''
        . /etc/profile.d/jenkins.sh
        echo $MAVEN_HOME > m2home.txt
        echo $JAVA_HOME > javahome.txt
    '''

    // Set variables needed by Artifactory maven build
    env.MAVEN_HOME = readFile('m2home.txt').trim()
    env.JAVA_HOME = readFile('javahome.txt').trim()

    // Define Artifactory Parameters
    def artifactoryServer = Artifactory.newServer url: artifactoryUrl, credentialsId: credentials
    def rtMaven = Artifactory.newMavenBuild()
    def buildInfo = Artifactory.newBuildInfo()

    stage('Build') {
        git branch: branch, credentialsId: credentials, url: repositoryUrl

        sh 'git rev-parse HEAD > commit'
        COMMIT_HASH = readFile('commit').trim()
        echo 'commit hash: ' + COMMIT_HASH
        rtMaven.deployer snapshotRepo: snapshotRepo, server: artifactoryServer

        rtMaven.run pom: 'pom.xml', goals: '-U clean package -DskipTests', buildInfo: buildInfo

        stash includes: '**', name: artifactsName
    }

    stage('Unit Tests') {
        unstash name: artifactsName

        rtMaven.deployer snapshotRepo: snapshotRepo, server: artifactoryServer

        rtMaven.run pom: 'pom.xml', goals: 'install'

        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
    }

    stage('Publish Artifact to Artifactory') {

        artifactoryServer.publishBuildInfo buildInfo
    }
}
