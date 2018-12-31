---
layout: main
title: Mac SSL Fix
permalink: macssl
---

This wiki relates to fixing the issue of connecting to an SSL endpoint on the intranet. For example, chrome or safari won't let you access any https website, it may be because you are missing the UHG certificate that normally comes with the APPSTORE version of java. If you downloaded JAVA from the internet you, you won't have the UHG certs and may face this problem. The fix is to add the certs to your java keystore and you should be able to access https websites.

Steps to add certs to keystore.
1. Download the following certificates from the attachment
  - uhgrootca.pem
  - uhg-cert-chain-dest.pem
2. Navigate to your JAVA JDK version's security folder
```
cd /Library/Java/JavaVirtualMachines/<jdkVersion>.jdk/Contents/Home/jre/lib/security
```
3. Import the uhgroot cert and uhg cert chain using keytool (may require sudo / admin access)
```
sudo keytool -import -alias uhgroot -keystore cacerts -file path/to/cert/uhgrootca.pem
sudo keytool -import -alias uhgca -keystore cacerts -file path/to/cert/uhg-cert-chain.pem
```
This should resolve the ssl issues.
