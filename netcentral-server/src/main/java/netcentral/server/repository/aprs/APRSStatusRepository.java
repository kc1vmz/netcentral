package netcentral.server.repository.aprs;

import java.time.ZonedDateTime;
import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.aprs.APRSStatusRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface APRSStatusRepository extends CrudRepository<APRSStatusRecord, String> { 
    public List<APRSStatusRecord> findBycallsign_from(String callsign_from);
    public void deleteByHeard_timeBefore(ZonedDateTime heard_time);
}