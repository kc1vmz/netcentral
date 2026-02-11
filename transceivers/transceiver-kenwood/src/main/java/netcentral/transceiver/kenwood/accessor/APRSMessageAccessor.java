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

import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;
import com.kc1vmz.netcentral.common.exception.LoginFailureException;

import io.micronaut.context.ApplicationContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kenwood.client.NetCentralRESTClient;

@Singleton
public class APRSMessageAccessor {
    @Inject
    ApplicationContext applicationContext;
    @Inject
    private NetCentralRESTClient netControlRESTClient;
    @Inject
    private StatisticsAccessor statisticsAccessor;

    public void sendObject(String objectName, String message, boolean alive, String lat, String lon) {
        APRSListenerAccessor la = applicationContext.getBean(APRSListenerAccessor.class);
        if (la != null) {
            la.sendObject(objectName, message, alive, lat, lon);
        }
    }

    public void sendAckMessage(String callsignFrom, String callsignTo, String messageText) {
        statisticsAccessor.incrementAcksSent();
        APRSListenerAccessor la = applicationContext.getBean(APRSListenerAccessor.class);
        if (la != null) {
            la.sendMessage(callsignFrom, callsignTo, messageText);
        }
    }

    public synchronized void sendMessage(String callsignFrom, String callsignTo, String messageText, boolean ackRequested) {
        // add message number for ack
        if (ackRequested) {
            String ack = getAckCounter(callsignFrom, callsignTo);
            if (ack != null) {
                messageText += String.format("{%s", ack);
            }
        }
        APRSListenerAccessor la = applicationContext.getBean(APRSListenerAccessor.class);
        if (la != null) {
            la.sendMessage(callsignFrom, callsignTo, messageText);
        }
    }

    private String getAckCounter(String callsignFrom, String callsignTo) {
        try {
            return netControlRESTClient.getAckCounter(callsignFrom, callsignTo);
        } catch (LoginFailureException e) {
        }
        return null;
    }

    public synchronized void sendBulletin(String callsignFrom, String callsignTo, String messageText) {
        // add message number for ack
        APRSListenerAccessor la = applicationContext.getBean(APRSListenerAccessor.class);
        if (la != null) {
            la.sendBulletin(callsignFrom, callsignTo, messageText);
        }
    }

    public synchronized void sendReport(String callsignFrom, APRSNetCentralReport obj) {
        // add message number for ack
        APRSListenerAccessor la = applicationContext.getBean(APRSListenerAccessor.class);
        if (la != null) {
            la.sendReport(callsignFrom, obj);
        }
    }

    public synchronized void sendQuery(String callsignFrom, String callsignTo, String queryType) {
        APRSListenerAccessor la = applicationContext.getBean(APRSListenerAccessor.class);
        if (la != null) {
            la.sendQuery(callsignFrom, callsignTo, queryType);
        }
    }
}
