package netcentral.server.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class NetConfigServerConfig {
    @Value("${netcentral.temp.dir}")
    private String tempDir;
    @Value("${netcentral.temp.dir.report}")
    private String tempReportDir;
    @Value("${netcentral.temp.dir.log}")
    private String tempLogDir;
    @Value("${netcentral.aprs.object.beacon.minutes}")
    private Integer objectBeaconMinutes;
    @Value("${netcentral.object.cleanup.minutes}")
    private Integer objectCleanupMinutes;
    @Value("${netcentral.report.cleanup.minutes}")
    private Integer reportCleanupMinutes;
    @Value("${netcentral.schedulednet.check.minutes}")
    private Integer scheduledNetCheckMinutes;
    @Value("${netcentral.participant.reminder.minutes}")
    private Integer netParticipantReminderMinutes;
    @Value("${netcentral.aprs.bulletin.announce}")
    private String bulletinAnnounce;
    @Value("${netcentral.queue.objecthandler.size}")
    private Integer queueObjectHandlerSize;
    @Value("${netcentral.queue.objecthandler.threads}")
    private Integer queueObjectHandlerThreads;
    
    public String getTempDir() {
        return tempDir;
    }
    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }
    public Integer getObjectBeaconMinutes() {
        return objectBeaconMinutes;
    }
    public void setObjectBeaconMinutes(Integer objectBeaconMinutes) {
        this.objectBeaconMinutes = objectBeaconMinutes;
    }
    public Integer getReportCleanupMinutes() {
        return reportCleanupMinutes;
    }
    public void setReportCleanupMinutes(Integer reportCleanupMinutes) {
        this.reportCleanupMinutes = reportCleanupMinutes;
    }
    public Integer getScheduledNetCheckMinutes() {
        return scheduledNetCheckMinutes;
    }
    public void setScheduledNetCheckMinutes(Integer scheduledNetCheckMinutes) {
        this.scheduledNetCheckMinutes = scheduledNetCheckMinutes;
    }
    public Integer getNetParticipantReminderMinutes() {
        return netParticipantReminderMinutes;
    }
    public void setNetParticipantReminderMinutes(Integer netParticipantReminderMinutes) {
        this.netParticipantReminderMinutes = netParticipantReminderMinutes;
    }
    public String getBulletinAnnounce() {
        return bulletinAnnounce;
    }
    public void setBulletinAnnounce(String bulletinAnnounce) {
        this.bulletinAnnounce = bulletinAnnounce;
    }
    public Integer getQueueObjectHandlerSize() {
        return queueObjectHandlerSize;
    }
    public void setQueueObjectHandlerSize(Integer queueObjectHandlerSize) {
        this.queueObjectHandlerSize = queueObjectHandlerSize;
    }
    public String getTempReportDir() {
        return tempReportDir;
    }
    public void setTempReportDir(String tempReportDir) {
        this.tempReportDir = tempReportDir;
    }
    public String getTempLogDir() {
        return tempLogDir;
    }
    public void setTempLogDir(String tempLogDir) {
        this.tempLogDir = tempLogDir;
    }
    public Integer getObjectCleanupMinutes() {
        return objectCleanupMinutes;
    }
    public void setObjectCleanupMinutes(Integer objectCleanupMinutes) {
        this.objectCleanupMinutes = objectCleanupMinutes;
    }
    public Integer getQueueObjectHandlerThreads() {
        return queueObjectHandlerThreads;
    }
    public void setQueueObjectHandlerThreads(Integer queueObjectHandlerThreads) {
        this.queueObjectHandlerThreads = queueObjectHandlerThreads;
    }
}