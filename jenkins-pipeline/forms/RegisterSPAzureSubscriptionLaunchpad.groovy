properties([
    parameters([
        password(defaultValue: '', description: 'Client Secret', name: 'clientSecret'),
        password(defaultValue: '', description: 'Subscription Id', name: 'subscriptionId'),
        password(defaultValue: '', description: 'Client Id', name: 'clientId'),
        password(defaultValue: '', description: 'Tenant Id', name: 'tenantId')
    ])
])


node('docker-azure-slave') {
  wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${params.clientSecret}", var: 'clientSecret']]]) {
   
   stage('Validate Creds') {
       try {
           sh """
            . /etc/profile.d/jenkins.sh
           az login --service-principal -u ${params.clientId} -p ${params.clientSecret.plainText} --tenant ${params.tenantId} > results.json
           """
            def results = readJSON file: 'results.json'
            archiveArtifacts 'results.json'
            sh 'rm -f results.json'
            assert results.any { sub -> sub.id == params.subscriptionId.plainText }
        } catch (AssertionError e) {
        	println "Validation Error: Service Principal does not have access to subscription"
        	throw e
        } catch (Exception e) {
        	println "Authenticaton Error: az login failed for provided parameters"
        	throw e
        }
   }
  
   
   withCredentials([usernamePassword(credentialsId: 'mgrose-gh-token', passwordVariable: 'token', usernameVariable: 'user')]) {
    stage('Create Jenkins Creds') {
       sh """
       curl -X POST 'https://${user}:${token}@jenkins.optum.com/commercialcloudeac/job/CommercialCloud-EAC/job/azure_tenant_refresh/credentials/store/folder/domain/_/createCredentials' \
    --data-urlencode 'json={
      "": "0",
      "credentials": {
        "scope": "GLOBAL",
        "id": "${params.subscriptionId}",
        "clientId": "${params.clientId}",
        "clientSecret": "${clientSecret}",
        "tenant": "${params.tenantId}",
        "subscriptionId": "${params.subscriptionId}",
        "azureEnvironmentName":"Azure",
        "description": "",
        "\$class": "com.microsoft.azure.util.AzureCredentials"
      }
    }'
       """
       }
   }
    withCredentials([usernamePassword(credentialsId: 'ccloudeac_admin-github-token', passwordVariable: 'token', usernameVariable: 'user')]) {

   stage('Update YML in Github') {
       git url: 'https://github.optum.com/CommercialCloud-EAC/azure_tenant_refresh.git', credentialsId: 'ccloudeac_admin-github-token' 

       def tenants = readYaml file: 'tenants.yml'
       println(tenants.dump())

       if (tenants[params.tenantId.plainText] == null || tenants[params.tenantId.plainText].empty) {
           tenants[params.tenantId.plainText] = []
       }
       
       if (!tenants[params.tenantId.plainText].any { it == params.subscriptionId.plainText }) {
           tenants[params.tenantId.plainText].add(params.subscriptionId.plainText)
       
           sh 'rm -f tenants.yml' 
           //writeYaml file: 'tenants.yml', data: tenants
           writeFile file: 'tenants.yml', text: mapToYaml(tenants)
           archiveArtifacts 'tenants.yml'
           
           sh """
           . /etc/profile.d/jenkins.sh
           
           git config --global user.email "matthew.grose@optum.com"
           git config --global user.name "ccloudeac_admin"
           git add tenants.yml
           git commit -m "add subscription ${params.subscriptionId.plainText}"
           git remote add github https://${token}:x-oauth-basic@github.optum.com/CommercialCloud-EAC/azure_tenant_refresh.git
           git push --set-upstream github master
           """
           }
       }
    }
  }
}


/***********************************
 writeYAML DSL
 writes a groovy map to a yaml file
 example usage
 writeYAML
   file: 'myfile.yaml'
   map: ['mykey':'myvalue']
 )
 ************************************/
@Grab(group='org.yaml', module='snakeyaml', version='1.18')
import org.yaml.snakeyaml.*
import groovy.json.JsonOutput

@NonCPS
def mapToYaml(map) {
  def json = JsonOutput.toJson(map)
  def options = new DumperOptions()
  options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
  //options.setDefaultScalarStyle(DumperOptions.ScalarStyle.LITERAL);
  options.setIndent(4)
  def yaml = new Yaml(options)
  def result = yaml.load(json)
  return yaml.dump(result)
}
