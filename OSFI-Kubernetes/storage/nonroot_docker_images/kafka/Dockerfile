FROM ubuntu:16.04 

ENV KAFKA_USER=ses_beta \
KAFKA_GROUP=ses_beta_grp \
KAFKA_DATA_DIR=/var/lib/kafka/data \
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 \
KAFKA_HOME=/opt/kafka \
PATH=$PATH:/opt/kafka/bin

ARG KAFKA_VERSION=0.10.2.1
ARG KAFKA_DIST=kafka_2.11-0.10.2.1
RUN set -x \
    && apt-get update \
    && apt-get install -y openjdk-8-jre-headless wget \
    && wget -q "http://www.apache.org/dist/kafka/$KAFKA_VERSION/$KAFKA_DIST.tgz" \
    && wget -q "http://www.apache.org/dist/kafka/$KAFKA_VERSION/$KAFKA_DIST.tgz.asc" \
    && wget -q "http://kafka.apache.org/KEYS" \
    && export GNUPGHOME="$(mktemp -d)" \
    && gpg --import KEYS \
    && gpg --batch --verify "$KAFKA_DIST.tgz.asc" "$KAFKA_DIST.tgz" \
    && tar -xzf "$KAFKA_DIST.tgz" -C /opt \
    && rm -r "$GNUPGHOME" "$KAFKA_DIST.tgz" "$KAFKA_DIST.tgz.asc"
    
COPY log4j.properties /opt/$KAFKA_DIST/config/

RUN set -x \
    && groupadd $KAFKA_GROUP -g 54175 \
    && useradd $KAFKA_USER -u 625216 -g $KAFKA_GROUP \
    && [ `id -u $KAFKA_USER` -eq 625216 ] \
    && [ `id -g $KAFKA_USER` -eq  54175 ] \
    && ln -s /opt/$KAFKA_DIST $KAFKA_HOME \
    && mkdir -p $KAFKA_DATA_DIR \
    && chown -R "$KAFKA_USER:$KAFKA_GROUP"  /opt/$KAFKA_DIST \
    && chown -R "$KAFKA_USER:$KAFKA_GROUP" $KAFKA_DATA_DIR
