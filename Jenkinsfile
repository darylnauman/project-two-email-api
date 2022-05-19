pipeline {
  agent any
  stages {
    stage('Unit Testing') {
            when {
                anyOf {branch 'ft_*'; branch 'bg_*'}
                // branch 'ft_jenkins'
            }
            steps {
                withMaven {
                    sh 'mvn test'
                }
                junit skipPublishingChecks: true, testResults: 'target/surefire-reports/*.xml'
            }
        }
    stage('Build') {
        when {
            //branch 'main'
            branch 'ft_jenkins'
        }
        steps{
            withMaven {
                sh 'mvn package -DskipTests'
            }
        }
    }
    stage('Docker Image') {
            when {
                //branch 'main'
                branch 'ft_jenkins'
            }
            steps{
                script {
                    echo "$registry:$currentBuild.number"
                    dockerImage = docker.build "$registry:$currentBuild.number"
                }
            }
        }

  }
}