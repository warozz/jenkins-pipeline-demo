
pipeline {
    //agent any  
    agent { 
        label 'master'//ระบุว่าจะรัน Pipeline นี้ที่เครื่อง jenkins Master
    }
   
     parameters {
        string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')

        text(name: 'BIOGRAPHY', defaultValue: '', description: 'Enter some information about the person')

        booleanParam(name: 'TOGGLE', defaultValue: true, description: 'Toggle this value')

        choice(name: 'CHOICE', choices: ['One', 'Two', 'Three'], description: 'Pick something')

        
    }

    stages {
        stage('Display value of Parameter'){
            steps {
		        echo "Hello ${params.PERSON}"
                echo "Biography: ${params.BIOGRAPHY}"
                echo "Toggle: ${params.TOGGLE}"
                echo "Choice: ${params.CHOICE}"
            }//end steps
        }//end stage

        stage('Input with parameter'){
            
                input {
                message "Should we continue?"
                ok "Yes, we should."
                submitter "alice,bob"
                parameters {
                    string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
                }
            }
        
            steps {
		        sh "echo 'Input with parameter'"		
            }//end steps
        }//end stage
        
        
        
       stage('Input with define submitter'){
            steps {
                input message: 'Deploy this version to PREPROD Environment?', ok: 'Deploy', submitter: 'admin,adminuser,sunan,chanon,tirada,yupa,usa'
		        sh "echo 'Input with define submitter'"		
            }//end steps
        }//end stage
        
        
        
        
    }//end stages

post {
        always {
            cleanWs()//Clean Workspace
        }
     }//end post

}//end pipeline
