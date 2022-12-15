
pipeline {
    //agent any  
    agent { 
        label 'master'//ระบุว่าจะรัน Pipeline นี้ที่เครื่อง jenkins Master
    }
    environment {
        project = "new-test" //Block นี้จะเป็นการตั้งตัวแปร environment และการ Assign ค่าลงในตัวแปร
    }//end env 
    
    stages {
        stage('state1'){
            steps {
		        sh "echo 'stage 1'"
            }//end steps
        }//end stage

        stage('state2'){
            steps {
		        sh "echo 'stage 2'"		
            }//end steps
        }//end stage
    }//end stages

post {
        always {
            cleanWs()//Clean Workspace
        }
     }//end post

}//end pipeline
