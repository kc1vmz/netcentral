package com.kc1vmz.netcentral.aprsobject.object;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class APRSObjectResource {
    private String source;
    private ZonedDateTime heardTime;
    private Optional<String> id;
    private Optional<APRSAgrelo> innerAPRSAgrelo;
    private Optional<APRSItem> innerAPRSItem;
    private Optional<APRSMaidenheadLocatorBeacon> innerAPRSMaidenheadLocatorBeacon;
    private Optional<APRSMessage> innerAPRSMessage;
    private Optional<APRSMicE> innerAPRSMicE;
    private Optional<APRSMicEOld> innerAPRSMicEOld;
    private Optional<APRSObject> innerAPRSObject;
    private Optional<APRSPosition> innerAPRSPosition;
    private Optional<APRSQuery> innerAPRSQuery;
    private Optional<APRSStationCapabilities> innerAPRSStationCapabilities;
    private Optional<APRSStatus> innerAPRSStatus;
    private Optional<APRSTelemetry> innerAPRSTelemetry;
    private Optional<APRSTest> innerAPRSTest;
    private Optional<APRSThirdPartyTraffic> innerAPRSThirdPartyTraffic;
    private Optional<APRSUnknown> innerAPRSUnknown;
    private Optional<APRSUserDefined> innerAPRSUserDefined;
    private Optional<APRSWeatherReport> innerAPRSWeatherReport;
    private Optional<APRSRaw> innerAPRSRaw;
    private Optional<AGWRaw> innerAGWRaw;
    private Optional<List<String>> innerDigipeaters;
    private Optional<List<String>> innerIgates;

    public APRSObjectResource() {
    }
    public APRSObjectResource(String id, APRSAgrelo innerAPRSAgrelo, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSAgrelo = Optional.ofNullable(innerAPRSAgrelo);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSMicE innerAPRSMicE, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSMicE = Optional.ofNullable(innerAPRSMicE);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSItem innerAPRSItem, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSItem = Optional.ofNullable(innerAPRSItem);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSMaidenheadLocatorBeacon innerAPRSMaidenheadLocatorBeacon, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSMaidenheadLocatorBeacon = Optional.ofNullable(innerAPRSMaidenheadLocatorBeacon);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSMessage innerAPRSMessage, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSMessage = Optional.ofNullable(innerAPRSMessage);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSObject innerAPRSObject, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSObject = Optional.ofNullable(innerAPRSObject);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSPosition innerAPRSPosition, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSPosition = Optional.ofNullable(innerAPRSPosition);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSQuery innerAPRSQuery, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSQuery = Optional.ofNullable(innerAPRSQuery);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSStationCapabilities innerAPRSStationCapabilities, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSStationCapabilities = Optional.ofNullable(innerAPRSStationCapabilities);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSStatus innerAPRSStatus, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSStatus = Optional.ofNullable(innerAPRSStatus);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSTelemetry innerAPRSTelemetry, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSTelemetry = Optional.ofNullable(innerAPRSTelemetry);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSTest innerAPRSTest, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSTest = Optional.ofNullable(innerAPRSTest);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSThirdPartyTraffic innerAPRSThirdPartyTraffic, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSThirdPartyTraffic = Optional.ofNullable(innerAPRSThirdPartyTraffic);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSUnknown innerAPRSUnknown, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSUnknown = Optional.ofNullable(innerAPRSUnknown);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSUserDefined = Optional.ofNullable(innerAPRSUserDefined);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSWeatherReport innerAPRSWeatherReport, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSWeatherReport =Optional.ofNullable( innerAPRSWeatherReport);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, APRSRaw innerAPRSRaw, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAPRSRaw = Optional.ofNullable(innerAPRSRaw);
        this.source = source;
        this.heardTime = heardTime;
    }
    public APRSObjectResource(String id, AGWRaw innerAGWRaw, String source, ZonedDateTime heardTime) {
        this.id = Optional.ofNullable(id);
        this.innerAGWRaw = Optional.ofNullable(innerAGWRaw);
        this.source = source;
        this.heardTime = heardTime;
    }

    public Optional<String> getId() {
        return id;
    }
    public void setId(String id) {
        this.id = Optional.ofNullable(id);
    }

    public Optional<APRSAgrelo> getInnerAPRSAgrelo() {
        return innerAPRSAgrelo;
    }

    public void setInnerAPRSAgrelo(APRSAgrelo innerAPRSAgrelo) {
        this.innerAPRSAgrelo = Optional.ofNullable(innerAPRSAgrelo);
    }

    public Optional<APRSItem> getInnerAPRSItem() {
        return innerAPRSItem;
    }

    public void setInnerAPRSItem(APRSItem innerAPRSItem) {
        this.innerAPRSItem = Optional.ofNullable(innerAPRSItem);
    }

    public Optional<APRSMaidenheadLocatorBeacon> getInnerAPRSMaidenheadLocatorBeacon() {
        return innerAPRSMaidenheadLocatorBeacon;
    }

    public void setInnerAPRSMaidenheadLocatorBeacon(APRSMaidenheadLocatorBeacon innerAPRSMaidenheadLocatorBeacon) {
        this.innerAPRSMaidenheadLocatorBeacon = Optional.ofNullable(innerAPRSMaidenheadLocatorBeacon);
    }

    public Optional<APRSMessage> getInnerAPRSMessage() {
        return innerAPRSMessage;
    }

    public void setInnerAPRSMessage(APRSMessage innerAPRSMessage) {
        this.innerAPRSMessage = Optional.ofNullable(innerAPRSMessage);
    }

    public Optional<APRSMicE> getInnerAPRSMicE() {
        return innerAPRSMicE;
    }

    public void setInnerAPRSMicE(APRSMicE innerAPRSMicE) {
        this.innerAPRSMicE = Optional.ofNullable(innerAPRSMicE);
    }

    public Optional<APRSMicEOld> getInnerAPRSMicEOld() {
        return innerAPRSMicEOld;
    }

    public void setInnerAPRSMicEOld(APRSMicEOld innerAPRSMicEOld) {
        this.innerAPRSMicEOld = Optional.ofNullable(innerAPRSMicEOld);
    }

    public Optional<APRSObject> getInnerAPRSObject() {
        return innerAPRSObject;
    }

    public void setInnerAPRSObject(APRSObject innerAPRSObject) {
        this.innerAPRSObject = Optional.ofNullable(innerAPRSObject);
    }

    public Optional<APRSPosition> getInnerAPRSPosition() {
        return innerAPRSPosition;
    }

    public void setInnerAPRSPosition(APRSPosition innerAPRSPosition) {
        this.innerAPRSPosition = Optional.ofNullable(innerAPRSPosition);
    }

    public Optional<APRSQuery> getInnerAPRSQuery() {
        return innerAPRSQuery;
    }

    public void setInnerAPRSQuery(APRSQuery innerAPRSQuery) {
        this.innerAPRSQuery = Optional.ofNullable(innerAPRSQuery);
    }

    public Optional<APRSStationCapabilities> getInnerAPRSStationCapabilities() {
        return innerAPRSStationCapabilities;
    }

    public void setInnerAPRSStationCapabilities(APRSStationCapabilities innerAPRSStationCapabilities) {
        this.innerAPRSStationCapabilities = Optional.ofNullable(innerAPRSStationCapabilities);
    }

    public Optional<APRSStatus> getInnerAPRSStatus() {
        return innerAPRSStatus;
    }

    public void setInnerAPRSStatus(APRSStatus innerAPRSStatus) {
        this.innerAPRSStatus = Optional.ofNullable(innerAPRSStatus);
    }

    public Optional<APRSTelemetry> getInnerAPRSTelemetry() {
        return innerAPRSTelemetry;
    }

    public void setInnerAPRSTelemetry(APRSTelemetry innerAPRSTelemetry) {
        this.innerAPRSTelemetry = Optional.ofNullable(innerAPRSTelemetry);
    }

    public Optional<APRSTest> getInnerAPRSTest() {
        return innerAPRSTest;
    }

    public void setInnerAPRSTest(APRSTest innerAPRSTest) {
        this.innerAPRSTest = Optional.ofNullable(innerAPRSTest);
    }

    public Optional<APRSThirdPartyTraffic> getInnerAPRSThirdPartyTraffic() {
        return innerAPRSThirdPartyTraffic;
    }

    public void setInnerAPRSThirdPartyTraffic(APRSThirdPartyTraffic innerAPRSThirdPartyTraffic) {
        this.innerAPRSThirdPartyTraffic = Optional.ofNullable(innerAPRSThirdPartyTraffic);
    }

    public Optional<APRSUnknown> getInnerAPRSUnknown() {
        return innerAPRSUnknown;
    }

    public void setInnerAPRSUnknown(APRSUnknown innerAPRSUnknown) {
        this.innerAPRSUnknown = Optional.ofNullable(innerAPRSUnknown);
    }

    public Optional<APRSUserDefined> getInnerAPRSUserDefined() {
        return innerAPRSUserDefined;
    }

    public void setInnerAPRSUserDefined(APRSUserDefined innerAPRSUserDefined) {
        this.innerAPRSUserDefined = Optional.ofNullable(innerAPRSUserDefined);
    }

    public Optional<APRSWeatherReport> getInnerAPRSWeatherReport() {
        return innerAPRSWeatherReport;
    }

    public void setInnerAPRSWeatherReport(APRSWeatherReport innerAPRSWeatherReport) {
        this.innerAPRSWeatherReport = Optional.ofNullable(innerAPRSWeatherReport);
    }
    public Optional<APRSRaw> getInnerAPRSRaw() {
        return innerAPRSRaw;
    }
    public void setInnerAPRSRaw(APRSRaw innerAPRSRaw) {
        this.innerAPRSRaw = Optional.ofNullable(innerAPRSRaw);
    }

    public Optional<AGWRaw> getInnerAGWRaw() {
        return innerAGWRaw;
    }
    public void setInnerAGWRaw(AGWRaw innerAGWRaw) {
        this.innerAGWRaw = Optional.ofNullable(innerAGWRaw);
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public ZonedDateTime getHeardTime() {
        return heardTime;
    }
    public void setHeardTime(ZonedDateTime heardTime) {
        this.heardTime = heardTime;
    }
    public Optional<List<String>> getInnerDigipeaters() {
        return innerDigipeaters;
    }
    public void setInnerDigipeaters(List<String> innerDigipeaters) {
        this.innerDigipeaters = Optional.ofNullable(innerDigipeaters);
    }
    public Optional<List<String>> getInnerIgates() {
        return innerIgates;
    }
    public void setInnerIgates(List<String> innerIgates) {
        this.innerIgates = Optional.ofNullable(innerIgates);
    }

}
