FROM tomcat
RUN rm -fr /opt/tomcat/webapps/*
COPY target/*.war /opt/tomcat/webapps/ROOT.war
RUN mkdir logs

ENTRYPOINT /opt/tomcat/bin/catalina.sh run

