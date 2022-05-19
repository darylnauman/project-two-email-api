pipeline {
  agent any
  stages {
    stage('Unit Testing') {
            when {
                anyOf {branch 'ft_*'; branch 'bg_*'; branch 'ft_jenkins'}
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

  }
}