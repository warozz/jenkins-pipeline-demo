
pipeline {
     agent { label 'k8sbuild'}

    environment {

        namespace = "SSW" // CONTACT SSW
        deployment_metadata = "project-deployment"
        
        //Image
        serverName = "10.102.60.87:5000" // DEV/SIT :10.102.60.87:5000, UAT : kbnon-registry.thailife.com
        appName = "SSW" // CONTACT SSW
        tagVersion="${BUILD_NUMBER}"

        // Application
        warFile="app.jar"
        appDes = "$WORKSPACE/app"
        kubeConfig = "/var/jenkins_home/kube-secrets/cideployment-dev"
    }
    
    stages {
        stage('Clone App'){
            steps {
                  dir('app'){ \
                  git credentialsId: 'gitlabdevuserid', \
                  url: 'Application', \
                  branch: 'main'}
            }
        }
        
        stage('Clone Docker'){
            steps {
                  dir('dockerbuild'){ \
                  git credentialsId: 'gitlabdevuserid', \
                  url: 'DEV', \ // Dockerfile
                  branch: 'DEV'}
            }
        }

        stage('Build App'){
            steps {
                script{
                    sh script: '''
                     id
                        cd $WORKSPACE/app
                        ls
                        docker run --rm --name builder -v maven-repo:/root/.m2 -v $appDes:/usr/src/mymaven -w /usr/src/mymaven maven:3.3-jdk-8 mvn clean install
                        cp $WORKSPACE/app/target/*.jar $WORKSPACE/dockerbuild/API/app.jar

                    '''
                  }
            }
        }

        stage('Build Docker'){
            steps {
                script{
                    sh script: '''
                        
                        echo $tagVersion
                        
                        cd $WORKSPACE/dockerbuild/API
                        ls
                        
                        buildDockerImg="docker build -t $serverName/$appName:$tagVersion ."
                        echo $buildDockerImg
    
                        run=$(eval $buildDockerImg)

                    '''
                  }
            }
        }

        stage('Push Image'){
            steps {
                script{
                    sh script: '''

                        dockerPush="docker push $serverName/$appName:$tagVersion"
                        run=$(eval $dockerPush)

                    '''
                  }
            }
        }

        stage('Deploy') {
                agent { label 'master'}
                steps {
                
                dir('deploymentfile'){ \
                git credentialsId: 'gitlabdevuserid', \
                url: 'SSW', \  // CONTACT SSW
                branch: 'SSW'} // CONTACT SSW



                script {
                    sh script: '''
                        cd deploymentfile
                        ls 

                        # ----------------- Edit Project -----------------
                        sed -i -- "s/{{projectName-version}}/$tagVersion/g" projectName-deployment.yaml.template
                        cp $WORKSPACE/deploymentfile/projectName-deployment.yaml.template $WORKSPACE/deploymentfile/deployment.yaml

                        kubectl --kubeconfig=$kubeConfig apply -f deployment.yaml

                    '''
                  }
            }
        }

        stage('Remove Image') {
            steps {
                script {
                    sh script: '''

                        dockerRemove="docker rmi $serverName/$appName:$tagVersion"
                        run=$(eval $dockerRemove) 
                       
                    '''
                }
            }
        }
    }

    post {  always {
            cleanWs()
        }
    }     

}
