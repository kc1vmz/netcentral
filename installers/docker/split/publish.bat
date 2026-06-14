SET NETCENTRAL_VERSION=1.0.23
docker tag netcentral-server:%NETCENTRAL_VERSION% kc1vmz/netcentral-server:%NETCENTRAL_VERSION%
docker tag netcentral-server-h2:%NETCENTRAL_VERSION% kc1vmz/netcentral-server-h2:%NETCENTRAL_VERSION%
docker tag osm-proxy-cache:%NETCENTRAL_VERSION% kc1vmz/osm-proxy-cache:%NETCENTRAL_VERSION%
docker tag netcentral-ui:%NETCENTRAL_VERSION% kc1vmz/netcentral-ui:%NETCENTRAL_VERSION%
docker tag netcentral-transceiver-aprsis:%NETCENTRAL_VERSION% kc1vmz/netcentral-transceiver-aprsis:%NETCENTRAL_VERSION%
docker tag netcentral-transceiver-kenwood:%NETCENTRAL_VERSION% kc1vmz/netcentral-transceiver-kenwood:%NETCENTRAL_VERSION%
docker tag netcentral-transceiver-kiss:%NETCENTRAL_VERSION% kc1vmz/netcentral-transceiver-kiss:%NETCENTRAL_VERSION%
docker push kc1vmz/netcentral-server:%NETCENTRAL_VERSION%
docker push kc1vmz/netcentral-server-h2:%NETCENTRAL_VERSION%
docker push kc1vmz/osm-proxy-cache:%NETCENTRAL_VERSION%
docker push kc1vmz/netcentral-ui:%NETCENTRAL_VERSION%
docker push kc1vmz/netcentral-transceiver-aprsis:%NETCENTRAL_VERSION%
docker push kc1vmz/netcentral-transceiver-kenwood:%NETCENTRAL_VERSION%
docker push kc1vmz/netcentral-transceiver-kiss:%NETCENTRAL_VERSION%
SET NETCENTRAL_VERSION=
