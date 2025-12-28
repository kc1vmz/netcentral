package netcentral.server.config;

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
    @Value("${netcentral.update.url}")
    private String updateUrl;
    @Value("${netcentral.update.port}")
    private Integer updatePort;
    @Value("${netcentral.map.default.latitude.min}")
    private Integer latitudeMin;
    @Value("${netcentral.map.default.latitude.max}")
    private Integer latitudeMax;
    @Value("${netcentral.map.default.longitude.min}")
    private Integer longitudeMin;
    @Value("${netcentral.map.default.longitude.max}")
    private Integer longitudeMax;
    @Value("${netcentral.federated}")
    private Boolean federated;
    @Value("${netcentral.net.report.minutes}")
    private Integer netReportMinutes;

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
    public String getUpdateUrl() {
        return updateUrl;
    }
    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }
    public Integer getUpdatePort() {
        return updatePort;
    }
    public void setUpdatePort(Integer updatePort) {
        this.updatePort = updatePort;
    }
    public Integer getLatitudeMin() {
        return latitudeMin;
    }
    public void setLatitudeMin(Integer latitudeMin) {
        this.latitudeMin = latitudeMin;
    }
    public Integer getLatitudeMax() {
        return latitudeMax;
    }
    public void setLatitudeMax(Integer latitudeMax) {
        this.latitudeMax = latitudeMax;
    }
    public Integer getLongitudeMin() {
        return longitudeMin;
    }
    public void setLongitudeMin(Integer longitudeMin) {
        this.longitudeMin = longitudeMin;
    }
    public Integer getLongitudeMax() {
        return longitudeMax;
    }
    public void setLongitudeMax(Integer longitudeMax) {
        this.longitudeMax = longitudeMax;
    }
    public Boolean isFederated() {
        return federated;
    }
    public void setFederated(Boolean federated) {
        this.federated = federated;
    }
    public Integer getNetReportMinutes() {
        return netReportMinutes;
    }
    public void setNetReportMinutes(Integer netReportMinutes) {
        this.netReportMinutes = netReportMinutes;
    }
}