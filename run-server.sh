#!/bin/sh

SERVER_HOST=$(hostname -f)

docker run -d --rm \
  --name=JBoss-Test \
  -p 8080:8080 \
  -p 1098-1099:1098-1099 \
  -e JAVA_OPTS=-Djava.rmi.server.hostname=$SERVER_HOST \
  degas:5000/jboss-4.2

echo -n "...starting..."
sleep 10
echo
docker logs --tail 15 JBoss-Test

echo
echo "# to run subscriber client:"
echo "mvn compile exec:java -Djava.naming.provider.url=$SERVER_HOST -Dexec.args=listen"
echo
echo "# to run publisher client:"
echo "mvn compile exec:java -Djava.naming.provider.url=$SERVER_HOST"
echo
