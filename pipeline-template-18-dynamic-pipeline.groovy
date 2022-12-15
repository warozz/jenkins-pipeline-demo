import hudson.model.Result
import jenkins.model.CauseOfInterruption
def skipRemainingStages = false

pipeline{
agent any
parameters { 
        string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
        text(name: 'BIOGRAPHY', defaultValue: '', description: 'Enter some information about the person')
        booleanParam(name: 'TOGGLE', defaultValue: true, description: 'Toggle this value')
        choice(name: 'CHOICE', choices: ['One', 'Two', 'Three'], description: 'Pick something')
        password(name: 'PASSWORD', defaultValue: 'SECRET', description: 'Enter a password')
}
environment {

image="uat-tli-java-policy:1.0.${BUILD_NUMBER}"
tmageTag="1.0.${BUILD_NUMBER}"
tgz="uat-tli-java-policy"
chart="$WORKSPACE/app/chart/$tgz/"
yaml="/var/lib/jenkins/workspace/config/helm-tli-java-claim.yaml"
release="uat-tli-java-policy--ci-test-uat"
context="cluster/backend"

//SVN
svnapp="$SVN231/svn/tlicomponent/GroupLife/MobileAppEB/UAT/30-06-2020/apps/tli-java-policy-dev/"
svnconfig="$SVN231/svn/tlicomponent/GroupLife/MobileAppEB/UAT/30-06-2020/apps/backend-cluster-config/"
}

stages{
// DEV
stage("SVN: Checkout on Branch UAT") {
   steps {
       script {
             def userInput = input(id: 'userInput', message: 'Merge to?',
             parameters: [[$class: 'ChoiceParameterDefinition', defaultValue: 'strDef', 
                description:'describing choices', name:'nameChoice', choices: "QA\nUAT\nProduction\nDevelop\nMaster"]
             ])

            println(userInput); //Use this value to branch to different logic if needed
        }//end script
       
       
     deleteDir()
         sh script: '''
                    cd $WORKSPACE 
                    #svn co $svnapp app 
                    #svn co $svnconfig config
                  '''
    }//end step
}//end state 


stage("Show value of parameter") {
when {expression {!skipRemainingStages}}
steps {
             //script{skipRemainingStages = true}
             echo "Hello, ${PERSON}, nice to meet you."
}} //end state
      


stage("last state") {
when {expression {!skipRemainingStages}}
steps {
             echo "exit here right"
}} //end state




stage('Input on Multi values') {

steps {
      script {
                    def inputConfig
                    def inputTest
                    def userInput = input(
                            id: 'userInput', message: 'Enter path of test reports:?',
                            parameters: [

                                    string(defaultValue: 'None',
                                            description: 'Path of config file',
                                            name: 'Config'),
                                    string(defaultValue: 'None',
                                            description: 'Test Info file',
                                            name: 'Test'),
                            ])

                    // Save to variables. Default to empty string if not found.
                    inputConfig = userInput.Config?:''
                    inputTest = userInput.Test?:''

                    // Echo to console
                    //echo("IQA Sheet Path: ${inputConfig}")
                    //echo("Test Info file path: ${inputTest}")
                    echo ("IQA Sheet Path: "+userInput['Config'])
                    echo ("Test Info file path: "+userInput['Test'])

      }//end script
}// end step
}//end state

}//end all states





}//end pipeline