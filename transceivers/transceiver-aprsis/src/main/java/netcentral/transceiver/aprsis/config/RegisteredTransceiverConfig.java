package netcentral.transceiver.aprsis.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class RegisteredTransceiverConfig {
    @Value("${netcentral.transceiver.name}")
    private String name;
    @Value("${netcentral.transceiver.description}")
    private String description;
    @Value("${netcentral.transceiver.type}")
    private String type;
    @Value("${netcentral.transceiver.port}")
    private Integer port;

    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }


}