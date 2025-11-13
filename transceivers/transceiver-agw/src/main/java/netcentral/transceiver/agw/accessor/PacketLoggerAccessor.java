package netcentral.transceiver.agw.accessor;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.agw.config.FeatureConfiguration;

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
