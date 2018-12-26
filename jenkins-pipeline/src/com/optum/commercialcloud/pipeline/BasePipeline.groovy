package com.optum.commercialcloud.pipeline

def init() {
    if (env.CHANGE_ID) { // check if pull request
        node('docker-ruby-slave') {
            stage('Checkout') {
                checkout scm
            }
            withCredentials([ [$class: 'StringBinding', credentialsId: 'testing-ARM_CLIENT_ID', variable: 'ARM_CLIENT_ID'], \
                                    [$class: 'StringBinding', credentialsId: 'testing-ARM_CLIENT_SECRET', variable: 'ARM_CLIENT_SECRET'], \
                                    [$class: 'StringBinding', credentialsId: 'testing-ARM_TENANT_ID', variable: 'ARM_TENANT_ID'], \
                                    [$class: 'StringBinding', credentialsId: 'testing-ARM_SUBSCRIPTION_ID', variable: 'ARM_SUBSCRIPTION_ID'],
            ]) {            
                stage('Unit Tests') {

                    try {
                        sh """#!/bin/bash
                        export TERRAFORM_VERSION=0.11.1
                        . /etc/profile.d/jenkins.sh

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
                    } catch (Exception ex) { 

                    } finally {
                        junit 'rspec.xml'
                    }
                }
                if( fileExists("inspec") ) {
                    stage('Integration Tests') {
                        try {
                            sh """#!/bin/bash
                            export TERRAFORM_VERSION=0.11.1
                            . /etc/profile.d/jenkins.sh

                            export AZURE_TENANT_ID="${ARM_TENANT_ID}"
                            export AZURE_CLIENT_ID="${ARM_CLIENT_ID}"
                            export AZURE_CLIENT_SECRET="${ARM_CLIENT_SECRET}"
                            export AZURE_SUBSCRIPTION_ID="${ARM_SUBSCRIPTION_ID}"

                            echo 'running setup...'
                            terraform -v
                            az -v
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
                                terraform init && terraform apply -auto-approve -input=false && echo 'writing output...' && terraform output --json > ../../inspec/files/\$filename.json
                                cd ../../
                            done

                            # run inspec to execute the tests
                            echo 'running inspec...'
                            inspec exec inspec/ -t azure:// --reporter=junit:inspec.xml cli

                            # destroy infrastructure that was created.
                            ls inspec/controls/ | tr '.' ' ' | awk '{ print \$1 }' | sed 's/_spec//g'| while read filename; do 
                                cd examples/\$filename/
                                echo 'running terraform destroy...'
                                terraform destroy -force
                                cd ../../
                            done
                            """
                        } catch (Exception ex) { 

                        } finally {
                            junit 'inspec.xml'
                        }
                    }
                }
            }
        }
    }
}
