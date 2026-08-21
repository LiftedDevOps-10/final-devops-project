pipeline {

    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-amazon-corretto.x86_64'
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }

    stages {

        stage('Checkout') {

            steps {

                echo 'Checking out source code...'

                checkout scm
            }
        }


        stage('Verify Environment') {

            steps {

                echo 'Checking build environment...'

                sh 'java --version'

                sh 'mvn --version'

                sh 'docker --version'

                sh 'docker compose version'
            }
        }


        stage('Build Java Application') {

            steps {

                echo 'Building Java application with Maven...'

                dir('java-app') {

                    sh 'mvn clean package'
                }
            }
        }


        stage('Build Docker Images') {

            steps {

                echo 'Building Docker images...'

                sh 'docker compose build'
            }
        }


       stage('Deploy Applications') {
           steps {
               sh 'docker compose down || true'
               sh 'docker compose up -d'
           }
       } 


        stage('Verify Deployment') {

            steps {

                echo 'Checking running containers...'

                sh 'docker compose ps'
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