package netcentral.transceiver.kiss.object;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.micronaut.serde.annotation.Serdeable;
import netcentral.transceiver.kiss.enums.UserRole;

@Serdeable
public class User {
    private String id;
    private String emailAddress;
    private Callsign callsign;
    private String password;
    private String accessToken;
    private UserRole role;
    private String firstName;
    private String lastName;

    @JsonIgnore
    private List<String> organizationIds;

    public User(){
        this.role = UserRole.UNKNOWN;
    }
    public User(String id){
        this.id = id;
        this.role = UserRole.UNKNOWN;
    }
    public User(String id, UserRole role){
        this.id = id;
        this.role = role;
    }
    public User(String id, String emailAddress, String password, UserRole role, String callsign, String firstName, String lastName) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.password = password;
        this.role = role;
        if (callsign != null) {
            Callsign objCallsign = new Callsign();
            objCallsign.setCallsign(callsign);
            this.callsign = objCallsign;
        }
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public Callsign getCallsign() {
        return callsign;
    }
    public void setCallsign(Callsign callsign) {
        this.callsign = callsign;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }
    public List<String> getOrganizationIds() {
        return organizationIds;
    }
    public void setOrganization_ids(List<String> organizationIds) {
        this.organizationIds = organizationIds;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
