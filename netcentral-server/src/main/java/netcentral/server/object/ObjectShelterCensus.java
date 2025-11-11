package netcentral.server.object;

public class ObjectShelterCensus {
    private Integer population03;
    private Integer population47;
    private Integer population812;
    private Integer population1318;
    private Integer population1965;
    private Integer population66;


    public ObjectShelterCensus() {
    }
    public ObjectShelterCensus(Integer population03, Integer population47, Integer population812, Integer population1318, Integer population1965, Integer population66) {
        setPopulation03(population03);
        setPopulation47(population47);
        setPopulation812(population812);
        setPopulation1318(population1318);
        setPopulation1965(population1965);
        setPopulation66(population66);
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
}

