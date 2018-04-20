#!/bin/sh

# fetch and unpack vanilla JBoss-4.2.3.GA distribution:
wget -N https://kent.dl.sourceforge.net/project/jboss/JBoss/JBoss-4.2.3.GA/jboss-4.2.3.GA.zip
rm -rvf ./image
unzip -d ./image -u jboss-4.2.3.GA.zip

# generate alpine based image:
cat > image/Dockerfile <<EOD
FROM alpine:3.7
RUN apk add --update ca-certificates && update-ca-certificates
RUN apk add --update tzdata

RUN apk add --update openjdk7
RUN addgroup -S jboss && adduser -S -G jboss jboss -h /var/local/jboss

# copy distribution folder and add symlinks to writable subfolders in server/all/{log,work,data,tmp}:
COPY jboss-4.2.3.GA/ /usr/share/jboss/

RUN mkdir -pv /var/log/jboss /var/cache/jboss /var/local/jboss/work /var/local/jboss/data /tmp/jboss /etc/jboss
RUN chown jboss:jboss /var/log/jboss /var/cache/jboss /var/local/jboss/work /var/local/jboss/data /tmp/jboss /etc/jboss

RUN ln -s /var/log/jboss /usr/share/jboss/server/all/log
RUN ln -s /var/local/jboss/work /usr/share/jboss/server/all/work
RUN ln -s /var/local/jboss/data /usr/share/jboss/server/all/data
RUN ln -s /etc/jboss /usr/share/jboss/server/all/conf/local
RUN ln -s /tmp/jboss /usr/share/jboss/server/all/tmp

ENV LAUNCH_JBOSS_IN_BACKGROUND=1
ENV TZ=Europe/Copenhagen

USER jboss
WORKDIR /usr/share/jboss
CMD bin/run.sh -c all -b 0.0.0.0

EXPOSE 1098 1099 1100 1101 1102 8080 8443
EOD

# build...
docker build -t jboss-4.2 image/

echo
echo "# to run in Docker:"
echo "docker run -it --rm --name=JBoss-Test -p 8080:8080 -p 1099:1099 jboss-4.2"
echo
echo "# to run on host:"
echo "./image/jboss-4.2.3.GA/bin/run.sh -c all -b 0.0.0.0"
echo