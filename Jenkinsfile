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
        stage('test echo') {
            steps {
                echo "測試 echo 是否成功"
            }
        }
        stage {
            steps {
//             超過5天都沒同意就會視為失敗
                timeout(time:5, unit:'DAYS'){
                    input message: "是否執行 jar 包？？"
                }
                sh 'java -jar ./target/ExcelAccountingManager-0.0.1-SNAPSHOT.jar'
            }
            post {
                success {
                    echo 'jar 包執行成功 ^^'
                }

                failure {
                    echo 'jar 包執行失敗！！'
                }
            }
        }
    }
}