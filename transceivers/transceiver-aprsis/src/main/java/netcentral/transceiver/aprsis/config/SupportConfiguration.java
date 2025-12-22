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
public class SupportConfiguration {
    @Value("${support.contact.name}")
    private String contactName;
    @Value("${support.contact.callsign}")
    private String contactCallsign;
    @Value("${support.contact.email}")
    private String contactEmail;
    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public String getContactCallsign() {
        return contactCallsign;
    }
    public void setContactCallsign(String contactCallsign) {
        this.contactCallsign = contactCallsign;
    }
    public String getContactEmail() {
        return contactEmail;
    }
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
