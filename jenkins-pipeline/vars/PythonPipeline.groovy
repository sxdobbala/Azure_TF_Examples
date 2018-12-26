
import groovy.transform.Field
@Field String _PYTHON_VERSION = 3
@Field String _TERRAFORM_VERSION = 0.11

def call() {
  if (env.CHANGE_ID) { // check if pull request
    node('docker-cc-azure-slave') {
      stage('Checkout') {
        checkout scm
      }

      stage('Unit Tests') {
        try {
          sh '''#!/bin/bash -l
          python3 -m venv venv
          . venv/bin/activate
          python -m pip install pytest
          if [ -f ./requirements.txt ]; then
              python -m pip install -r requirements.txt
          fi
          python -m pytest --junitxml pytest.xml
          '''
        } finally {
          junit 'pytest.xml'
        }
      }

      if (fileExists("aws-cli-saml")) {
        stage('AWS CLI Integration Tests') {
        withCredentials([usernamePassword(credentialsId: 'AWS_AUTOMATION_USER', usernameVariable: 'AWS_SAML_USER', passwordVariable: 'AWS_SAML_PASSWORD')])
        {
            sh '''
            # Install and execute a virtual environment to run and collect the AWS credentials
             python3 -m venv venv
            . venv/bin/activate
            
            # change to the directory with the authenticate_py3 script, cert files and requirements
            cd aws-cli-saml

            export acc=618102609712
            export AWS_SAML_ROLE="arn:aws:iam::${acc}:role/AWS_${acc}_Service"
            python -m pip install -r requirements.txt
            python authenticate_py3.py
            aws s3 ls --profile=saml
            '''
        }
      }
      }
    }
  }
}
