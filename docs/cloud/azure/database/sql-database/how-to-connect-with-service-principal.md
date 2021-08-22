---
title: Connect to SQL Database with a Service Principal
description: Learn how to create a new Azure Active Directory (Azure AD) application and service principal that can be used with the role-based access control.
ms.assetid: 0f7abdee-b1e6-4c4a-a40f-1335dc9bc9dc
ms.topic: how-to
ms.date: 06/11/2021
ms.custom: ""
ms.services: "azure-sql-database"
ms.author: "bashey"
_tocTitle: "Azure"
---

## Overview

This article shows you how to create a new Azure Active Directory (Azure AD) application and service principal that can be used with the role-based access control. When you have code that needs to access or modify resources, you can create an identity for the app. This identity is known as a service principal. You can then assign the required permissions to the service principal. This article shows how to create the service principal using terraform.

To access resources that are secured by an Azure AD tenant, the entity that requires access must be represented by a security principal. The security principal defines the access policy and permissions for the user/application in the Azure AD tenant. This enables core features such as authentication of the user/application during sign-in, and authorization during resource.

> **Note: Service Principal authentication for App service is only supported by below framework**
>
> - .NET Framework 4.7.2
> - .NET Core 2.2
>

## Steps

1. Create Service Principal
1. Create AAD Group
1. Add SPN as member to AAD Group
1. Create SQL Server in Azure
1. Add AAD Group as Active Directive admin for SQL server
1. Connect with Azure SQL Server using the SPN Token from Resource URI Azure Database

### Step 1-3

Please use [this terraform code](https://github.optum.com/Dojo360/azure-active-directory-service-principal) to create an AD group in azure for lower and higher environments and service principal to add to that group.

**Important**: Make sure you copy the result of terraform apply command, it will prompt object id, secret and client id which are used in later steps.

### Step 4 and 5

To give admin access to group create for Azure sql server with Service principal added pls fallow below documentation to login to sql server as admin and run below sql commands:

```tsql
CREATE USER [groupcreated by terraform with SP added] FROM EXTERNAL PROVIDER;
    ALTER ROLE db_datareader ADD MEMBER [groupcreated by terraform with SP added];
    ALTER ROLE db_datawriter ADD MEMBER [groupcreated by terraform with SP added]
    ALTER ROLE db_ddladmin ADD MEMBER [groupcreated by terraform with SP added]; 
    GO
```

### Step 6

Modifications to be done to your .Net applications to use Service principal added to sql server admin group are listed below.

In Visual Studio, open the Package Manager Console and add the NuGet package Microsoft.Azure.Services.AppAuthentication

```powershell
Install-Package Microsoft.Azure.Services.AppAuthentication -Version 1.3.1
```

In Web.config, working from the top of the file and make the following change
In ```<configSections>```, add the following section declaration in it:

```xml
<section name="SqlAuthenticationProviders" type="System.Data.SqlClient.SqlAuthenticationProviderConfigurationSection, System.Data, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089" />
```

below the closing ```</configSections>``` tag, add the following XML code for ```<SqlAuthenticationProviders>```.

```xml
<SqlAuthenticationProviders>
<providers>
<add name="Active Directory Interactive" type="Microsoft.Azure.Services.AppAuthentication.SqlAppAuthenticationProvider, Microsoft.Azure.Services.AppAuthentication" />
```

### Modify App Connection String

Find the connection string called MyDbConnection and replace its connectionString value with

```csharp
"server=tcp:<server-name>.database.windows.net,1433;database=<db-name>;UID=AnyString;Authentication=Active Directory Interactive"
```

Replace ```<server-name>``` and ```<db-name>``` with your server name and database name.

#### Using az cli

```powershell
az webapp config connection-string set -resource-group myResourceGroup -name <app name> -settings MyDbConnection='Server=tcp:<server_name>.database.windows.net,1433;Database=<db_name>;' -connection-string-type SQLAzure
```
