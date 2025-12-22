package netcentral.server.object;

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

import netcentral.server.enums.ObjectShelterState;
import netcentral.server.enums.ObjectShelterStatus;

public class ObjectShelterOperationalStatus {
    private ObjectShelterStatus status;
    private ObjectShelterState state;
    private String message;

    public ObjectShelterOperationalStatus() {
    }
    public ObjectShelterOperationalStatus(ObjectShelterStatus status, ObjectShelterState state, String message) {
        setStatus(status);
        setState(state);
        setMessage(message);
    }

    public ObjectShelterStatus getStatus() {
        return status;
    }
    public void setStatus(ObjectShelterStatus status) {
        this.status = status;
    }
    public ObjectShelterState getState() {
        return state;
    }
    public void setState(ObjectShelterState state) {
        this.state = state;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

