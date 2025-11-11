package netcentral.server.repository;

import java.util.List;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.CallsignAceRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface CallsignAceRepository extends CrudRepository<CallsignAceRecord, String> { 
    public List<CallsignAceRecord> findBycallsign_target(String callsign_target);
    CallsignAceRecord find(@NonNull String callsign_target, @NonNull String callsign_checked);
}