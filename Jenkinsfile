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
            }
        }

        stage('Deploy to EC2') {
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
