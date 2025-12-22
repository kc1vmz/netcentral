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

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCContactReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCMobilizationReport;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.accessor.ReportAccessor;
import netcentral.server.accessor.TransceiverCommunicationAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.enums.ObjectEOCStatus;
import netcentral.server.object.ObjectEOCContact;
import netcentral.server.object.report.ObjectEOCContactReport;
import netcentral.server.object.ObjectEOCMobilization;
import netcentral.server.object.report.ObjectEOCMobilizationReport;
import netcentral.server.object.User;

@Controller("/api/v1/eocReports") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class EOCReportController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private ReportAccessor reportAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    

    @Delete("/{callsign}")
    public void delete(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        reportAccessor.deleteAllEOCMobilizationInformation(loggedInUser, obj.getCallsignFrom());
        reportAccessor.deleteAllEOCContactInformation(loggedInUser, obj.getCallsignFrom());
        return;
    }

    @Get("/{callsign}/mobilizations/latest")
    public ObjectEOCMobilizationReport getEOCMobilizationReport(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralEOCMobilizationReport report =  reportAccessor.getEOCMobilizationInformation(loggedInUser, obj.getCallsignFrom());

        ObjectEOCMobilizationReport ret = new ObjectEOCMobilizationReport(report.getObjectName(), report.getEocName(), obj.getComment(), ObjectType.EOC, 
                                                            obj.getLat(), obj.getLon(), obj.isAlive(), 
                                                            ObjectEOCStatus.values()[report.getStatus()],
                                                            report.getLevel(), report.getLastReportedTime());

        return ret;
    }

    @Get("/{callsign}/mobilizations")
    public List<ObjectEOCMobilizationReport> getEOCMobilizationReports(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        List<APRSNetCentralEOCMobilizationReport> reports =  reportAccessor.getAllEOCMobilizationInformation(loggedInUser, obj.getCallsignFrom());

        List<ObjectEOCMobilizationReport> ret = new ArrayList<>();
        if (reports != null) {
            for (APRSNetCentralEOCMobilizationReport report : reports) {
                ret.add(new ObjectEOCMobilizationReport(report.getObjectName(), report.getEocName(), obj.getComment(), ObjectType.EOC, 
                                                            obj.getLat(), obj.getLon(), obj.isAlive(), 
                                                            ObjectEOCStatus.values()[report.getStatus()],
                                                            report.getLevel(), report.getLastReportedTime()));
            }
        }

        return ret;
    }

    @Get("/{callsign}/contacts/latest")
    public ObjectEOCContactReport getEOCContactReport(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralEOCContactReport report =  reportAccessor.getEOCContactInformation(loggedInUser, obj.getCallsignFrom());

        ObjectEOCContactReport ret = new ObjectEOCContactReport(report.getObjectName(), obj.getComment(), ObjectType.EOC, 
                                                            obj.getLat(), obj.getLon(), obj.isAlive(), 
                                                            report.getDirectorName(),
                                                            report.getIncidentCommanderName(), report.getLastReportedTime());

        return ret;
    }

    @Get("/{callsign}/contacts")
    public List<ObjectEOCContactReport> getEOCContactReports(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        List<APRSNetCentralEOCContactReport> reports =  reportAccessor.getAllEOCContactInformation(loggedInUser, obj.getCallsignFrom());

        List<ObjectEOCContactReport> ret = new ArrayList<>();
        if (reports != null) {
            for (APRSNetCentralEOCContactReport report : reports) {
                ret.add(new ObjectEOCContactReport(report.getObjectName(), obj.getComment(), ObjectType.EOC, 
                                                            obj.getLat(), obj.getLon(), obj.isAlive(), 
                                                            report.getDirectorName(),
                                                            report.getIncidentCommanderName(), report.getLastReportedTime()));
            }
        }

        return ret;
    }

    @Post("/{callsign}/contacts")
    public ObjectEOCContact createEOCContactReport(HttpRequest<?> request, @PathVariable String callsign, @Body ObjectEOCContact report) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralEOCContactReport aprsReport = new APRSNetCentralEOCContactReport(obj.getCallsignFrom(), report.getDirectorName(), report.getIncidentCommanderName(), ZonedDateTime.now());
        aprsReport = reportAccessor.addEOCContactReport(loggedInUser, aprsReport);
        if (aprsReport != null) {
            // send message
            transceiverMessageAccessor.sendReport(loggedInUser, aprsReport);

            return report;
        }


        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not updated");
    }

    @Post("/{callsign}/mobilizations")
    public ObjectEOCMobilization createEOCMobilizationReport(HttpRequest<?> request, @PathVariable String callsign, @Body ObjectEOCMobilization report) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject obj = aprsObjectAccessor.getObjectByCallsign(loggedInUser, callsign);

        APRSNetCentralEOCMobilizationReport aprsReport = new APRSNetCentralEOCMobilizationReport(obj.getCallsignFrom(), report.getEocName(), report.getStatus().ordinal(), report.getLevel(), ZonedDateTime.now());
        aprsReport = reportAccessor.addEOCMobilizationReport(loggedInUser, aprsReport);
        if (aprsReport != null) {
            // send message
            transceiverMessageAccessor.sendReport(loggedInUser, aprsReport);

            return report;
        }

        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Report not updated");
    }

}
