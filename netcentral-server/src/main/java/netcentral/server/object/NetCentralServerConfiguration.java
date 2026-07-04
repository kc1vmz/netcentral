package netcentral.server.object;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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

public class NetCentralServerConfiguration {
    private int configSet;
    private int objectBeaconMinutes;
    private int objectCleanupMinutes;
    private int reportCleanupMinutes;
    private int scheduledNetCheckMinutes;
    private int netParticipantReminderMinutes;
    private int netReportMinutes;
    private String bulletinAnnounce;
    private int mapDefaultLatitudeMin;
    private int mapDefaultLongitudeMin;
    private int mapDefaultLatitudeMax;
    private int mapDefaultLongitudeMax;

    private boolean federated;
    private boolean federatedPushUdp;
    private boolean federatedPushMessage;
    private boolean federatedInterrogate;
    private boolean logRawPackets;

    private boolean netMgrEnabled;
    private String netMgrCallsign;
    private String netMgrLon;
    private String netMgrLat;
    private boolean osmPreCache;

    public NetCentralServerConfiguration() {
    }
    public NetCentralServerConfiguration(int configSet, int objectBeaconMinutes, int objectCleanupMinutes, int reportCleanupMinutes, int scheduledNetCheckMinutes, int netParticipantReminderMinutes,
                                                int netReportMinutes, String bulletinAnnounce, int mapDefaultLatitudeMin, int mapDefaultLongitudeMin, int mapDefaultLatitudeMax, int mapDefaultLongitudeMax,
                                                boolean federated, boolean federatedPushUdp, boolean federatedPushMessage, boolean federatedInterrogate, boolean logRawPackets, boolean netMgrEnabled,
                                                String netMgrCallsign, String netMgrLon, String netMgrLat, boolean osmPreCache) {
        this.configSet = configSet;
        this.objectBeaconMinutes = objectBeaconMinutes;
        this.objectCleanupMinutes = objectCleanupMinutes;
        this.reportCleanupMinutes = reportCleanupMinutes;
        this.scheduledNetCheckMinutes = scheduledNetCheckMinutes;
        this.netParticipantReminderMinutes = netParticipantReminderMinutes;
        this.netReportMinutes = netReportMinutes;
        this.bulletinAnnounce = bulletinAnnounce;
        this.mapDefaultLatitudeMin = mapDefaultLatitudeMin;
        this.mapDefaultLongitudeMin = mapDefaultLongitudeMin;
        this.mapDefaultLatitudeMax = mapDefaultLatitudeMax;
        this.mapDefaultLongitudeMax = mapDefaultLongitudeMax;
        this.federated = federated;
        this.federatedPushUdp = federatedPushUdp;
        this.federatedPushMessage = federatedPushMessage;
        this.federatedInterrogate = federatedInterrogate;
        this.logRawPackets = logRawPackets;
        this.netMgrEnabled = netMgrEnabled;
        this.netMgrCallsign = netMgrCallsign;
        this.netMgrLon = netMgrLon;
        this.netMgrLat = netMgrLat;
        this.osmPreCache = osmPreCache;
    }
    public int getConfigSet() {
        return configSet;
    }
    public void setConfigSet(int configSet) {
        this.configSet = configSet;
    }
    public int getObjectBeaconMinutes() {
        return objectBeaconMinutes;
    }
    public void setObjectBeaconMinutes(int objectBeaconMinutes) {
        this.objectBeaconMinutes = objectBeaconMinutes;
    }
    public int getObjectCleanupMinutes() {
        return objectCleanupMinutes;
    }
    public void setObjectCleanupMinutes(int objectCleanupMinutes) {
        this.objectCleanupMinutes = objectCleanupMinutes;
    }
    public int getReportCleanupMinutes() {
        return reportCleanupMinutes;
    }
    public void setReportCleanupMinutes(int reportCleanupMinutes) {
        this.reportCleanupMinutes = reportCleanupMinutes;
    }
    public int getScheduledNetCheckMinutes() {
        return scheduledNetCheckMinutes;
    }
    public void setScheduledNetCheckMinutes(int scheduledNetCheckMinutes) {
        this.scheduledNetCheckMinutes = scheduledNetCheckMinutes;
    }
    public int getNetParticipantReminderMinutes() {
        return netParticipantReminderMinutes;
    }
    public void setNetParticipantReminderMinutes(int netParticipantReminderMinutes) {
        this.netParticipantReminderMinutes = netParticipantReminderMinutes;
    }
    public int getNetReportMinutes() {
        return netReportMinutes;
    }
    public void setNetReportMinutes(int netReportMinutes) {
        this.netReportMinutes = netReportMinutes;
    }
    public String getBulletinAnnounce() {
        return bulletinAnnounce;
    }
    public void setBulletinAnnounce(String bulletinAnnounce) {
        this.bulletinAnnounce = bulletinAnnounce;
    }
    public int getMapDefaultLatitudeMin() {
        return mapDefaultLatitudeMin;
    }
    public void setMapDefaultLatitudeMin(int mapDefaultLatitudeMin) {
        this.mapDefaultLatitudeMin = mapDefaultLatitudeMin;
    }
    public int getMapDefaultLongitudeMin() {
        return mapDefaultLongitudeMin;
    }
    public void setMapDefaultLongitudeMin(int mapDefaultLongitudeMin) {
        this.mapDefaultLongitudeMin = mapDefaultLongitudeMin;
    }
    public int getMapDefaultLatitudeMax() {
        return mapDefaultLatitudeMax;
    }
    public void setMapDefaultLatitudeMax(int mapDefaultLatitudeMax) {
        this.mapDefaultLatitudeMax = mapDefaultLatitudeMax;
    }
    public int getMapDefaultLongitudeMax() {
        return mapDefaultLongitudeMax;
    }
    public void setMapDefaultLongitudeMax(int mapDefaultLongitudeMax) {
        this.mapDefaultLongitudeMax = mapDefaultLongitudeMax;
    }
    public boolean isFederated() {
        return federated;
    }
    public void setFederated(boolean federated) {
        this.federated = federated;
    }
    public boolean isFederatedPushUdp() {
        return federatedPushUdp;
    }
    public void setFederatedPushUdp(boolean federatedPushUdp) {
        this.federatedPushUdp = federatedPushUdp;
    }
    public boolean isFederatedPushMessage() {
        return federatedPushMessage;
    }
    public void setFederatedPushMessage(boolean federatedPushMessage) {
        this.federatedPushMessage = federatedPushMessage;
    }
    public boolean isFederatedInterrogate() {
        return federatedInterrogate;
    }
    public void setFederatedInterrogate(boolean federatedInterrogate) {
        this.federatedInterrogate = federatedInterrogate;
    }
    public boolean isLogRawPackets() {
        return logRawPackets;
    }
    public void setLogRawPackets(boolean logRawPackets) {
        this.logRawPackets = logRawPackets;
    }
    public boolean isNetMgrEnabled() {
        return netMgrEnabled;
    }
    public void setNetMgrEnabled(boolean netMgrEnabled) {
        this.netMgrEnabled = netMgrEnabled;
    }
    public String getNetMgrCallsign() {
        return netMgrCallsign;
    }
    public void setNetMgrCallsign(String netMgrCallsign) {
        this.netMgrCallsign = netMgrCallsign;
    }
    public String getNetMgrLon() {
        return netMgrLon;
    }
    public void setNetMgrLon(String netMgrLon) {
        this.netMgrLon = netMgrLon;
    }
    public String getNetMgrLat() {
        return netMgrLat;
    }
    public void setNetMgrLat(String netMgrLat) {
        this.netMgrLat = netMgrLat;
    }
    public boolean isOsmPreCache() {
        return osmPreCache;
    }
    public void setOsmPreCache(boolean osmPreCache) {
        this.osmPreCache = osmPreCache;
    }
}
