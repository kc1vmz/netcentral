docker build --no-cache -t netcentral-server:1.0.8 -f Dockerfile-server ../..
docker build --no-cache -t netcentral-ui:1.0.8 -f Dockerfile-ui ../..
docker build --no-cache -t netcentral-transceiver-aprsis:1.0.8 -f Dockerfile-aprsis ../..
docker build --no-cache -t netcentral-transceiver-kenwood:1.0.8 -f Dockerfile-kenwood ../..
docker build --no-cache -t netcentral-transceiver-kiss:1.0.8 -f Dockerfile-kiss ../..
