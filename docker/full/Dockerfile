FROM eclipse-temurin:21-jdk-alpine
RUN ["apk", "update"]
RUN ["apk", "add", "bash"]
RUN ["apk", "add", "sudo"]
RUN ["apk", "add", "openrc"]

RUN mkdir -p /run/openrc
RUN touch /run/openrc/softlevel
RUN mkdir -p /var/log/mysql

RUN ["apk", "add", "mariadb"]
RUN ["apk", "add", "mariadb-client"]
RUN ["apk", "add", "mariadb-openrc"]
RUN ["apk", "add", "npm"]
RUN ["apk", "add", "libcap"]

ARG USER_UID=1000
ARG GROUP_GID=1000
ARG UNAME=netcentral
ARG GNAME=netcentralgroup
RUN addgroup -S -g ${GROUP_GID} ${GNAME} && \
    adduser -S -D -H -u ${USER_UID} -G ${GNAME} ${UNAME}

# init MariaDB
RUN ["mariadb-install-db", "--user=mysql", "--datadir=/var/lib/mysql"]
RUN sed -i "s|skip-networking|#skip-networking|g" /etc/my.cnf.d/mariadb-server.cnf

WORKDIR /netcentral
RUN mkdir -p /netcentral/tmp
RUN mkdir -p /netcentral/tmp/logs
RUN mkdir -p /netcentral/tmp/reports
RUN mkdir -p /netcentral/src
RUN mkdir -p /netcentral/src/ui

ADD docker/full/image_files/all_processes.sh /netcentral/src/all_processes.sh
ADD docker/full/image_files/nc_mysql_init.sh /netcentral/src/nc_mysql_init.sh
ADD docker/full/image_files/nc_mysql_init_done.sh /netcentral/src/nc_mysql_init_done.sh
RUN ["dos2unix", "/netcentral/src/all_processes.sh"]
RUN ["dos2unix", "/netcentral/src/nc_mysql_init.sh"]
RUN ["dos2unix", "/netcentral/src/nc_mysql_init_done.sh"]

ADD docker/full/image_files/nc_setcap.sh /netcentral/src/nc_setcap.sh
RUN ["dos2unix", "/netcentral/src/nc_setcap.sh"]
RUN ["/netcentral/src/nc_setcap.sh"]

ADD netcentral-server/target/netcentral-server-1.0.10.jar /netcentral/src/netcentral-server.jar
ADD transceivers/transceiver-agw/target/transceiver-agw-1.0.10.jar /netcentral/src/transceiver-agw.jar
ADD transceivers/transceiver-aprsis/target/transceiver-aprsis-1.0.10.jar /netcentral/src/transceiver-aprsis.jar
ADD transceivers/transceiver-kenwood/target/transceiver-kenwood-1.0.10.jar /netcentral/src/transceiver-kenwood.jar
ADD transceivers/transceiver-kiss/target/transceiver-kiss-1.0.10.jar /netcentral/src/transceiver-kiss.jar
ADD ui/net-central-ui/target/netcentral-ui-1.0.10.zip /netcentral/src/netcentral-ui.zip
RUN chown -R ${UNAME}:${GNAME} /netcentral

# make sure mariadb auto-starts
RUN rc-update add mariadb default

# make sure netcentral-transceiver-aprsis auto-starts
ADD docker/full/image_files/config_env.sh /netcentral/src/config_env.sh
RUN ["dos2unix", "/netcentral/src/config_env.sh"]
RUN chmod +x /netcentral/src/config_env.sh

# make sure netcentral-server auto-starts
ADD docker/full/image_files/netcentral-server /etc/init.d/netcentral-server
RUN ["dos2unix", "/etc/init.d/netcentral-server"]
RUN chmod +x /etc/init.d/netcentral-server
RUN rc-update add netcentral-server default

# make sure netcentral-transceiver-aprsis auto-starts
ADD docker/full/image_files/netcentral-transceiver-aprsis /etc/init.d/netcentral-transceiver-aprsis
RUN ["dos2unix", "/etc/init.d/netcentral-transceiver-aprsis"]
RUN chmod +x /etc/init.d/netcentral-transceiver-aprsis
RUN rc-update add netcentral-transceiver-aprsis default

# make sure netcentral-ui auto-starts
ADD docker/full/image_files/netcentral-ui /etc/init.d/netcentral-ui
RUN ["dos2unix", "/etc/init.d/netcentral-ui"]
RUN chmod +x /etc/init.d/netcentral-ui
RUN rc-update add netcentral-ui default

# expand Node.JS UI
WORKDIR /netcentral/src/ui
RUN unzip /netcentral/src/netcentral-ui.zip
RUN rm -rf /netcentral/src/netcentral-ui.zip
RUN npm install
WORKDIR /netcentral/src

CMD ["/bin/sh", "-c", "/netcentral/src/all_processes.sh & sleep infinity"]