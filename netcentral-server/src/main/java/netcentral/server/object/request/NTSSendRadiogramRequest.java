package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record NTSSendRadiogramRequest(String number, String precedence, String hx, String callsignOrigin, String locationOrigin, String timeFiled, String dateFiled,
                String name, String address, String cityState, String phoneNumber, String emailAddress, String signature, String operatorNote,
                String message1, String message2, String message3, String message4, String message5, String message6, String message7, String message8, String message9, String message10,
                String message11, String message12, String message13, String message14, String message15, String message16, String message17, String message18, String message19, String message20,
                String message21, String message22, String message23, String message24, String message25, String check) {
}
