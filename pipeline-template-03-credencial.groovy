pipeline {
    //agent any  
    agent { 
        label 'master'
    }
  stages {
        stage('withCredentials'){
            environment {
                    project = "new-test" 
            }//end env 
            steps {
        		withCredentials([usernamePassword(credentialsId: 'deployuser', \
        		                passwordVariable: 'i_password', \
        		                usernameVariable: 'i_username')]) {
        		                    
                		sh script: '''
                			echo $i_username
                			echo $i_password
                            echo $project
                		'''
                	
                	 	cleanWs()
        		}//end withCredentials 
        }//end steps
      }//end stage
    }//end stages
}//end pipeline
