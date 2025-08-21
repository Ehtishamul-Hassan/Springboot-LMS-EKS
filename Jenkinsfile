pipeline {
    agent any

    environment {
        PATH = "$PATH:/opt/maven/apache-maven-3.9.9/bin"
        GIT_REPO = 'https://github.com/Ehtishamul-Hassan/Springboot-LMS-EKS.git'
        BRANCH = 'main'
        AWS_REGION = 'ap-south-1'
        ECR_REPO = '897722672704.dkr.ecr.ap-south-1.amazonaws.com/springboot-lib-app'
        IMAGE_TAG = 'latest'
        DOCKERHUB_REPO = 'springboot-lib'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Running checkout on Master Node'
                git branch: "${BRANCH}", url: "${GIT_REPO}"
                stash name: 'source', includes: '**/*'
            }
        }

        stage('Terraform Init & Apply') {
            steps {
                dir('terraform/environments/dev') {
                    sh 'terraform init && terraform validate && terraform apply -auto-approve'
                }
            }
        }

        stage('Wait for Instances') {
            steps {
                sh 'sleep 30'
            }
        }

        stage('Configure Sonar using Ansible') {
            steps {
                dir('ansible') {
                    sh '''
                        ansible-playbook install_sonar.yml
                    '''
                }
            }
        }

        stage('Configure Nexus-Agent using Ansible') {
            steps {
                dir('ansible') {
                    sh '''
                ansible-playbook install_nexus.yml
            '''
                }
            }
        }

        stage('Configure Build-Agent using Ansible') {
            steps {
                dir('ansible') {
                    sh '''
                        ansible-playbook builds_agent.yml
                    '''
                }
            }
        }

        stage('Configure Docker-Agent using Ansible') {
            steps {
                dir('ansible') {
                    sh '''
                        ansible-playbook docker.yml
                    '''
                }
            }
        }

        stage('Configure Automation-Host-Agent using Ansible') {
            steps {
                dir('ansible') {
                    sh '''
                        ansible-playbook managed_host.yml
                    '''
                }
            }
        }

        stage('Code Quality') {
            steps {
                withCredentials([string(credentialsId: 'sonar-creds', variable: 'SONAR_TOKEN')]) {
                    sh '''
                    mvn sonar:sonar \
                      -Dsonar.projectKey=springboot_lms \
                      -Dsonar.host.url=http://65.2.172.7:9000/ \
                      -Dsonar.login=$SONAR_TOKEN
                '''
                }
            }
        }

        stage('Build') {
            agent { label 'build-agent' }
            steps {
                echo 'Running build on Build Agent'
                unstash 'source'
                sh 'mvn clean package -DskipTests'
                stash name: 'build-output', includes: '**/*'
            }
        }

        stage('Artifact') {
            steps {
                nexusArtifactUploader artifacts: [[artifactId: 'Springboot-LibraryManagementSystem', classifier: '', file: 'target/Springboot-LibraryManagementSystem-0.0.1-SNAPSHOT.jar', type: 'jar']], credentialsId: 'nexus-cred', groupId: 'com.java', nexusUrl: '43.205.255.106:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'springartifact', version: '0.0.1-SNAPSHOT'
            }
        }

        stage('Docker Build & Push to ECR') {
            agent { label 'docker-agent' }
            steps {
                unstash 'build-output'
                sh '''
        # Login to ECR
        aws ecr get-login-password --region $AWS_REGION \
          | docker login --username AWS --password-stdin $ECR_REPO

        # Prepare JAR for Docker
        cp target/Springboot-LibraryManagementSystem-0.0.1-SNAPSHOT.jar app.jar

        # Build Docker Image
        docker build -t myapp:$IMAGE_TAG .

        # Tag Image for ECR
        docker tag myapp:$IMAGE_TAG $ECR_REPO:$IMAGE_TAG

        # Push to ECR
        docker push $ECR_REPO:$IMAGE_TAG
        '''
            }
        }

        stage('Push to Docker Hub') {
            agent { label 'docker-agent' }
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                              usernameVariable: 'DOCKERHUB_USER',
                                              passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        sh """
                  echo "${DOCKERHUB_PASSWORD}" | docker login -u "${DOCKERHUB_USER}" --password-stdin
                  docker tag myapp:${IMAGE_TAG} ${DOCKERHUB_USER}/${DOCKERHUB_REPO}:${IMAGE_TAG}
                  docker push ${DOCKERHUB_USER}/${DOCKERHUB_REPO}:${IMAGE_TAG}
                """
                                              }
                }
            }
        }

        stage('Update ArgoCD Manifests') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'github-creds', variable: 'GIT_TOKEN')]) {
                        sh """
                  git config --global user.email "asif282hassan@gmail.com"
                  git config --global user.name "Ehtishamul-Hassan"
                  git config --global --unset credential.helper || true

                  if [ -d "Springboot-LMS-EKS" ]; then
                      echo "Repo already exists. Resetting and pulling latest..."
                      cd Springboot-LMS-EKS
                      git reset --hard
                      git pull
                      git remote set-url origin https://git:${GIT_TOKEN}@github.com/Ehtishamul-Hassan/Springboot-LMS-EKS.git
                  else
                      git clone https://git:${GIT_TOKEN}@github.com/Ehtishamul-Hassan/Springboot-LMS-EKS.git
                      cd Springboot-LMS-EKS
                  fi

                  cd helm-k8-service/user-service
                  sed -i "s|tag:.*|tag: \\"${IMAGE_TAG}\\"|g" values-efk.yaml

                  git add values-efk.yaml
                  git commit -m "Update image tag to ${IMAGE_TAG}" || echo "No changes"
                  git push origin main
                """
                    }
                }
            }
        }

        stage('Deploy to EKS using Helm') {
            agent { label 'automation-host' }
            steps {
                unstash 'source'
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                          usernameVariable: 'DOCKERHUB_USER',
                                          passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh """
              aws eks update-kubeconfig --region ap-south-1 --name my-ec2-eks
              cd helm-k8-service/user-service/

            helm upgrade --install user-service . \
    -f values-efk.yaml \
    --namespace user-service \
    --create-namespace \
    --set app.image.repository=${DOCKERHUB_USER}/${DOCKERHUB_REPO} \
    --set app.image.tag=${IMAGE_TAG}

            """
                                          }
            }
        }
    }
}
