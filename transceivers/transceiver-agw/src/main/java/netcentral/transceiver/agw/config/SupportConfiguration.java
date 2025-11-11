package netcentral.transceiver.agw.config;

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
