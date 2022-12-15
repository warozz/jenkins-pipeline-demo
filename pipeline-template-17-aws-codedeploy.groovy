pipeline {
    agent any

    stages { // all states

        stage('checkout GIT') {
            steps {
                script {
                    sh script: '''
                        cd $WORKSPACE/
                    ''' 

                    // dir('cicdscript'){
                    //     git credentialsId: 'awvmpdap01a', url: 'https://awvmpdap03a.thailife.local/DigitalForLife/cicdscript.git'
                    // }
                    dir('codedeploy-script') {
                        git branch: 'master', credentialsId: 'jenkins_oncloud', url: 'https://gitserver.thailife.com/dev-mst/non-agent/dfl/jenkins/web-survey-bg-pipeline.git'
                    }

                    dir('thai-life-rating-page'){
                        git branch: 'uat', credentialsId: 'jenkins_oncloud', url: 'https://gitserver.thailife.com/dev-mst/non-agent/dfl/web/thai-life-rating-page.git'
                    }

                    sh script: '''
                        pwd
                        ls -ll
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh script:'''
                        cd $WORKSPACE/thai-life-rating-page
                        git status

                        //docker image rm -f img-build-dfl-web-ratting-uat || (echo "Image img-build-dfl-web-ratting-uat didn't exist so not removed."; exit 0) && \
                        //docker build -t img-build-dfl-web-ratting-uat . && \
                        //docker run --rm --name build-dfl-web-ratting-uat \
                        //-v ${PWD}:/code img-build-dfl-web-ratting-uat && \
                        //docker image rm -f img-build-dfl-web-ratting-uat || (echo "Image img-build-dfl-web-ratting-uat didn't exist so not removed."; exit 0)
                        
                        pwd
                        ls -ll
                    '''
                }//end script

                // withCredentials([usernamePassword(credentialsId: 'deployuser', passwordVariable: 'i_password', usernameVariable: 'i_username')]) {
                //     sh script: '''
                //     cd $WORKSPACE/cicdscript
                //     sh copyartifact.sh "$i_password" "DFL-WEB-SURVEY-UAT" "thai-life-rating-page"
                //     '''
                // }//end withCredentials 

            }//end of steps
            //post {always {cleanWs()}}//end post
        }

        stage('Deploy to UAT') {
            steps {
                script { 
                    sh script:'''
                        pwd 
                        ls -ll
                    ''' 
                }//end script 
                //deleteDir()
               
                // dir('codedeploy-script') {
                //     git branch: 'master', credentialsId: 'jenkins_oncloud', url: 'https://gitserver.thailife.com/dev-mst/non-agent/dfl/jenkins/web-survey-bg-pipeline.git'
                // }

                // dir('cicdscript'){
                //   git credentialsId: 'awvmpdap01a', url: 'https://awvmpdap03a.thailife.local/DigitalForLife/cicdscript.git'
                // }

                // withCredentials([usernamePassword(credentialsId: 'deployuser', passwordVariable: 'i_password', usernameVariable: 'i_username')]) {
                //    sh script: '''
                //     cd $WORKSPACE/cicdscript
                //     sh copytoworkspace.sh "$i_password" "DFL-WEB-SURVEY-UAT"
                //     cd $WORKSPACE
                //    '''
                // }//end withCredentials 
 
                //Deployment
                step([$class: 'AWSCodeDeployPublisher', 
                applicationName: 'dfl-survey',
                awsAccessKey: '', awsSecretKey: '', 
                credentials: 'iamRoleArn', 
                deploymentConfig: 'CodeDeployDefault.OneAtATime', 
                deploymentGroupAppspec: false, 
                deploymentGroupName: 'dfl-survey-uat',
                deploymentMethod: 'deploy', excludes: '', 
                iamRoleArn: 'arn:aws:iam::195049965683:role/jenkins-deploy-role',
                includes: '**', pollingFreqSec: 15, 
                pollingTimeoutSec: 1000, proxyHost: '', 
                proxyPort: 0, region: 'ap-southeast-1', 
                s3bucket: 'codedeploy-pkg.thailife.local', 
                s3prefix: 'DFL/WEB/DFL-WEB-SURVEY-UAT-pipeline-Blue_green/$BUILD_NUMBER',
                subdirectory: 'codedeploy-script', 
                versionFileName: '', 
                waitForCompletion: true])
                     
              
            }//end of steps
            post {always {cleanWs()}}//end post
        }

    } //end all states
}//end pipeline
