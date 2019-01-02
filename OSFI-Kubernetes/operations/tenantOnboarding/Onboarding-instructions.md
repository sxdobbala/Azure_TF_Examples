
# Tenant Onboarding Instructions

OSFI Portal application is designed with self-service provisioning capability. It automates various steps required to provision a tenant namespace such as creation of secure groups, creation of namespace, service accounts and role-bindings.

### Step 1: Go to the [OSFI Kubernetes portal](https://osfi-k8s-ctc.optum.com)

<p align="center">
  <img src="/images/NewPortalHome.png" width="700"/>
</p>

Select the Get Started button. You will be prompted to enter your user

<p align="center">
  <img src="/images/PortalLogin.png" width="400"/>
</p>

### Step 2: Create new namespace (Kubernetes tenancy)

Once you are successfully authenticated, you will be taken to the Project window of the portal. If you have not created a project the Project window will appear as below without project boxes displayed in the middle. 

<p align="center">
  <img src="/images/NewDashboard.png" width="700"/>
</p>

Select the “+” button on the right to create a new Project. The Project Registration dialog box will appear.

<p align="center">
  <img src="/images/TenantRegistration.png" width="700"/>
</p>

Please enter the following information by clicking Next button on each form:
 * Name of the Project: The name you would like to give the project you are creating
 * Description of the Project: A brief description of the project you are creating
 * MS ID of the Project Owner: The MS ID of the person who will be the owner of the Project. This will be populated with a default value of the MS ID used to log in. 
 * Owner's Employeed Id: Enter your valid Employee ID
 * ASK ID of the Project: The ASK ID to which the project resources should be billed
 * CMDB Code : The CMDB Code to which project resources should be billed
 * Environment: Pick either Prod / non Prod
 * Data Center : Currently 2 data centers listed CTC/ELR
 * Namespace of the Project: Kubernetes Name Space name
 * Name of the Service Account: The name of the kubernetes service account with admin role for the namespace
 * E-mail Address of the Owner: The e-mail address of the person who will be the owner of the Project. This will be populated with a default value of the e-mail address associated with the MS ID used to log in.

### Step 3 Login to Kubernetes Dashboard

Click on the Kubernetes icon next to "+" button on the page to login to Kubernetes Dashboard

Login to Kubernetes Dashboard using your MS ID

<p align="center">
  <img src="/images/Kubernetes login.png" width="700"/>
</p>

You will be placed to 'default' namespace once you login to kubernetes dashboard. You will be notified with warning messages such as you cannot access 'default' namespace objects.

<p align="center">
  <img src="/images/Kubernetes dashboard.png" width="700"/>
</p>

Select your newly created namespace from the namespace dropdown
Continue use it for managing namespace objects such as Pods, Replicasets etc.

### Step 4 Manage Role-Based Access Control ( IAM ) from OSFI Portal
After successfully creating the namespace, a owner of the project namespace can grant/revoke all access to the users or groups
* Sign in to the OSFI Portal
* Choose the project namespace from the list of projects view, click on the dots which opens a context menu
* Click IAM from the menu
<p align="center">
  <img src="/images/contextmenu.png" width="700"/>
</p>
<p align="center">
  <img src="/images/Portal Manage Permission.png" width="700"/>
</p>
 * User Id: The user id should be valid MS ID
 * Group: Group name should be valid Group Name ( prior to entering the group name must be created in the secure )

### Step 5 Manage Resource Quota ( Quota ) from OSFI Portal
After successfully creating the namespace, a owner/admin of the project namespace can manage the Resource Quota for the namespace.
* Sign in to the OSFI Portal
* Choose the project namespace from the list of projects view, click on the dots which opens a context menu
* Click Quota from the menu
<p align="center">
  <img src="/images/contextmenu.png" width="700"/>
</p>
<p align="center">
  <img src="/images/quota.png" width="700"/>
</p>

### Step 6 Configure Kubernetes CLI - kubectl

An Edge Node is configured with Docker daemon and Kubectl client for the cluster. Tenants can leverage this edge node to build docker images, publish imaged to Optum Docker Registry, commit code to github and use Kubernetes command line tool 'kubectl' for kubernetes API server communication.

**Edge Node: dbslp1748**

**Global Group for getting access to the edge node: k8s_prod_usr**

Kubernetes CLI kubectl is allowed to configure only by the service account owners. The current authentication mechanism for kubectl is kubernetes provided service account token. Only service account owner has access to the token to setup CLI.

Follow the [kubectl setup instruction](SettingUpYourKubectl.md)

The service account token can be retrieved from Kubernetes Dashboard
* Open the secret associated to the service account
* Click on the view icon next to token

Tenant admin can create additional Service Accounts for developers or regular users for CLI access. We recommend assigning  'developer' cluster role to developers/regular users.
Follow [Creating a New ServiceAccount within Your namespace](CreateAUserwithinYourNamespace.md)

