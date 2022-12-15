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
        // UAT
        stage("Subversion Checkout on Branch UAT") {
            steps {
                deleteDir()
                dir('TLPromptWeb'){
                    //checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: 'svnjenkins', depthOption: 'infinity', ignoreExternalsOption: true, local: '.', remote: 'http://206.1.1.231/svn/TLIProject/Mdaplus/TLTeam/branches/UAT/']], quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                    checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[credentialsId: 'svnjenkins', depthOption: 'infinity', ignoreExternalsOption: true, local: '.', remote: "${svnURL}"]], quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                }
            }
        }

        stage("Build TLTEAM iOS for UAT environment"){
            steps {
       
                script{
                    sh script: '''
                    mkdir $WORKSPACE/UAT-TLPromptWeb
                    cp -rp $WORKSPACE/TLPromptWeb/* $WORKSPACE/UAT-TLPromptWeb
                    
                    sed -i.xml 's/ionic:build/ionic:build": "node --max-old-space-size=8192 --stack-size=1968  build/g' $WORKSPACE/UAT-TLPromptWeb/config.xml
                    

                    cd $WORKSPACE/UAT-TLPromptWeb
                    #rm -rf /tmp/npm-*
                    
                    export PATH=$PATH:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin
                    export LIBRARY_PATH=/usr/local/Cellar/gsl/2.6/lib/
                    cd $WORKSPACE/UAT-TLPromptWeb
                    # Clean npm
                    npm cache clean --force
                    rm -rf node_modules
                    npm i
                    #npm uninstall -g
                    #npm i -g
                    npm run clean
                    npm run build

                    node set_env.js UAT

                    $(which ionic) cordova platform remove ios
                    $(which ionic) cordova platform add ios@5.1.1
                    


                    cp -R ./resources/ios/icon_uat/*.* ./resources/ios/icon
                    cp ./GoogleService-Info-uat.plist ./GoogleService-Info.plist
                    cp ./src/envs/env.uat.ts ./src/envs/env.ts

                    #cd ./platforms/ios
                    #rm Podfile.lock
                    #pod install

                    cd $WORKSPACE/UAT-TLPromptWeb
                    rm build.json    
                    echo '{
                            "android": {
                            "debug": {
                            "keystore": "",
                            "storePassword": "",
                            "alias": "",
                            "password" : "",
                            "keystoreType": ""
                            },
                            "release": {
                            "keystore": "",
                        "storePassword": "",
                            "alias": "",
                            "password" : "",
                            "keystoreType": ""
                            }
                        },
                        "ios": {
                            "debug": {
                            "codeSignIdentitiy": "TL-Team DEV",
                            "developmentTeam":"UG96VZGJW3",
                            "packageType": "developer",
                            "iCloudContainerEnvironment": "Development"
                            },
                            "release": {
                             "codeSignIdentitiy": "Thai Life Insurance Company Limited",
                            "developmentTeam":"JUG2EKBCEL",
                            "provisioningProfile": "TLTeam UAT",
                            "packageType": "enterprise",
                            "iCloudContainerEnvironment": "Production"
                            }
                        }
                    }' >build.json
            
                    cordova build ios --device -prod --release --minifyjs --minifycss --optimizejs --aot --buildFlag='-UseModernBuildSystem=0'

                    '''
                }

                    
            }
        
        }
        stage("Approved"){
            steps {
                //input message: '[UAT] Approve on this version?', ok: 'Approved', submitter: 'admin,devuser,adminuser' 
                archiveArtifacts 'UAT-TLPromptWeb/platforms/ios/build/device/TL Team UAT.ipa'
            }           
        }
    }

}