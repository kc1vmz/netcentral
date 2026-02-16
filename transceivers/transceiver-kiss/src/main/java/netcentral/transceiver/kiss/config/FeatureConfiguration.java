package netcentral.transceiver.kiss.config;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import java.util.Optional;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class FeatureConfiguration {
    @Value("${feature.listener.sleep.enabled}")
    private boolean listenerSleep;
    @Value("${feature.listener.readlock.enabled}")
    private boolean listenerReadLock;
    @Value("${feature.listener.enabled}")
    private boolean listenerEnabled;
    @Value("${feature.listener.igate}")
    private boolean listenerIgateEnabled;
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
    public boolean isListenerIgateEnabled() {
        return listenerIgateEnabled;
    }
    public void setListenerIgateEnabled(boolean listenerIgateEnabled) {
        this.listenerIgateEnabled = listenerIgateEnabled;
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
