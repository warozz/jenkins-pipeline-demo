pipeline{
    agent any
    environment {
        project = "dfl"
    }     
       stages{
          stage("Checkout SVN on Branch DEV") {
             steps {

                deleteDir()
                checkout([$class: 'SubversionSCM', additionalCredentials: [], \
                    excludedCommitMessages: '', excludedRegions: '', \
                    excludedRevprop: '', excludedUsers: '', \
                    filterChangelog: false, ignoreDirPropChanges: false, \
                    includedRegions: '', locations: [[credentialsId: 'svnjenkins', \
                    depthOption: 'infinity', ignoreExternalsOption: true, \
                    local: 'DFLBackend', \
                    remote: "${svnserver}/svn/TLIProject/DFLService/communication/branches/communication_dev/"]], \
                    quietOperation: true, \
                    workspaceUpdater: [$class: 'UpdateUpdater']])
            }
        } //end stage
        
        stage("Build API with Maven") {
            steps {
                    sh script: '''
                        cd $WORKSPACE/DFLBackend
                        mvn clean install
                        
                        #mvn clean install -DskipTests=true
                        '''
            }//end steps
        }//end state build war file
    }//end stages
}//end pipeline