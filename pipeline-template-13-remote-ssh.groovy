
properties([parameters([string(name: 'IPAddress', trim: true)])])

def remote = [:]
remote.name = "k8sbuild"
//remote.host = "kbnon-mgt.thailife.com"
remote.host = "$params.IPAddress"
remote.allowAnyHosts = true



pipeline {
    //agent any  
    agent { 
        label 'master'//ระบุว่าจะรัน Pipeline นี้ที่เครื่อง jenkins Master
    }
    
      //agent { label 'k8sbuild'}
    environment {
        project = "new-test" //Block นี้จะเป็นการตั้งตัวแปร environment และการ Assign ค่าลงในตัวแปร
    }//end env 
    
    stages {
        stage('state1'){
            steps {
		        echo "Hello $params.IPAddress"
            }//end steps
        }//end stage

        stage('state2'){
            steps {
		        sh "echo 'stage 2'"	
		        
		        withCredentials([usernamePassword(credentialsId: 'deployuser', passwordVariable: 'i_password', usernameVariable: 'i_username')]) {
                    script{
                       remote.user = i_username
                       remote.password = i_password
                       sshCommand remote: remote, command: 'ls'
                     }//script
		        }
            }//end steps
        }//end stage
    }//end stages

post {
        always {
            cleanWs()//Clean Workspace
        }
     }//end post

}//end pipeline
