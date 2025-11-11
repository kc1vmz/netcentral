package netcentral.server.object;

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

