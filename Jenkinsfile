pipeline {
// any 任何節點上都可以運行這個 pipeline
    agent any
    tools {
        maven 'local maven'
    }
    stages{
        stage('Build'){
            steps {
                sh 'mvn clean package'
            }
            post {
            // 成功後執行這個區塊
                success {
                    echo '開始存檔案...'
                    archiveArtifacts artifacts: '**/target/*.jar'
                }
            }
        }
        stage('java -jar xxx.jar') {
            steps {
            sh 'java -jar ./target/ExcelAccountingManager-0.0.1-SNAPSHOT.jar'
            }
        }
    }
}