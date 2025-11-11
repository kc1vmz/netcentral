package netcentral.transceiver.agw.config;

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
}
