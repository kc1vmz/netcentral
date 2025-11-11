package netcentral.server.repository;

import java.time.ZonedDateTime;
import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.TrackedStationRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface TrackedStationRepository extends CrudRepository<TrackedStationRecord, String> { 
        public List<TrackedStationRecord> findByname(String name);
        public List<TrackedStationRecord> findBycallsign(String callsign);
        public List<TrackedStationRecord> findBytype(Integer type);
        public void deleteByLast_heard_timeBefore(ZonedDateTime heard_time);
}