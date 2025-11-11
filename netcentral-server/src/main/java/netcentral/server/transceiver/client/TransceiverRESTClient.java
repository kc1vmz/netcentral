package netcentral.server.transceiver.client;

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
import com.kc1vmz.netcentral.aprsobject.common.TransceiverReport;

import jakarta.inject.Singleton;

@Singleton
public class TransceiverRESTClient {
    private static final Logger logger = LogManager.getLogger(TransceiverRESTClient.class);

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
            ObjectMapper objectMapper = getObjectMapper();
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(msg);

            post(buildURL(fqdName, port, "api/v1/transceiverMessages"), json, transceiverId);
        } catch (Exception e) {
            logger.error("Exception caught sending message to transceiver", e);
        }
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
        return response.body().toString();
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
