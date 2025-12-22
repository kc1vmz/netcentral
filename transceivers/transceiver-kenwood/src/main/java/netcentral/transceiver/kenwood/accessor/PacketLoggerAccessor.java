package netcentral.transceiver.kenwood.accessor;

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

import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kenwood.config.FeatureConfiguration;

@Singleton
public class PacketLoggerAccessor {
    private static final Logger logger = LogManager.getLogger(PacketLoggerAccessor.class);

    @Inject
    FeatureConfiguration featureConfiguration;

    public synchronized void savePacket(String contentToAppend) {
        if (!featureConfiguration.isPacketLoggingEnabled()) {
            return;
        }

        if (!featureConfiguration.getPacketLoggingFilename().isPresent()) {
            logger.error("No packet logger filename specified");
            return;
        }

        try {
            FileWriter fw = new FileWriter(featureConfiguration.getPacketLoggingFilename().get(), true); 
            fw.write(contentToAppend+"\n");
            fw.close();
        } catch (IOException e) {
            logger.error("IOException caught saving packet", e);
        } catch (Exception e) {
            logger.error("Exception caught saving packet", e);
        }
    }
}
