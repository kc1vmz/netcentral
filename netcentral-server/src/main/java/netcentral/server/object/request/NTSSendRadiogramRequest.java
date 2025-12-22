package netcentral.server.object.request;

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

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record NTSSendRadiogramRequest(String number, String precedence, String hx, String callsignOrigin, String locationOrigin, String timeFiled, String dateFiled,
                String name, String address, String cityState, String phoneNumber, String emailAddress, String signature, String operatorNote,
                String message1, String message2, String message3, String message4, String message5, String message6, String message7, String message8, String message9, String message10,
                String message11, String message12, String message13, String message14, String message15, String message16, String message17, String message18, String message19, String message20,
                String message21, String message22, String message23, String message24, String message25, String check) {
}
