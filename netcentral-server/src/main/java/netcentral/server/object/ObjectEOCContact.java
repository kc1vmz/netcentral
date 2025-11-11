package netcentral.server.object;


public class ObjectEOCContact {
    private String directorName;
    private String incidentCommanderName;

    public ObjectEOCContact() {
    }
    public ObjectEOCContact(String directorName, String incidentCommanderName) {
        setDirectorName(directorName);
        setIncidentCommanderName(incidentCommanderName);
    }
    public String getDirectorName() {
        return directorName;
    }
    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }
    public String getIncidentCommanderName() {
        return incidentCommanderName;
    }
    public void setIncidentCommanderName(String incidentCommanderName) {
        this.incidentCommanderName = incidentCommanderName;
    }
}
