package com.kc1vmz.netcentral.aprsobject.common;

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

import java.time.ZonedDateTime;

public class NetCentralServerStatistics {
    private long netsStarted;
    private long netsClosed;
    private long userLogins;
    private long userLogouts;
    private long objectsReceived;
    private long objectsSent;
    private long messagesSent;
    private long reportsSent;
    private ZonedDateTime startTime;
    private long startTimeMinutes;
    private ZonedDateTime lastHeartBeatTime1;
    private ZonedDateTime lastHeartBeatTime2;
    private ZonedDateTime lastHeartBeatTime3;
    private ZonedDateTime lastHeartBeatTime4;
    private String lastHeartBeatName1;
    private String lastHeartBeatName2;
    private String lastHeartBeatName3;
    private String lastHeartBeatName4;
    private long lastHeartBeatSecondsSince1;
    private long lastHeartBeatSecondsSince2;
    private long lastHeartBeatSecondsSince3;
    private long lastHeartBeatSecondsSince4;
    private ZonedDateTime lastReceivedTime;
    private ZonedDateTime lastSentTime;
    private long lastReceivedSecondsSince;
    private long lastSentSecondsSince;
    private long acksRequested;
    private long acksSent;
    private long rejsSent;
    private long outstandingObjects;

    public NetCentralServerStatistics(){
    }

    public NetCentralServerStatistics(NetCentralServerStatistics statistics) {
        netsStarted = statistics.getNetsStarted();
        netsClosed  = statistics.getNetsClosed();
        userLogins  = statistics.getUserLogins();
        userLogouts  = statistics.getUserLogouts();
        objectsReceived  = statistics.getObjectsReceived();
        objectsSent = statistics.getObjectsSent();
        messagesSent  = statistics.getMessagesSent();
        reportsSent  = statistics.getReportsSent();
        startTime = statistics.getStartTime();
        startTimeMinutes = statistics.getStartTimeMinutes();
        lastHeartBeatTime1  = statistics.getLastHeartBeatTime1();
        lastHeartBeatTime2  = statistics.getLastHeartBeatTime2();
        lastHeartBeatTime3  = statistics.getLastHeartBeatTime3();
        lastHeartBeatTime4  = statistics.getLastHeartBeatTime4();
        lastHeartBeatName1  = statistics.getLastHeartBeatName1();
        lastHeartBeatName2  = statistics.getLastHeartBeatName2();
        lastHeartBeatName3  = statistics.getLastHeartBeatName3();
        lastHeartBeatName4  = statistics.getLastHeartBeatName4();
        lastHeartBeatSecondsSince1  = statistics.getLastHeartBeatSecondsSince1();
        lastHeartBeatSecondsSince2  = statistics.getLastHeartBeatSecondsSince2();
        lastHeartBeatSecondsSince3  = statistics.getLastHeartBeatSecondsSince3();
        lastHeartBeatSecondsSince4  = statistics.getLastHeartBeatSecondsSince4();
        lastReceivedTime = statistics.getLastReceivedTime();
        lastSentTime = statistics.getLastSentTime();
        lastReceivedSecondsSince = statistics.getLastReceivedSecondsSince();
        lastSentSecondsSince = statistics.getLastSentSecondsSince();
        acksRequested = statistics.getAcksRequested();
        acksSent = statistics.getAcksSent();
        rejsSent = statistics.getRejsSent();
        outstandingObjects = statistics.getOutstandingObjects();
    }

    public long getNetsStarted() {
        return netsStarted;
    }
    public void setNetsStarted(long netsStarted) {
        this.netsStarted = netsStarted;
    }
    public long getNetsClosed() {
        return netsClosed;
    }
    public void setNetsClosed(long netsClosed) {
        this.netsClosed = netsClosed;
    }
    public long getUserLogins() {
        return userLogins;
    }
    public void setUserLogins(long userLogins) {
        this.userLogins = userLogins;
    }
    public long getUserLogouts() {
        return userLogouts;
    }
    public void setUserLogouts(long userLogouts) {
        this.userLogouts = userLogouts;
    }
    public long getObjectsReceived() {
        return objectsReceived;
    }
    public void setObjectsReceived(long objectsReceived) {
        this.objectsReceived = objectsReceived;
    }
    public long getObjectsSent() {
        return objectsSent;
    }
    public void setObjectsSent(long objectsSent) {
        this.objectsSent = objectsSent;
    }
    public long getMessagesSent() {
        return messagesSent;
    }
    public void setMessagesSent(long messagesSent) {
        this.messagesSent = messagesSent;
    }
    public ZonedDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }
    public long getStartTimeMinutes() {
        return startTimeMinutes;
    }
    public void setStartTimeMinutes(long startTimeMinutes) {
        this.startTimeMinutes = startTimeMinutes;
    }
    public ZonedDateTime getLastHeartBeatTime1() {
        return lastHeartBeatTime1;
    }
    public void setLastHeartBeatTime1(ZonedDateTime lastHeartBeatTime1) {
        this.lastHeartBeatTime1 = lastHeartBeatTime1;
    }
    public ZonedDateTime getLastHeartBeatTime2() {
        return lastHeartBeatTime2;
    }
    public void setLastHeartBeatTime2(ZonedDateTime lastHeartBeatTime2) {
        this.lastHeartBeatTime2 = lastHeartBeatTime2;
    }
    public ZonedDateTime getLastHeartBeatTime3() {
        return lastHeartBeatTime3;
    }
    public void setLastHeartBeatTime3(ZonedDateTime lastHeartBeatTime3) {
        this.lastHeartBeatTime3 = lastHeartBeatTime3;
    }
    public ZonedDateTime getLastHeartBeatTime4() {
        return lastHeartBeatTime4;
    }
    public void setLastHeartBeatTime4(ZonedDateTime lastHeartBeatTime4) {
        this.lastHeartBeatTime4 = lastHeartBeatTime4;
    }
    public String getLastHeartBeatName1() {
        return lastHeartBeatName1;
    }
    public void setLastHeartBeatName1(String lastHeartBeatName1) {
        this.lastHeartBeatName1 = lastHeartBeatName1;
    }
    public String getLastHeartBeatName2() {
        return lastHeartBeatName2;
    }
    public void setLastHeartBeatName2(String lastHeartBeatName2) {
        this.lastHeartBeatName2 = lastHeartBeatName2;
    }
    public String getLastHeartBeatName3() {
        return lastHeartBeatName3;
    }
    public void setLastHeartBeatName3(String lastHeartBeatName3) {
        this.lastHeartBeatName3 = lastHeartBeatName3;
    }
    public String getLastHeartBeatName4() {
        return lastHeartBeatName4;
    }
    public void setLastHeartBeatName4(String lastHeartBeatName4) {
        this.lastHeartBeatName4 = lastHeartBeatName4;
    }
    public long getLastHeartBeatSecondsSince1() {
        return lastHeartBeatSecondsSince1;
    }
    public void setLastHeartBeatSecondsSince1(long lastHeartBeatSecondsSince1) {
        this.lastHeartBeatSecondsSince1 = lastHeartBeatSecondsSince1;
    }
    public long getLastHeartBeatSecondsSince2() {
        return lastHeartBeatSecondsSince2;
    }
    public void setLastHeartBeatSecondsSince2(long lastHeartBeatSecondsSince2) {
        this.lastHeartBeatSecondsSince2 = lastHeartBeatSecondsSince2;
    }
    public long getLastHeartBeatSecondsSince3() {
        return lastHeartBeatSecondsSince3;
    }
    public void setLastHeartBeatSecondsSince3(long lastHeartBeatSecondsSince3) {
        this.lastHeartBeatSecondsSince3 = lastHeartBeatSecondsSince3;
    }
    public long getLastHeartBeatSecondsSince4() {
        return lastHeartBeatSecondsSince4;
    }
    public void setLastHeartBeatSecondsSince4(long lastHeartBeatSecondsSince4) {
        this.lastHeartBeatSecondsSince4 = lastHeartBeatSecondsSince4;
    }
    public ZonedDateTime getLastReceivedTime() {
        return lastReceivedTime;
    }
    public void setLastReceivedTime(ZonedDateTime lastReceivedTime) {
        this.lastReceivedTime = lastReceivedTime;
    }
    public ZonedDateTime getLastSentTime() {
        return lastSentTime;
    }
    public void setLastSentTime(ZonedDateTime lastSentTime) {
        this.lastSentTime = lastSentTime;
    }
    public long getLastReceivedSecondsSince() {
        return lastReceivedSecondsSince;
    }
    public void setLastReceivedSecondsSince(long lastReceivedSecondsSince) {
        this.lastReceivedSecondsSince = lastReceivedSecondsSince;
    }
    public long getLastSentSecondsSince() {
        return lastSentSecondsSince;
    }
    public void setLastSentSecondsSince(long lastSentSecondsSince) {
        this.lastSentSecondsSince = lastSentSecondsSince;
    }
    public long getAcksRequested() {
        return acksRequested;
    }
    public void setAcksRequested(long acksRequested) {
        this.acksRequested = acksRequested;
    }
    public long getAcksSent() {
        return acksSent;
    }
    public void setAcksSent(long acksSent) {
        this.acksSent = acksSent;
    }
    public long getReportsSent() {
        return reportsSent;
    }
    public void setReportsSent(long reportsSent) {
        this.reportsSent = reportsSent;
    }
    public long getRejsSent() {
        return rejsSent;
    }
    public void setRejsSent(long rejsSent) {
        this.rejsSent = rejsSent;
    }
    public long getOutstandingObjects() {
        return outstandingObjects;
    }
    public void setOutstandingObjects(long outstandingObjects) {
        this.outstandingObjects = outstandingObjects;
    }
}
