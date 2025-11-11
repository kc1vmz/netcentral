package netcentral.transceiver.agw.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class TNCConfiguration {

    @Value("${tnc.hostname}")
    private String hostname;
    @Value("${tnc.port}")
    private int port;
    @Value("${tnc.channel}")
    private int channel;

    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public int getChannel() {
        return channel;
    }
    public void setChannel(int channel) {
        this.channel = channel;
    }
}
