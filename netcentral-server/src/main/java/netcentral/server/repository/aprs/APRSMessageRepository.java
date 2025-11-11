package netcentral.server.repository.aprs;

import java.time.ZonedDateTime;
import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.aprs.APRSMessageRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface APRSMessageRepository extends CrudRepository<APRSMessageRecord, String> { 
    public List<APRSMessageRecord> findBycallsign_from(String callsign_from);
    public List<APRSMessageRecord> findBycallsign_to(String callsign_to);
    public List<APRSMessageRecord> findBycompleted_net_id(String completed_net_id);
    public void deleteByHeard_timeBefore(ZonedDateTime heard_time);
}