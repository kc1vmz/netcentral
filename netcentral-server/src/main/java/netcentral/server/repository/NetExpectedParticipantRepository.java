package netcentral.server.repository;

import java.util.List;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.ExpectedParticipantRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface NetExpectedParticipantRepository extends CrudRepository<ExpectedParticipantRecord, String> { 
    public List<ExpectedParticipantRecord> findBynet_callsign(String net_callsign);
    public List<ExpectedParticipantRecord> findBycallsign(String callsign);
    public List<ExpectedParticipantRecord> findBycompleted_net_id(String completed_net_id);

    ExpectedParticipantRecord find(@NonNull String net_callsign, @NonNull String callsign);
}