SET NETCENTRAL_VERSION=1.0.26
docker tag netcentral-full-h2:%NETCENTRAL_VERSION% kc1vmz/netcentral-full-h2:%NETCENTRAL_VERSION%
docker push kc1vmz/netcentral-full-h2:%NETCENTRAL_VERSION%
SET NETCENTRAL_VERSION=
