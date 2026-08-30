pipeline {

    agent any

    environment {
        AWS_DEFAULT_REGION = 'eu-north-1'
        AWS_REGION         = 'eu-north-1'

        TERRAFORM_DIR      = 'terraform'
        ANSIBLE_DIR        = 'ansible'

        ANSIBLE_PLAYBOOK   = 'deploy.yml'
        ANSIBLE_INVENTORY  = 'inventory.ini'
        
        AWS_CREDS_ID       = 'aws-credentials-id' 
    }

    stages {

        stage('Checkout') {
            steps {
                echo '=== CHECKOUT ==='
                git branch: 'main', 
                    url: 'https://github.com/LiftedDevOps-10/final-devops-project.git'

                sh '''
                    echo "Current directory:"
                    pwd
                    echo "Project files:"
                    ls -la
                '''
            }
        }

        stage('Verify Tools') {
            steps {
                echo '=== VERIFY TOOLS ==='
                withCredentials([usernamePassword(credentialsId: "${AWS_CREDS_ID}", passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                    sh '''
                        set -e

                        echo "Terraform:"
                        terraform version

                        echo ""
                        echo "AWS CLI:"
                        aws --version

                        echo ""
                        echo "Ansible:"
                        /var/lib/jenkins/.local/bin/ansible --version

                        echo ""
                        echo "Ansible Playbook:"
                        /var/lib/jenkins/.local/bin/ansible-playbook --version

                        echo ""
                        echo "Docker:"
                        docker --version

                        echo ""
                        echo "Docker Compose:"
                        docker compose version

                        echo ""
                        echo "AWS Identity:"
                        aws sts get-caller-identity
                    '''
                }
            }
        }

        stage('Terraform Init') {
            steps {
                echo '=== TERRAFORM INIT ==='
                withCredentials([usernamePassword(credentialsId: "${AWS_CREDS_ID}", passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                    dir("${TERRAFORM_DIR}") {
                        sh '''
                            set -e
                            terraform init
                        '''
                    }
                }
            }
        }

        stage('Terraform Plan') {
            steps {
                echo '=== TERRAFORM PLAN ==='
                withCredentials([usernamePassword(credentialsId: "${AWS_CREDS_ID}", passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                    dir("${TERRAFORM_DIR}") {
                        sh '''
                            set -e
                            terraform plan -out=tfplan
                        '''
                    }
                }
            }
        }

        stage('Terraform Apply') {
            steps {
                echo '=== TERRAFORM APPLY ==='
                withCredentials([usernamePassword(credentialsId: "${AWS_CREDS_ID}", passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                    dir("${TERRAFORM_DIR}") {
                        sh '''
                            set -e
                            terraform apply tfplan
                       '''
                    }
                }
            }
        }

        stage('Get Public IP') {
            steps {
                echo '=== GET EC2 PUBLIC IP ==='
                script {
                    withCredentials([usernamePassword(credentialsId: "${AWS_CREDS_ID}", passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                        env.EC2_PUBLIC_IP = sh(
