dcos auth logout
dcos auth login
dcos config set spark.app_id team-b-spark
dcos spark run --submit-args='--class HelloWorld http://digitalemil.de/SparkHelloWorld1.6.3-assembly-1.0.jar'
