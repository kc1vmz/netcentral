package netcentral.transceiver.kenwood.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class RegisteredTransceiverConfig {
    @Value("${netcontrol.transceiver.name}")
    private String name;
    @Value("${netcontrol.transceiver.description}")
    private String description;
    @Value("${netcontrol.transceiver.type}")
    private String type;
    @Value("${netcontrol.transceiver.port}")
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