package netcentral.transceiver.aprsis.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class NetControlClientConfig {
    @Value("${netcontrol.config.server}")
    private String server;
    @Value("${netcontrol.config.port}")
    private Integer port;
    @Value("${netcontrol.config.user.name}")
    private String username;
    @Value("${netcontrol.config.user.password}")
    private String password;

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
}