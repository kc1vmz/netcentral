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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.kc1vmz.netcentral.aprsobject.common.NetCentralServerStatistics;
import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;
import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.APRSStatus;
import com.kc1vmz.netcentral.aprsobject.object.APRSWeatherReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterCensusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalFoodReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalMaterielReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterWorkerReport;

import io.micronaut.views.fields.FormGenerator;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.accessor.ActivityAccessor;
import netcentral.server.accessor.CallsignAccessor;
import netcentral.server.accessor.CompletedNetAccessor;
import netcentral.server.accessor.CompletedParticipantAccessor;
import netcentral.server.accessor.NetAccessor;
import netcentral.server.accessor.NetParticipantAccessor;
import netcentral.server.accessor.ParticipantAccessor;
import netcentral.server.accessor.RegisteredTransceiverAccessor;
import netcentral.server.accessor.ReportAccessor;
import netcentral.server.accessor.ScheduledNetAccessor;
import netcentral.server.accessor.StatisticsAccessor;
import netcentral.server.accessor.TrackedStationAccessor;
import netcentral.server.accessor.UIAccessor;
import netcentral.server.accessor.UserAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.auth.SessionTrackedObjectAccessor;
import netcentral.server.config.NetConfigServerConfig;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.ObjectShelterReportingTimeframe;
import netcentral.server.enums.RadioStyle;
import netcentral.server.enums.ScheduledNetType;
import netcentral.server.object.Activity;
import netcentral.server.object.Callsign;
import netcentral.server.object.CompletedNet;
import netcentral.server.object.CompletedParticipant;
import netcentral.server.object.request.LoginRequest;
import netcentral.server.object.Net;
import netcentral.server.object.Participant;
import netcentral.server.object.ScheduledNet;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;
import netcentral.server.object.UserEditRequest;
import netcentral.server.object.UsernameEditRequest;
import netcentral.server.object.request.BlankRequest;
import netcentral.server.object.request.CallsignEditRequest;
import netcentral.server.object.request.NetCreateRequest;
import netcentral.server.object.request.NetMessageRequest;
import netcentral.server.object.request.ObjectCreateRequest;
import netcentral.server.object.request.PasswordEditRequest;
import netcentral.server.object.request.RegisterRequest;
import netcentral.server.object.request.ScheduledNetCreateRequest;
import netcentral.server.object.request.ScheduledNetCreateRequestExternal;
import netcentral.server.object.request.TrackedStationEditRequest;
import netcentral.server.utils.MapMarkerFactory;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS) 
public class UIController {
    @Inject
    private UIAccessor uiAccessor;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor; 
    @Inject
    private NetAccessor netAccessor;
    @Inject
    private ParticipantAccessor participantAccessor;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private CompletedNetAccessor completedNetAccessor;
    @Inject
    private ActivityAccessor activityAccessor;
    @Inject
    private CallsignAccessor callsignAccessor;
    @Inject
    private UserAccessor userAccessor;
    @Inject
    private SessionAccessor sessionAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private CompletedParticipantAccessor completedParticipantAccessor;
    @Inject
    private NetConfigServerConfig netConfigServerConfig;
    @Inject
    private ReportAccessor reportAccessor;
    @Inject
    private ScheduledNetAccessor scheduledNetAccessor;
    @Inject
    private SessionTrackedObjectAccessor sessionTrackedObjectAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;

    private FormGenerator formGenerator;

    private String getFriendlyName(User user) {
        String friendlyName = null;
        if (user != null) {
            User temp = userAccessor.get(user, user.getId());
            if (temp.getCallsign() != null) {
                friendlyName = temp.getCallsign().getCallsign();
            } else {
                friendlyName = temp.getEmailAddress();
            }
        }
        return friendlyName;
    }

    public UIController(FormGenerator formGenerator) {
        this.formGenerator = formGenerator;
    }

    @View("archives")
    @Get("/archives")
    public HttpResponse<?> archives(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<CompletedNet> nets = completedNetAccessor.getAll(loggedInUser, null);
        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, 
                                                        "netsCount", (nets != null) ? nets.size() : 0,
                                                        "user", loggedInUser, "nets", nets));
    }

    @View("home")
    @Get("/")
    public HttpResponse<?> home(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        boolean trackOnly = sessionTrackedObjectAccessor.isTrackingOnly(token);

        List<Net> nets = netAccessor.getAll(loggedInUser, null);
        List<Participant> participants = participantAccessor.getAll(loggedInUser, null);
        List<TrackedStation> trackedStations = trackedStationAccessor.getAll(loggedInUser, null, null, null);

        int netCount = (nets == null) ? 0 : nets.size();
        int participantCount = (participants == null) ? 0 : participants.size();
        int trackedStationCount = (trackedStations == null) ? 0 : trackedStations.size();

        int trackedOnly = 0;
        if (trackedStations != null) {
            for (TrackedStation trackedStation : trackedStations) {
                if (trackedStation.isTrackingActive()) {
                    trackedOnly++;
                }
            }
        }

        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser),"token", token, "user", loggedInUser, "netCount", netCount, 
                                                        "tracking", ((trackOnly) ? "yes" : null),
                                                        "participantCount", participantCount, "trackedStationCount", trackedStationCount, "trackedOnly", trackedOnly));
    }


    @SuppressWarnings("unchecked")
    @View("home-track")
    @Get("/home-track")
    public Map<String, Object> displayOnlyTrackedStations(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "form", formGenerator.generate("/home-track-action", BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/home-track-action")
    HttpResponse<?> displayOnlyTrackedStationsAction(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);

        sessionTrackedObjectAccessor.add(token, true);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/home").build());
    } 

    @SuppressWarnings("unchecked")
    @View("home-untrack")
    @Get("/home-untrack")
    public Map<String, Object> displayAllStations(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "form", formGenerator.generate("/home-untrack-action", BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/home-untrack-action")
    HttpResponse<?> displayAllStationsAction(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
 
        sessionTrackedObjectAccessor.add(token, false);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/home").build());
    } 

    @View("activities")
    @Get("/activities")
    public HttpResponse<?> activities(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<Activity> activities = activityAccessor.getAll(loggedInUser);
        if (activities != null) {
            for (Activity activity: activities) {
                if (activity.getUser() != null) {
                    try {
                        User user = userAccessor.get(loggedInUser, activity.getUser().getId());
                        if (user != null) {
                            activity.setUser(user);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }

        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, 
                                                                "activitiesCount", (activities != null) ? activities.size() : 0,
                                                                "user", loggedInUser, "activities", activities));
    }

    @View("nets")
    @Get("/nets")
    public HttpResponse<?> nets(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<Net> nets = netAccessor.getAll(loggedInUser, null);
        if ((nets != null) && (!nets.isEmpty())) {
            for (Net net : nets) {
                List<Participant> participants = netParticipantAccessor.getAllParticipants(loggedInUser, net);
                if ((participants != null) && (!participants.isEmpty())) {
                    net.setParticipantCount(participants.size());
                } else {
                    net.setParticipantCount(0);
                }
            }
        }
        List<ScheduledNet> scheduledNets = scheduledNetAccessor.getAll(loggedInUser, null);
        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "nets", nets, "netsCount", (nets != null) ? nets.size() : 0,
                                    "scheduledNets", scheduledNets, "scheduledNetsCount", (scheduledNets != null) ? scheduledNets.size() : 0));
    }

    @View("net")
    @Get("/net/{callsign}")
    public HttpResponse<?> net(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        List<Participant> participants = new ArrayList<>();
        List<APRSMessage> messages = new ArrayList<>();

        Net net = netAccessor.getByCallsign(loggedInUser, callsign);
        try {
            messages = aprsObjectAccessor.getCompletedNetMessages(loggedInUser, net.getCompletedNetId());
        } catch (Exception e) {
            // ignore
        }

        try {
            participants = netParticipantAccessor.getAllParticipants(loggedInUser, net); // was List<Participant>  netParticipants
        } catch (Exception e) {
            // ignore
        }

        MapMarkerFactory mapMarkerFactory = new MapMarkerFactory();
        mapMarkerFactory.processParticipants(participants, "|");

        if (mapMarkerFactory.getCount() > 0) {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                                /* map */
                                                "itemCount", mapMarkerFactory.getCount(),
                                                "latitude", mapMarkerFactory.getCenterLatitude(),
                                                "longitude", mapMarkerFactory.getCenterLongitude(),
                                                "lonString", mapMarkerFactory.getLongitudes(),
                                                "latString", mapMarkerFactory.getLatitudes(),
                                                "labelString", mapMarkerFactory.getDisplayInfos(),
                                                "titleString", mapMarkerFactory.getTitles(),
                                                "minLongitude", mapMarkerFactory.getMinLongitude(),
                                                "minLatitude", mapMarkerFactory.getMinLatitude(),
                                                "maxLongitude", mapMarkerFactory.getMaxLongitude(),
                                                "maxLatitude", mapMarkerFactory.getMaxLatitude(),
                                                "net", net, "messages", messages, 
                                                "itemSeperator", '|',
                                                "showMap", "yes",
                                                "participantsCount", (participants != null) ? participants.size() : 0,
                                                "messagesCount", (messages != null) ? messages.size() : 0,
                                                "participants", participants));
        } else {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                            "net", net, "messages", messages, 
                                            "participantsCount", (participants != null) ? participants.size() : 0,
                                            "messagesCount", (messages != null) ? messages.size() : 0,
                                            "participants", participants));
        }
    }

    @SuppressWarnings("unchecked")
    @View("nets-create")
    @Get("/nets-create")
    public Map<String, Object> createNet(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "form", formGenerator.generate("/nets-create-action", NetCreateRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/nets-create-action")
    HttpResponse<?> createNetAction(HttpRequest<?> request, @Valid @Body NetCreateRequest messageRequest) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        boolean remind = false;
        if (messageRequest.checkinReminder() != null) {
            remind = true;
        }
        boolean announce = false;
        if (messageRequest.announce() != null) {
            announce = true;
        }

        Net net = new Net(messageRequest.callsign(), messageRequest.name(), messageRequest.description(), messageRequest.voiceFrequency(), 
                                null, null, messageRequest.lat(), messageRequest.lon(), announce,
                                getUserName(loggedInUser), remind, messageRequest.checkinMessage(), messageRequest.open(), messageRequest.participantInviteAllowed(), false);
        netAccessor.create(loggedInUser, net);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/nets").build());
    } 

    private String getUserName(User loggedInUser) {
        User user = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((user.getFirstName() != null) && (user.getLastName() != null)) {
            return user.getFirstName()+" "+user.getLastName();
        }
        return user.getEmailAddress();
    }

    @SuppressWarnings("unchecked")
    @View("net-edit")
    @Get("/net-edit/{callsign}")
    public Map<String, Object> editNet(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.getByCallsign(loggedInUser, callsign);

        String callsignOriginal = net.getCallsign();
        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "net", net, "callsignOriginal", callsignOriginal, "form", formGenerator.generate("/net-edit-action/"+callsign, NetCreateRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/net-edit-action/{callsign}")
    HttpResponse<?> editNetAction(HttpRequest<?> request, @Valid @Body NetCreateRequest messageRequest,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.getByCallsign(loggedInUser, callsign);
        net.setCallsign(messageRequest.callsign());
        net.setName(messageRequest.name());
        net.setDescription(messageRequest.description());
        net.setVoiceFrequency(messageRequest.voiceFrequency());
        net.setLat(messageRequest.lat());
        net.setLon(messageRequest.lon());

        netAccessor.update(loggedInUser, callsign, net);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/nets").build());
    } 

    @SuppressWarnings("unchecked")
    @View("net-message")
    @Get("/net-message/{callsign}")
    public Map<String, Object> messageNet(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                            "net", net,
                                            "form", formGenerator.generate("/net-message-action/"+callsign, NetMessageRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/net-message-action/{callsign}")
    HttpResponse<?> messageNetAction(HttpRequest<?> request, @Valid @Body NetMessageRequest messageRequest,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.getByCallsign(loggedInUser, callsign);

        uiAccessor.sendNetMessage(loggedInUser, net, messageRequest.message());
        return HttpResponse.seeOther(UriBuilder.of("/").path("/nets").build());
    } 

    @SuppressWarnings("unchecked")
    @View("net-end")
    @Get("/net-end/{callsign}")
    public Map<String, Object> endNet(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        netAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "callsign", callsign, "form", formGenerator.generate("/net-end-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/net-end-action/{callsign}")
    HttpResponse<?> endNetAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        uiAccessor.endNet(loggedInUser, callsign);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/nets").build());
    } 




    @View("schedulednet")
    @Get("/schedulednet/{callsign}")
    public HttpResponse<?> schedulednet(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNet scheduledNet = scheduledNetAccessor.get(loggedInUser, callsign);
        String timeStartStr = null;
        if (scheduledNet.getType().equals(ScheduledNetType.ONE_TIME_ONLY)) {
            timeStartStr = String.format("%s-%s-%s %s:%s", scheduledNet.getNextStartTime().getYear(),
                scheduledNet.getNextStartTime().getMonthValue(),
                scheduledNet.getNextStartTime().getDayOfMonth(),
                scheduledNet.getNextStartTime().getHour(),
                scheduledNet.getNextStartTime().getMinute());
        } else {
            timeStartStr = String.format("%02d:%02d", (scheduledNet.getTimeStart() / 100), (scheduledNet.getTimeStart() - ((scheduledNet.getTimeStart() / 100)*100)));
        }

        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                        "scheduledNet", scheduledNet, "timeStartStr", timeStartStr));
    }

    @SuppressWarnings("unchecked")
    @View("schedulednet-create")
    @Get("/schedulednet-create")
    public Map<String, Object> createScheduledNet(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                            "form", formGenerator.generate("/schedulednet-create-action", ScheduledNetCreateRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/schedulednet-create-action")
    HttpResponse<?> createScheduledNetAction(HttpRequest<?> request, @Valid @Body ScheduledNetCreateRequest messageRequest) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNetCreateRequestExternal extRequest = new ScheduledNetCreateRequestExternal(messageRequest, getUserName(loggedInUser));
        ScheduledNet scheduledNet = extRequest.getScheduledNet();

        scheduledNetAccessor.create(loggedInUser, scheduledNet);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/nets").build());
    } 

    private ZonedDateTime convertTimeStartStringToZDT(String timeStartStr) {
        // form YYYY-MM-DD HH:MM
        String [] dtValues = timeStartStr.split(" ");
        if ((dtValues != null) && (dtValues.length == 2)) {
            String [] ymdValues = dtValues[0].split("-");
            if ((ymdValues != null) && (ymdValues.length == 3)) {
                String [] hmValues = dtValues[1].split(":");
                if ((hmValues != null) && (hmValues.length == 2)) { 
                    return ZonedDateTime.of(Integer.parseInt(ymdValues[0]), Integer.parseInt(ymdValues[1]), Integer.parseInt(ymdValues[2]), 
                                                Integer.parseInt(hmValues[0]), Integer.parseInt(hmValues[1]), 0, 0, ZoneId.systemDefault());
                }
            }
        }
        return ZonedDateTime.now().plusYears(300);
    }

    @SuppressWarnings("unchecked")
    @View("schedulednet-edit")
    @Get("/schedulednet-edit/{callsign}")
    public Map<String, Object> editScheduledNet(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNet scheduledNet = scheduledNetAccessor.get(loggedInUser, callsign);

        String timeStartStr = null;
        if (scheduledNet.getType().equals(ScheduledNetType.ONE_TIME_ONLY)) {
            timeStartStr = String.format("%04d-%02d-%02d %02d:%02d", scheduledNet.getNextStartTime().getYear(), scheduledNet.getNextStartTime().getMonthValue(), 
                                                        scheduledNet.getNextStartTime().getDayOfMonth(), scheduledNet.getNextStartTime().getHour(), scheduledNet.getNextStartTime().getMinute());
        } else {
            timeStartStr = String.format("%02d:%02d", (scheduledNet.getTimeStart() / 100), (scheduledNet.getTimeStart() - ((scheduledNet.getTimeStart() / 100)*100)));
        }
        String callsignOriginal = scheduledNet.getCallsign();
        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                                "scheduledNet", scheduledNet, "callsignOriginal", callsignOriginal, 
                                                "timeStartStr", timeStartStr,
                                                "scheduledNetType"+scheduledNet.getType().ordinal(), "yes",
                                                "dayStart"+scheduledNet.getDayStart(), "yes",
                                                "form", formGenerator.generate("/schedulednet-edit-action/"+callsign, ScheduledNetCreateRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/schedulednet-edit-action/{callsign}")
    HttpResponse<?> editScheduledNetAction(HttpRequest<?> request, @Valid @Body ScheduledNetCreateRequest messageRequest,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNet scheduledNet = scheduledNetAccessor.get(loggedInUser, callsign);
        scheduledNet.setCallsign(messageRequest.callsign());
        scheduledNet.setName(messageRequest.name());
        scheduledNet.setDescription(messageRequest.description());
        scheduledNet.setVoiceFrequency(messageRequest.voiceFrequency());
        scheduledNet.setLat(messageRequest.lat());
        scheduledNet.setLon(messageRequest.lon());
        scheduledNet.setType(ScheduledNetType.values()[messageRequest.type()]);
        scheduledNet.setDayStart(messageRequest.dayStart());
        scheduledNet.setDuration(messageRequest.duration());
        if (ScheduledNetType.values()[messageRequest.type()].equals(ScheduledNetType.ONE_TIME_ONLY)) {
            ZonedDateTime nextStartTime = convertTimeStartStringToZDT(messageRequest.timeStartStr());
            scheduledNet.setNextStartTime(nextStartTime);
        } else {
            scheduledNet.setTimeStart(convertTimeStartStringToInt(messageRequest.timeStartStr()));
            scheduledNet.setNextStartTime(scheduledNet.calculateNextStartTime());
        }
        scheduledNetAccessor.update(loggedInUser, callsign, scheduledNet);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/nets").build());
    } 

    private int convertTimeStartStringToInt(String timeStartStr) {
        int ret = 0;
        // string HH:MM (24hr time)
        if (timeStartStr == null) {
            return ret;
        }
        String [] parts = timeStartStr.split(":");
        if ((parts != null) && (parts.length == 2)) {
            // return HHMM in decimal ex: 1745
            ret = ((Integer.parseInt(parts[0])) * 100) + (Integer.parseInt(parts[1]));
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @View("schedulednet-delete")
    @Get("/schedulednet-delete/{callsign}")
    public Map<String, Object> deleteScheduledNet(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        scheduledNetAccessor.get(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/schedulednet-delete-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/schedulednet-delete-action/{callsign}")
    HttpResponse<?> deleteScheduledNetAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        scheduledNetAccessor.delete(loggedInUser, callsign);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/nets").build());
    } 

    @SuppressWarnings("unchecked")
    @View("transceiver-enable")
    @Get("/transceiver-enable/{id}")
    public Map<String, Object> enableTransceiver(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, id);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "id", id, "name", transceiver.getName(), 
                                            "form", formGenerator.generate("/transceiver-enable-action/"+id, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/transceiver-enable-action/{id}")
    HttpResponse<?> enableTransceiverAction(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, id);
        if (transceiver != null) {
            transceiver.setEnabledReceive(true);
            registeredTransceiverAccessor.update(loggedInUser, id, transceiver);
        }

        return HttpResponse.seeOther(UriBuilder.of("/").path("/transceivers").build());
    } 
 
    @SuppressWarnings("unchecked")
    @View("transceiver-disable")
    @Get("/transceiver-disable/{id}")
    public Map<String, Object> disableTransceiver(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, id);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "id", id, "name", transceiver.getName(), 
                                            "form", formGenerator.generate("/transceiver-disable-action/"+id, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/transceiver-disable-action/{id}")
    HttpResponse<?> disableTransceiverAction(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, id);
        if (transceiver != null) {
            transceiver.setEnabledReceive(false);
            registeredTransceiverAccessor.update(loggedInUser, id, transceiver);
        }

        return HttpResponse.seeOther(UriBuilder.of("/").path("/transceivers").build());
    } 

    @SuppressWarnings("unchecked")
    @View("transceiver-enable-transmit")
    @Get("/transceiver-enable-transmit/{id}")
    public Map<String, Object> enableTransceiverTransmit(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, id);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "id", id, "name", transceiver.getName(), 
                                            "form", formGenerator.generate("/transceiver-enable-transmit-action/"+id, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/transceiver-enable-transmit-action/{id}")
    HttpResponse<?> enableTransceiverTransmitAction(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, id);
        if (transceiver != null) {
            transceiver.setEnabledTransmit(true);
            registeredTransceiverAccessor.update(loggedInUser, id, transceiver);
        }

        return HttpResponse.seeOther(UriBuilder.of("/").path("/transceivers").build());
    } 
 
    @SuppressWarnings("unchecked")
    @View("transceiver-disable-transmit")
    @Get("/transceiver-disable-transmit/{id}")
    public Map<String, Object> disableTransceiverTransmit(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, id);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "id", id, "name", transceiver.getName(), 
                                            "form", formGenerator.generate("/transceiver-disable-transmit-action/"+id, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/transceiver-disable-transmit-action/{id}")
    HttpResponse<?> disableTransceiverTransmitAction(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, id);
        if (transceiver != null) {
            transceiver.setEnabledTransmit(false);
            registeredTransceiverAccessor.update(loggedInUser, id, transceiver);
        }

        return HttpResponse.seeOther(UriBuilder.of("/").path("/transceivers").build());
    } 

    @SuppressWarnings("unchecked")
    @View("transceiver-delete")
    @Get("/transceiver-delete/{id}")
    public Map<String, Object> deleteTransceiver(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, id);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "id", id, "name", transceiver.getName(), 
                                            "form", formGenerator.generate("/transceiver-delete-action/"+id, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/transceiver-delete-action/{id}")
    HttpResponse<?> deleteTransceiverAction(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        registeredTransceiverAccessor.delete(loggedInUser, id);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/transceivers").build());
    } 

    @View("objects")
    @Get("/objects")
    public HttpResponse<?> objects(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<APRSObject> aprsObjects = aprsObjectAccessor.getObjects(loggedInUser, false);

        MapMarkerFactory mapMarkerFactory = new MapMarkerFactory();
        mapMarkerFactory.processObjects(aprsObjects, "|");

        if (mapMarkerFactory.getCount() > 0) {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                            "objectsCount", (aprsObjects != null) ? aprsObjects.size() : 0,
                                            /* map */
                                            "itemCount", mapMarkerFactory.getCount(),
                                            "latitude", mapMarkerFactory.getCenterLatitude(),
                                            "longitude", mapMarkerFactory.getCenterLongitude(),
                                            "lonString", mapMarkerFactory.getLongitudes(),
                                            "latString", mapMarkerFactory.getLatitudes(),
                                            "labelString", mapMarkerFactory.getDisplayInfos(),
                                            "titleString", mapMarkerFactory.getTitles(),
                                            "minLongitude", mapMarkerFactory.getMinLongitude(),
                                            "minLatitude", mapMarkerFactory.getMinLatitude(),
                                            "maxLongitude", mapMarkerFactory.getMaxLongitude(),
                                            "maxLatitude", mapMarkerFactory.getMaxLatitude(),
                                            "itemSeperator", '|',
                                            "showMap", "true",
                                            "objects", aprsObjects));
        } else {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                            /* map */
                                            "objectsCount", (aprsObjects != null) ? aprsObjects.size() : 0,
                                            "objects", aprsObjects));
        }
    }

    @View("object")
    @Get("/object/{id}")
    public HttpResponse<?> object(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        boolean shelter = false;
        boolean medical  = false;
        boolean eoc = false;

        List<APRSMessage> messages = new ArrayList<>();
        APRSObject obj = aprsObjectAccessor.getObject(loggedInUser, id);
        APRSNetCentralShelterCensusReport reportPopulation = null;
        APRSNetCentralShelterWorkerReport reportWorkers = null;
        APRSNetCentralShelterOperationalFoodReport reportFood = null;
        APRSNetCentralShelterOperationalMaterielReport reportMateriel = null;

        try {
            List<APRSMessage> messagesFrom = aprsObjectAccessor.getMessagesFrom(loggedInUser, obj.getCallsignFrom());
            messages.addAll(messagesFrom);
        } catch (Exception e) {
            // ignore
        }

        try {
            List<APRSMessage> messagesTo = aprsObjectAccessor.getMessagesTo(loggedInUser, obj.getCallsignFrom());
            messages.addAll(messagesTo);
        } catch (Exception e) {
            // ignore
        }

        try {
            List<APRSMessage> messagesTo = aprsObjectAccessor.getMessagesTo(loggedInUser, obj.getCallsignFrom());
            messages.addAll(messagesTo);
        } catch (Exception e) {
            // ignore
        }

        if (obj.getType().equals(ObjectType.SHELTER)) {
            reportPopulation = reportAccessor.getLatestShelterCensusReport(loggedInUser, obj.getCallsignFrom());
            reportWorkers = reportAccessor.getLatestShelterWorkerReport(loggedInUser, obj.getCallsignFrom(), 1);
            reportFood = reportAccessor.getLatestShelterOperationalFoodReport(loggedInUser, obj.getCallsignFrom(), ObjectShelterReportingTimeframe.ON_HAND);
            reportMateriel = reportAccessor.getLatestShelterOperationalMaterielReport(loggedInUser, obj.getCallsignFrom(), ObjectShelterReportingTimeframe.ON_HAND);
            shelter = true;
        } else if (obj.getType().equals(ObjectType.MEDICAL)) {
            medical = true;
        } else if (obj.getType().equals(ObjectType.EOC)) {
            eoc = true;
        }

        List<APRSStatus> aprsStatus = aprsObjectAccessor.getStatus(loggedInUser, obj.getCallsignFrom());

        MapMarkerFactory mapMarkerFactory = new MapMarkerFactory();
        mapMarkerFactory.processObject(obj, "|");

        if (mapMarkerFactory.getCount() > 0) {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "obj", obj, "messages", messages, "status", aprsStatus,
                                                            "messagesCount", (messages != null) ? messages.size() : 0,
                                                            /* map */
                                                            "itemCount", mapMarkerFactory.getCount(),
                                                            "latitude", mapMarkerFactory.getCenterLatitude(),
                                                            "longitude", mapMarkerFactory.getCenterLongitude(),
                                                            "lonString", mapMarkerFactory.getLongitudes(),
                                                            "latString", mapMarkerFactory.getLatitudes(),
                                                            "labelString", mapMarkerFactory.getDisplayInfos(),
                                                            "titleString", mapMarkerFactory.getTitles(),
                                                            "minLongitude", mapMarkerFactory.getMinLongitude(),
                                                            "minLatitude", mapMarkerFactory.getMinLatitude(),
                                                            "maxLongitude", mapMarkerFactory.getMaxLongitude(),
                                                            "maxLatitude", mapMarkerFactory.getMaxLatitude(),
                                                            "itemSeperator", '|',
                                                            "showMap", "yes",
                                                            "shelter", (shelter) ? "Yes" : null, 
                                                            "medical", (medical) ? "Yes" : null, 
                                                            "eoc", (eoc) ? "Yes" : null, 
                                                            "reportPopulation", reportPopulation, "reportWorkers", reportWorkers, "reportFood", reportFood, "reportMateriel", reportMateriel,
                                                            "statusesCount", (aprsStatus != null) ? aprsStatus.size() : 0));
        } else {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "obj", obj, "messages", messages, "status", aprsStatus,
                                                            "reportPopulation", reportPopulation, "reportWorkers", reportWorkers, "reportFood", reportFood, "reportMateriel", reportMateriel,
                                                            "messagesCount", (messages != null) ? messages.size() : 0,
                                                            "statusesCount", (aprsStatus != null) ? aprsStatus.size() : 0));
        }
    }

    @SuppressWarnings("unchecked")
    @View("object-delete")
    @Get("/object-delete/{id}")
    public Map<String, Object> deleteObject(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject object = aprsObjectAccessor.getObject(loggedInUser, id);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "callsign", object.getCallsignFrom(), "id", id,
                                            "form", formGenerator.generate("/object-delete-action/"+id, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/object-delete-action/{id}")
    HttpResponse<?> deleteObjectAction(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        aprsObjectAccessor.deleteObject(loggedInUser, id);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/objects").build());
    } 

    @SuppressWarnings("unchecked")
    @View("object-remove")
    @Get("/object-remove/{id}")
    public Map<String, Object> removeObject(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSObject object = aprsObjectAccessor.getObject(loggedInUser, id);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "callsign", object.getCallsignFrom(), "id", id,
                                            "form", formGenerator.generate("/object-remove-action/"+id, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/object-remove-action/{id}")
    HttpResponse<?> removeObjectAction(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        APRSObject object = aprsObjectAccessor.getObject(loggedInUser, id);

        ObjectCreateRequest messageRequest = new ObjectCreateRequest(object.getType().ordinal(), object.getCallsignTo(), object.getComment(), object.getLat(), object.getLon());
        uiAccessor.downObject(loggedInUser, messageRequest);
        aprsObjectAccessor.deleteObject(loggedInUser, id);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/objects").build());
    } 

    @SuppressWarnings("unchecked")
    @View("object-create")
    @Get("/object-create")
    public Map<String, Object> createObject(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, 
                        "user", loggedInUser, "form", formGenerator.generate("/object-create-action", ObjectCreateRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/object-create-action")
    HttpResponse<?> createObjectAction(HttpRequest<?> request, @Valid @Body ObjectCreateRequest messageRequest) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        uiAccessor.upObject(loggedInUser, messageRequest);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/objects").build());
    } 

    @View("stations")
    @Get("/stations")
    public HttpResponse<?> stations(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        boolean trackOnly = sessionTrackedObjectAccessor.isTrackingOnly(token);

        List<TrackedStation> trackedStations = uiAccessor.getStations(loggedInUser, trackOnly);

        MapMarkerFactory mapMarkerFactory = new MapMarkerFactory();
        mapMarkerFactory.processTrackedStations(trackedStations, "|");

        if (mapMarkerFactory.getCount() > 0) {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), 
                                                "token", token, "user", loggedInUser, 
                                                "trackedStationsCount", (trackedStations != null) ? trackedStations.size() : 0,
                                                /* map */
                                                "itemCount", mapMarkerFactory.getCount(),
                                                "latitude", mapMarkerFactory.getCenterLatitude(),
                                                "longitude", mapMarkerFactory.getCenterLongitude(),
                                                "lonString", mapMarkerFactory.getLongitudes(),
                                                "latString", mapMarkerFactory.getLatitudes(),
                                                "labelString", mapMarkerFactory.getDisplayInfos(),
                                                "titleString", mapMarkerFactory.getTitles(),
                                                "minLongitude", mapMarkerFactory.getMinLongitude(),
                                                "minLatitude", mapMarkerFactory.getMinLatitude(),
                                                "maxLongitude", mapMarkerFactory.getMaxLongitude(),
                                                "maxLatitude", mapMarkerFactory.getMaxLatitude(),
                                                "itemSeperator", '|',
                                                "showMap", "Yes",
                                                "trackedStations", trackedStations));
        } else {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), 
                                                "token", token, "user", loggedInUser, 
                                                "trackedStationsCount", (trackedStations != null) ? trackedStations.size() : 0,
                                                /* map */
                                                "trackedStations", trackedStations));
        }
    }

    @View("station")
    @Get("/station/{id}")
    public HttpResponse<?> station(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<APRSMessage> messages = new ArrayList<>();
        TrackedStation trackedStation = trackedStationAccessor.get(loggedInUser, id);

        try {
            List<APRSMessage> messagesFrom = aprsObjectAccessor.getMessagesFrom(loggedInUser, trackedStation.getCallsign());
            messages.addAll(messagesFrom);
        } catch (Exception e) {
            // ignore
        }

        try {
            List<APRSMessage> messagesTo = aprsObjectAccessor.getMessagesTo(loggedInUser, trackedStation.getCallsign());
            messages.addAll(messagesTo);
        } catch (Exception e) {
            // ignore
        }

        List<APRSStatus> aprsStatus = aprsObjectAccessor.getStatus(loggedInUser, trackedStation.getCallsign());

        MapMarkerFactory mapMarkerFactory = new MapMarkerFactory();
        mapMarkerFactory.processTrackedStation(trackedStation, "|");

        if (mapMarkerFactory.getCount() > 0) {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                                /* map */
                                                "itemCount", mapMarkerFactory.getCount(),
                                                "latitude", mapMarkerFactory.getCenterLatitude(),
                                                "longitude", mapMarkerFactory.getCenterLongitude(),
                                                "lonString", mapMarkerFactory.getLongitudes(),
                                                "latString", mapMarkerFactory.getLatitudes(),
                                                "labelString", mapMarkerFactory.getDisplayInfos(),
                                                "titleString", mapMarkerFactory.getTitles(),
                                                "minLongitude", mapMarkerFactory.getMinLongitude(),
                                                "minLatitude", mapMarkerFactory.getMinLatitude(),
                                                "maxLongitude", mapMarkerFactory.getMaxLongitude(),
                                                "maxLatitude", mapMarkerFactory.getMaxLatitude(),
                                                "itemSeperator", '|',
                                                "showMap", "yes",
                                                "tracking", (trackedStation.isTrackingActive() ? "yes" : null),
                                                "trackedStation", trackedStation, "messages", messages, "status", aprsStatus,
                                                "messagesCount", (messages != null) ? messages.size() : 0,
                                                "statusesCount", (aprsStatus != null) ? aprsStatus.size() : 0));
        } else {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                                            "tracking", (trackedStation.isTrackingActive() ? "yes" : null),
                                                            "trackedStation", trackedStation, "messages", messages, "status", aprsStatus,
                                                            "messagesCount", (messages != null) ? messages.size() : 0,
                                                            "statusesCount", (aprsStatus != null) ? aprsStatus.size() : 0));
        }
    }

    @SuppressWarnings("unchecked")
    @View("station-delete")
    @Get("/station-delete/{callsign}")
    public Map<String, Object> deleteStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/station-delete-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/station-delete-action/{callsign}")
    HttpResponse<?> deleteStationAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        trackedStationAccessor.delete(loggedInUser, trackedStation.getId());

        return HttpResponse.seeOther(UriBuilder.of("/").path("/stations").build());
    } 

    @SuppressWarnings("unchecked")
    @View("station-track")
    @Get("/station-track/{callsign}")
    public Map<String, Object> trackStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/station-track-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/station-track-action/{callsign}")
    HttpResponse<?> trackStationAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        trackedStation.setTrackingActive(true);
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/stations").build());
    } 

    @SuppressWarnings("unchecked")
    @View("station-untrack")
    @Get("/station-untrack/{callsign}")
    public Map<String, Object> untrackStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/station-untrack-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/station-untrack-action/{callsign}")
    HttpResponse<?> untrackStationAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        trackedStation.setTrackingActive(false);
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/stations").build());
    } 

    @SuppressWarnings("unchecked")
    @View("station-edit")
    @Get("/station-edit/{callsign}")
    public Map<String, Object> editStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "trackedStation", trackedStation, 
                                            "electricalPowerType"+trackedStation.getElectricalPowerType().ordinal(), "yes",
                                            "backupElectricalPowerType"+trackedStation.getBackupElectricalPowerType().ordinal(), "yes",
                                            "radioStyle"+trackedStation.getRadioStyle().ordinal(), "yes",
                                            "form", formGenerator.generate("/station-edit-action/"+callsign, TrackedStationEditRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/station-edit-action/{callsign}")
    HttpResponse<?> editStationAction(HttpRequest<?> request, @Valid @Body TrackedStationEditRequest messageRequest,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);
        trackedStation.setName(messageRequest.name());
        trackedStation.setDescription(messageRequest.description());
        trackedStation.setElectricalPowerType(ElectricalPowerType.values()[messageRequest.electricalPowerType()]);
        trackedStation.setBackupElectricalPowerType(ElectricalPowerType.values()[messageRequest.backupElectricalPowerType()]);
        trackedStation.setRadioStyle(RadioStyle.values()[messageRequest.radioStyle()]);
        trackedStation.setFrequencyRx(messageRequest.receiveFrequency());
        trackedStation.setFrequencyTx(messageRequest.transmitFrequency());
        trackedStation.setTone(messageRequest.tone());
        trackedStation.setTransmitPower(messageRequest.transmitPower());

        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/stations").build());
    } 

    @View("callsigns")
    @Get("/callsigns")
    public HttpResponse<?> callsigns(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<Callsign> callsigns = callsignAccessor.getAll(loggedInUser, null);

        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), 
                                            "token", token, "user", loggedInUser, 
                                            "callsignsCount", (callsigns != null) ? callsigns.size() : 0,
                                            /* map */
                                            "callsigns", callsigns));
    }

    @View("callsign")
    @Get("/callsign/{callsign}")
    public HttpResponse<?> callsign(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Callsign callsignObj = callsignAccessor.getByCallsign(loggedInUser, callsign);

        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "callsign", callsignObj));
    }

    @SuppressWarnings("unchecked")
    @View("callsign-identify")
    @Get("/callsign-identify/{callsign}")
    public Map<String, Object> identifyCallsign(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Callsign callsignObj = callsignAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "callsign",callsignObj.getCallsign(),
                                            "form", formGenerator.generate("/callsign-identify-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/callsign-identify-action/{callsign}")
    HttpResponse<?> identifyCallsignAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        // poke WHO-15 to get the info
        uiAccessor.identifyCallsign(loggedInUser, callsign);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/callsigns").build());
    } 

    @SuppressWarnings("unchecked")
    @View("callsign-edit")
    @Get("/callsign-edit/{callsign}")
    public Map<String, Object> editCallsign(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Callsign callsignObj = callsignAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "callsign", callsignObj, 
                                            "form", formGenerator.generate("/callsign-edit-action/"+callsign, CallsignEditRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/callsign-edit-action/{callsign}")
    HttpResponse<?> editCallsignAction(HttpRequest<?> request, @Valid @Body CallsignEditRequest messageRequest,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Callsign callsignObj = callsignAccessor.getByCallsign(loggedInUser, callsign);
        callsignObj.setName(messageRequest.name());
        callsignObj.setState(messageRequest.state());
        callsignObj.setCountry(messageRequest.country());
        callsignObj.setLicense(messageRequest.license());

        callsignAccessor.update(loggedInUser, callsign, callsignObj);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/callsigns").build());
    } 

    @SuppressWarnings("unchecked")
    @View("callsign-delete")
    @Get("/callsign-delete/{callsign}")
    public Map<String, Object> deleteCallsign(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Callsign callsignObj = callsignAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "callsign",callsignObj.getCallsign(),
                                            "form", formGenerator.generate("/callsign-delete-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/callsign-delete-action/{callsign}")
    HttpResponse<?> deleteCallsignAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        callsignAccessor.delete(loggedInUser, callsign);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/callsigns").build());
    } 

    @View("infras")
    @Get("/infras")
    public HttpResponse<?> infras(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        boolean trackOnly = sessionTrackedObjectAccessor.isTrackingOnly(token);

        List<TrackedStation> trackedStations = uiAccessor.getInfrastructureStations(loggedInUser, trackOnly);

        MapMarkerFactory mapMarkerFactory = new MapMarkerFactory();
        mapMarkerFactory.processTrackedStations(trackedStations, "|");

        if (mapMarkerFactory.getCount() > 0) {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), 
                                                "token", token, "user", loggedInUser, 
                                                "trackedStationsCount", (trackedStations != null) ? trackedStations.size() : 0,
                                                /* map */
                                                "itemCount", mapMarkerFactory.getCount(),
                                                "latitude", mapMarkerFactory.getCenterLatitude(),
                                                "longitude", mapMarkerFactory.getCenterLongitude(),
                                                "lonString", mapMarkerFactory.getLongitudes(),
                                                "latString", mapMarkerFactory.getLatitudes(),
                                                "labelString", mapMarkerFactory.getDisplayInfos(),
                                                "titleString", mapMarkerFactory.getTitles(),
                                                "minLongitude", mapMarkerFactory.getMinLongitude(),
                                                "minLatitude", mapMarkerFactory.getMinLatitude(),
                                                "maxLongitude", mapMarkerFactory.getMaxLongitude(),
                                                "maxLatitude", mapMarkerFactory.getMaxLatitude(),
                                                "itemSeperator", '|',
                                                "showMap", "Yes",
                                                "trackedStations", trackedStations));
        } else {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), 
                                                "token", token, "user", loggedInUser, 
                                                "trackedStationsCount", (trackedStations != null) ? trackedStations.size() : 0,
                                                /* map */
                                                "trackedStations", trackedStations));
        }
    }

    @View("infra")
    @Get("/infra/{id}")
    public HttpResponse<?> infra(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<APRSMessage> messages = new ArrayList<>();
        TrackedStation trackedStation = trackedStationAccessor.get(loggedInUser, id);

        try {
            List<APRSMessage> messagesFrom = aprsObjectAccessor.getMessagesFrom(loggedInUser, trackedStation.getCallsign());
            messages.addAll(messagesFrom);
        } catch (Exception e) {
            // ignore
        }

        try {
            List<APRSMessage> messagesTo = aprsObjectAccessor.getMessagesTo(loggedInUser, trackedStation.getCallsign());
            messages.addAll(messagesTo);
        } catch (Exception e) {
            // ignore
        }

        List<APRSStatus> aprsStatus = aprsObjectAccessor.getStatus(loggedInUser, trackedStation.getCallsign());

        MapMarkerFactory mapMarkerFactory = new MapMarkerFactory();
        mapMarkerFactory.processTrackedStation(trackedStation, "|");

        if (mapMarkerFactory.getCount() > 0) {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                                            "trackedStation", trackedStation, "messages", messages, "status", aprsStatus,
                                                            "messagesCount", (messages != null) ? messages.size() : 0,
                                                            /* map */
                                                            "itemCount", mapMarkerFactory.getCount(),
                                                            "latitude", mapMarkerFactory.getCenterLatitude(),
                                                            "longitude", mapMarkerFactory.getCenterLongitude(),
                                                            "lonString", mapMarkerFactory.getLongitudes(),
                                                            "latString", mapMarkerFactory.getLatitudes(),
                                                            "labelString", mapMarkerFactory.getDisplayInfos(),
                                                            "titleString", mapMarkerFactory.getTitles(),
                                                            "minLongitude", mapMarkerFactory.getMinLongitude(),
                                                            "minLatitude", mapMarkerFactory.getMinLatitude(),
                                                            "maxLongitude", mapMarkerFactory.getMaxLongitude(),
                                                            "maxLatitude", mapMarkerFactory.getMaxLatitude(),
                                                            "itemSeperator", '|',
                                                            "showMap", "yes",
                                                            "tracking", (trackedStation.isTrackingActive() ? "yes" : null),
                                                            "statusesCount", (aprsStatus != null) ? aprsStatus.size() : 0));
        } else {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                                            "trackedStation", trackedStation, "messages", messages, "status", aprsStatus,
                                                            "tracking", (trackedStation.isTrackingActive() ? "yes" : null),
                                                            "messagesCount", (messages != null) ? messages.size() : 0,
                                                            "statusesCount", (aprsStatus != null) ? aprsStatus.size() : 0));
        }
    }

    @SuppressWarnings("unchecked")
    @View("infra-delete")
    @Get("/infra-delete/{callsign}")
    public Map<String, Object> deleteInfrastructureStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/infra-delete-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/infra-delete-action/{callsign}")
    HttpResponse<?> deleteInfrastructureStationAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        trackedStationAccessor.delete(loggedInUser, trackedStation.getId());

        return HttpResponse.seeOther(UriBuilder.of("/").path("/infras").build());
    } 

    @SuppressWarnings("unchecked")
    @View("infra-edit")
    @Get("/infra-edit/{callsign}")
    public Map<String, Object> editInfrastructureStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "trackedStation", trackedStation, 
                                            "electricalPowerType"+trackedStation.getElectricalPowerType().ordinal(), "yes",
                                            "backupElectricalPowerType"+trackedStation.getBackupElectricalPowerType().ordinal(), "yes",
                                            "radioStyle"+trackedStation.getRadioStyle().ordinal(), "yes",
                                            "form", formGenerator.generate("/infra-edit-action/"+callsign, TrackedStationEditRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/infra-edit-action/{callsign}")
    HttpResponse<?> editInfrastructureStationAction(HttpRequest<?> request, @Valid @Body TrackedStationEditRequest messageRequest,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);
        trackedStation.setName(messageRequest.name());
        trackedStation.setDescription(messageRequest.description());
        trackedStation.setElectricalPowerType(ElectricalPowerType.values()[messageRequest.electricalPowerType()]);
        trackedStation.setBackupElectricalPowerType(ElectricalPowerType.values()[messageRequest.backupElectricalPowerType()]);
        trackedStation.setRadioStyle(RadioStyle.values()[messageRequest.radioStyle()]);
        trackedStation.setFrequencyRx(messageRequest.receiveFrequency());
        trackedStation.setFrequencyTx(messageRequest.transmitFrequency());
        trackedStation.setTone(messageRequest.tone());
        trackedStation.setTransmitPower(messageRequest.transmitPower());

        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/infras").build());
    } 

    @SuppressWarnings("unchecked")
    @View("infra-track")
    @Get("/infra-track/{callsign}")
    public Map<String, Object> trackInfrastructureStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/infra-track-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/infra-track-action/{callsign}")
    HttpResponse<?> trackInfrastructureStationAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        trackedStation.setTrackingActive(true);
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/infras").build());
    } 

    @SuppressWarnings("unchecked")
    @View("infra-untrack")
    @Get("/infra-untrack/{callsign}")
    public Map<String, Object> untrackInfrastructureStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/infra-untrack-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/infra-untrack-action/{callsign}")
    HttpResponse<?> untrackInfrastructureStationAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        trackedStation.setTrackingActive(false);
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/infras").build());
    } 

    @View("transceivers")
    @Get("/transceivers")
    public HttpResponse<?> transceivers(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<RegisteredTransceiver> registeredTransceivers = registeredTransceiverAccessor.getAll(loggedInUser);
        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "registeredTransceiversCount", (registeredTransceivers != null) ? registeredTransceivers.size() : 0,
                                    "registeredTransceivers", registeredTransceivers));
    }

    @View("users")
    @Get("/users")
    public HttpResponse<?> users(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<User> users = userAccessor.getAll(loggedInUser);
        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                                    "usersCount", (users != null) ? users.size() : 0, "users", users));
    }

    @SuppressWarnings("unchecked")
    @View("user-edit")
    @Get("/user-edit/{id}")
    public Map<String, Object> editUser(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        User user = userAccessor.get(loggedInUser, id);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "user", user, "form", formGenerator.generate("/user-edit-action/"+id, UserEditRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/user-edit-action/{id}")
    HttpResponse<?> editUserAction(HttpRequest<?> request, @Valid @Body UserEditRequest messageRequest,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        User user = userAccessor.get(loggedInUser, id);
        user.setEmailAddress(messageRequest.emailAddress());
        user.setFirstName(messageRequest.firstName());
        user.setLastName(messageRequest.lastName());
        Callsign callsign = new Callsign();
        callsign.setCallsign(messageRequest.callsign());
        user.setCallsign(callsign);

        userAccessor.update(loggedInUser, id, user);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/users").build());
    } 

    @SuppressWarnings("unchecked")
    @View("user-delete")
    @Get("/user-delete/{id}")
    public Map<String, Object> deleteUser(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        User user = userAccessor.get(loggedInUser, id);
        boolean me = false;
        if (loggedInUser.getId().equals(user.getId())) {
            me = true;
        }

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "userdelete", user, "me", me, "form", formGenerator.generate("/user-delete-action/"+id, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/user-delete-action/{id}")
    HttpResponse<?> deleteUserAction(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        User user = userAccessor.get(loggedInUser, id);

        userAccessor.delete(loggedInUser, id);
        if (user.getId().equals(loggedInUser.getId())) {
            userAccessor.logout(user);
            return HttpResponse.seeOther(UriBuilder.of("/").path("/loginUser").build());
        }

        return HttpResponse.seeOther(UriBuilder.of("/").path("/users").build());
    } 

    @View("weathers")
    @Get("/weathers")
    public HttpResponse<?> weathers(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        boolean trackOnly = sessionTrackedObjectAccessor.isTrackingOnly(token);

        List<TrackedStation> trackedStations = uiAccessor.getWeatherStations(loggedInUser, trackOnly);

        MapMarkerFactory mapMarkerFactory = new MapMarkerFactory();
        mapMarkerFactory.processTrackedStations(trackedStations, "|");

        if (mapMarkerFactory.getCount() > 0) {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                                "trackedStationsCount", (trackedStations != null) ? trackedStations.size() : 0,
                                                /* map */
                                                "itemCount", mapMarkerFactory.getCount(),
                                                "latitude", mapMarkerFactory.getCenterLatitude(),
                                                "longitude", mapMarkerFactory.getCenterLongitude(),
                                                "lonString", mapMarkerFactory.getLongitudes(),
                                                "latString", mapMarkerFactory.getLatitudes(),
                                                "labelString", mapMarkerFactory.getDisplayInfos(),
                                                "titleString", mapMarkerFactory.getTitles(),
                                                "minLongitude", mapMarkerFactory.getMinLongitude(),
                                                "minLatitude", mapMarkerFactory.getMinLatitude(),
                                                "maxLongitude", mapMarkerFactory.getMaxLongitude(),
                                                "maxLatitude", mapMarkerFactory.getMaxLatitude(),
                                                "showMap", "yes",
                                                "itemSeperator", '|',
                                                "trackedStations", trackedStations));
        } else {
            return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                                "trackedStationsCount", (trackedStations != null) ? trackedStations.size() : 0,
                                                "trackedStations", trackedStations));
        }
    }

    @View("weather")
    @Get("/weather/{id}")
    public HttpResponse<?> weather(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.get(loggedInUser, id);
        List<APRSWeatherReport> messages = aprsObjectAccessor.getWeatherReports(loggedInUser, trackedStation.getCallsign());

        MapMarkerFactory mapMarkerFactory = new MapMarkerFactory();
        mapMarkerFactory.processTrackedStation(trackedStation, "|");

        if (mapMarkerFactory.getCount() > 0) {
             return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "trackedStation", trackedStation, "messages", messages,
                                                /* map */
                                                "itemCount", mapMarkerFactory.getCount(),
                                                "latitude", mapMarkerFactory.getCenterLatitude(),
                                                "longitude", mapMarkerFactory.getCenterLongitude(),
                                                "lonString", mapMarkerFactory.getLongitudes(),
                                                "latString", mapMarkerFactory.getLatitudes(),
                                                "labelString", mapMarkerFactory.getDisplayInfos(),
                                                "titleString", mapMarkerFactory.getTitles(),
                                                "minLongitude", mapMarkerFactory.getMinLongitude(),
                                                "minLatitude", mapMarkerFactory.getMinLatitude(),
                                                "maxLongitude", mapMarkerFactory.getMaxLongitude(),
                                                "maxLatitude", mapMarkerFactory.getMaxLatitude(),
                                                "itemSeperator", '|',
                                                "showMap", "yes",
                                                "tracking", (trackedStation.isTrackingActive() ? "yes" : null),
                                                "messagesCount", (messages != null) ? messages.size() : 0));
        } else {
             return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "trackedStation", trackedStation, "messages", messages,
                                                            "tracking", (trackedStation.isTrackingActive() ? "yes" : null),
                                                            "messagesCount", (messages != null) ? messages.size() : 0));
        }
    }

    @SuppressWarnings("unchecked")
    @View("weather-delete")
    @Get("/weather-delete/{callsign}")
    public Map<String, Object> deleteWeatherStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/weather-delete-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/weather-delete-action/{callsign}")
    HttpResponse<?> deleteWeatherStationAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);
        trackedStationAccessor.delete(loggedInUser, trackedStation.getId());

        return HttpResponse.seeOther(UriBuilder.of("/").path("/weathers").build());
    } 

    @SuppressWarnings("unchecked")
    @View("weather-edit")
    @Get("/weather-edit/{callsign}")
    public Map<String, Object> editWeatherStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "trackedStation", trackedStation, 
                                            "electricalPowerType"+trackedStation.getElectricalPowerType().ordinal(), "yes",
                                            "backupElectricalPowerType"+trackedStation.getBackupElectricalPowerType().ordinal(), "yes",
                                            "radioStyle"+trackedStation.getRadioStyle().ordinal(), "yes",
                                            "form", formGenerator.generate("/weather-edit-action/"+callsign, TrackedStationEditRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/weather-edit-action/{callsign}")
    HttpResponse<?> editWeatherStationAction(HttpRequest<?> request, @Valid @Body TrackedStationEditRequest messageRequest,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);
        trackedStation.setName(messageRequest.name());
        trackedStation.setDescription(messageRequest.description());
        trackedStation.setElectricalPowerType(ElectricalPowerType.values()[messageRequest.electricalPowerType()]);
        trackedStation.setBackupElectricalPowerType(ElectricalPowerType.values()[messageRequest.backupElectricalPowerType()]);
        trackedStation.setRadioStyle(RadioStyle.values()[messageRequest.radioStyle()]);

        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/weathers").build());
    } 

    @SuppressWarnings("unchecked")
    @View("weather-track")
    @Get("/weather-track/{callsign}")
    public Map<String, Object> trackWeatherStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/weather-track-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/weather-track-action/{callsign}")
    HttpResponse<?> trackWeatherStationAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        trackedStation.setTrackingActive(true);
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/weathers").build());
    } 

    @SuppressWarnings("unchecked")
    @View("weather-untrack")
    @Get("/weather-untrack/{callsign}")
    public Map<String, Object> untrackWeatherStation(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, 
                                    "callsign", callsign, "form", formGenerator.generate("/weather-untrack-action/"+callsign, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/weather-untrack-action/{callsign}")
    HttpResponse<?> untrackWeatherStationAction(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        trackedStation.setTrackingActive(false);
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/weathers").build());
    } 

    @View("completednet")
    @Get("/completednet/{id}")
    public HttpResponse<?> completednet(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        List<CompletedParticipant> participants = new ArrayList<>();
        List<APRSMessage> messages = new ArrayList<>();

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);
        try {
            List<APRSMessage> messagesFrom = aprsObjectAccessor.getCompletedNetMessages(loggedInUser, id);
            messages.addAll(messagesFrom);
        } catch (Exception e) {
            // ignore
        }

        try {
            participants = completedParticipantAccessor.getAllByCompletedNetId(loggedInUser, id);
        } catch (Exception e) {
            // ignore
        }
        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "net", net, "messages", messages, "participants", participants,
                                                            "messagesCount", (messages != null) ? messages.size() : 0,
                                                            "participantsCount", (participants != null) ? participants.size() : 0));
    }

    @SuppressWarnings("unchecked")
    @View("completednet-report")
    @Get("/completednet-report/{id}")
    public Map<String, Object> completedNetReport(HttpRequest<?> request,  @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser, "id", id, "name", net.getName(), "net", net,
                                            "form", formGenerator.generate("/completednet-report-action/"+id, BlankRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/completednet-report-action/{id}")
    HttpResponse<?> completedNetReportAction(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);

        String filename = uiAccessor.generateCompletedNetReport(loggedInUser, net);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/completednet-report-pdf/"+id+"/"+filename).build());
    } 

    @Get(uri = "/completednet-report-pdf/{id}/{filename}", produces = MediaType.APPLICATION_PDF)
    public HttpResponse<byte[]> downloadCompletedNetReport(HttpRequest<?> request, @PathVariable String id, @PathVariable String filename) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);

        try {
            filename = netConfigServerConfig.getTempReportDir()+filename;
            byte[] fileBytes = Files.readAllBytes(Paths.get(filename));
            String newFilename = String.format("NetReport-%s-%s.pdf", net.getCallsign(), net.getPrettyStartTime());
            return HttpResponse.ok(fileBytes)
                    .header("Content-Disposition", "attachment; filename=\""+newFilename+"\"");
        } catch (Exception e) {
            return HttpResponse.serverError();
        }
    }

    @View("profile")
    @Get("/profile")
    public HttpResponse<?> profile(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        if (loggedInUser != null) {
            loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        } else {
            token = null;
        }

        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser));
    }


    @SuppressWarnings("unchecked")
    @View("profile-username-edit")
    @Get("/profile-username-edit")
    public Map<String, Object> usernameEdit(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        if (loggedInUser != null) {
            loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        } else {
            token = null;
        }

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser,
            "form", formGenerator.generate("/profile-username-edit-action", UsernameEditRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/profile-username-edit-action")
    HttpResponse<?> usernameEditAction(HttpRequest<?> request, @Valid @Body UsernameEditRequest qEditRequest) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        if (loggedInUser != null) {
            loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        } else {
            token = null;
        }

        User existing = null;
        if (loggedInUser != null) {
            try {
                if (!qEditRequest.username().equalsIgnoreCase(loggedInUser.getEmailAddress())) {
                    // if changing the user name
                    existing = userAccessor.getUserByName(loggedInUser, qEditRequest.username(), false); 
                    if ((existing != null) && (!loggedInUser.getId().equals(existing.getId()))) {
                        // someone else has that username
                    }
                }
            } catch (Exception e) {
                // no one else with that name
                existing = null;
            }

            if (existing != null) {
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Username already in use");
            }

            loggedInUser.setEmailAddress(qEditRequest.username());
            if (qEditRequest.callsign() != null) {
                Callsign co = new Callsign();
                co.setCallsign(qEditRequest.callsign());
                loggedInUser.setCallsign(co);
            }
            loggedInUser.setFirstName(qEditRequest.firstName());
            loggedInUser.setLastName(qEditRequest.lastName());
            userAccessor.update(loggedInUser, loggedInUser.getId(), loggedInUser);
        }
        return HttpResponse.seeOther(UriBuilder.of("/").path("/profile").build());
    } 

    @SuppressWarnings("unchecked")
    @View("profile-password-edit")
    @Get("/profile-password-edit")
    public Map<String, Object> passwordEdit(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        if (loggedInUser != null) {
            loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        } else {
            token = null;
        }

        return CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser,
            "form", formGenerator.generate("/profile-password-edit-action", PasswordEditRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/profile-password-edit-action")
    HttpResponse<?> passwordEditAction(HttpRequest<?> request, @Valid @Body PasswordEditRequest qEditRequest) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        if (loggedInUser != null) {
            loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        } else {
            token = null;
        }

        if (!qEditRequest.password().equals(qEditRequest.password2())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        if (loggedInUser != null) {
            loggedInUser.setPassword(qEditRequest.password());
            userAccessor.update(loggedInUser, loggedInUser.getId(), loggedInUser);
        }
        return HttpResponse.seeOther(UriBuilder.of("/").path("/profile").build());
    } 

    @View("login")
    @Get("/loginUser")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> login(HttpRequest<?> request) {
        return Collections.singletonMap("form",
            formGenerator.generate("/login-action", LoginRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/login-action")
    HttpResponse<?> loginAction(@Valid @Body LoginRequest loginRequest) {
        User user = userAccessor.login(loginRequest);

        if (user == null) {
            return HttpResponse.seeOther(UriBuilder.of("/").path("/authFailed").build());
        }
        String time = LocalDateTime.now().toString();
        String cookieText = String.format("SessionID=%s; path=/;expires %s", user.getAccessToken(), time);
        return HttpResponse.seeOther(UriBuilder.of("/").path("/").build()).header("Set-Cookie", cookieText).header("SessionID", user.getAccessToken());
    } 

    @View("register")
    @Get("/register")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> register(HttpRequest<?> request) {
        return Collections.singletonMap("form",
            formGenerator.generate("/register-action", RegisterRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/register-action")
    HttpResponse<?> registerAction(HttpRequest<?> request, @Valid @Body RegisterRequest registerRequest) {
        userAccessor.register(registerRequest);
/*         Organization org = organizationAccessor.register(registerRequest);
        if (org != null) {
            organizationUserAccessor.addUser(sessionAccessor.getSystemUser(), org, user, UserRole.ADMIN, true);
        }*/
        return HttpResponse.seeOther(UriBuilder.of("/").path("/loginUser").build());
    } 

    @View("logout")
    @Get("/logoutUser")
    @Produces(MediaType.TEXT_HTML)
    public  HttpResponse<?> logout(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return HttpResponse.ok(CollectionUtils.mapOf("friendlyName", getFriendlyName(loggedInUser), "token", token, "user", loggedInUser));
    }

    @View("loggedout")
    @Get("/loggedout")
    @Produces(MediaType.TEXT_HTML)
    public  HttpResponse<?> logoutYes(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        if (loggedInUser != null) {
            userAccessor.logout(loggedInUser);
        } else {
            token = null;
        }
        return HttpResponse.ok().header("Set-Cookie", "SessionID=; path=/;expires Thu, 01 Jan 1970 00:00:00 GMT").header("SessionID", "");
    }

    @View("authFailed")
    @Get("/authFailed")
    public HttpResponse<?> authFailed(HttpRequest<?> request) {
        return HttpResponse.ok();
    }

    @View("statistics")
    @Get("/statistics")
    public HttpResponse<?> statistics(HttpRequest<?> request) {
        NetCentralServerStatistics stats = statisticsAccessor.get();
        String duration = formatDuration(stats.getStartTimeMinutes());
        User loggedInUser = new User();
        List<RegisteredTransceiver> registeredTransceivers = registeredTransceiverAccessor.getAll(loggedInUser);
        return HttpResponse.ok(CollectionUtils.mapOf("stats", stats, "duration", duration, "registeredTransceivers", registeredTransceivers));
    }

    private String formatDuration(Long minutesIn) {
        Long seconds = minutesIn * 60;
        Long days = seconds / (3600*24);
        Long remain = (seconds) - (days*3600*24);
        Long hours = remain/3600;
        remain = (seconds) - (days*3600*24) - (hours*3600);
        Long minutes = remain / 60;

        String formattedDays = Long.toString(days);
        String formattedHours = Long.toString(hours);
        String formattedMinutes = Long.toString(minutes);

        return String.format("%s days %s hours %s minutes", formattedDays, formattedHours, formattedMinutes);
    }


}
