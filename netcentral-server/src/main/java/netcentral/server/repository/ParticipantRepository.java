package netcentral.server.repository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.ParticipantRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface ParticipantRepository extends CrudRepository<ParticipantRecord, String> { 
    ParticipantRecord find(@NonNull String callsign);
}