package netcentral.server.accessor;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.NetCentralServerConfig;
import netcentral.server.object.NetCentralServerConfiguration;
import netcentral.server.object.User;
import netcentral.server.record.NetCentralServerConfigRecord;
import netcentral.server.repository.NetCentralServerConfigRepository;

@Singleton
public class NetCentralServerConfigAccessor {

    @Inject
    private NetCentralServerConfig netCentralServerConfig;
    @Inject
    private NetCentralServerConfigRepository netCentralServerConfigRepository;

    private static final int CONFIG_SET_DEFAULT = 0;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    NetCentralServerConfigRecord cachedRecord = null;
 
    private NetCentralServerConfigRecord getConfigRecord(int config_set) {
        readLock.lock();
        if (cachedRecord != null) {
            NetCentralServerConfigRecord ret = cachedRecord;
            readLock.unlock();
            return ret;
        }
        readLock.unlock();

        writeLock.lock();
        try {
            Optional<NetCentralServerConfigRecord> rec = netCentralServerConfigRepository.findById(config_set);
            if (rec.isPresent()) {
                NetCentralServerConfigRecord ret = rec.get();
                cachedRecord = ret;
                writeLock.unlock();
                return ret;
            }
        } catch (Exception e) {
        }

        NetCentralServerConfigRecord ret = new NetCentralServerConfigRecord(CONFIG_SET_DEFAULT, netCentralServerConfig.getObjectBeaconMinutes(), netCentralServerConfig.getObjectCleanupMinutes(), 
                            netCentralServerConfig.getReportCleanupMinutes(), netCentralServerConfig.getScheduledNetCheckMinutes(), netCentralServerConfig.getNetParticipantReminderMinutes(), 
                            netCentralServerConfig.getNetReportMinutes(), netCentralServerConfig.getBulletinAnnounce(), netCentralServerConfig.getLatitudeMin(), netCentralServerConfig.getLongitudeMin(), 
                            netCentralServerConfig.getLatitudeMax(), netCentralServerConfig.getLongitudeMax(), netCentralServerConfig.isFederated(), netCentralServerConfig.isFederatedPushUserDefinedPacket(), 
                            netCentralServerConfig.isFederatedPushMessage(), netCentralServerConfig.isFederatedInterrogate(), netCentralServerConfig.isLogRawPackets());

        try {
            netCentralServerConfigRepository.save(cachedRecord);
            cachedRecord = ret;
        } catch (Exception e) {
        }

        writeLock.unlock();
        return ret;
    }

    public NetCentralServerConfig getNetCentralServerConfig() {
        return netCentralServerConfig;
    }
    public void setNetCentralServerConfig(NetCentralServerConfig netCentralServerConfig) {
        this.netCentralServerConfig = netCentralServerConfig;
    }
    public String getTempDir() {
        return netCentralServerConfig.getTempDir();
    }
    public void setTempDir(String tempDir) {
        netCentralServerConfig.setTempDir(tempDir);
    }
    public Integer getObjectBeaconMinutes() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.object_beacon_minutes();
    }
    public void setObjectBeaconMinutes(Integer objectBeaconMinutes) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), objectBeaconMinutes, rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    private void updateConfigurationData(NetCentralServerConfigRecord recNew) {
        writeLock.lock();
        try {
            cachedRecord = netCentralServerConfigRepository.update(recNew);
        } catch (Exception e) {
        }
        writeLock.unlock();
    }

    public Integer getReportCleanupMinutes() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.report_cleanup_minutes();
    }
    public void setReportCleanupMinutes(Integer reportCleanupMinutes) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), reportCleanupMinutes, 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Integer getScheduledNetCheckMinutes() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.scheduled_net_check_minutes();
    }
    public void setScheduledNetCheckMinutes(Integer scheduledNetCheckMinutes) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        scheduledNetCheckMinutes, rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Integer getNetParticipantReminderMinutes() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.net_participant_reminder_minutes();
    }
    public void setNetParticipantReminderMinutes(Integer netParticipantReminderMinutes) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), netParticipantReminderMinutes, rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public String getBulletinAnnounce() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.bulletin_announce();
    }
    public void setBulletinAnnounce(String bulletinAnnounce) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), bulletinAnnounce, 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Integer getQueueObjectHandlerSize() {
        return netCentralServerConfig.getQueueObjectHandlerSize();
    }
    public void setQueueObjectHandlerSize(Integer queueObjectHandlerSize) {
        netCentralServerConfig.setQueueObjectHandlerSize(queueObjectHandlerSize);
    }
    public String getTempReportDir() {
        return netCentralServerConfig.getTempReportDir();
    }
    public void setTempReportDir(String tempReportDir) {
        netCentralServerConfig.setTempReportDir(tempReportDir);
    }
    public String getTempLogDir() {
        return netCentralServerConfig.getTempLogDir();
    }
    public void setTempLogDir(String tempLogDir) {
        netCentralServerConfig.setTempLogDir(tempLogDir);
    }
    public Integer getObjectCleanupMinutes() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.object_cleanup_minutes();
    }
    public void setObjectCleanupMinutes(Integer objectCleanupMinutes) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), objectCleanupMinutes, rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Integer getQueueObjectHandlerThreads() {
        return netCentralServerConfig.getQueueObjectHandlerThreads();
    }
    public void setQueueObjectHandlerThreads(Integer queueObjectHandlerThreads) {
        netCentralServerConfig.setQueueObjectHandlerThreads(queueObjectHandlerThreads);
    }
    public String getUpdateUrl() {
        return netCentralServerConfig.getUpdateUrl();
    }
    public void setUpdateUrl(String updateUrl) {
        netCentralServerConfig.setUpdateUrl(updateUrl);
    }
    public Integer getUpdatePort() {
        return netCentralServerConfig.getUpdatePort();
    }
    public void setUpdatePort(Integer updatePort) {
        netCentralServerConfig.setUpdatePort(updatePort);
    }
    public Integer getLatitudeMin() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.map_default_latitude_min();
    }
    public void setLatitudeMin(Integer latitudeMin) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        latitudeMin, rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Integer getLatitudeMax() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.map_default_latitude_max();
    }
    public void setLatitudeMax(Integer latitudeMax) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), latitudeMax, rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Integer getLongitudeMin() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.map_default_longitude_min();
    }
    public void setLongitudeMin(Integer longitudeMin) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), longitudeMin, rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Integer getLongitudeMax() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.map_default_longitude_max();
    }
    public void setLongitudeMax(Integer longitudeMax) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), longitudeMax, rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Boolean isFederated() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.federated();
    }
    public void setFederated(Boolean federated) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), federated, 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Integer getNetReportMinutes() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.net_report_minutes();
    }
    public void setNetReportMinutes(Integer netReportMinutes) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), netReportMinutes, rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Boolean isLogRawPackets() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.log_raw_packets();
    }
    public void setLogRawPackets(Boolean logRawPackets) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), logRawPackets);
        updateConfigurationData(recNew);
    }
    public Boolean isFederatedPushUserDefinedPacket() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.federated_push_udp();
    }
    public void setFederatedPushUserDefinedPacket(Boolean federatedPushUserDefinedPacket) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        federatedPushUserDefinedPacket, rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Boolean isFederatedPushMessage() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.federated_push_message();
    }
    public void setFederatedPushMessage(Boolean federatedPushMessage) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), federatedPushMessage, rec.federated_interrogate(), rec.log_raw_packets());
        updateConfigurationData(recNew);
    }
    public Boolean isFederatedInterrogate() {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        return rec.federated_interrogate();
    }
    public void setFederatedInterrogate(Boolean federatedInterrogate) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfigRecord recNew = new NetCentralServerConfigRecord(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                        rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                        rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                        rec.federated_push_udp(), rec.federated_push_message(), federatedInterrogate, rec.log_raw_packets());
        updateConfigurationData(recNew);
    }

    public NetCentralServerConfiguration getDefault(User loggedInUser) {
        NetCentralServerConfigRecord rec = getConfigRecord(CONFIG_SET_DEFAULT);
        NetCentralServerConfiguration ret = new NetCentralServerConfiguration(rec.config_set(), rec.object_beacon_minutes(), rec.object_cleanup_minutes(), rec.report_cleanup_minutes(), 
                rec.scheduled_net_check_minutes(), rec.net_participant_reminder_minutes(), rec.net_report_minutes(), rec.bulletin_announce(), 
                rec.map_default_latitude_min(), rec.map_default_longitude_min(), rec.map_default_latitude_max(), rec.map_default_longitude_max(), rec.federated(), 
                rec.federated_push_udp(), rec.federated_push_message(), rec.federated_interrogate(), rec.log_raw_packets());
        return ret;
    }

    public NetCentralServerConfiguration updateDefault(User loggedInUser, NetCentralServerConfiguration obj) {
         NetCentralServerConfigRecord ret =  new NetCentralServerConfigRecord(obj.getConfigSet(), obj.getObjectBeaconMinutes(), obj.getObjectCleanupMinutes(), obj.getReportCleanupMinutes(), 
                            obj.getScheduledNetCheckMinutes(), obj.getNetParticipantReminderMinutes(), obj.getNetReportMinutes(), obj.getBulletinAnnounce(), 
                        obj.getMapDefaultLatitudeMin(), obj.getMapDefaultLongitudeMin(), obj.getMapDefaultLatitudeMax(), obj.getMapDefaultLongitudeMax(), obj.isFederated(), 
                        obj.isFederatedPushUdp(), obj.isFederatedPushMessage(), obj.isFederatedInterrogate(), obj.isLogRawPackets());
        updateConfigurationData(ret);
        return obj;
    }
}