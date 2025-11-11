package netcentral.server.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class SupportConfiguration {
    @Value("${netcentral.support.contact.name}")
    private String contactName;
    @Value("${netcentral.support.contact.callsign}")
    private String contactCallsign;
    @Value("${netcentral.support.contact.email}")
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
