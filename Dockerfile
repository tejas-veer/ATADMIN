FROM harbor-adc.internal.media.net/kbb/aws/tomcat7:latest
ADD ./setenv.sh /opt/tomcat/bin/setenv.sh
ADD target/ATAdmin.war /opt/tomcat/webapps/