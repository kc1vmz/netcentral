package netcentral.server.accessor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetControlShelterCensusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetControlShelterOperationalFoodReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetControlShelterOperationalMaterielReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetControlShelterStatusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetControlShelterWorkerReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetControlEOCContactReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetControlEOCMobilizationReport;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ObjectShelterReportingTimeframe;
import netcentral.server.object.User;
import netcentral.server.record.report.EOCContactReportRecord;
import netcentral.server.record.report.EOCMobilizationReportRecord;
import netcentral.server.record.report.ShelterCensusReportRecord;
import netcentral.server.record.report.ShelterOperationalFoodReportRecord;
import netcentral.server.record.report.ShelterOperationalMaterielReportRecord;
import netcentral.server.record.report.ShelterStatusReportRecord;
import netcentral.server.record.report.ShelterWorkerReportRecord;
import netcentral.server.repository.report.EOCContactReportRepository;
import netcentral.server.repository.report.EOCMobilizationReportRepository;
import netcentral.server.repository.report.ShelterCensusReportRepository;
import netcentral.server.repository.report.ShelterOperationalFoodReportRepository;
import netcentral.server.repository.report.ShelterOperationalMaterielReportRepository;
import netcentral.server.repository.report.ShelterStatusReportRepository;
import netcentral.server.repository.report.ShelterWorkerReportRepository;

@Singleton
public class ReportAccessor {
    private static final Logger logger = LogManager.getLogger(ReportAccessor.class);

    @Inject
    private ShelterStatusReportRepository shelterStatusReportRepository;
    @Inject
    private ShelterCensusReportRepository shelterCensusReportRepository;
    @Inject
    private ShelterWorkerReportRepository shelterWorkerReportRepository;
    @Inject
    private ShelterOperationalFoodReportRepository shelterOperationalFoodReportRepository;
    @Inject
    private ShelterOperationalMaterielReportRepository shelterOperationalMaterielReportRepository;
    @Inject
    private EOCMobilizationReportRepository eocMobilizationReportRepository;
    @Inject
    private EOCContactReportRepository eocContactReportRepository;


    public APRSNetControlShelterCensusReport getLatestShelterCensusReport(User loggedInUser, String callsign) {
        APRSNetControlShelterCensusReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterCensusReportRecord> records = shelterCensusReportRepository.findAll();
            ShelterCensusReportRecord latestRecord = null;
            ZonedDateTime latestTime = ZonedDateTime.now().minusYears(100);
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterCensusReportRecord rec : records) {
                    if (latestTime.isBefore(rec.reported_date())) {
                        latestTime = rec.reported_date();
                        latestRecord = rec;
                    }
                }
            }

            if (latestRecord != null) {
                 report = new APRSNetControlShelterCensusReport(latestRecord.callsign(), latestRecord.p03(), latestRecord.p47(), latestRecord.p812(), latestRecord.p1318(), latestRecord.p1965(), latestRecord.p66(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }


    public List<APRSNetControlShelterCensusReport> getAllLatestShelterCensusReport(User loggedInUser, String callsign) {
        List<APRSNetControlShelterCensusReport> reports = new ArrayList<>();

        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterCensusReportRecord> records = shelterCensusReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterCensusReportRecord rec : records) {
                    reports.add(new APRSNetControlShelterCensusReport(rec.callsign(), rec.p03(), rec.p47(), rec.p812(), rec.p1318(), rec.p1965(), rec.p66(), rec.reported_date()));
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught getting all reports", e);
        }
        return reports;
    }

    public APRSNetControlShelterStatusReport getLatestShelterStatusReport(User loggedInUser, String callsign) {
        APRSNetControlShelterStatusReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterStatusReportRecord> records = shelterStatusReportRepository.findAll();
            ShelterStatusReportRecord latestRecord = null;
            ZonedDateTime latestTime = ZonedDateTime.now().minusYears(100);
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterStatusReportRecord rec : records) {
                    if (latestTime.isBefore(rec.reported_date())) {
                        latestTime = rec.reported_date();
                        latestRecord = rec;
                    }
                }
            }

            if (latestRecord != null) {
                 report = new APRSNetControlShelterStatusReport(latestRecord.callsign(), latestRecord.status(), latestRecord.state(), latestRecord.message(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public APRSNetControlShelterWorkerReport getLatestShelterWorkerReport(User loggedInUser, String callsign, int shift) {
        APRSNetControlShelterWorkerReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterWorkerReportRecord> records = shelterWorkerReportRepository.findAll();
            ShelterWorkerReportRecord latestRecord = null;
            ZonedDateTime latestTime = ZonedDateTime.now().minusYears(100);
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterWorkerReportRecord rec : records) {
                    if (rec.shift() == shift) {
                        if (latestTime.isBefore(rec.date())) {
                            latestTime = rec.date();
                            latestRecord = rec;
                        }
                    }
                }
            }

            if (latestRecord != null) {
                 report = new APRSNetControlShelterWorkerReport(latestRecord.callsign(), latestRecord.shift(), latestRecord.health(), latestRecord.mental(), latestRecord.spiritual(), 
                                                                latestRecord.caseworker(), latestRecord.feeding(), latestRecord.other(), latestRecord.date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public APRSNetControlShelterOperationalFoodReport getLatestShelterOperationalFoodReport(User loggedInUser, String callsign, ObjectShelterReportingTimeframe timeframe) {
        APRSNetControlShelterOperationalFoodReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterOperationalFoodReportRecord> records = shelterOperationalFoodReportRepository.findAll();
            ShelterOperationalFoodReportRecord latestRecord = null;
            ZonedDateTime latestTime = ZonedDateTime.now().minusYears(100);
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterOperationalFoodReportRecord rec : records) {
                    if (rec.timeframe() == timeframe.ordinal()) {
                        if (latestTime.isBefore(rec.date())) {
                            latestTime = rec.date();
                            latestRecord = rec;
                        }
                    }
                }
            }

            if (latestRecord != null) {
                 report = new APRSNetControlShelterOperationalFoodReport(latestRecord.callsign(), latestRecord.timeframe(), latestRecord.breakfast(), latestRecord.lunch(), latestRecord.dinner(), latestRecord.snack(), latestRecord.date(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public APRSNetControlShelterOperationalMaterielReport getLatestShelterOperationalMaterielReport(User loggedInUser, String callsign, ObjectShelterReportingTimeframe timeframe) {
        APRSNetControlShelterOperationalMaterielReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterOperationalMaterielReportRecord> records = shelterOperationalMaterielReportRepository.findAll();
            ShelterOperationalMaterielReportRecord latestRecord = null;
            ZonedDateTime latestTime = ZonedDateTime.now().minusYears(100);
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterOperationalMaterielReportRecord rec : records) {
                    if (rec.timeframe() == timeframe.ordinal()) {
                        if (latestTime.isBefore(rec.date())) {
                            latestTime = rec.date();
                            latestRecord = rec;
                        }
                    }
                }
            }

            if (latestRecord != null) {
                 report = new APRSNetControlShelterOperationalMaterielReport(latestRecord.callsign(), latestRecord.timeframe(), latestRecord.cots(), latestRecord.blankets(), 
                                        latestRecord.comfort(), latestRecord.cleanup(), latestRecord.signage(), latestRecord.other(), latestRecord.date(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public APRSNetControlEOCMobilizationReport getEOCMobilizationInformation(User loggedInUser, String callsign) {
        APRSNetControlEOCMobilizationReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCMobilizationReportRecord> records = eocMobilizationReportRepository.findAll();
            EOCMobilizationReportRecord latestRecord = null;
            ZonedDateTime latestTime = ZonedDateTime.now().minusYears(100);
            
            if ((records != null) && (!records.isEmpty())) {
                for (EOCMobilizationReportRecord rec : records) {
                    if (latestTime.isBefore(rec.reported_date())) {
                        latestTime = rec.reported_date();
                        latestRecord = rec;
                    }
                }
            }

            if (latestRecord != null) {
                 report = new APRSNetControlEOCMobilizationReport(latestRecord.callsign(), latestRecord.eoc_name(), latestRecord.status(), latestRecord.level(), latestRecord.reported_date());
            } else {
                // default report 
                 report = new APRSNetControlEOCMobilizationReport(callsign, "", 0, 0, ZonedDateTime.now());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public List<APRSNetControlEOCMobilizationReport> getAllEOCMobilizationInformation(User loggedInUser, String callsign) {
        List<APRSNetControlEOCMobilizationReport> reports = new ArrayList<>();
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCMobilizationReportRecord> records = eocMobilizationReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (EOCMobilizationReportRecord rec : records) {
                    reports.add(new APRSNetControlEOCMobilizationReport(rec.callsign(), rec.eoc_name(), rec.status(), rec.level(), rec.reported_date()));
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return reports;
   }

    public APRSNetControlEOCContactReport getEOCContactInformation(User loggedInUser, String callsign) {
        APRSNetControlEOCContactReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCContactReportRecord> records = eocContactReportRepository.findAll();
            EOCContactReportRecord latestRecord = null;
            ZonedDateTime latestTime = ZonedDateTime.now().minusYears(100);
            
            if ((records != null) && (!records.isEmpty())) {
                for (EOCContactReportRecord rec : records) {
                    if (latestTime.isBefore(rec.reported_date())) {
                        latestTime = rec.reported_date();
                        latestRecord = rec;
                    }
                }
            }

            if (latestRecord != null) {
                 report = new APRSNetControlEOCContactReport(latestRecord.callsign(), latestRecord.director_name(), latestRecord.incident_commander_name(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public List<APRSNetControlEOCContactReport> getAllEOCContactInformation(User loggedInUser, String callsign) {
        List<APRSNetControlEOCContactReport> reports = new ArrayList<>();
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCContactReportRecord> records = eocContactReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (EOCContactReportRecord rec : records) {
                    reports.add(new APRSNetControlEOCContactReport(rec.callsign(), rec.director_name(), rec.incident_commander_name(), rec.reported_date()));
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return reports;
   }

	public void deleteAllEOCMobilizationInformation(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCMobilizationReportRecord> records = eocMobilizationReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (EOCMobilizationReportRecord rec : records) {
                    eocMobilizationReportRepository.delete(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught deleting reports", e);
        }
	}

    public void deleteAllEOCContactInformation(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCContactReportRecord> records = eocContactReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (EOCContactReportRecord rec : records) {
                    eocContactReportRepository.delete(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught deleting reports", e);
        }
    }

    public void deleteAllShelterCensusInformation(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterCensusReportRecord> records = shelterCensusReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterCensusReportRecord rec : records) {
                    shelterCensusReportRepository.delete(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught deleting reports", e);
        }
    }

    public void deleteAllShelterOperationalFoodInformation(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterOperationalFoodReportRecord> records = shelterOperationalFoodReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterOperationalFoodReportRecord rec : records) {
                    shelterOperationalFoodReportRepository.delete(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught deleting reports", e);
        }
    }

    public void deleteAllShelterOperationalMaterielInformation(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterOperationalMaterielReportRecord> records = shelterOperationalMaterielReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterOperationalMaterielReportRecord rec : records) {
                    shelterOperationalMaterielReportRepository.delete(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught deleting reports", e);
        }
    }

    public void deleteAllShelterWorkerInformation(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterWorkerReportRecord> records = shelterWorkerReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterWorkerReportRecord rec : records) {
                    shelterWorkerReportRepository.delete(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught deleting reports", e);
        }
    }

    public void deleteAllShelterStatusInformation(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterStatusReportRecord> records = shelterStatusReportRepository.findAll();
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterStatusReportRecord rec : records) {
                    shelterStatusReportRepository.delete(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught deleting reports", e);
        }
    }


    public APRSNetControlShelterCensusReport addShelterCensusReport(User loggedInUser, APRSNetControlShelterCensusReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            ShelterCensusReportRecord rec = new ShelterCensusReportRecord(UUID.randomUUID().toString(), report.getObjectName(), report.getP03(), report.getP47(), report.getP812(),
                                                                                report.getP1318(), report.getP1965(), report.getP66(), ZonedDateTime.now());
            shelterCensusReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

    public APRSNetControlShelterStatusReport addShelterStatusReport(User loggedInUser, APRSNetControlShelterStatusReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            ShelterStatusReportRecord rec = new ShelterStatusReportRecord(UUID.randomUUID().toString(), report.getObjectName(), report.getState(), report.getStatus(), 
                                                            report.getMessage(), ZonedDateTime.now());
            shelterStatusReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

    public APRSNetControlShelterWorkerReport addShelterWorkerReport(User loggedInUser, APRSNetControlShelterWorkerReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            ShelterWorkerReportRecord rec = new ShelterWorkerReportRecord(UUID.randomUUID().toString(), report.getObjectName(), report.getShift(), report.getHealth(),
                                                                        report.getMental(), report.getSpiritual(), report.getCaseworker(), report.getFeeding(),
                                                                        report.getOther(), ZonedDateTime.now(), report.getDateReported());
            shelterWorkerReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

    public APRSNetControlShelterOperationalFoodReport addShelterOperationalFoodReport(User loggedInUser, APRSNetControlShelterOperationalFoodReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            ShelterOperationalFoodReportRecord rec = new ShelterOperationalFoodReportRecord(UUID.randomUUID().toString(), report.getObjectName(), report.getTimePeriod(), report.getBreakfast(),
                                                                            report.getLunch(), report.getDinner(), report.getSnack(), ZonedDateTime.now(), report.getDateReported());
            shelterOperationalFoodReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

    public APRSNetControlShelterOperationalMaterielReport addShelterOperationalMaterielReport(User loggedInUser, APRSNetControlShelterOperationalMaterielReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            ShelterOperationalMaterielReportRecord rec = new ShelterOperationalMaterielReportRecord(UUID.randomUUID().toString(), report.getObjectName(), report.getTimePeriod(),
                                                                report.getCots(), report.getBlankets(), report.getComfort(), report.getCleanup(), report.getSignage(), report.getOther(),
                                                                report.getDate(), report.getDateReported());
            shelterOperationalMaterielReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

    public APRSNetControlEOCMobilizationReport addEOCMobilizationReport(User loggedInUser, APRSNetControlEOCMobilizationReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            EOCMobilizationReportRecord rec = new EOCMobilizationReportRecord(UUID.randomUUID().toString(), report.getEocName(), report.getObjectName(), report.getStatus(), report.getLevel(),
                                                                                ZonedDateTime.now());
            eocMobilizationReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

    public APRSNetControlEOCContactReport addEOCContactReport(User loggedInUser, APRSNetControlEOCContactReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            EOCContactReportRecord rec = new EOCContactReportRecord(UUID.randomUUID().toString(), report.getObjectName(), report.getDirectorName(), report.getIncidentCommanderName(),
                                                                        ZonedDateTime.now());
            eocContactReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

}