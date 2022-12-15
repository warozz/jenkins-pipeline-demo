
pipeline {
    //agent any  
    agent { 
        label 'master'
    }
  stages {
        stage('Git Clone'){
            steps {
                deleteDir()
                dir('script'){
                    git credentialsId: 'gitlabuser', \
                    url: 'http://172.25.122.8/customer-dashboard/non-prod.git', \
                    branch: 'master'
                }

        }//end steps
      }//end stage
    }//end stages
}//end pipeline
