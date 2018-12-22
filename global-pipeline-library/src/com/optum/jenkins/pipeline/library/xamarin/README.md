# Xamarin Tools

## Setup

### mac_build_agent

Jenkins -> Manage Jenkins -> Nodes -> New Node

### storeCredentials



### keyCredentials

### keystore

## Use Case

Checkout code first and then run build from project root

``` groovy
node('mac_build_agent') {
    stage('build') {

        AndroidBuild android = new AndroidBuild();
        def buildConfig = {
            appName= "App_Name"
            storeCredentials= 'Store_Credential'
            keyCredentials= 'Key_Credential'
            keystore= 'Keystore'
        }
        android.buildApk buildConfig
    }
}
```