package netcentral.server.osm.client;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.NetCentralServerConfig;

@Singleton
public class OSMServerRESTClient {
    private static final Logger logger = LogManager.getLogger(OSMServerRESTClient.class);

    @Inject
    private NetCentralServerConfig netCentralServerConfig;
    public void sendPrecacheRequest(String latitude, String longitude) {
        if (!netCentralServerConfig.getOsmServerPreCache()) {
            return;
        }

        String fqdName = netCentralServerConfig.getOsmServerHost();
        int port = netCentralServerConfig.getOsmServerPort();

        try {
            String uri = String.format("api/v1/tiles/precaches/aprs/%s/%s", latitude, longitude);

            get(buildURL(fqdName, port, uri));
        } catch (Exception e) {
            logger.error("Exception caught sending precache request", e);
        }
    }

    private String buildURL(String fqdName, int port, String tail) {
        return String.format("http://%s:%d/%s", fqdName, port, tail);
    }

    private String get(String uri) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpResponse<?> response = client.send(request,  BodyHandlers.ofString());
        if  ((response != null) && (response.body() != null)) {
            return response.body().toString();
        }
        return null;
    }
}
