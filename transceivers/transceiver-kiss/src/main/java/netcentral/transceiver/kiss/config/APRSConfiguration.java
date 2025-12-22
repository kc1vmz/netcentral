package netcentral.transceiver.kiss.config;

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
    @Value("${aprs.config.callsign}")
    private String callsign;
    @Value("${aprs.config.digipeaters}")
    private String digipeaters;
    @Value("${aprs.config.applicationName}")
    private String applicationName;

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getDigipeaters() {
        return digipeaters;
    }
    public void setDigipeaters(String digipeaters) {
        this.digipeaters = digipeaters;
    }
    public String getApplicationName() {
        return applicationName;
    }
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
