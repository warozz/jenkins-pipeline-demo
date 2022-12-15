pipeline {
 agent none

  stages {
    stage('Hello') {
      agent { label 'master'}
      steps {
        sh '''
          touch file.txt
          mkdir -p target
          touch target/file2.jar
          touch target/file3.war
          ls -lR
        '''
        stash(name: 'myStash')
      }
    }
    stage('check the file') {
      agent { label 'Agent' }
      steps {
        unstash 'myStash'
        sh 'ls -lR'
      }
    }
  }
}