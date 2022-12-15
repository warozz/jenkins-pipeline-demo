pipeline{
    agent any
       stages{
          stage("Subversion Checkout on Branch DEV") {
             steps {
                deleteDir()
                    checkout([$class: 'SubversionSCM', additionalCredentials: [], \
                    excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', \
                    excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, \
                    includedRegions: '', locations: [[credentialsId: 'svnjenkins', \
                    depthOption: 'infinity', ignoreExternalsOption: true, \
                    local: '.', remote: "${svnserver}/svn/tlicomponent/CRM/CustomerInfo/pam/pam-api/PamAPI/branches/dev-pam/"]], \
                    quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
            }
        } //end state
        
        stage("SonarQube Scanning Code Analysis") {
            steps {
                script{
                    scannerHome = tool 'mysonar'
                }
                withSonarQubeEnv('mysonarserver') {
                    sh "${scannerHome}/bin/sonar-scanner  \
                    -Dsonar.projectKey=PamAPI-key \
                    -Dsonar.projectName=PAMAPI \
                    -Dsonar.projectVersion=1.0 \
                    -Dsonar.language=java \
                    -Dsonar.sources=PamAPI \
                    -Dsonar.java.binaries=/usr/bin"
                }
            }
        }
       //end state SonarQube scan
       
        stage("Build Ant") {
            steps {
                script{
                    sh script: '''
                        cp /home/cideployment/buildfile/PamAPI.xml $WORKSPACE/PamAPI/
                        cd $WORKSPACE/PamAPI
                        ant -f PamAPI.xml

                    '''
                }
            }
        }
       //end state build war file
 

    }//stages
}//end pipeline