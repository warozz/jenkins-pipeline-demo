pipeline{
    agent { 
        node { 
            label 'mac-release' 
        } 
    }

    environment {
	   svnURL="$svnserver/svn/TLIProject/Mdaplus/TLTeam/branches/UAT/"
    }

    
    stages{
        // DEV
        stage("Subversion Checkout on Branch UAT") {
            steps {
                deleteDir()
                dir('TLPromptWeb'){
                    //checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: 'svnjenkins', depthOption: 'infinity', ignoreExternalsOption: true, local: '.', remote: 'http://206.1.1.231/svn/TLIProject/Mdaplus/TLTeam/branches/UAT/']], quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                    checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: 'svnjenkins', depthOption: 'infinity', ignoreExternalsOption: true, local: '.', remote: "${svnURL}"]], quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                }
            }
        }

        stage("Build TLTeamPlus for UAT environment"){
            steps {
       
                script{
                    sh script: '''
                    mkdir $WORKSPACE/UAT-TLPromptWeb
                    cp -rp $WORKSPACE/TLPromptWeb/* $WORKSPACE/UAT-TLPromptWeb
                    
                    sed -i.xml 's/ionic:build/ionic:build": "node --max-old-space-size=8192 --stack-size=1968  build/g' $WORKSPACE/UAT-TLPromptWeb/config.xml

                    cd $WORKSPACE/UAT-TLPromptWeb
                    rm -rf /tmp/npm-*
                
                    
                    export PATH=$PATH:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin
                    cd $WORKSPACE/UAT-TLPromptWeb
                    
                    rm -rf node_modules

                    npm i
                    #npm uninstall -g
                    #npm i -g

                    

                    npm run clean
                    npm run build

                    node set_env.js UAT

                    $(which ionic) cordova platform remove android
                    $(which ionic) cordova platform add android@7.1.4

                    cp -R ./resources/android/icon_uat/*.* ./resources/android/icon
                    cp ./GoogleService-Info-uat.plist ./GoogleService-Info.plist
                    cp ./src/envs/env.uat.ts ./src/envs/env.ts

                    
                    # IgnoreSSL
                    ## IOS

                    ionic cordova build android --prod --release 
                    cp platforms/android/app/build/outputs/apk/release/app-release-unsigned.apk ./app-release-unsigned.apk
                    jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore locus-release-key.jks -storepass locus123 app-release-unsigned.apk locus


                    rm TL-Team.apk
                    /Users/macbook/Library/Android/sdk/build-tools/28.0.3/zipalign -v 4 app-release-unsigned.apk TL-Team.apk

                    ''' 
                
                }
                
            }
        
        }
        
         stage("Developer Approved"){
            steps {
                //input message: '[Developer] Approve on this version?', ok: 'Approved', submitter: 'admin,devuser,adminuser' 
                archiveArtifacts 'UAT-TLPromptWeb/TL-Team.apk'
            }           
        }
       
    }
}