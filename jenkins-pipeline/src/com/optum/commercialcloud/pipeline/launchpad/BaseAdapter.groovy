/*
   Base Adapter

   Abstract Adapter class for the respective Cloud Services (Azure or AWS), 
   defining how the Central and Launchpad Accounts are tested and applied.
*/

package com.optum.commercialcloud.pipeline.launchpad

abstract class BaseAdapter implements Serializable {
  def jenkins
  
  /*
     Default constructor setting the jenkins var
  */
  BaseAdapter(jenkins) {
    this.jenkins = jenkins
  }

  /*
     Method to test/apply Launchpad to the Central Management Account
  */
  abstract def deployCentralManagement(def master, def config)

  /*
     Method to test/apply Launchpad onto a single or all the accounts under the 
     provide masterAccount
  */
  abstract def deployLaunchpadOnAccount(def nodeLabel, def masterAccount, def config, def singleAccountID, def newAccount)

}