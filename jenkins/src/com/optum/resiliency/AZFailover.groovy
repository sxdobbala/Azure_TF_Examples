#!/usr/bin/env groovy
package com.optum.resiliency

import com.optum.utils.ResiliencyHelpers

def call(Object azFailover)
{
    String vpcId = sh(script: "aws ec2 describe-vpcs --filters \"Name=tag:Name,Values=${azFailover.vpcName}\" --query \"Vpcs[*].VpcId\" --output text --profile saml", returnStdout: true).trim()
    String newNacl = sh(script: "aws ec2 create-network-acl --vpc-id \"${vpcId}\" --output text --profile saml", returnStdout: true).trim()
    String[] newNaclToEdit = newNacl.split("\t")
    String newNaclID = newNaclToEdit[2]
    println "new nacl ID is : " + newNaclID

    String allZones = sh(script: "aws ec2 describe-subnets --filters \"Name=vpc-id,Values=${vpcId}\" --query \"Subnets[*].AvailabilityZone\" --output text --profile saml", returnStdout: true).trim()
    String[] randomZones = allZones.split("\t")
    Random random = new Random()
    def randomZoneSize = randomZones.size()
    int randomNumber = random.nextInt(randomZoneSize)
    String randomZone = randomZones[randomNumber]
    String subnetIDs = sh(script: "aws ec2 describe-subnets --filters \"Name=availability-zone,Values=${randomZone}\" \"Name=vpc-id,Values=${vpcId}\" --query \"Subnets[*].SubnetId\" --output text --profile saml", returnStdout: true).trim()
    String[] subnetIDsToEditList = subnetIDs.split("\t")

    Map<String, String> subnetMap = new HashMap<String, String>()
    for (i = 0; i < subnetIDsToEditList.length; i++)
    {
        String subnetID = subnetIDsToEditList[i]
        String naclID = sh(script: "aws ec2 describe-network-acls --filters \"Name=vpc-id,Values=${vpcId}\" \"Name=association.subnet-id,Values=${subnetID}\" --query \"NetworkAcls[*].NetworkAclId\" --output text --profile saml", returnStdout: true).trim()
        subnetMap.put(subnetID,naclID)
    }

    Map<String, String> associateIDMap = new HashMap<String, String>()
    for (i = 0; i< subnetMap.size(); i++)
    {
        String subnetID = subnetMap.keySet().toArray()[i]
        String naclID = subnetMap.get(subnetID)
        println "[SubnetID:NaclID] is: [" + subnetID + ":" + naclID + "]"
        String assocID = sh(script: "aws ec2 describe-network-acls --filters \"Name=vpc-id,Values=${vpcId}\" \"Name=network-acl-id,Values=${naclID}\" --query \"NetworkAcls[*].Associations[*]\" --output text --profile saml", returnStdout: true).trim()
        String[] assocIDlist = assocID.split("\n")
        for (int i=0; i<assocIDlist.length; i++ )
        {
            if(assocIDlist[i].contains(subnetID))
            {
                String[] assocIDs = assocIDlist[i].split("\t")
                associateIDMap.put(assocIDs[0],naclID)
            }
        }
    }

    Map<String, String> newassociateIDMap = new HashMap<String, String>()
    if(azFailover.dryRun == null || (azFailover.dryRun != null && azFailover.dryRun == false))
    {
        for (i = 0; i< associateIDMap.size(); i++)
        {
            String associationID = associateIDMap.keySet().toArray()[i]
            String naclID = associateIDMap.get(associationID)
            String newAssID = sh(script: "aws ec2 replace-network-acl-association --association-id ${associationID} --network-acl-id ${newNaclID} --output text --profile saml", returnStdout: true).trim()
            newassociateIDMap.put(newAssID,naclID)
        }

        def timer = azFailover.statusCheckSleepTimeInSeconds
        if (timer == null)
        {
            timer = 1200
        }
        sleep (timer)

        for (i = 0; i< newassociateIDMap.size(); i++)
        {
            String associationID = newassociateIDMap.keySet().toArray()[i]
            String oldNacl = newassociateIDMap.get(associationID)
            String newAssID = sh(script: "aws ec2 replace-network-acl-association --association-id ${associationID} --network-acl-id ${oldNacl} --output text --profile saml", returnStdout: true).trim()
        }
        sh "aws ec2 delete-network-acl --network-acl-id ${newNaclID} --profile saml"
    }
}
