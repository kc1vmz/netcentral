package netcentral.transceiver.aprsis.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class APRSConfiguration {
    @Value("${aprs.config.server}")
    private String server;
    @Value("${aprs.config.port}")
    private Integer port;
    @Value("${aprs.config.user.name}")
    private String username;
    @Value("${aprs.config.user.password}")
    private String password;
    @Value("${aprs.config.query}")
    private String query;

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
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
}
