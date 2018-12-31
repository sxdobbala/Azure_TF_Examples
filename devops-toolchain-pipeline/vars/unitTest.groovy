def call(String languageType, String artifactsName){
    switch (languageType) {
        case "java":
            println("in java")
            javaMavenUnitTest(artifactsName);
            break;
        default:
            println("in default")
            return languageType + "is not currently supported";
    }
}

def javaMavenUnitTest(String artifactsName){
    println("starting UTs")
    sh '''
        . /etc/profile.d/jenkins.sh > /dev/null 2>&1
        echo $MAVEN_HOME > m2home.txt
        echo $JAVA_HOME > javahome.txt
    '''

    // Set variables needed by Artifactory maven build
    env.MAVEN_HOME = readFile('m2home.txt').trim()
    env.JAVA_HOME = readFile('javahome.txt').trim()

    // Define Artifactory Parameters
    def rtMaven = Artifactory.newMavenBuild()

    unstash name: artifactsName

    withCredentials([[$class: 'FileBinding', credentialsId: 'application-secrets', variable: 'APPLICATION_SECRETS']]) {
        def secretsFile = readFile(env.APPLICATION_SECRETS).trim().tokenize("\n")
        withEnv(secretsFile){
            rtMaven.run pom: 'pom.xml', goals: 'test'
        }
    }

    step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}
