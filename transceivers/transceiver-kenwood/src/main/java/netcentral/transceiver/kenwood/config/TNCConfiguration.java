package netcentral.transceiver.kenwood.config;

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
public class TNCConfiguration {
    @Value("${tnc.port}")
    private String port;
    @Value("${tnc.baudRate}")
    private int baudRate;
    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public int getBaudRate() {
        return baudRate;
    }
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }
}
