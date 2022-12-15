
pipeline {
    //agent any  
    agent { 
        label 'master'
    }
  stages {
        stage('Checkout source code from SNV '){
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
                    quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                    
        }//end steps
      }//end stage
    }//end stages
}//end pipeline
