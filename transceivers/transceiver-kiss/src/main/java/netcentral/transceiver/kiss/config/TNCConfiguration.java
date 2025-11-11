package netcentral.transceiver.kiss.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class TNCConfiguration {
    @Value("${tnc.port}")
    private String port;
    @Value("${tnc.baudRate}")
    private int baudRate;
    @Value("${tnc.initCommand1}")
    private String initCommand1;
    @Value("${tnc.initCommand2}")
    private String initCommand2;
    @Value("${tnc.hostname}")
    private String hostname;

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
    public String getInitCommand1() {
        return initCommand1;
    }
    public void setInitCommand1(String initCommand1) {
        this.initCommand1 = initCommand1;
    }
    public String getInitCommand2() {
        return initCommand2;
    }
    public void setInitCommand2(String initCommand2) {
        this.initCommand2 = initCommand2;
    }
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
