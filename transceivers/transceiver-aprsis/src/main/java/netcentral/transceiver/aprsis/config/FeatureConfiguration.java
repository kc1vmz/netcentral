package netcentral.transceiver.aprsis.config;

import java.util.Optional;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class FeatureConfiguration {
    @Value("${feature.listener.sleep.enabled}")
    private boolean listenerSleep;
    @Value("${feature.listener.readlock.enabled}")
    private boolean listenerReadLock;
    @Value("${feature.messagecache.delete.seconds}")
    private Integer messageCacheDeleteSeconds;
    @Value("${feature.listener.enabled}")
    private boolean listenerEnabled;
    @Value("${feature.packet.logging}")
    private boolean packetLoggingEnabled;
    @Value("${feature.packet.logging.filename}")
    private Optional<String> packetLoggingFilename;

    public boolean isListener() {
        return listenerEnabled;
    }
    public void setListener(boolean listenerEnabled) {
        this.listenerEnabled = listenerEnabled;
    }
    public boolean isListenerSleep() {
        return listenerSleep;
    }
    public void setListenerSleep(boolean listenerSleep) {
        this.listenerSleep = listenerSleep;
    }
    public boolean isListenerReadLock() {
        return listenerReadLock;
    }
    public void setListenerReadLock(boolean listenerReadLock) {
        this.listenerReadLock = listenerReadLock;
    }
    public Integer getMessageCacheDeleteSeconds() {
        return messageCacheDeleteSeconds;
    }
    public void setMessageCacheDeleteSeconds(Integer messageCacheDeleteSeconds) {
        this.messageCacheDeleteSeconds = messageCacheDeleteSeconds;
    }
    public boolean isPacketLoggingEnabled() {
        return packetLoggingEnabled;
    }
    public void setPacketLoggingEnabled(boolean packetLoggingEnabled) {
        this.packetLoggingEnabled = packetLoggingEnabled;
    }
    public Optional<String> getPacketLoggingFilename() {
        return packetLoggingFilename;
    }
    public void setPacketLoggingFilename(Optional<String> packetLoggingFilename) {
        this.packetLoggingFilename = packetLoggingFilename;
    }
}
