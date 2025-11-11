package netcentral.server.accessor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.NetConfigServerConfig;


@Singleton
public class ReportCleanupAccessor {
    private static final Logger logger = LogManager.getLogger(ReportCleanupAccessor.class);

    @Inject
    private NetConfigServerConfig netConfigServerConfig;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();

    Map<String, ZonedDateTime> reportFilenames = null;
    private boolean stop = false;

    public void deleteReportFiles() {
        ZonedDateTime n = ZonedDateTime.now().plusMinutes(3);
        writeLock.lock();
        try {
            File directory = new File(netConfigServerConfig.getTempReportDir());
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        if (file.isFile()) {
                            if (file.lastModified() < n.toEpochSecond()*1000) {
                                Path pathToDelete = Paths.get(file.getAbsolutePath()); // Replace with your file path
                                Files.delete(pathToDelete);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Failed to delete file: " + file.getAbsolutePath(), e);
                    }
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void shutdown() {
        stop = true;
    }

    public boolean stayRunning() {
        return !stop;
    }
}
