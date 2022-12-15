pipeline{
    agent any
    stages{
        stage("SVN Checkout") {
             steps {
                deleteDir()
                checkout([$class: 'SubversionSCM', \
                additionalCredentials: [], excludedCommitMessages: '', \
                excludedRegions: '', excludedRevprop: '', excludedUsers: '', \
                filterChangelog: false, ignoreDirPropChanges: false, \
                includedRegions: '', \
                locations: [[credentialsId: 'svnjenkins', \
                depthOption: 'infinity', ignoreExternalsOption: true, \
                local: 'maincode', \
                remote: "${svnserver}/svn/tlicomponent/CRM/CustomerDashboard/customer-forward/branches/DEV"]], \
                quietOperation: true, \
                workspaceUpdater: [$class: 'UpdateUpdater']])
            }
        } //end state
     
stage("Build Yarn") {
    steps {
        sh script: '''
            cd $WORKSPACE/maincode && ls
            
            docker run -v $(pwd):/DEV \
            -v /home/cideployment/jenkins/workspace/noderepo:/tmp/repo \
            -w /DEV \
            --rm node:12.21 /bin/bash -c "npm config set prefix /tmp/repo \
            && yarn && yarn build:dev"

            cd $WORKSPACE/maincode/ && ls
            cd $WORKSPACE/maincode/build/ && ls
        '''
    }//end steps
}//end stage build yarn

  stage("Deploy React App on PM2") {
        environment {
            ipSERVERs = "10.102.60.66" //IP;IP
        }  
        steps {

            dir('script'){
                    git credentialsId: 'gitlabuser', \
                    url: 'http://172.25.122.8/labs/cicdscript.git', \
                    branch: 'master'
             }
            withCredentials([usernamePassword(credentialsId: 'deployuser', passwordVariable: 'i_password', usernameVariable: 'i_username')]) {
                sh script: '''
                    sh $WORKSPACE/script/deploy-ract.sh $i_password $ipSERVERs
                '''
            }//end withCredentials 
        }//end steps
    }//end stage

    }//end stages
post {
    always {
        cleanWs()
    }    
}
}//end pipeline

