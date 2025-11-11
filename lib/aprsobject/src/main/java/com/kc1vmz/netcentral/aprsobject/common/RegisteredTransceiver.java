package com.kc1vmz.netcentral.aprsobject.common;

public class RegisteredTransceiver {
    private String id;
    private String name;
    private String description;
    private String fqdName;
    private String type;
    private int port;
    private boolean enabledReceive;
    private boolean enabledTransmit;

    public RegisteredTransceiver() {
    }
    public RegisteredTransceiver(String id, String name, String description, String fqdName, String type, int port, boolean enabledReceive, boolean enabledTransmit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fqdName = fqdName;
        this.type = type;
        this.port = port;
        this.enabledReceive = enabledReceive;
        this.enabledTransmit = enabledTransmit;

    }
    public RegisteredTransceiver(RegisteredTransceiver registeredTransceiver) {
        if (registeredTransceiver != null) {
            this.id = registeredTransceiver.getId();
            this.name = registeredTransceiver.getName();
            this.description = registeredTransceiver.getDescription();
            this.fqdName = registeredTransceiver.getFqdName();
            this.type = registeredTransceiver.getType();
            this.port = registeredTransceiver.getPort();
            this.enabledReceive = registeredTransceiver.isEnabledReceive();
            this.enabledTransmit = registeredTransceiver.isEnabledTransmit();
        }
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
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFqdName() {
        return fqdName;
    }
    public void setFqdName(String fqdName) {
        this.fqdName = fqdName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public boolean isEnabledReceive() {
        return enabledReceive;
    }
    public void setEnabledReceive(boolean enabledReceive) {
        this.enabledReceive = enabledReceive;
    }
    public boolean isEnabledTransmit() {
        return enabledTransmit;
    }
    public void setEnabledTransmit(boolean enabledTransmit) {
        this.enabledTransmit = enabledTransmit;
    }
}
