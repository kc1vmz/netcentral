package netcentral.server.transceiver.client;

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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessage;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessageMany;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverObject;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverQuery;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverReport;

import jakarta.inject.Singleton;
import netcentral.server.utils.Stripper;

@Singleton
public class TransceiverRESTClient {
    private static final Logger logger = LogManager.getLogger(TransceiverRESTClient.class);

    public void sendQuery(String fqdName, int port, TransceiverQuery msg, String transceiverId) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(msg);

            post(buildURL(fqdName, port, "api/v1/transceiverQueries"), json, transceiverId);
        } catch (Exception e) {
            logger.error("Exception caught sending query to transceiver", e);
        }
    }

    public void sendObject(String fqdName, int port, TransceiverObject msg, String transceiverId) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(msg);

            post(buildURL(fqdName, port, "api/v1/transceiverObjects"), json, transceiverId);
        } catch (Exception e) {
            logger.error("Exception caught sending object to transceiver", e);
        }
    }

    public void sendReport(String fqdName, int port, TransceiverReport msg, String transceiverId) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(msg);

            post(buildURL(fqdName, port, "api/v1/transceiverReports"), json, transceiverId);
        } catch (Exception e) {
            logger.error("Exception caught sending message to transceiver", e);
        }
    }

    public void sendMessage(String fqdName, int port, TransceiverMessage msg, String transceiverId) {
        try {
            if (isBadCallsign(msg.getCallsignTo())) {
                return;
            }
            ObjectMapper objectMapper = getObjectMapper();
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(msg);

            post(buildURL(fqdName, port, "api/v1/transceiverMessages"), json, transceiverId);
        } catch (Exception e) {
            logger.error("Exception caught sending message to transceiver", e);
        }
    }

    private boolean isBadCallsign(String callsignTo) {
        if (callsignTo == null) {
            return true;
        }
        callsignTo = Stripper.stripWhitespace(callsignTo);
        if ((callsignTo.length() == 0) || (callsignTo.length() > 9)) {
            return true;
        }
        if (!callsignTo.contains("-")) {
            return true;
        }
        if (callsignTo.contains(" ")) {
            return true;
        }
        if ((callsignTo.charAt(0) >= '0') && (callsignTo.charAt(0) <= '9')) {
            // starts with a number
            return true;
        }

        return false;
    }

    public void sendMessages(String fqdName, int port, TransceiverMessageMany messages, String transceiverId) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(messages);

            post(buildURL(fqdName, port, "api/v1/transceiverMessages/many"), json, transceiverId);
        } catch (Exception e) {
            logger.error("Exception caught sending messages to transceiver", e);
        }
    }

    private String buildURL(String fqdName, int port, String tail) {
        return String.format("http://%s:%d/%s", fqdName, port, tail);
    }

    private String post(String uri, String data, String transceiverId) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(BodyPublishers.ofString(data))
                .build();

        HttpResponse<?> response = client.send(request,  BodyHandlers.ofString());
        if  ((response != null) && (response.body() != null)) {
            return response.body().toString();
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
}
