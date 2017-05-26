dcos auth logout
dcos auth login
dcos config set spark.app_id team-a-spark
dcos spark run --submit-args='--class HelloWorld http://digitalemil.de/SparkApp2.1.0-assembly-1.0.jar'
