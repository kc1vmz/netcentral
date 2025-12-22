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

import java.util.Optional;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class NetCentralClientConfig {
    @Value("${netcentral.config.server}")
    private String server;
    @Value("${netcentral.config.port}")
    private Integer port;
    @Value("${netcentral.config.user.name}")
    private String username;
    @Value("${netcentral.config.user.password}")
    private String password;
    @Value("${netcentral.transceiver.hostname}")
    private Optional<String> hostname;

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
    public Optional<String> getHostname() {
        return hostname;
    }
    public void setHostname(Optional<String> hostname) {
        this.hostname = hostname;
    }
}