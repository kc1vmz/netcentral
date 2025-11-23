package netcentral.transceiver.aprsis.accessor;

import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;

import io.micronaut.context.ApplicationContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.client.NetCentralRESTClient;
import netcentral.transceiver.aprsis.exception.LoginFailureException;

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
}
