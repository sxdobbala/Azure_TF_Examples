import groovy.transform.Field

@Field String _PYTHON_VERSION = "3.6"
@Field String _AZURECLI_VERSION = "2.0.37"
@Field String _TERRAFORM_VERSION = "0.11.1"

def call() {
  if (env.CHANGE_ID) {
    node('docker-azure-module-slave') {
      stage('Checkout') {
        checkout scm
      }

      def testServicePrincipal = "keystone-nonprod-module-testing-sp"

      withCredentials([
        azureServicePrincipal(credentialsId: testServicePrincipal, subscriptionIdVariable: 'ARM_SUBSCRIPTION_ID', 
                              clientIdVariable: 'ARM_CLIENT_ID', clientSecretVariable: 'ARM_CLIENT_SECRET', 
                              tenantIdVariable: 'ARM_TENANT_ID')
      ]) {
        stage('Unit Tests') {
          try {
            sh """#!/bin/bash
            export TERRAFORM_VERSION=$_TERRAFORM_VERSION
            export AZURECLI_VERSION=$_AZURECLI_VERSION
            . /etc/profile.d/jenkins.sh
            export PATH="/tools/azurecli/azurecli-$_AZURECLI_VERSION/env-$_AZURECLI_VERSION/bin:\$PATH"

            echo 'running setup...'
            terraform -v
            az -v
            az login --service-principal -u ${ARM_CLIENT_ID} -p ${ARM_CLIENT_SECRET} --tenant ${ARM_TENANT_ID}
            source '/home/jenkins/.rvm/scripts/rvm'
            rvm use '2.3.1@global'

            # bundler
            echo 'running gem and bundler...'
            gem install bundler
            echo "" >> Gemfile
            echo "gem 'rspec_junit_formatter', '~> 0.3.0'" >> Gemfile
            bundle install
                            
            # rspec
            echo 'running rspec...'
            bundle exec rspec --format RspecJunitFormatter --out rspec.xml
            """
          } catch (Exception e) {

          } finally {
            junit 'rspec.xml'
          }
        }

        if (fileExists("tests")) {
          stage('Integration Tests') {

            // Deploy examples
            try {
              sh """
              export TERRAFORM_VERSION=$_TERRAFORM_VERSION
              export AZURECLI_VERSION=$_AZURECLI_VERSION
              . /etc/profile.d/jenkins.sh
              export PATH="/tools/azurecli/azurecli-$_AZURECLI_VERSION/env-$_AZURECLI_VERSION/bin:\$PATH"
              
              for file in `ls tests/`; do
                if [[ \$file == test_* ]] && [[ \$file == *.py ]]
                then
                  example=\"\$( echo \"\$file\" | sed -e 's#^test_##; s#.py\$##' )\"
                  echo \"running terraform for example \$example\"
                  cd examples/\$example
                  terraform init && terraform apply -auto-approve -input=false && echo 'writing output...' && terraform output --json > \$example.json
                  cd ../../
                fi
              done
              """
            } catch (Exception e) {

            }

            // Run pytest
            try {
              sh """
              export PYTHON_VERSION=$_PYTHON_VERSION
              . /etc/profile.d/jenkins.sh

              python3.6 -m venv venv
              . venv/bin/activate

              pip install pytest==3.6.3
              pip install azure

              pytest --junitxml pytest.xml
              """
            } catch (Exception e) {

            } finally {
              junit "pytest.xml"
            }

            // Destroy examples
            try {
              sh """
              export TERRAFORM_VERSION=$_TERRAFORM_VERSION
              export AZURECLI_VERSION=$_AZURECLI_VERSION
              . /etc/profile.d/jenkins.sh
              export PATH="/tools/azurecli/azurecli-$_AZURECLI_VERSION/env-$_AZURECLI_VERSION/bin:\$PATH"
              
              for file in `ls tests/`; do
                if [[ \$file == test_* ]] && [[ \$file == *.py ]]
                then
                  example=\"\$( echo \"\$file\" | sed -e 's#^test_##; s#.py\$##' )\"
                  echo \"destroying the example for \$example\"
                  cd examples/\$example
                  terraform destroy -force
                  cd ../../
                fi
              done
              """
            } catch (Exception e) {

            }
          }                  
        }
      }
    }
  }
}