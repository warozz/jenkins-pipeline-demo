pipeline {
     agent { label 'k8sbuild'}
    
    environment {
        vEnv = "uat"
        appDes = "$WORKSPACE/app"

    }//end env 
    
   parameters {
        string(name: 'paraTag', defaultValue: 'develop', description: 'Git Branch or Tag?')
    }


    stages {

     stage('Clone Tag'){
            steps {
             dir('dockerbuild'){ \
             checkout([$class: 'GitSCM', branches: [[name: "${params.paraTag}"]], \
             userRemoteConfigs: [[url: 'https://gitserver.thailife.com/ssw/k8s-dockerfile.git', \
             credentialsId: 'gitlabdevuserid']]])}
             
             sh "cd $WORKSPACE/dockerbuild && ls"
             
            }//end steps
        }//end stage

    }//end states


////////////////////////////////

    post {  always {
            cleanWs()
                    sh script: '''
                       echo ".."
                        #rm -rf $WORKSPACE/deploy*
                        #rm -rf $WORKSPACE/Docker*
                    '''
        }
    }


////////////////////////////////        


}//end pipeline
