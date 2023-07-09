pipeline {
    agent any

    tools{
        maven 'local maven'
    }

    parameters{
        string(name: 'tomcat_dev', defaultValue: '176.34.17.207', description: 'Staging Server')
        string(name: 'tomcat_prod', defaultValue: '35.78.238.49', description: 'Production Server')
    }

    triggers {
         pollSCM('* * * * *')
     }

     stages{
        stage('Build'){
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    echo '開始存欓...'
                    archiveArtifacts artifacts: '**/target/*.war'
                }
            }
        }

     stage ('Deployments'){
            parallel{
                stage ('Deploy to Staging'){
                    steps {
                        sh "scp -i /Users/sam_huang/Desktop/jenkins.pem **/target/*.jar ec2-user@${params.tomcat_dev}:/opt"
                    }
                }

                stage ("Deploy to Production"){
                    steps {
                        sh "scp -i /Users/sam_huang/Desktop/jenkins.pem **/target/*.jar ec2-user@${params.tomcat_prod}:/opt"
                    }
                }
            }
        }
    }
}