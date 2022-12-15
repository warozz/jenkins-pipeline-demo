pipeline{
    agent any
    environment {
        project = "dfl"
    }     
       stages{
          stage("Checkout on Branch DEV") {
             steps {
                 
                deleteDir()
                dir('DFLCommunicationProject'){
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
            }
        } //end stage
        
        stage("SonarQube Scanning Code Analysis") {
            steps {
                script{
                    scannerHome = tool 'mysonar'
                }
                withSonarQubeEnv('mysonarserver') {
                    sh "${scannerHome}/bin/sonar-scanner  \
                    -Dsonar.projectKey=th.co.thailife.dfl-communicationservice \
                    -Dsonar.projectName=DFL-CommunicationService \
                    -Dsonar.projectVersion=1.0 \
                    -Dsonar.language=java \
                    -Dsonar.sources=DFLCommunicationProject \
                    -Dsonar.java.binaries=/usr/bin"
                }
                cleanWs()//Clean Workspace
            }
        }
       //end stage SonarQube scan
    }//end stages
}//end pipeline