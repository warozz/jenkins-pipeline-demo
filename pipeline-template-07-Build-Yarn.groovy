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

            cd $WORKSPACE/maincode && ls
            cd $WORKSPACE/maincode/build/ && ls
            ls
        '''
    }//end steps
}//end stage build yarn


}//end stages
post {
    always {
        cleanWs()
    }    
}
}//end pipeline