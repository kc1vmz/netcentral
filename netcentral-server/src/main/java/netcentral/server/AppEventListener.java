package netcentral.server;

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

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSObjectResource;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.accessor.NetParticipantReminderAccessor;
import netcentral.server.accessor.NetQuestionReminderAccessor;
import netcentral.server.accessor.NetSchedulerAccessor;
import netcentral.server.accessor.ObjectBeaconAccessor;
import netcentral.server.accessor.ObjectCleanupAccessor;
import netcentral.server.accessor.ReportCleanupAccessor;
import netcentral.server.accessor.StatisticsAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.config.NetConfigServerConfig;
import netcentral.server.object.User;
import netcentral.server.utils.APRSCreateObjectQueue;


@Singleton
public class AppEventListener implements ApplicationEventListener<StartupEvent> {
    private static final Logger logger = LogManager.getLogger(AppEventListener.class);
    @Inject
    private ReportCleanupAccessor reportCleanupAccessor;
    @Inject
    private NetSchedulerAccessor netSchedulerAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private NetConfigServerConfig netConfigServerConfig;
    @Inject
    private ObjectBeaconAccessor priorityObjectBeaconAccessor;
    @Inject
    private NetParticipantReminderAccessor netParticipantReminderAccessor;
    @Inject
    private NetQuestionReminderAccessor netQuestionReminderAccessor;
    @Inject
    private APRSCreateObjectQueue aprsCreateObjectQueue;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private ObjectCleanupAccessor objectCleanupAccessor;
    @Inject
    private SessionAccessor sessionAccessor;

    private static final long MINUTES_TO_MILLIS = 60000L;

    @Override
    public void onApplicationEvent(StartupEvent event) {
        statisticsAccessor.markStartTime();
        startAPRSObjectProcessorThreads();
        startReportCleanupThread();
        startNetSchedulerThread();
        startObjectBeaconThread();
        startNetParticipantReminderThread();
        startObjectCleanupThread();
        startNetQuestionReminderThread();
    }

    void startReportCleanupThread() {
        new Thread(() -> {
            statisticsAccessor.setLastHeartBeatName1("Report Cleanup Thread");
            boolean run = true;
            while (run) {
                try {
                    Thread.sleep(MINUTES_TO_MILLIS*netConfigServerConfig.getReportCleanupMinutes());
                    statisticsAccessor.markLastHeartBeatTime1();
                    reportCleanupAccessor.deleteReportFiles();
                    run = reportCleanupAccessor.stayRunning();
                } catch (InterruptedException e) {
                    logger.error("Exception caught cleaning up reports", e);
                    run = false;
                } catch (Exception e) {
                    logger.error("Exception caught cleaning up reports", e);
                }
            }
        }).start();
    }

    void startNetSchedulerThread() {
        new Thread(() -> {
            statisticsAccessor.setLastHeartBeatName2("Scheduled Net Thread");
            boolean run = true;
            while (run) {
                try {
                    Thread.sleep(MINUTES_TO_MILLIS*netConfigServerConfig.getScheduledNetCheckMinutes());
                    statisticsAccessor.markLastHeartBeatTime2();
                    netSchedulerAccessor.startAndStopNets();
                    run = netSchedulerAccessor.stayRunning();
                } catch (InterruptedException e) {
                    logger.error("Exception caught in scheduled net loop", e);
                    run = false;
                } catch (Exception e) {
                    logger.error("Exception caught in scheduled net loop", e);
                }
            }
        }).start();
    }

    void startObjectBeaconThread() {
        new Thread(() -> {
            statisticsAccessor.setLastHeartBeatName3("Object Beacon Thread");
            boolean run = true;
            while (run) {
                try {
                    Thread.sleep(MINUTES_TO_MILLIS*netConfigServerConfig.getObjectBeaconMinutes());
                    statisticsAccessor.markLastHeartBeatTime3();
                    priorityObjectBeaconAccessor.beaconObjects();
                    run = priorityObjectBeaconAccessor.beaconStayRunning();
                } catch (InterruptedException e) {
                    logger.error("Exception caught beaconing objects", e);
                    run = false;
                } catch (Exception e) {
                    logger.error("Exception caught beaconing objects", e);
                }
            }
        }).start();
    }

    void startNetQuestionReminderThread() {
        new Thread(() -> {
            boolean run = true;
            while (run) {
                try {
                    Thread.sleep(MINUTES_TO_MILLIS*1); // check every minute
                    netQuestionReminderAccessor.sendQuestionReminders();
                    run = netQuestionReminderAccessor.stayRunning();
                } catch (InterruptedException e) {
                    logger.error("Exception caught reminding net participants of questions", e);
                    run = false;
                } catch (Exception e) {
                    logger.error("Exception caught reminding net participants of questions", e);
                }
            }
        }).start();
    }

    void startNetParticipantReminderThread() {
        new Thread(() -> {
            statisticsAccessor.setLastHeartBeatName4("Net Participant Reminder Thread");
            boolean run = true;
            while (run) {
                try {
                    Thread.sleep(MINUTES_TO_MILLIS*netConfigServerConfig.getNetParticipantReminderMinutes());
                    statisticsAccessor.markLastHeartBeatTime4();
                    netParticipantReminderAccessor.sendParticipantReminders();
                    run = netParticipantReminderAccessor.stayRunning();
                } catch (InterruptedException e) {
                    logger.error("Exception caught reminding net participants", e);
                    run = false;
                } catch (Exception e) {
                    logger.error("Exception caught reminding net participants", e);
                }
            }
        }).start();
    }

    void startObjectCleanupThread() {
        new Thread(() -> {
            boolean run = true;
            User systemUser = sessionAccessor.getSystemUser();
            while (run) {
                try {
                    objectCleanupAccessor.cleanupObjectsByTime(systemUser);
                    run = objectCleanupAccessor.stayRunning();
                    Thread.sleep(MINUTES_TO_MILLIS*netConfigServerConfig.getObjectCleanupMinutes());
                } catch (InterruptedException e) {
                    logger.error("InterruptedException caught cleaning up objects", e);
                    run = false;
                } catch (Exception e) {
                    logger.error("Exception caught cleaning up objects", e);
                }
            }
        }).start();
    }

    void startAPRSObjectProcessorThreads() {

        for (int i = 0; i < netConfigServerConfig.getQueueObjectHandlerThreads(); i++) {
            new Thread(() -> {
                boolean run = true;
                boolean shutdownok = false;
                ArrayBlockingQueue<APRSObjectResource> queue = aprsCreateObjectQueue.getQueue();
                User user = new User();
                while (run) {
                    try {
                        APRSObjectResource aprsObject = queue.take();
                        int queueSize = queue.size();
                        statisticsAccessor.setOutstandingObjects(queueSize);
                        aprsObjectAccessor.create(user, aprsObject);
                        run = aprsCreateObjectQueue.stayRunning();
                    } catch (InterruptedException e) {
                        logger.error("InterruptedException caught processing objects", e);
                        run = false;
                        shutdownok = true;
                    } catch (Exception e) {
                        logger.error("Exception caught processing objects", e);
                    }
                }
                if (!shutdownok) { 
                    logger.error("Unexpected end of processing loop");
                } else {
                    logger.info("End of processing loop");
                }
            }).start();
        }
    }
}

