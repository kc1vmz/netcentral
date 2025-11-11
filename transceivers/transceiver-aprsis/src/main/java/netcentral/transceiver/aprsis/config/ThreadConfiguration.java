package netcentral.transceiver.aprsis.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class ThreadConfiguration {
    @Value("${threads.count}")
    private Integer count;
    @Value("${threads.listener.pause.seconds}")
    private Integer listenerPauseInSeconds;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getListenerPauseInSeconds() {
        return listenerPauseInSeconds;
    }

    public void setListenerPauseInSeconds(Integer listenerPauseInSeconds) {
        this.listenerPauseInSeconds = listenerPauseInSeconds;
    }
}
