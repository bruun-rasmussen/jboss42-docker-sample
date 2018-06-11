#!/bin/sh

SERVER_HOST=$(hostname -f)

docker run -i -t --rm \
  -p 8080:8080 \
  -p 1098-1099:1098-1099 \
  -e JAVA_OPTS=-Djava.rmi.server.hostname=$SERVER_HOST \
  jboss-4.2 &

echo -n "...starting..."
sleep 10
echo
docker logs --tail 15 JBoss-Test

echo
echo "# to run subscriber client:"
echo "mvn compile exec:java -Djava.naming.provider.url=$SERVER_HOST:1099 -Dexec.args=listen"
echo
echo "# to run publisher client:"
echo "mvn compile exec:java -Djava.naming.provider.url=$SERVER_HOST:1099"
echo
