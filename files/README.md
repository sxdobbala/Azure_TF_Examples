# Azure File Upload

Simple tool to upload files to Azure Storage

#### Set Configuration Properties

Create a Configuration file with the following properties:
```
accountName=accountName
accountKey=accountKey
blobContainer=blobContainer
fileContainer=fileContainer

useBlob=false
overwrite=false
```

Use the VM /status API of OEA Workbench to get the correct values for `blobContainer` and `fileContainer`.

#### Upload a directory or a file to Azure

```bash
java -jar target/azure-file-upload-1.0-SNAPSHOT-jar-with-dependencies.jar settings.properties folder-to-upload'
```

#### Update SSL Certificates:

OSX: open Keychain, select 'System' keychain, select 'Certificates' category. Select Optum Root CA, right-click: Export .cer file.

```bash
keytool -importcert -trustcacerts -file "Optum Root CA.cer" -alias optum -keystore optumkeystore.jks
```
