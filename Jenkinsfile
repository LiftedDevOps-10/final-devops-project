pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Verify Jenkins Environment') {
            steps {
                echo 'Checking Jenkins environment...'

                sh '''
                    java --version
                    mvn --version
                    docker --version
                    terraform version
                    ansible --version
                '''
            }
        }

        stage('Terraform Init') {
            steps {
                echo 'Initializing Terraform...'

                sh '''
                    cd terraform
                    terraform init
                '''
            }
        }

        stage('Terraform Validate') {
            steps {
                echo 'Validating Terraform configuration...'

                sh '''
                    cd terraform
                    terraform validate
                '''
            }
        }

        stage('Terraform Plan') {
            steps {
                echo 'Creating Terraform execution plan...'

                sh '''
                    cd terraform
                    terraform plan -out=tfplan
                '''
            }
        }

        stage('Terraform Apply') {
            steps {
                echo 'Applying Terraform infrastructure...'

                sh '''
                    cd terraform
                    terraform apply -auto-approve tfplan
                '''
            }
        }

        stage('Get EC2 IP') {
            steps {
                script {
                    env.EC2_IP = sh(
                        script: 'cd terraform && terraform output -raw ec2_public_ip',
                        returnStdout: true
                    ).trim()

                    echo "Terraform EC2 Public IP: ${env.EC2_IP}"
                }
            }
        }

        stage('Generate Ansible Inventory') {
            steps {
                echo 'Generating dynamic Ansible inventory...'

                sh '''
                    cat > ansible/inventory.ini <<EOF
[webserver]
terraform_ec2 ansible_host=${EC2_IP}

[webserver:vars]
ansible_user=ec2-user
ansible_ssh_private_key_file=/var/lib/jenkins/devproject-key.pem
EOF
                '''
            }
        }

        stage('Ansible Ping') {
            steps {
                echo 'Testing Ansible connection to EC2...'

                sh '''
                    ansible webserver \
                    -i ansible/inventory.ini \
                    -m ping
                '''
            }
        }

        stage('Ansible Deploy') {
            steps {
                echo 'Configuring EC2 and deploying applications with Ansible...'

                sh '''
                    ansible-playbook \
                    -i ansible/inventory.ini \
                    ansible/deploy.yml
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Checking applications on EC2...'

                sh '''
                    ssh -i /var/lib/jenkins/devproject-key.pem \
                    -o StrictHostKeyChecking=no \
                    ec2-user@${EC2_IP} \
                    "docker ps"
                '''
            }
        }
    }

    post {

        success {
            echo '''
========================================
CI/CD PIPELINE COMPLETED SUCCESSFULLY
========================================
'''
        }

        failure {
            echo '''
========================================
CI/CD PIPELINE FAILED
========================================
Check the Jenkins console output.
'''
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}
