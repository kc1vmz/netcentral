package netcentral.transceiver.kenwood.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class TNCConfiguration {
    @Value("${tnc.port}")
    private String port;
    @Value("${tnc.baudRate}")
    private int baudRate;
    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public int getBaudRate() {
        return baudRate;
    }
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }
}
