
# SSCCE for Docker'ed JBoss remote access challenge

To exercise, first build a basic JBoss 4.2 image by running the `./mk-server.sh` script.

Then launch an instance of the image:
```
docker run -i -t --rm \
  -p 1098-1099:1098-1099 -p 8080:8080 \
  -e JAVA_OPTS=-Djava.rmi.server.hostname=$(hostname -f) \
  jboss-4.2
```
Once up and running, in another terminal, try to access one of the sample services with [JBossClientTest.java](src/main/java/dk/br/JBossClientTest.java):
```
mvn compile exec:java -Djava.naming.provider.url=$(hostname -f):1099
```
To see the intended behavior, stop the container with (Ctrl-C) and launch a regular server instead:
```
./image/jboss-4.2.3.GA/bin/run.sh -c all -b 0.0.0.0
```
This time, `mvn exec:java` should just work.
