import groovy.transform.Field

/*
   AWS Module Pipeline

   This AWS Module Pipeline is invoked by each of the AWS Modules to ensure 
   that all tests are ran during a PR and pass before a merge and approval.

   How to Call:
   AWSModulePipeline() - Default Call (same as AWSModulePipeline(false)), will run all tests in the module during PRs and won't lock each PR build
   AWSModulePipeline(true) - Will run all tests in the module during PRs and lock each PR build
   AWSModulePipeline(false) - Will run all tests in the module during PRs and won't lock each PR build

*/

@Field String _PYTHON_VERSION = 3.6
@Field String _TERRAFORM_VERSION = 0.11

/**
    Main entry point and will run all viable tests within the module.

    @param lockPR flag to indicate a lock between PRs; assumes no locking by default.
*/
def call(boolean lockPR=false) {
    if (env.CHANGE_ID) {
        if(lockPR) {
            def repo = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/.')[-2] + "-PR"
            lock(resource: repo) {
                runTests()
            }
        }
        else {
            runTests()
        }
    }
}

/**
    Runs rspec (unit), inspec (unit), and pytest (integration) tests in the 
    module and adds the results to the Jenkins tests
*/
def runTests() {
    node('docker-ruby-slave') {

        stage('Checkout') {
            checkout scm
        }

        withCredentials([
            usernamePassword(credentialsId: 'CC_Automation', usernameVariable: 'AWS_CLI_username', passwordVariable: 'AWS_CLI_password'),
            string(credentialsId: 'AWS_TEST_ROLE', variable: 'AWS_Test_role')
        ]){
            stage('Configure AWS') {

                sh """#!/bin/bash +x
                # Export Python 3 and execute the jenkins mixin scripts
                export PYTHON_VERSION=$_PYTHON_VERSION
                export TERRAFORM_VERSION=$_TERRAFORM_VERSION
                . /etc/profile.d/jenkins.sh
                
                # Make empty credentials files for AWS
                export AWS_LOC=\$HOME/.aws

                # Install and execute a virtual environment to run and collect the AWS credentials
                python$_PYTHON_VERSION -m venv venv
                . venv/bin/activate

                # Download python script and files to authenticate to AWS
                export AUTH_LOC=\$HOME/aws-cli-saml
                rm -rf \$AUTH_LOC
                mkdir \$AUTH_LOC

                cd \$AUTH_LOC
                for file in authenticate_py3.py prod.cer requirements.txt sandbox.cer; do \
                    curl https://github.optum.com/raw/CommercialCloud-EAC/python-scripts/master/aws-cli-saml/\$file > \$AUTH_LOC/\$file
                done;

                python -m pip install -r requirements.txt
 
                python authenticate_py3.py -e sandbox -u \$AWS_CLI_username -p \$AWS_CLI_password -r \$AWS_Test_role --profile default
                """
            }

            // RSPEC Unit Tests
            if( fileExists("spec") ) {
                stage('RSpec Unit Tests') {
                    try {
                        sh """#!/bin/bash -l
                        export TERRAFORM_VERSION=$_TERRAFORM_VERSION
                        . /etc/profile.d/jenkins.sh

                        echo 'running setup...'
                        terraform -v
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
                    } catch (Exception ex) { 
                        println("Error: ${ex.message}")
                    } finally {
                        junit 'rspec.xml'
                    }
                }
            }

            // Inspec Integration Tests
            if( fileExists("inspec") ) {
                stage('Inpsec Integration Tests') {
                    try {
                        sh """#!/bin/bash -l
                        export TERRAFORM_VERSION=$_TERRAFORM_VERSION
                        . /etc/profile.d/jenkins.sh

                        echo 'running setup...'
                        terraform -v
                        source '/home/jenkins/.rvm/scripts/rvm'
                        rvm use '2.3.1@global'
                        
                        # bundler
                        echo 'running gem and bundler...'
                        gem install bundler
                        echo "" >> Gemfile
                        echo "gem 'rspec_junit_formatter', '~> 0.3.0'" >> Gemfile
                        bundle install
                        
                        mkdir -p inspec/files

                        # only apply required infrastructure.
                        ls inspec/controls/ | tr '.' ' ' | awk '{ print \$1 }' | sed 's/_spec//g' | while read filename; do 
                            echo \"running terraform init and apply for \$filename...\"
                            cd examples/\$filename/
                            terraform init && terraform validate && terraform apply -auto-approve -input=false && echo 'writing output...' && terraform output --json > ../../inspec/files/\$filename.json
                            cd ../../
                        done

                        # run inspec to execute the tests
                        echo 'running inspec...'
                        inspec exec inspec/ -t aws:// --reporter=junit:inspec.xml cli

                        # destroy infrastructure that was created.
                        ls inspec/controls/ | tr '.' ' ' | awk '{ print \$1 }' | sed 's/_spec//g'| while read filename; do 
                            cd examples/\$filename/
                            echo 'running terraform destroy...'
                            terraform destroy -force
                            cd ../../
                        done
                        """
                    } catch (Exception ex) { 
                        println("Error: ${ex.message}")
                    } finally {
                        junit 'inspec.xml'
                    }
                }
            }

            // Pytest Unit Tests
            if (fileExists("pspec")) {
                stage('Pytest Unit Tests') {

                    // Run pytest
                    try {
                        sh """
                        cd pspec
                        export PYTHON_VERSION=$_PYTHON_VERSION
                        export TERRAFORM_VERSION=$_TERRAFORM_VERSION
                        . /etc/profile.d/jenkins.sh
                        terraform -v
                        python$_PYTHON_VERSION -m venv venv
                        . venv/bin/activate
                        python -m pip install pytest
                        if [ -f ./requirements.txt ]; then
                            python -m pip install -r requirements.txt
                        fi
                        python -m pytest --junitxml pytest.xml
                        """
                    } catch (Exception ex) {
                        println("Error: ${ex.message}")
                    } finally {
                        junit "pspec/pytest.xml"
                    }
                }                  
            }

            // Pytest Fully Automated Integration Tests
            if (fileExists("ptests")) {
                stage('Pytest Full Integration Tests') {

                    // Run pytest
                    try {
                        sh """
                        cd ptests
                        export PYTHON_VERSION=$_PYTHON_VERSION
                        export TERRAFORM_VERSION=$_TERRAFORM_VERSION
                        . /etc/profile.d/jenkins.sh  
                        python$_PYTHON_VERSION -m venv venv
                        . venv/bin/activate
                        python -m pip install pytest
                        if [ -f ./requirements.txt ]; then
                            python -m pip install -r requirements.txt
                        fi
                        python -m pytest --junitxml pytest.xml
                        """
                    } catch (Exception ex) {
                        println("Error: ${ex.message}")
                    } finally {
                       junit "ptests/pytest.xml"
                    }

                 }                  
            }

            // Pytest Integration Tests
            if (fileExists("tests")) {
                stage('Pytest Integration Tests') {

                    // Deploy examples
                    try {
                        sh """
                        export TERRAFORM_VERSION=$_TERRAFORM_VERSION
                        . /etc/profile.d/jenkins.sh
                        
                        for file in `ls tests/`; do
                            if [[ \$file == test_* ]] && [[ \$file == *.py ]]
                            then
                            example=\"\$( echo \"\$file\" | sed -e 's#^test_##; s#.py\$##' )\"
                            echo \"running terraform for example \$example\"
                            cd examples/\$example
                            terraform init && terraform validate && terraform apply -auto-approve -input=false && echo 'writing output...' && terraform output --json > \$example.json
                            cd ../../
                            fi
                        done
                        """
                    } catch (Exception ex) {
                        println("Error: ${ex.message}")
                    }

                    // Run pytest
                    try {
                        sh """
                        export PYTHON_VERSION=$_PYTHON_VERSION
                        . /etc/profile.d/jenkins.sh  

                        python$_PYTHON_VERSION -m venv venv
                        . venv/bin/activate
                        python -m pip install pytest
                        if [ -f ./requirements.txt ]; then
                            python -m pip install -r requirements.txt
                        fi

                        python -m pytest --junitxml pytest.xml
                        """
                    } catch (Exception ex) {
                        println("Error: ${ex.message}")
                    } finally {
                        junit "pytest.xml"
                    }

                    // Destroy examples
                    try {
                        sh """
                        export TERRAFORM_VERSION=$_TERRAFORM_VERSION
                        . /etc/profile.d/jenkins.sh
                        
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
                    } catch (Exception ex) {
                        println("Error: ${ex.message}")
                    }
                }                  
            }
        }
    }
}
