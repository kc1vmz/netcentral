package com.kc1vmz.netcentral.aprsobject.object;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;
import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

public class APRSMessage implements APRSPacketInterface {
    private String id;
    private String addressee;
    private String message;
    private String messageNumber;
    private boolean mustAck;
    private byte [] data;
    private byte [] header;
    private String callsignFrom;
    private String callsignTo;
    private boolean bulletin;
    private boolean announcement;
    private boolean groupBulletin;
    private boolean NWSBulletin;
    private String NWSLevel;
    private boolean query;
    private String queryType;
    private byte dti;
    private ZonedDateTime heardTime;
    private String completedNetId;
    private String prettyHeardTime;

    public APRSMessage() {
    }
    public APRSMessage(String id,
            String callsignFrom, String callsignTo,
            String message, String messageNumber,
            Boolean must_ack, ZonedDateTime heardTime, Boolean isBulletin,
            Boolean isAnnouncement, Boolean isGroupBulletin, Boolean isNWSBulletin,
            String NWSlevel, String queryType,
            Boolean isQuery, String completedNetId) {
                this.id = id;
                this.callsignFrom = callsignFrom;
                this.callsignTo = callsignTo;
                this.message = message;
                this.messageNumber = messageNumber;
                this.mustAck = must_ack;
                this.heardTime = heardTime;
                this.bulletin = isBulletin;
                this.announcement = isAnnouncement;
                this.groupBulletin = isGroupBulletin;
                this.NWSBulletin = isNWSBulletin;
                this.NWSLevel = NWSlevel;
                this.queryType = queryType;
                this.query = isQuery;
                this.completedNetId = completedNetId;
                this.prettyHeardTime = PrettyZonedDateTimeFormatter.format(heardTime);
    }
    public String getAddressee() {
        return addressee;
    }
    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessageNumber() {
        return messageNumber;
    }
    public void setMessageNumber(String messageNumber) {
        this.messageNumber = messageNumber;
    }
    public boolean isMustAck() {
        return mustAck;
    }
    public void setMustAck(boolean mustAck) {
        this.mustAck = mustAck;
    }
    public byte [] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public byte [] getHeader() {
        return header;
    }
    public void setHeader(byte[] header) {
        this.header = header;
    }
    public String getCallsignFrom() {
        return callsignFrom;
    }
    public void setCallsignFrom(String callsignFrom) {
        this.callsignFrom = callsignFrom;
    }
    public String getCallsignTo() {
        return callsignTo;
    }
    public void setCallsignTo(String callsignTo) {
        this.callsignTo = callsignTo;
    }
    public boolean isBulletin() {
        return bulletin;
    }
    public void setBulletin(boolean bulletin) {
        this.bulletin = bulletin;
    }
    public boolean isAnnouncement() {
        return announcement;
    }
    public void setAnnouncement(boolean announcement) {
        this.announcement = announcement;
    }
    public boolean isGroupBulletin() {
        return groupBulletin;
    }
    public void setGroupBulletin(boolean groupBulletin) {
        this.groupBulletin = groupBulletin;
    }
    public boolean isNWSBulletin() {
        return NWSBulletin;
    }
    public void setNWSBulletin(boolean nWSBulletin) {
        NWSBulletin = nWSBulletin;
    }
    public String getNWSLevel() {
        return NWSLevel;
    }
    public void setNWSLevel(String NWSLevel) {
        this.NWSLevel = NWSLevel;
    }
    public boolean isQuery() {
        return query;
    }
    public void setQuery(boolean query) {
        this.query = query;
    }
    public String getQueryType() {
        return queryType;
    }
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }
    public byte getDti() {
        return dti;
    }
    public void setDti(byte dti) {
        this.dti = dti;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public ZonedDateTime getHeardTime() {
        return heardTime;
    }
    public void setHeardTime(ZonedDateTime heardTime) {
        this.heardTime = heardTime;
        this.prettyHeardTime = PrettyZonedDateTimeFormatter.format(heardTime);
    }
    public String getCompletedNetId() {
        return completedNetId;
    }
    public void setCompletedNetId(String completedNetId) {
        this.completedNetId = completedNetId;
    }
    public String getPrettyHeardTime() {
        return prettyHeardTime;
    }
    public void setPrettyHeardTime(String prettyHeardTime) {
        this.prettyHeardTime = prettyHeardTime;
    }

}
