pipeline {
    agent any

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