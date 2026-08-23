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

                sh 'java --version'
                sh 'mvn --version'
                sh 'docker --version'
                sh 'terraform --version'
                sh 'ansible --version'
                sh 'ssh -V'
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
                echo 'Provisioning AWS infrastructure with Terraform...'

                sh '''
                    cd terraform
                    terraform apply -auto-approve tfplan
                '''
            }
        }

        stage('Ansible Configure EC2') {
            steps {
                echo 'Configuring EC2 server with Ansible...'

                sh '''
                    ansible-playbook \
                    -i ansible/inventory.ini \
                    ansible/deploy.yml \
                    --private-key /var/lib/jenkins/devproject-key.pem
                '''
            }
        }

        stage('Deploy Application') {
            steps {
                echo 'Deploying application to Terraform EC2...'

                sh '''
                    ssh -i /var/lib/jenkins/devproject-key.pem \
                    -o StrictHostKeyChecking=no \
                    ec2-user@16.171.237.164 \
                    "
                    cd /home/ec2-user/final-devops-project &&
                    git pull origin main &&
                    docker-compose down || true &&
                    docker-compose up -d --build
                    "
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Checking applications on EC2...'

                sh '''
                    ssh -i /var/lib/jenkins/devproject-key.pem \
                    -o StrictHostKeyChecking=no \
                    ec2-user@16.171.237.164 \
                    "docker ps"
                '''
            }
        }
    }

    post {

        success {
            echo '======================================'
            echo 'CI/CD PIPELINE COMPLETED SUCCESSFULLY'
            echo '======================================'
        }

        failure {
            echo '======================================'
            echo 'CI/CD PIPELINE FAILED'
            echo 'Check the Jenkins console output.'
            echo '======================================'
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}
