# How to cleanup assets created
You will need to manually go to each of the services created and delete each one.  These include:  Jenkins folder, GitHub Enterprise repo, OpenShift project, Artifactory, DTR.  See [CS Welcome Guide](https://www.optumdeveloper.com/content/odv-optumdev/optum-developer/en/development-tools-and-standards/cloud-application-scaffolding/cloud-application-scaffolding-welcome-guide.html)

# Secure global group cleanup
To view the groups you own, login to [Secure](https://secure.uhc.com).  Then go to Access Support – Windows Group Maintenance.
Then click on the Show My Groups button

This will list all the groups you own.  Find all the ones that end in _jenkins and jot them down somewhere.
 
Then go to [Service Now](https://optum.service-now.com/itss/category_browse.do?sysparm_document_key=sc_category,59a8862e1380d200615e31a63244b080&sysparm_cat_title=Access%20and%20Security) and select Access Administration - Windows group maintenance.
When selecting recipient, it’s easiest to select the magnifying glass and then search by Login ID and enter your numeric employee ID.
 
If you need to delete many groups, attach a Word or Excel doc with their names.
 
Once you submit the request, you will get an email from ServiceNow notifying you that a request has been opened.  Once approved, you will get another email asking for your approval.  Title starts with “Approval required for Optum ServiceNow Request Center Item”.  Scroll down and click the approve link which will open a new email.  Don’t forget to hit Send button!
 
**One last thing to note according to [this post](https://hubconnect.uhg.com/groups/access-administration/blog/2017/06/13/global-group-support#anchor3): Once a group has been deleted, it cannot be recreated with the same name.  Thus, if you have a “team name” you use often as part of your testing, be sure not to request it be deleted!**
