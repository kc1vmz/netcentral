package netcentral.transceiver.aprsis.config;

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

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class APRSConfiguration {
    @Value("${aprs.config.server}")
    private String server;
    @Value("${aprs.config.port}")
    private Integer port;
    @Value("${aprs.config.user.name}")
    private String username;
    @Value("${aprs.config.user.password}")
    private String password;
    @Value("${aprs.config.query}")
    private String query;

    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
}
