pipeline {
    agent any
    stages{
        stage('Build'){
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    echo '開始存欓...'
                    archiveArtifacts artifacts: '**/target/*.jar'
                }
            }
        }
    }
}