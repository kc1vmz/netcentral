SET NETCENTRAL_VERSION=1.0.27
docker tag netcentral-full:%NETCENTRAL_VERSION% kc1vmz/netcentral-full:%NETCENTRAL_VERSION%
docker push kc1vmz/netcentral-full:%NETCENTRAL_VERSION%
SET NETCENTRAL_VERSION=
