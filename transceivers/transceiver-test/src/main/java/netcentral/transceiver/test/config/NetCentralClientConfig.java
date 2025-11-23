package netcentral.transceiver.test.config;

import java.util.Optional;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class NetCentralClientConfig {
    @Value("${netcentral.config.server}")
    private String server;
    @Value("${netcentral.config.port}")
    private Integer port;
    @Value("${netcentral.config.user.name}")
    private String username;
    @Value("${netcentral.config.user.password}")
    private String password;
    @Value("${netcentral.transceiver.hostname}")
    private Optional<String> hostname;


    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Optional<String> getHostname() {
        return hostname;
    }
    public void setHostname(Optional<String> hostname) {
        this.hostname = hostname;
    }
}