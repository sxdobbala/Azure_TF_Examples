#!groovy

def REPO_URL = '@@REPO_URL@@'
def ADMIN_CRED_ID = '@@ADMIN_CRED_ID@@'
def REPO_BRANCH = '@@REPO_BRANCH@@'
def ARTIFACTORY_URL = '@@ARTIFACTORY_URL@@'
def COMMIT_HASH
def SNAPSHOT_REPO = '@@SNAPSHOT_REPO@@'
def LANGUAGE_TYPE = '@@LANGUAGE_TYPE@@'
def PLATFORM_TYPE = '@@PLATFORM_TYPE@@'
def OCP_URL = '@@OCP_PROTOCOL@@://@@OCP_SERVER@@'
def ARTIFACTS_PATH = '@@ARTIFACTS_PATH@@'
def SERVICE_HOST = '@@OCP_PROTOCOL@@://@@OCP_APP@@-@@OCP_PROJECT@@.@@OCP_SERVER@@'
def INTEGRATION_TEST_FOLDER = '@@INTEGRATION_TEST_FOLDER@@'
def ARTIFACTS_NAME = '@@ARTIFACTS_NAME@@'
def PROJECT_NAME = '@@PROJECT_NAME@@'
def APP_NAME = '@@APP_NAME@@'

@Library("com.optum.jenkins.pipeline.pbi.library@master") _

node('docker-maven-slave') {
    stage('Build, Unit Test and Publish to Artifactory') {
        buildUnitTestPublish(LANGUAGE_TYPE, REPO_URL, REPO_BRANCH, ARTIFACTS_NAME, ADMIN_CRED_ID, SNAPSHOT_REPO, ARTIFACTORY_URL)
    }
}

node('docker-oc-slave') {
    stage('Build Image on Developer OCP Project') {
        buildImage(PLATFORM_TYPE, ARTIFACTS_NAME, OCP_URL, PROJECT_NAME, APP_NAME, ARTIFACTS_PATH, ADMIN_CRED_ID)
    }
}

node('docker-maven-slave') {
    stage('Integration Tests against Dev OCP Project'){
        runIntegrationTests(LANGUAGE_TYPE, ARTIFACTS_NAME, SERVICE_HOST, INTEGRATION_TEST_FOLDER)
    }
}