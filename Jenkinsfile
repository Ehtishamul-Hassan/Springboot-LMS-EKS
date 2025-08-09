pipeline {
    agent any

    environment {
        PATH = "$PATH:/opt/maven/apache-maven-3.9.9/bin"
        GIT_REPO = 'https://github.com/Ehtishamul-Hassan/Springboot-LMS.git'
        BRANCH = 'main'
        REMOTE_HOST = '13.232.225.107'
        REMOTE_USER = 'ec2-user'
        REMOTE_DIR  = '/home/ec2-user/docker-build'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Terraform Init & Apply') {
            steps {
                dir('terraform') {
                    sh '''
                        terraform init
                        terraform validate
                        terraform apply -auto-approve
                    '''
                }
            }
        }

        // stage('Wait for Instances') {
        //     steps {
        //         sh 'sleep 30'
        //     }
        // }

        // stage('Configure Sonar using Ansible') {
        //     steps {
        //         dir('ansible') {
        //             sh '''
        //                 ansible-playbook install_sonar.yml
        //             '''
        //         }
        //     }
        // }

        // stage('Configure Nexus using Ansible') {
        //     steps {
        //         dir('ansible') {
        //             sh '''
        //         ansible-playbook install_nexus.yml
        //     '''
        //         }
        //     }
        // }

        stage('Configure Docker using Ansible') {
            steps {
                dir('ansible') {
                    sh '''
                        ansible-playbook docker.yml
                    '''
                }
            }
        }

        // stage('Code Quality') {
        //     steps {
        //         withCredentials([string(credentialsId: 'sonar-creds', variable: 'SONAR_TOKEN')]) {
        //             sh '''
        //             mvn sonar:sonar \
        //               -Dsonar.projectKey=springboot_lms \
        //               -Dsonar.host.url=http://65.2.172.7:9000/ \
        //               -Dsonar.login=$SONAR_TOKEN
        //         '''
        //         }
        //     }
        // }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Artifact') {
            steps {
                nexusArtifactUploader artifacts: [[artifactId: 'Springboot-LibraryManagementSystem', classifier: '', file: 'target/Springboot-LibraryManagementSystem-0.0.1-SNAPSHOT.jar', type: 'jar']], credentialsId: 'nexus-cred', groupId: 'com.java', nexusUrl: '3.110.94.2:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'springartifact', version: '0.0.1-SNAPSHOT'
            }
        }

        stage('Download JAR from Nexus') {
            steps {
                sshagent(['ansible-ec2-key']) {
                    sh """
        ssh -o StrictHostKeyChecking=no $REMOTE_USER@$REMOTE_HOST '
            mkdir -p $REMOTE_DIR &&
            wget http://3.110.94.2:8081/repository/springartifact/com/java/Springboot-LibraryManagementSystem/0.0.1-SNAPSHOT/Springboot-LibraryManagementSystem-0.0.1-20250802.180553-2.jar -O /home/ec2-user/docker-build/app.jar
        '
        """
                }
            }
        }

        stage('Copy Docker Files to Docker-Server') {
            steps {
                sshagent(['ansible-ec2-key']) {
                    sh """
                    for file in Dockerfile docker-compose.yml .env; do
                        rsync -avz --ignore-times -e "ssh -o StrictHostKeyChecking=no" \
                        /var/lib/jenkins/workspace/JenkinsPipeline/\$file \
                        $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/
                    done
                    """
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sshagent(['ansible-ec2-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no $REMOTE_USER@$REMOTE_HOST "
                            cd $REMOTE_DIR &&
                            docker-compose build
                        "
                    '''
                }
            }
        }

        stage('Run Docker Compose') {
            steps {
                sshagent(['ansible-ec2-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no $REMOTE_USER@$REMOTE_HOST "
                            cd $REMOTE_DIR &&
                            docker-compose up -d
                        "
                    '''
                }
            }
        }
    }
}
