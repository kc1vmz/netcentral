# What is this?

osm-proxt-cache is a Micronaut service providing proxy and caching services for OpenStreetMap tiles.
Net Central is configured to use this service to talk to a public OSM server, and it will cache tiles for 60 days (application setting)

Anyone using a map in Net Central will benefit from the cache, especially in non-Internet accessible scenarios.
Simply use Net Central to traverse the map areas of interest before disconnecting, then those maps will be cached and available when you are disconnected.

Because of the performance and storage needs of the cache, osm-proxy-cache currently uses MySQL as its storage technology.


## Environment Variables

The following envirnonment variables control the behavior of osm-proxy-cache.

NETCENTRAL_OSMPC_TEMP_DIR       /netcentral/tmp
NETCENTRAL_OSMPC_HTTP_PORT      8889
NETCENTRAL_OSMPC_HTTPS_PORT     4449
NETCENTRAL_OSMPC_CONFIG         proxycache (default) | cache | proxy

NETCENTRAL_OSMPC_MYSQL_HOST     localhost
NETCENTRAL_OSMPC_MYSQL_PORT     3306
NETCENTRAL_OSMPC_MYSQL_DBNAME   netcentral_osmproxycache
NETCENTRAL_OSMPC_MYSQL_USERNAME netcentral_osmproxycache
NETCENTRAL_OSMPC_MYSQL_PASSWORD netcentral_osmproxycache
