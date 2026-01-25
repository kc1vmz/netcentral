docker build --no-cache -t netcentral-server:1.0.7 -f Dockerfile-server ../..
docker build --no-cache -t netcentral-ui:1.0.7 -f Dockerfile-ui ../..
docker build --no-cache -t netcentral-transceiver-aprsis:1.0.7 -f Dockerfile-aprsis ../..
docker build --no-cache -t netcentral-transceiver-kenwood:1.0.7 -f Dockerfile-kenwood ../..
docker build --no-cache -t netcentral-transceiver-kiss:1.0.7 -f Dockerfile-kiss ../..
