SET NETCENTRAL_VERSION=1.0.26
docker build --no-cache -t netcentral-server:%NETCENTRAL_VERSION% -f Dockerfile-server ../../..
docker build --no-cache -t netcentral-server-h2:%NETCENTRAL_VERSION% -f Dockerfile-server-h2 ../../..
docker build --no-cache -t osm-proxy-cache:%NETCENTRAL_VERSION% -f Dockerfile-osm-proxy-cache ../../..
docker build --no-cache -t netcentral-ui:%NETCENTRAL_VERSION% -f Dockerfile-ui ../../..
docker build --no-cache -t netcentral-transceiver-aprsis:%NETCENTRAL_VERSION% -f Dockerfile-aprsis ../../..
docker build --no-cache -t netcentral-transceiver-kenwood:%NETCENTRAL_VERSION% -f Dockerfile-kenwood ../../..
docker build --no-cache -t netcentral-transceiver-kiss:%NETCENTRAL_VERSION% -f Dockerfile-kiss ../../..
SET NETCENTRAL_VERSION=
