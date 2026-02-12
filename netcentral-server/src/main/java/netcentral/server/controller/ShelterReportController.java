package netcentral.server.controller;

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

import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterCensusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalFoodReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalMaterielReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterStatusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterWorkerReport;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.accessor.ReportAccessor;
import netcentral.server.accessor.SummaryAccessor;
import netcentral.server.accessor.TransceiverCommunicationAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.enums.ObjectShelterReportingTimeframe;
import netcentral.server.object.ObjectShelterCensus;
import netcentral.server.object.ObjectShelterOperationalFood;
import netcentral.server.object.ObjectShelterOperationalMateriel;
import netcentral.server.object.ObjectShelterOperationalStatus;
import netcentral.server.object.ObjectShelterWorker;
import netcentral.server.object.User;
import netcentral.server.object.report.ObjectShelterCensusReport;
import netcentral.server.object.report.ObjectShelterConsolidatedReport;
import netcentral.server.object.report.ObjectShelterOperationalFoodReport;
import netcentral.server.object.report.ObjectShelterOperationalMaterielReport;
import netcentral.server.object.report.ObjectShelterStatusReport;
import netcentral.server.object.report.ObjectShelterWorkerReport;

@Controller("/api/v1/shelterReports") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class ShelterReportController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private SummaryAccessor summaryAccessor;
    @Inject
    private ReportAccessor reportAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;


    @Get("/{callsign}/consolidated")
    public ObjectShelterConsolidatedReport getShelterReport(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        ObjectShelterConsolidatedReport report = (ObjectShelterConsolidatedReport) summaryAccessor.getShelterInformation(loggedInUser, obj);

        return report;
    }

    @Get("/{callsign}/status")
    public ObjectShelterStatusReport getShelterStatusReport(HttpRequest<?> request, @PathVariable String callsign, @QueryValue String reportDate) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        ObjectShelterStatusReport report = (ObjectShelterStatusReport) summaryAccessor.getShelterInformation(loggedInUser, obj);

        return report;
    }


    @Get("/{callsign}/workers")
    public ObjectShelterWorkerReport getShelterWorkerReport(HttpRequest<?> request, @PathVariable String callsign, @QueryValue Integer shift, @QueryValue String reportDate) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralShelterWorkerReport report = reportAccessor.getShelterWorkerReport(loggedInUser, obj.getCallsignFrom(), shift, reportDate);
        if (report == null) {
            return null;
        }
        ObjectShelterWorkerReport ret = new ObjectShelterWorkerReport(report.getObjectName(), shift, report.getHealth(), 
                                                    report.getMental(), report.getSpiritual(), report.getCaseworker(), report.getFeeding(), report.getOther(), report.getDateReported());
        return ret;
    }

    @Get("/{callsign}/operationalFoods")
    public ObjectShelterOperationalFoodReport getShelterOperationalFoodReport(HttpRequest<?> request, @PathVariable String callsign, @QueryValue Integer timeframe, @QueryValue String reportDate) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralShelterOperationalFoodReport report = reportAccessor.getShelterOperationalFoodReport(loggedInUser, obj.getCallsignFrom(), ObjectShelterReportingTimeframe.values()[timeframe], reportDate);
        if (report == null) {
            return null;
        }
        ObjectShelterOperationalFoodReport ret = new ObjectShelterOperationalFoodReport(report.getObjectName(), ObjectShelterReportingTimeframe.values()[timeframe], 
                                                                                            report.getBreakfast(), report.getLunch(), report.getDinner(), report.getSnack(), report.getDateReported());

        return ret;
    }

    @Get("/{callsign}/operationalMateriels")
    public ObjectShelterOperationalMaterielReport getShelterOperationalMaterielReport(HttpRequest<?> request, @PathVariable String callsign, @QueryValue Integer timeframe, @QueryValue String reportDate) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralShelterOperationalMaterielReport report = reportAccessor.getShelterOperationalMaterielReport(loggedInUser, obj.getCallsignFrom(), ObjectShelterReportingTimeframe.values()[timeframe], reportDate);
        if (report == null) {
            return null;
        }
        ObjectShelterOperationalMaterielReport ret = new ObjectShelterOperationalMaterielReport(report.getObjectName(), ObjectShelterReportingTimeframe.values()[timeframe], report.getCots(), report.getBlankets(), 
                                                                     report.getComfort(), report.getCleanup(), report.getSignage(), report.getOther(), report.getDateReported());

        return ret;
    }

    @Get("/{callsign}/census")
    public ObjectShelterCensusReport getShelterCensusReport(HttpRequest<?> request, @PathVariable String callsign, @QueryValue String reportDate) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralShelterCensusReport report = reportAccessor.getShelterCensusReport(loggedInUser, obj.getCallsignFrom(),reportDate);
        if (report == null) {
            return null;
        }
        ObjectShelterCensusReport ret = new ObjectShelterCensusReport(report.getObjectName(), report.getP03(), report.getP47(), report.getP812(), report.getP1318(), report.getP1965(), report.getP66(), report.getDateReported());

        return ret;
    }

    @Get("/{callsign}/census/all")
    public List<ObjectShelterCensusReport> getAllShelterCensusReport(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        List<ObjectShelterCensusReport> ret = new ArrayList<>();

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        List<APRSNetCentralShelterCensusReport> reports = reportAccessor.getAllLatestShelterCensusReport(loggedInUser, obj.getCallsignFrom());
        if (reports != null) {
            for (APRSNetCentralShelterCensusReport report : reports) {
                ret.add(new ObjectShelterCensusReport(report.getObjectName(), report.getP03(), report.getP47(), report.getP812(), report.getP1318(), report.getP1965(), report.getP66(), report.getDateReported()));
            }
        }
        return ret;
    }

    @Delete("/{callsign}")
    public void delete(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        reportAccessor.deleteAllShelterCensusInformation(loggedInUser, obj.getCallsignFrom());
        reportAccessor.deleteAllShelterOperationalFoodInformation(loggedInUser, obj.getCallsignFrom());
        reportAccessor.deleteAllShelterOperationalMaterielInformation(loggedInUser, obj.getCallsignFrom());
        reportAccessor.deleteAllShelterWorkerInformation(loggedInUser, obj.getCallsignFrom());
        reportAccessor.deleteAllShelterStatusInformation(loggedInUser, obj.getCallsignFrom());
        return;
    }

    @Post("/{callsign}/status")
    public ObjectShelterOperationalStatus createShelterStatusReport(HttpRequest<?> request, @PathVariable String callsign, @Body ObjectShelterOperationalStatus report) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralShelterStatusReport aprsReport = new APRSNetCentralShelterStatusReport(obj.getCallsignFrom(), report.getStatus().ordinal(), report.getState().ordinal(), report.getMessage(), ZonedDateTime.now());
        aprsReport = reportAccessor.addShelterStatusReport(loggedInUser, aprsReport);
        if (aprsReport != null) {
            // send message
            transceiverMessageAccessor.sendReport(loggedInUser, aprsReport);

            return report;
        }

        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not updated");
    }

    @Post("/{callsign}/census")
    public ObjectShelterCensus createShelterCensusReport(HttpRequest<?> request, @PathVariable String callsign, @Body ObjectShelterCensus report) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralShelterCensusReport aprsReport = new APRSNetCentralShelterCensusReport(obj.getCallsignFrom(), report.getPopulation03(), report.getPopulation47(), report.getPopulation812(),
                                                                report.getPopulation1318(), report.getPopulation1965(), report.getPopulation66(), report.getReportTime());
        aprsReport = reportAccessor.addShelterCensusReport(loggedInUser, aprsReport);
        if (aprsReport != null) {
            // send message
            transceiverMessageAccessor.sendReport(loggedInUser, aprsReport);

            return report;
        }

        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not updated");
    }

    @Post("/{callsign}/workers")
    public ObjectShelterWorker createShelterWorkersReport(HttpRequest<?> request, @PathVariable String callsign, @Body ObjectShelterWorker report) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralShelterWorkerReport aprsReport = new APRSNetCentralShelterWorkerReport(obj.getCallsignFrom(), report.getShift(), report.getHealth(), report.getMental(), report.getSpiritual(),
                                                                        report.getCaseworker(), report.getFeeding(), report.getOther(), report.getReportTime());
        aprsReport = reportAccessor.addShelterWorkerReport(loggedInUser, aprsReport);
        if (aprsReport != null) {
            // send message
            transceiverMessageAccessor.sendReport(loggedInUser, aprsReport);

            return report;
        }

        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not updated");
    }

    @Post("/{callsign}/operationalFoods")
    public ObjectShelterOperationalFood createShelterOperationalFoodsReport(HttpRequest<?> request, @PathVariable String callsign, @Body ObjectShelterOperationalFood report) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralShelterOperationalFoodReport aprsReport = new APRSNetCentralShelterOperationalFoodReport(obj.getCallsignFrom(), report.getTimeframe().ordinal(), report.getBreakfast(), report.getLunch(), report.getDinner(),
                                                                                report.getSnack(),
                                                                                report.getReportTime(), ZonedDateTime.now());
        aprsReport = reportAccessor.addShelterOperationalFoodReport(loggedInUser, aprsReport);
        if (aprsReport != null) {
            // send message
            transceiverMessageAccessor.sendReport(loggedInUser, aprsReport);

            return report;
        }

        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not updated");
    }

    @Post("/{callsign}/operationalMateriels")
    public ObjectShelterOperationalMateriel createShelterOperationalMaterieldsReport(HttpRequest<?> request, @PathVariable String callsign, @Body ObjectShelterOperationalMateriel report) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralShelterOperationalMaterielReport aprsReport = new APRSNetCentralShelterOperationalMaterielReport(obj.getCallsignFrom(), report.getTimeframe().ordinal(), report.getCots(),
                                                                                report.getBlankets(), report.getComfort(), report.getCleanup(), report.getSignage(), report.getOther(),
                                                                                report.getReportTime(), ZonedDateTime.now());
        aprsReport = reportAccessor.addShelterOperationalMaterielReport(loggedInUser, aprsReport);
        if (aprsReport != null) {
            // send message
            transceiverMessageAccessor.sendReport(loggedInUser, aprsReport);

            return report;
        }

        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not updated");
    }
}
