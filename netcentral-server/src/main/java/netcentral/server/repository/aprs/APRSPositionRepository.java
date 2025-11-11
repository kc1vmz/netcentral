package netcentral.server.repository.aprs;

import java.time.ZonedDateTime;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.aprs.APRSPositionRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface APRSPositionRepository extends CrudRepository<APRSPositionRecord, String> { 
    public void deleteByHeard_timeBefore(ZonedDateTime heard_time);
}