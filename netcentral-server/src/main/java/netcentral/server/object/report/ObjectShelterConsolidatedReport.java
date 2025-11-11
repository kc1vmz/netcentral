package netcentral.server.object.report;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import netcentral.server.enums.ObjectShelterState;
import netcentral.server.enums.ObjectShelterStatus;

public class ObjectShelterConsolidatedReport {
    private String callsign;
    private String description;
    private ObjectType type;
    private String lat;
    private String lon;
    private boolean alive;
    private ObjectShelterStatus status;
    private ObjectShelterState state;
    private Integer population03;
    private Integer population47;
    private Integer population812;
    private Integer population1318;
    private Integer population1965;
    private Integer population66;
    private ZonedDateTime populationLastReportedTime;
    private String prettyPopulationLastReportedTime;
    private String populationLastReportedTimeDelta;
    private Integer foodTodayBreakfast;
    private Integer foodTodayLunch;
    private Integer foodTodayDinner;
    private Integer foodTodaySnack;
    private ZonedDateTime foodTodayLastReportedTime;
    private String prettyFoodTodayLastReportedTime;
    private String foodTodayLastReportedTimeDelta;
    private Integer foodTomorrowBreakfast;
    private Integer foodTomorrowLunch;
    private Integer foodTomorrowDinner;
    private Integer foodTomorrowSnack;
    private ZonedDateTime foodTomorrowLastReportedTime;
    private String prettyFoodTomorrowLastReportedTime;
    private String foodTomorrowLastReportedTimeDelta;
    private Integer foodNeededBreakfast;
    private Integer foodNeededLunch;
    private Integer foodNeededDinner;
    private Integer foodNeededSnack;
    private ZonedDateTime foodNeededLastReportedTime;
    private String prettyFoodNeededLastReportedTime;
    private String foodNeededLastReportedTimeDelta;
    private Integer materielTodayCots;
    private Integer materielTodayBlankets;
    private Integer materielTodayComfort;
    private Integer materielTodayCleanup;
    private Integer materielTodaySignage;
    private Integer materielTodayOther;
    private ZonedDateTime materielTodayLastReportedTime;
    private String prettyMaterielTodayLastReportedTime;
    private String materielTodayLastReportedTimeDelta;
    private Integer materielTomorrowCots;
    private Integer materielTomorrowBlankets;
    private Integer materielTomorrowComfort;
    private Integer materielTomorrowCleanup;
    private Integer materielTomorrowSignage;
    private Integer materielTomorrowOther;
    private ZonedDateTime materielTomorrowLastReportedTime;
    private String prettyMaterielTomorrowLastReportedTime;
    private String materielTomorrowLastReportedTimeDelta;
    private Integer materielNeededCots;
    private Integer materielNeededBlankets;
    private Integer materielNeededComfort;
    private Integer materielNeededCleanup;
    private Integer materielNeededSignage;
    private Integer materielNeededOther;
    private ZonedDateTime materielNeededLastReportedTime;
    private String prettyMaterielNeededLastReportedTime;
    private String materielNeededLastReportedTimeDelta;
    private Integer workersFirstHealth;
    private Integer workersFirstMental;
    private Integer workersFirstSpiritual;
    private Integer workersFirstCaseWorker;
    private Integer workersFirstFeeding;
    private Integer workersFirstOther;
    private ZonedDateTime workersFirstLastReportedTime;
    private String prettyWorkersFirstLastReportedTime;
    private String workersFirstLastReportedTimeDelta;
    private Integer workersSecondHealth;
    private Integer workersSecondMental;
    private Integer workersSecondSpiritual;
    private Integer workersSecondCaseWorker;
    private Integer workersSecondFeeding;
    private Integer workersSecondOther;
    private ZonedDateTime workersSecondLastReportedTime;
    private String prettyWorkersSecondLastReportedTime;
    private String workersSecondLastReportedTimeDelta;
    private Integer workersThirdHealth;
    private Integer workersThirdMental;
    private Integer workersThirdSpiritual;
    private Integer workersThirdCaseWorker;
    private Integer workersThirdFeeding;
    private Integer workersThirdOther;
    private ZonedDateTime workersThirdLastReportedTime;
    private String prettyWorkersThirdLastReportedTime;
    private String workersThirdLastReportedTimeDelta;


    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLon() {
        return lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }
    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public ObjectShelterStatus getStatus() {
        return status;
    }
    public void setStatus(ObjectShelterStatus status) {
        this.status = status;
    }
    public ObjectShelterState getState() {
        return state;
    }
    public void setState(ObjectShelterState state) {
        this.state = state;
    }
    public Integer getPopulation03() {
        return population03;
    }
    public void setPopulation03(Integer population03) {
        this.population03 = population03;
    }
    public Integer getPopulation47() {
        return population47;
    }
    public void setPopulation47(Integer population47) {
        this.population47 = population47;
    }
    public Integer getPopulation812() {
        return population812;
    }
    public void setPopulation812(Integer population812) {
        this.population812 = population812;
    }
    public Integer getPopulation1318() {
        return population1318;
    }
    public void setPopulation1318(Integer population1318) {
        this.population1318 = population1318;
    }
    public Integer getPopulation1965() {
        return population1965;
    }
    public void setPopulation1965(Integer population1965) {
        this.population1965 = population1965;
    }
    public Integer getPopulation66() {
        return population66;
    }
    public void setPopulation66(Integer population66) {
        this.population66 = population66;
    }
    public ZonedDateTime getPopulationLastReportedTime() {
        return populationLastReportedTime;
    }
    public void setPopulationLastReportedTime(ZonedDateTime populationLastReportedTime) {
        this.populationLastReportedTime = populationLastReportedTime;
        this.prettyPopulationLastReportedTime = PrettyZonedDateTimeFormatter.format(populationLastReportedTime);
    }
    public Integer getFoodTodayBreakfast() {
        return foodTodayBreakfast;
    }
    public void setFoodTodayBreakfast(Integer foodTodayBreakfast) {
        this.foodTodayBreakfast = foodTodayBreakfast;
    }
    public Integer getFoodTodayLunch() {
        return foodTodayLunch;
    }
    public void setFoodTodayLunch(Integer foodTodayLunch) {
        this.foodTodayLunch = foodTodayLunch;
    }
    public Integer getFoodTodayDinner() {
        return foodTodayDinner;
    }
    public void setFoodTodayDinner(Integer foodTodayDinner) {
        this.foodTodayDinner = foodTodayDinner;
    }
    public Integer getFoodTodaySnack() {
        return foodTodaySnack;
    }
    public void setFoodTodaySnack(Integer foodTodaySnack) {
        this.foodTodaySnack = foodTodaySnack;
    }
    public ZonedDateTime getFoodTodayLastReportedTime() {
        return foodTodayLastReportedTime;
    }
    public void setFoodTodayLastReportedTime(ZonedDateTime foodTodayLastReportedTime) {
        this.foodTodayLastReportedTime = foodTodayLastReportedTime;
        this.prettyFoodTodayLastReportedTime = PrettyZonedDateTimeFormatter.format(foodTodayLastReportedTime);
    }
    public Integer getFoodTomorrowBreakfast() {
        return foodTomorrowBreakfast;
    }
    public void setFoodTomorrowBreakfast(Integer foodTomorrowBreakfast) {
        this.foodTomorrowBreakfast = foodTomorrowBreakfast;
    }
    public Integer getFoodTomorrowLunch() {
        return foodTomorrowLunch;
    }
    public void setFoodTomorrowLunch(Integer foodTomorrowLunch) {
        this.foodTomorrowLunch = foodTomorrowLunch;
    }
    public Integer getFoodTomorrowDinner() {
        return foodTomorrowDinner;
    }
    public void setFoodTomorrowDinner(Integer foodTomorrowDinner) {
        this.foodTomorrowDinner = foodTomorrowDinner;
    }
    public Integer getFoodTomorrowSnack() {
        return foodTomorrowSnack;
    }
    public void setFoodTomorrowSnack(Integer foodTomorrowSnack) {
        this.foodTomorrowSnack = foodTomorrowSnack;
    }
    public ZonedDateTime getFoodTomorrowLastReportedTime() {
        return foodTomorrowLastReportedTime;
    }
    public void setFoodTomorrowLastReportedTime(ZonedDateTime foodTomorrowLastReportedTime) {
        this.foodTomorrowLastReportedTime = foodTomorrowLastReportedTime;
        this.prettyFoodTomorrowLastReportedTime = PrettyZonedDateTimeFormatter.format(foodTomorrowLastReportedTime);
    }
    public Integer getFoodNeededBreakfast() {
        return foodNeededBreakfast;
    }
    public void setFoodNeededBreakfast(Integer foodNeededBreakfast) {
        this.foodNeededBreakfast = foodNeededBreakfast;
    }
    public Integer getFoodNeededLunch() {
        return foodNeededLunch;
    }
    public void setFoodNeededLunch(Integer foodNeededLunch) {
        this.foodNeededLunch = foodNeededLunch;
    }
    public Integer getFoodNeededDinner() {
        return foodNeededDinner;
    }
    public void setFoodNeededDinner(Integer foodNeededDinner) {
        this.foodNeededDinner = foodNeededDinner;
    }
    public Integer getFoodNeededSnack() {
        return foodNeededSnack;
    }
    public void setFoodNeededSnack(Integer foodNeededSnack) {
        this.foodNeededSnack = foodNeededSnack;
    }
    public ZonedDateTime getFoodNeededLastReportedTime() {
        return foodNeededLastReportedTime;
    }
    public void setFoodNeededLastReportedTime(ZonedDateTime foodNeededLastReportedTime) {
        this.foodNeededLastReportedTime = foodNeededLastReportedTime;
        this.prettyFoodNeededLastReportedTime = PrettyZonedDateTimeFormatter.format(foodNeededLastReportedTime);
    }
    public Integer getMaterielTodayCots() {
        return materielTodayCots;
    }
    public void setMaterielTodayCots(Integer materielTodayCots) {
        this.materielTodayCots = materielTodayCots;
    }
    public Integer getMaterielTodayBlankets() {
        return materielTodayBlankets;
    }
    public void setMaterielTodayBlankets(Integer materielTodayBlankets) {
        this.materielTodayBlankets = materielTodayBlankets;
    }
    public Integer getMaterielTodayComfort() {
        return materielTodayComfort;
    }
    public void setMaterielTodayComfort(Integer materielTodayComfort) {
        this.materielTodayComfort = materielTodayComfort;
    }
    public Integer getMaterielTodayCleanup() {
        return materielTodayCleanup;
    }
    public void setMaterielTodayCleanup(Integer materielTodayCleanup) {
        this.materielTodayCleanup = materielTodayCleanup;
    }
    public Integer getMaterielTodaySignage() {
        return materielTodaySignage;
    }
    public void setMaterielTodaySignage(Integer materielTodaySignage) {
        this.materielTodaySignage = materielTodaySignage;
    }
    public Integer getMaterielTodayOther() {
        return materielTodayOther;
    }
    public void setMaterielTodayOther(Integer materielTodayOther) {
        this.materielTodayOther = materielTodayOther;
    }
    public ZonedDateTime getMaterielTodayLastReportedTime() {
        return materielTodayLastReportedTime;
    }
    public void setMaterielTodayLastReportedTime(ZonedDateTime materielTodayLastReportedTime) {
        this.materielTodayLastReportedTime = materielTodayLastReportedTime;
        this.prettyMaterielTodayLastReportedTime = PrettyZonedDateTimeFormatter.format(materielTodayLastReportedTime);
    }
    public Integer getMaterielTomorrowCots() {
        return materielTomorrowCots;
    }
    public void setMaterielTomorrowCots(Integer materielTomorrowCots) {
        this.materielTomorrowCots = materielTomorrowCots;
    }
    public Integer getMaterielTomorrowBlankets() {
        return materielTomorrowBlankets;
    }
    public void setMaterielTomorrowBlankets(Integer materielTomorrowBlankets) {
        this.materielTomorrowBlankets = materielTomorrowBlankets;
    }
    public Integer getMaterielTomorrowComfort() {
        return materielTomorrowComfort;
    }
    public void setMaterielTomorrowComfort(Integer materielTomorrowComfort) {
        this.materielTomorrowComfort = materielTomorrowComfort;
    }
    public Integer getMaterielTomorrowCleanup() {
        return materielTomorrowCleanup;
    }
    public void setMaterielTomorrowCleanup(Integer materielTomorrowCleanup) {
        this.materielTomorrowCleanup = materielTomorrowCleanup;
    }
    public Integer getMaterielTomorrowSignage() {
        return materielTomorrowSignage;
    }
    public void setMaterielTomorrowSignage(Integer materielTomorrowSignage) {
        this.materielTomorrowSignage = materielTomorrowSignage;
    }
    public Integer getMaterielTomorrowOther() {
        return materielTomorrowOther;
    }
    public void setMaterielTomorrowOther(Integer materielTomorrowOther) {
        this.materielTomorrowOther = materielTomorrowOther;
    }
    public ZonedDateTime getMaterielTomorrowLastReportedTime() {
        return materielTomorrowLastReportedTime;
    }
    public void setMaterielTomorrowLastReportedTime(ZonedDateTime materielTomorrowLastReportedTime) {
        this.materielTomorrowLastReportedTime = materielTomorrowLastReportedTime;
        this.prettyMaterielTomorrowLastReportedTime = PrettyZonedDateTimeFormatter.format(materielTomorrowLastReportedTime);
    }
    public Integer getMaterielNeededCots() {
        return materielNeededCots;
    }
    public void setMaterielNeededCots(Integer materielNeededCots) {
        this.materielNeededCots = materielNeededCots;
    }
    public Integer getMaterielNeededBlankets() {
        return materielNeededBlankets;
    }
    public void setMaterielNeededBlankets(Integer materielNeededBlankets) {
        this.materielNeededBlankets = materielNeededBlankets;
    }
    public Integer getMaterielNeededComfort() {
        return materielNeededComfort;
    }
    public void setMaterielNeededComfort(Integer materielNeededComfort) {
        this.materielNeededComfort = materielNeededComfort;
    }
    public Integer getMaterielNeededCleanup() {
        return materielNeededCleanup;
    }
    public void setMaterielNeededCleanup(Integer materielNeededCleanup) {
        this.materielNeededCleanup = materielNeededCleanup;
    }
    public Integer getMaterielNeededSignage() {
        return materielNeededSignage;
    }
    public void setMaterielNeededSignage(Integer materielNeededSignage) {
        this.materielNeededSignage = materielNeededSignage;
    }
    public Integer getMaterielNeededOther() {
        return materielNeededOther;
    }
    public void setMaterielNeededOther(Integer materielNeededOther) {
        this.materielNeededOther = materielNeededOther;
    }
    public ZonedDateTime getMaterielNeededLastReportedTime() {
        return materielNeededLastReportedTime;
    }
    public void setMaterielNeededLastReportedTime(ZonedDateTime materielNeededLastReportedTime) {
        this.materielNeededLastReportedTime = materielNeededLastReportedTime;
        this.prettyMaterielNeededLastReportedTime = PrettyZonedDateTimeFormatter.format(materielNeededLastReportedTime);
    }
    public Integer getWorkersFirstHealth() {
        return workersFirstHealth;
    }
    public void setWorkersFirstHealth(Integer workersFirstHealth) {
        this.workersFirstHealth = workersFirstHealth;
    }
    public Integer getWorkersFirstMental() {
        return workersFirstMental;
    }
    public void setWorkersFirstMental(Integer workersFirstMental) {
        this.workersFirstMental = workersFirstMental;
    }
    public Integer getWorkersFirstSpiritual() {
        return workersFirstSpiritual;
    }
    public void setWorkersFirstSpiritual(Integer workersFirstSpiritual) {
        this.workersFirstSpiritual = workersFirstSpiritual;
    }
    public Integer getWorkersFirstCaseWorker() {
        return workersFirstCaseWorker;
    }
    public void setWorkersFirstCaseWorker(Integer workersFirstCaseWorker) {
        this.workersFirstCaseWorker = workersFirstCaseWorker;
    }
    public Integer getWorkersFirstFeeding() {
        return workersFirstFeeding;
    }
    public void setWorkersFirstFeeding(Integer workersFirstFeeding) {
        this.workersFirstFeeding = workersFirstFeeding;
    }
    public Integer getWorkersFirstOther() {
        return workersFirstOther;
    }
    public void setWorkersFirstOther(Integer workersFirstOther) {
        this.workersFirstOther = workersFirstOther;
    }
    public ZonedDateTime getWorkersFirstLastReportedTime() {
        return workersFirstLastReportedTime;
    }
    public void setWorkersFirstLastReportedTime(ZonedDateTime workersFirstLastReportedTime) {
        this.workersFirstLastReportedTime = workersFirstLastReportedTime;
        this.prettyWorkersFirstLastReportedTime = PrettyZonedDateTimeFormatter.format(workersFirstLastReportedTime);
    }
    public Integer getWorkersSecondHealth() {
        return workersSecondHealth;
    }
    public void setWorkersSecondHealth(Integer workersSecondHealth) {
        this.workersSecondHealth = workersSecondHealth;
    }
    public Integer getWorkersSecondMental() {
        return workersSecondMental;
    }
    public void setWorkersSecondMental(Integer workersSecondMental) {
        this.workersSecondMental = workersSecondMental;
    }
    public Integer getWorkersSecondSpiritual() {
        return workersSecondSpiritual;
    }
    public void setWorkersSecondSpiritual(Integer workersSecondSpiritual) {
        this.workersSecondSpiritual = workersSecondSpiritual;
    }
    public Integer getWorkersSecondCaseWorker() {
        return workersSecondCaseWorker;
    }
    public void setWorkersSecondCaseWorker(Integer workersSecondCaseWorker) {
        this.workersSecondCaseWorker = workersSecondCaseWorker;
    }
    public Integer getWorkersSecondFeeding() {
        return workersSecondFeeding;
    }
    public void setWorkersSecondFeeding(Integer workersSecondFeeding) {
        this.workersSecondFeeding = workersSecondFeeding;
    }
    public Integer getWorkersSecondOther() {
        return workersSecondOther;
    }
    public void setWorkersSecondOther(Integer workersSecondOther) {
        this.workersSecondOther = workersSecondOther;
    }
    public ZonedDateTime getWorkersSecondLastReportedTime() {
        return workersSecondLastReportedTime;
    }
    public void setWorkersSecondLastReportedTime(ZonedDateTime workersSecondLastReportedTime) {
        this.workersSecondLastReportedTime = workersSecondLastReportedTime;
        this.prettyWorkersSecondLastReportedTime = PrettyZonedDateTimeFormatter.format(workersSecondLastReportedTime);
    }
    public Integer getWorkersThirdHealth() {
        return workersThirdHealth;
    }
    public void setWorkersThirdHealth(Integer workersThirdHealth) {
        this.workersThirdHealth = workersThirdHealth;
    }
    public Integer getWorkersThirdMental() {
        return workersThirdMental;
    }
    public void setWorkersThirdMental(Integer workersThirdMental) {
        this.workersThirdMental = workersThirdMental;
    }
    public Integer getWorkersThirdSpiritual() {
        return workersThirdSpiritual;
    }
    public void setWorkersThirdSpiritual(Integer workersThirdSpiritual) {
        this.workersThirdSpiritual = workersThirdSpiritual;
    }
    public Integer getWorkersThirdCaseWorker() {
        return workersThirdCaseWorker;
    }
    public void setWorkersThirdCaseWorker(Integer workersThirdCaseWorker) {
        this.workersThirdCaseWorker = workersThirdCaseWorker;
    }
    public Integer getWorkersThirdFeeding() {
        return workersThirdFeeding;
    }
    public void setWorkersThirdFeeding(Integer workersThirdFeeding) {
        this.workersThirdFeeding = workersThirdFeeding;
    }
    public Integer getWorkersThirdOther() {
        return workersThirdOther;
    }
    public void setWorkersThirdOther(Integer workersThirdOther) {
        this.workersThirdOther = workersThirdOther;
    }
    public ZonedDateTime getWorkersThirdLastReportedTime() {
        return workersThirdLastReportedTime;
    }
    public void setWorkersThirdLastReportedTime(ZonedDateTime workersThirdLastReportedTime) {
        this.workersThirdLastReportedTime = workersThirdLastReportedTime;
        this.prettyWorkersThirdLastReportedTime = PrettyZonedDateTimeFormatter.format(workersThirdLastReportedTime);
    }
    public String getPopulationLastReportedTimeDelta() {
        return populationLastReportedTimeDelta;
    }
    public void setPopulationLastReportedTimeDelta(String populationLastReportedTimeDelta) {
        this.populationLastReportedTimeDelta = populationLastReportedTimeDelta;
    }
    public String getFoodTodayLastReportedTimeDelta() {
        return foodTodayLastReportedTimeDelta;
    }
    public void setFoodTodayLastReportedTimeDelta(String foodTodayLastReportedTimeDelta) {
        this.foodTodayLastReportedTimeDelta = foodTodayLastReportedTimeDelta;
    }
    public String getFoodTomorrowLastReportedTimeDelta() {
        return foodTomorrowLastReportedTimeDelta;
    }
    public void setFoodTomorrowLastReportedTimeDelta(String foodTomorrowLastReportedTimeDelta) {
        this.foodTomorrowLastReportedTimeDelta = foodTomorrowLastReportedTimeDelta;
    }
    public String getFoodNeededLastReportedTimeDelta() {
        return foodNeededLastReportedTimeDelta;
    }
    public void setFoodNeededLastReportedTimeDelta(String foodNeededLastReportedTimeDelta) {
        this.foodNeededLastReportedTimeDelta = foodNeededLastReportedTimeDelta;
    }
    public String getMaterielTodayLastReportedTimeDelta() {
        return materielTodayLastReportedTimeDelta;
    }
    public void setMaterielTodayLastReportedTimeDelta(String materielTodayLastReportedTimeDelta) {
        this.materielTodayLastReportedTimeDelta = materielTodayLastReportedTimeDelta;
    }
    public String getMaterielTomorrowLastReportedTimeDelta() {
        return materielTomorrowLastReportedTimeDelta;
    }
    public void setMaterielTomorrowLastReportedTimeDelta(String materielTomorrowLastReportedTimeDelta) {
        this.materielTomorrowLastReportedTimeDelta = materielTomorrowLastReportedTimeDelta;
    }
    public String getMaterielNeededLastReportedTimeDelta() {
        return materielNeededLastReportedTimeDelta;
    }
    public void setMaterielNeededLastReportedTimeDelta(String materielNeededLastReportedTimeDelta) {
        this.materielNeededLastReportedTimeDelta = materielNeededLastReportedTimeDelta;
    }
    public String getWorkersFirstLastReportedTimeDelta() {
        return workersFirstLastReportedTimeDelta;
    }
    public void setWorkersFirstLastReportedTimeDelta(String workersFirstLastReportedTimeDelta) {
        this.workersFirstLastReportedTimeDelta = workersFirstLastReportedTimeDelta;
    }
    public String getWorkersSecondLastReportedTimeDelta() {
        return workersSecondLastReportedTimeDelta;
    }
    public void setWorkersSecondLastReportedTimeDelta(String workersSecondLastReportedTimeDelta) {
        this.workersSecondLastReportedTimeDelta = workersSecondLastReportedTimeDelta;
    }
    public String getWorkersThirdLastReportedTimeDelta() {
        return workersThirdLastReportedTimeDelta;
    }
    public void setWorkersThirdLastReportedTimeDelta(String workersThirdLastReportedTimeDelta) {
        this.workersThirdLastReportedTimeDelta = workersThirdLastReportedTimeDelta;
    }
    public ObjectType getType() {
        return type;
    }
    public void setType(ObjectType type) {
        this.type = type;
    }
    public String getPrettyPopulationLastReportedTime() {
        return prettyPopulationLastReportedTime;
    }
    public void setPrettyPopulationLastReportedTime(String prettyPopulationLastReportedTime) {
        this.prettyPopulationLastReportedTime = prettyPopulationLastReportedTime;
    }
    public String getPrettyFoodTodayLastReportedTime() {
        return prettyFoodTodayLastReportedTime;
    }
    public void setPrettyFoodTodayLastReportedTime(String prettyFoodTodayLastReportedTime) {
        this.prettyFoodTodayLastReportedTime = prettyFoodTodayLastReportedTime;
    }
    public String getPrettyFoodTomorrowLastReportedTime() {
        return prettyFoodTomorrowLastReportedTime;
    }
    public void setPrettyFoodTomorrowLastReportedTime(String prettyFoodTomorrowLastReportedTime) {
        this.prettyFoodTomorrowLastReportedTime = prettyFoodTomorrowLastReportedTime;
    }
    public String getPrettyFoodNeededLastReportedTime() {
        return prettyFoodNeededLastReportedTime;
    }
    public void setPrettyFoodNeededLastReportedTime(String prettyFoodNeededLastReportedTime) {
        this.prettyFoodNeededLastReportedTime = prettyFoodNeededLastReportedTime;
    }
    public String getPrettyMaterielTodayLastReportedTime() {
        return prettyMaterielTodayLastReportedTime;
    }
    public void setPrettyMaterielTodayLastReportedTime(String prettyMaterielTodayLastReportedTime) {
        this.prettyMaterielTodayLastReportedTime = prettyMaterielTodayLastReportedTime;
    }
    public String getPrettyMaterielTomorrowLastReportedTime() {
        return prettyMaterielTomorrowLastReportedTime;
    }
    public void setPrettyMaterielTomorrowLastReportedTime(String prettyMaterielTomorrowLastReportedTime) {
        this.prettyMaterielTomorrowLastReportedTime = prettyMaterielTomorrowLastReportedTime;
    }
    public String getPrettyMaterielNeededLastReportedTime() {
        return prettyMaterielNeededLastReportedTime;
    }
    public void setPrettyMaterielNeededLastReportedTime(String prettyMaterielNeededLastReportedTime) {
        this.prettyMaterielNeededLastReportedTime = prettyMaterielNeededLastReportedTime;
    }
    public String getPrettyWorkersFirstLastReportedTime() {
        return prettyWorkersFirstLastReportedTime;
    }
    public void setPrettyWorkersFirstLastReportedTime(String prettyWorkersFirstLastReportedTime) {
        this.prettyWorkersFirstLastReportedTime = prettyWorkersFirstLastReportedTime;
    }
    public String getPrettyWorkersSecondLastReportedTime() {
        return prettyWorkersSecondLastReportedTime;
    }
    public void setPrettyWorkersSecondLastReportedTime(String prettyWorkersSecondLastReportedTime) {
        this.prettyWorkersSecondLastReportedTime = prettyWorkersSecondLastReportedTime;
    }
    public String getPrettyWorkersThirdLastReportedTime() {
        return prettyWorkersThirdLastReportedTime;
    }
    public void setPrettyWorkersThirdLastReportedTime(String prettyWorkersThirdLastReportedTime) {
        this.prettyWorkersThirdLastReportedTime = prettyWorkersThirdLastReportedTime;
    }
}

