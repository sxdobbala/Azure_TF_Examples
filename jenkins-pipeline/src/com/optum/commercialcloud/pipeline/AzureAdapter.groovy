class AzureAdapter extends BaseAdapter() {
  def jenkins 

  def AzureAdapter(jenkins) {
    this.jenkins = jenkins
  }

  @Override
  def deployCentralManagement() {
    // checkout repo that has the terraform code needed

    try {
      jenkins.sh """
      cd azure_deployment 
      terraform init
      terraform plan
      terraform apply
      """
    } catch(Exception e) {

    }
  }
  
}