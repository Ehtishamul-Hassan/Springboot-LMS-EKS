# 🚀 Ansible Role: jenkins

This Ansible role installs and configures **Jenkins**, **Apache Maven**, and **Java 21** (Amazon Corretto)
 on **Amazon Linux 2 / RHEL**-based systems.

---

## 📦 Features

- Installs **Java 21 (Amazon Corretto)**
- Downloads and sets up **Apache Maven**
- Sets environment variables (`JAVA_HOME`, `M2_HOME`, `PATH`)
- Adds Jenkins repo and GPG key
- Installs and enables **Jenkins service**
- Optionally installs **Git**
- Verifies versions of Java, Maven, and Jenkins

---

## 📁 Role Structure
jenkins/
├── defaults/
│ └── main.yml
├── handlers/
│ └── main.yml
├── meta/
│ └── main.yml
├── tasks/
│ └── main.yml
├── vars/
│ └── main.yml
├── README.md



## 🔧 Variables

### `defaults/main.yml`

---
maven_version: "3.9.9"
install_dir: "/opt/maven"
profile_file: "/home/jenkins/.bashrc"   # Change based on target user


#vars/main.yml

maven_zip: "apache-maven-{{ maven_version }}-bin.zip"
maven_url: "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/{{ maven_version }}/{{ maven_zip }}"
jenkins_repo_url: "https://pkg.jenkins.io/redhat-stable/jenkins.repo"
jenkins_repo_key: "https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key"



## 🛠️ Handlers

#handlers/main.yml

- name: restart jenkins
  service:
    name: jenkins
    state: restarted


## ▶️ Example Playbook

- name: Install Jenkins with Maven
  hosts: jenkins
  become: yes
  roles:
    - jenkins



## 📥 How to Use

#1.Generate Role:

bash
Copy
Edit
ansible-galaxy init jenkins

#2.Copy role content into the generated folder.

#3.Create Inventory File (hosts.ini):


[jenkins]
your_server_ip ansible_user=ec2-user

#4.Run the playbook:


ansible-playbook -i hosts.ini install_jenkins.yml



## 🔍 Validation

#At the end of execution, the role runs:

java -version

mvn -v

jenkins --version

#You will see output like:


☕ Java: openjdk version "21"
📦 Maven: Apache Maven 3.9.9
🔧 Jenkins: 2.x.x


## 📌 Supported Platforms

#✅ Amazon Linux 2

#✅ RHEL 7/8

#⚠️ Not tested on Ubuntu (yet)

## 👤 Author

#Ehtishamul Hassan
#GitHub: Ehtishamul-Hassan

## 📝 License
This project is licensed under the MIT License.


---

Let me know if you'd like a zipped version of the role or help pushing this to a GitHub repo.











