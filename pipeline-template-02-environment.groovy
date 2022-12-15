

pipeline {
    //agent any  
    agent { 
        label 'master'
    }
    environment {
        project = "new-test" //Block นี้จะเป็นการตั้งตัวแปร environment และการ Assign ค่าลงในตัวแปร
    }//end env 
    
    stages {
        stage('state1'){
            environment {
                    varinstage = "สามารถตั้งตัวตัวแปร ใน stage ได้" 
            }//end env 
            steps {
		        
                sh "echo 'stage 1'"

                sh script: '''
                    echo "แสดงค่าตัวแปร Project"
                    echo $project
                    echo $varinstage
                '''
                
            }//end steps
        }//end stage

    }//end stages

}//end pipeline


