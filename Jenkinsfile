pipeline {
    agent any
    tools {
        gradle "GRADLE_LATEST"
    }
    stages {
        stage('Gradle') {
            steps {
                sh 'gradle --version'
            }
                }
            stage('Gradle Build') {
            steps{
                    sh './gradlew clean build'
              }
        }
    }
}