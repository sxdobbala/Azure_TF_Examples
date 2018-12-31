def call(String languageType, String artifactsName, String integrationTestServiceUrl, String integrationTestFolder){
    switch (languageType) {
        case "java":
            javaMavenIntegrationTest(artifactsName, integrationTestServiceUrl, integrationTestFolder);
            break;
        default:
            return languageType + "is not currently supported";
    }
}

def javaMavenIntegrationTest(String artifactsName, String integrationTestServiceUrl, String integrationTestFolder){
    sh '''
        . /etc/profile.d/jenkins.sh
        echo $MAVEN_HOME > m2home.txt
        echo $JAVA_HOME > javahome.txt
    '''

    // Set variables needed by Artifactory maven build
    env.MAVEN_HOME = readFile('m2home.txt').trim()
    env.JAVA_HOME = readFile('javahome.txt').trim()
    env.SERVICE_HOST = integrationTestServiceUrl

    // Define Artifactory Parameters
    def rtMaven = Artifactory.newMavenBuild()
    def intTestsLocation = integrationTestFolder + '/pom.xml'

    unstash name: artifactsName

    rtMaven.run pom: intTestsLocation, goals: 'test'

    step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}