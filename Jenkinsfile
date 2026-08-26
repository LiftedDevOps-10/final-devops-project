pipeline {

    agent any

    environment {
        AWS_DEFAULT_REGION = 'eu-north-1'
        AWS_REGION         = 'eu-north-1'

        TERRAFORM_DIR      = 'terraform'
        ANSIBLE_DIR        = 'ansible'

        ANSIBLE_PLAYBOOK   = 'deploy.yml'
        ANSIBLE_INVENTORY  = 'inventory.ini'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '=== CHECKOUT ==='

                checkout scm

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

        stage('Terraform Init') {
            steps {
                echo '=== TERRAFORM INIT ==='

                dir("${TERRAFORM_DIR}") {
                    sh '''
                        set -e

                        terraform init
                    '''
                }
            }
        }

        stage('Terraform Plan') {
            steps {
                echo '=== TERRAFORM PLAN ==='

                dir("${TERRAFORM_DIR}") {
                    sh '''
                        set -e

                        terraform plan -out=tfplan
                    '''
                }
            }
        }

        stage('Terraform Approval') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    input message: 'Terraform plan is ready. Do you want to apply these   changes?', 
                          ok: 'Apply Terraform'
               }
           }
        }

        stage('Terraform Apply') {
            steps {
                echo '=== TERRAFORM APPLY ==='

                dir("${TERRAFORM_DIR}") {
                    sh '''
                        set -e

                        terraform apply tfplan
                   '''
              }
          }
        }

        stage('Get Public IP') {
            steps {
                echo '=== GET EC2 PUBLIC IP ==='

                script {

                    env.EC2_PUBLIC_IP = sh(
                        script: '''
                            cd terraform
                            terraform output -raw ec2_public_ip
                        ''',
                        returnStdout: true
                    ).trim()

                    echo "EC2 Public IP: ${env.EC2_PUBLIC_IP}"

                    if (!env.EC2_PUBLIC_IP) {
                        error('Terraform did not return an EC2 public IP.')
                    }
                }
            }
        }

        stage('Create Dynamic Ansible Inventory') {
            steps {
                echo '=== CREATE DYNAMIC ANSIBLE INVENTORY ==='

                dir("${ANSIBLE_DIR}") {
                    sh """
                        set -e

                        cat > ${ANSIBLE_INVENTORY} <<EOF
[webserver]
terraform_ec2 ansible_host=${EC2_PUBLIC_IP}

[webserver:vars]
ansible_user=ec2-user
ansible_ssh_private_key_file=/var/lib/jenkins/devproject-key.pem
EOF

                        echo "Generated inventory:"
                        cat ${ANSIBLE_INVENTORY}
                    """
                }
            }
        }

        stage('Wait for SSH') {
            steps {
                echo '=== WAIT FOR EC2 SSH ==='

                sh '''
                    set -e

                    echo "Waiting for SSH on ${EC2_PUBLIC_IP}..."

                    for i in $(seq 1 12); do

                        if ssh \
                            -i /var/lib/jenkins/devproject-key.pem \
                            -o StrictHostKeyChecking=no \
                            -o ConnectTimeout=5 \
                            ec2-user@${EC2_PUBLIC_IP} "echo SSH_READY" 2>/dev/null
                        then
                            echo "SSH is ready."
                            exit 0
                        fi

                        echo "SSH not ready yet. Attempt $i/12..."
                        sleep 10
                    done

                    echo "ERROR: SSH connection could not be established."
                    exit 1
                '''
            }
        }

        stage('Ansible Deployment') {
            steps {
                echo '=== ANSIBLE DEPLOYMENT ==='

                dir("${ANSIBLE_DIR}") {
                    sh '''
                        set -e

                        /var/lib/jenkins/.local/bin/ansible-playbook \
                            -i inventory.ini \
                            deploy.yml
                    '''
                }
            }
        }

        stage('Docker Verification') {
            steps {
                echo '=== DOCKER VERIFICATION ==='

                sh '''
                    set -e

                    echo "Checking Docker on EC2..."

                    ssh \
                        -i /var/lib/jenkins/devproject-key.pem \
                        -o StrictHostKeyChecking=no \
                        ec2-user@${EC2_PUBLIC_IP} <<'EOF'

                    echo "=== Docker Version ==="
                    docker --version

                    echo ""
                    echo "=== Docker Compose Version ==="
                    docker compose version

                    echo ""
                    echo "=== Running Containers ==="
                    sudo docker ps

                    echo ""
                    echo "=== Project Directory ==="
                    ls -la ~/final-devops-project

                    EOF
                '''
            }
        }
    }

    post {

        success {
            echo """
            ==========================================
                    DEPLOYMENT SUCCESSFUL
            ==========================================

            EC2 Public IP:
            ${env.EC2_PUBLIC_IP}

            Terraform:
            SUCCESS

            Ansible:
            SUCCESS

            Docker:
            VERIFIED

            ==========================================
            """
        }

        failure {
            echo """
            ==========================================
                    DEPLOYMENT FAILED
            ==========================================

            Check the failed stage above.

            ==========================================
            """
        }

        always {
            echo 'Pipeline completed.'

            sh '''
                echo "Cleaning temporary Terraform plan..."

                rm -f terraform/tfplan || true
            '''
        }
    }
}
