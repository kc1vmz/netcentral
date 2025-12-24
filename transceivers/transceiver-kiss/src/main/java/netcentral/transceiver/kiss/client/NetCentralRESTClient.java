package netcentral.transceiver.kiss.client;

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

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kc1vmz.netcentral.aprsobject.common.LoginRequest;
import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.APRSObjectResource;
import com.kc1vmz.netcentral.common.exception.LoginFailureException;
import com.kc1vmz.netcentral.common.object.NetCentralServerUser;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kiss.config.NetCentralClientConfig;

@Singleton
public class NetCentralRESTClient {
    private static final Logger logger = LogManager.getLogger(NetCentralRESTClient.class);

    @Inject
    private NetCentralClientConfig netControlConfig;

    private String buildURL(String tail) {
        return String.format("http://%s:%d/%s", netControlConfig.getServer(), netControlConfig.getPort(), tail);
    }

    public APRSObjectResource create(APRSObjectResource res, String sessionId) throws LoginFailureException {
        APRSObjectResource ret = null;
        try {
            ObjectMapper objectMapper = getObjectMapper();
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(res);

            String val = post(buildURL("api/v1/APRSObjects"), sessionId, json);
            if (val != null) {
                logger.debug("POST response = " + val);
                ret = objectMapper.readValue(val, APRSObjectResource.class);
            }
        } catch (LoginFailureException e) {
            throw new LoginFailureException();
        } catch (Exception e) {
            logger.error("Exception caught creating APRS object", e);
        }
        return ret;
    }

    public NetCentralServerUser login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        ObjectMapper objectMapper = getObjectMapper();
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(loginRequest);

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildURL("api/v1/login")))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();

        HttpResponse<?> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String json2 = response.body().toString();
            NetCentralServerUser resp = objectMapper.readValue(json2, NetCentralServerUser.class);
            // success
            return resp;
        }
        return null;

    }

    public void sendMessage(String sessionId, String sourceId, String callsign, String to_callsign, String message) throws Exception {
        APRSObjectResource obj = new APRSObjectResource();
        obj.setSource(sourceId);
        obj.setHeardTime(ZonedDateTime.now());
        APRSMessage msg = new APRSMessage();
        msg.setCallsignFrom(callsign);
        msg.setCallsignTo(to_callsign);
        msg.setMessage(message);
        obj.setInnerAPRSMessage(msg);

        create(obj, sessionId);
    }

    public void logout(NetCentralServerUser loginResponse) throws Exception {
        ObjectMapper objectMapper = getObjectMapper();
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(loginResponse);

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildURL("api/v1/logout")))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();

        client.send(request, BodyHandlers.discarding());
    }

    public RegisteredTransceiver register(String accessToken, String name, String description, String type, Integer port) throws LoginFailureException {
        RegisteredTransceiver ret = null;
        try {
            String fqdName = getHostName();
            if (fqdName == null) {
                return null;
            }

            ObjectMapper objectMapper = getObjectMapper();
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();

            RegisteredTransceiver registeredTransceiver = new RegisteredTransceiver();
            registeredTransceiver.setName(name);
            registeredTransceiver.setDescription(description);
            registeredTransceiver.setFqdName(fqdName);
            registeredTransceiver.setType(type);
            registeredTransceiver.setPort(port);

            String json = ow.writeValueAsString(registeredTransceiver);

            String val = post(buildURL("api/v1/registeredTransceivers"), accessToken, json);
            ret = objectMapper.readValue(val, RegisteredTransceiver.class);
        } catch (LoginFailureException e) {
            throw new LoginFailureException();
        } catch (Exception e) {
            logger.error("Exception caught registering transceiver", e);
            ret = null;
        }
        return ret;
    }

    private String post(String uri, String sessionId, String data) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("SessionID", sessionId)
                .POST(BodyPublishers.ofString(data))
                .build();

        HttpResponse<?> response = client.send(request,  BodyHandlers.ofString());
        if ((response.statusCode() == 200) || (response.statusCode() == 201)) {
            return response.body().toString();
        } else if (response.statusCode() == 401) {
            // login failed
            throw new LoginFailureException();
        }
        logger.warn(String.format("Error creating APRS object with NetControl: %s %s", response.body(), data));
        return null;
    }

    private String getHostName() {
        if (netControlConfig.getHostname().isPresent()) {
            return netControlConfig.getHostname().get();
        }
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return ip.getHostAddress();
        } catch(Exception ex) {
        }
        return null;
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    public String getAckCounter(String callsignFrom, String callsignTo) throws LoginFailureException {
        String ret = null;
        try {
            String url = String.format("api/v1/callsignAckCounters/%s/%s", callsignFrom, callsignTo);
            ret = get(buildURL(url));
        } catch (LoginFailureException e) {
            throw new LoginFailureException();
        } catch (Exception e) {
            logger.error("Exception caughtgetting ack counter", e);
            ret = null;
        }
        return ret;
    }

    private String get(String uri) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpResponse<?> response = client.send(request,  BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body().toString();
        } else if (response.statusCode() == 401) {
            // login failed
            throw new LoginFailureException();
        }
        logger.warn(String.format("Error getting ack counter from Net Central for %s: %s", uri, response.body()));
        return null;
    }
}
