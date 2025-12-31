package netcentral.server.accessor;

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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterCensusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalFoodReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalMaterielReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterStatusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterWorkerReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCContactReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCMobilizationReport;

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


    public APRSNetCentralShelterCensusReport getLatestShelterCensusReport(User loggedInUser, String callsign) {
        APRSNetCentralShelterCensusReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterCensusReportRecord> records = shelterCensusReportRepository.findBycallsign(callsign);
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
                 report = new APRSNetCentralShelterCensusReport(latestRecord.callsign(), latestRecord.p03(), latestRecord.p47(), latestRecord.p812(), latestRecord.p1318(), latestRecord.p1965(), latestRecord.p66(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }


    public List<APRSNetCentralShelterCensusReport> getAllLatestShelterCensusReport(User loggedInUser, String callsign) {
        List<APRSNetCentralShelterCensusReport> reports = new ArrayList<>();

        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterCensusReportRecord> records = shelterCensusReportRepository.findBycallsign(callsign);
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterCensusReportRecord rec : records) {
                    reports.add(new APRSNetCentralShelterCensusReport(rec.callsign(), rec.p03(), rec.p47(), rec.p812(), rec.p1318(), rec.p1965(), rec.p66(), rec.reported_date()));
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught getting all reports", e);
        }
        return reports;
    }

    public APRSNetCentralShelterStatusReport getLatestShelterStatusReport(User loggedInUser, String callsign) {
        APRSNetCentralShelterStatusReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterStatusReportRecord> records = shelterStatusReportRepository.findBycallsign(callsign);
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
                 report = new APRSNetCentralShelterStatusReport(latestRecord.callsign(), latestRecord.status(), latestRecord.state(), latestRecord.message(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public APRSNetCentralShelterWorkerReport getLatestShelterWorkerReport(User loggedInUser, String callsign, int shift) {
        APRSNetCentralShelterWorkerReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterWorkerReportRecord> records = shelterWorkerReportRepository.findBycallsign(callsign);
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
                 report = new APRSNetCentralShelterWorkerReport(latestRecord.callsign(), latestRecord.shift(), latestRecord.health(), latestRecord.mental(), latestRecord.spiritual(), 
                                                                latestRecord.caseworker(), latestRecord.feeding(), latestRecord.other(), latestRecord.date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public APRSNetCentralShelterOperationalFoodReport getLatestShelterOperationalFoodReport(User loggedInUser, String callsign, ObjectShelterReportingTimeframe timeframe) {
        APRSNetCentralShelterOperationalFoodReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterOperationalFoodReportRecord> records = shelterOperationalFoodReportRepository.findBycallsign(callsign);
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
                 report = new APRSNetCentralShelterOperationalFoodReport(latestRecord.callsign(), latestRecord.timeframe(), latestRecord.breakfast(), latestRecord.lunch(), latestRecord.dinner(), latestRecord.snack(), latestRecord.date(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public APRSNetCentralShelterOperationalMaterielReport getLatestShelterOperationalMaterielReport(User loggedInUser, String callsign, ObjectShelterReportingTimeframe timeframe) {
        APRSNetCentralShelterOperationalMaterielReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<ShelterOperationalMaterielReportRecord> records = shelterOperationalMaterielReportRepository.findBycallsign(callsign);
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
                 report = new APRSNetCentralShelterOperationalMaterielReport(latestRecord.callsign(), latestRecord.timeframe(), latestRecord.cots(), latestRecord.blankets(), 
                                        latestRecord.comfort(), latestRecord.cleanup(), latestRecord.signage(), latestRecord.other(), latestRecord.date(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public APRSNetCentralEOCMobilizationReport getEOCMobilizationInformation(User loggedInUser, String callsign) {
        APRSNetCentralEOCMobilizationReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCMobilizationReportRecord> records = eocMobilizationReportRepository.findBycallsign(callsign);
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
                 report = new APRSNetCentralEOCMobilizationReport(latestRecord.callsign(), latestRecord.eoc_name(), latestRecord.status(), latestRecord.level(), latestRecord.reported_date());
            } else {
                // default report 
                 report = new APRSNetCentralEOCMobilizationReport(callsign, "", 0, 0, ZonedDateTime.now());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public List<APRSNetCentralEOCMobilizationReport> getAllEOCMobilizationInformation(User loggedInUser, String callsign) {
        List<APRSNetCentralEOCMobilizationReport> reports = new ArrayList<>();
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCMobilizationReportRecord> records = eocMobilizationReportRepository.findBycallsign(callsign);
            
            if ((records != null) && (!records.isEmpty())) {
                for (EOCMobilizationReportRecord rec : records) {
                    reports.add(new APRSNetCentralEOCMobilizationReport(rec.callsign(), rec.eoc_name(), rec.status(), rec.level(), rec.reported_date()));
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return reports;
   }

    public APRSNetCentralEOCContactReport getEOCContactInformation(User loggedInUser, String callsign) {
        APRSNetCentralEOCContactReport report = null;
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCContactReportRecord> records = eocContactReportRepository.findBycallsign(callsign);
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
                 report = new APRSNetCentralEOCContactReport(latestRecord.callsign(), latestRecord.director_name(), latestRecord.incident_commander_name(), latestRecord.reported_date());
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return report;
    }

    public List<APRSNetCentralEOCContactReport> getAllEOCContactInformation(User loggedInUser, String callsign) {
        List<APRSNetCentralEOCContactReport> reports = new ArrayList<>();
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCContactReportRecord> records = eocContactReportRepository.findBycallsign(callsign);
            
            if ((records != null) && (!records.isEmpty())) {
                for (EOCContactReportRecord rec : records) {
                    reports.add(new APRSNetCentralEOCContactReport(rec.callsign(), rec.director_name(), rec.incident_commander_name(), rec.reported_date()));
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest report", e);
        }
        return reports;
   }

   public void deleteAllData(User loggedInUser) {
        deleteAllEOCInformation(loggedInUser);
        deleteAllShelterInformation(loggedInUser);
   }

	public void deleteAllData(User loggedInUser, ZonedDateTime before) {
        deleteAllEOCInformation(loggedInUser, before);
        deleteAllShelterInformation(loggedInUser, before);
    }

    private void deleteAllEOCInformation(User loggedInUser, ZonedDateTime before) {
        try {
            eocContactReportRepository.deleteByReported_date(before);
            eocMobilizationReportRepository.deleteByReported_date(before);
        } catch (Exception e) {
            logger.error("Exception caught deleting EOC reports", e);
        }
    }

    private void deleteAllShelterInformation(User loggedInUser, ZonedDateTime before) {
        try {
            shelterCensusReportRepository.deleteByReported_date(before);
            shelterOperationalFoodReportRepository.deleteByReported_date(before);
            shelterOperationalMaterielReportRepository.deleteByReported_date(before);
            shelterStatusReportRepository.deleteByReported_date(before);
            shelterWorkerReportRepository.deleteByReported_date(before);
        } catch (Exception e) {
            logger.error("Exception caught deleting shelter reports", e);
        }
    }

    private void deleteAllShelterInformation(User loggedInUser) {
        try {
            shelterCensusReportRepository.deleteAll();
            shelterOperationalFoodReportRepository.deleteAll();
            shelterOperationalMaterielReportRepository.deleteAll();
            shelterStatusReportRepository.deleteAll();
            shelterWorkerReportRepository.deleteAll();
        } catch (Exception e) {
            logger.error("Exception caught deleting all Shelter reports", e);
        }
	}

    private void deleteAllEOCInformation(User loggedInUser) {
        try {
            eocContactReportRepository.deleteAll();
            eocMobilizationReportRepository.deleteAll();
        } catch (Exception e) {
            logger.error("Exception caught deleting all EOC reports", e);
        }
	}

    public void deleteAllEOCMobilizationInformation(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("callsign id not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }

        try {
            List<EOCMobilizationReportRecord> records = eocMobilizationReportRepository.findBycallsign(callsign);
            
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
            List<EOCContactReportRecord> records = eocContactReportRepository.findBycallsign(callsign);
            
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
            List<ShelterCensusReportRecord> records = shelterCensusReportRepository.findBycallsign(callsign);
            
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
            List<ShelterOperationalFoodReportRecord> records = shelterOperationalFoodReportRepository.findBycallsign(callsign);
            
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
            List<ShelterOperationalMaterielReportRecord> records = shelterOperationalMaterielReportRepository.findBycallsign(callsign);
            
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
            List<ShelterWorkerReportRecord> records = shelterWorkerReportRepository.findBycallsign(callsign);
            
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
            List<ShelterStatusReportRecord> records = shelterStatusReportRepository.findBycallsign(callsign);
            
            if ((records != null) && (!records.isEmpty())) {
                for (ShelterStatusReportRecord rec : records) {
                    shelterStatusReportRepository.delete(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught deleting reports", e);
        }
    }


    public APRSNetCentralShelterCensusReport addShelterCensusReport(User loggedInUser, APRSNetCentralShelterCensusReport report) {
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

    public APRSNetCentralShelterStatusReport addShelterStatusReport(User loggedInUser, APRSNetCentralShelterStatusReport report) {
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

    public APRSNetCentralShelterWorkerReport addShelterWorkerReport(User loggedInUser, APRSNetCentralShelterWorkerReport report) {
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

    public APRSNetCentralShelterOperationalFoodReport addShelterOperationalFoodReport(User loggedInUser, APRSNetCentralShelterOperationalFoodReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            ShelterOperationalFoodReportRecord rec = new ShelterOperationalFoodReportRecord(UUID.randomUUID().toString(), report.getObjectName(), report.getInfoType(), report.getBreakfast(),
                                                                            report.getLunch(), report.getDinner(), report.getSnack(), ZonedDateTime.now(), report.getDateReported());
            shelterOperationalFoodReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

    public APRSNetCentralShelterOperationalMaterielReport addShelterOperationalMaterielReport(User loggedInUser, APRSNetCentralShelterOperationalMaterielReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            ShelterOperationalMaterielReportRecord rec = new ShelterOperationalMaterielReportRecord(UUID.randomUUID().toString(), report.getObjectName(), report.getInfoType(),
                                                                report.getCots(), report.getBlankets(), report.getComfort(), report.getCleanup(), report.getSignage(), report.getOther(),
                                                                report.getDate(), report.getDateReported());
            shelterOperationalMaterielReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

    public APRSNetCentralEOCMobilizationReport addEOCMobilizationReport(User loggedInUser, APRSNetCentralEOCMobilizationReport report) {
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

    public APRSNetCentralEOCContactReport addEOCContactReport(User loggedInUser, APRSNetCentralEOCContactReport report) {
        if (report == null) {
            logger.debug("Report not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not provided");
        }

        try {
            EOCContactReportRecord rec = new EOCContactReportRecord(UUID.randomUUID().toString(), report.getObjectName(), report.getDirectorName(), report.getIncidentCommanderName(),
                                                                        report.getLastReportedTime());
            eocContactReportRepository.save(rec);
            return report;
        } catch (Exception e) {
            logger.error("Exception caught creating report", e);
        }
    
        return null;
    }

}