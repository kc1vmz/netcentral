package netcentral.server.repository;

import java.util.List;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.NetParticipantRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface NetParticipantRepository extends CrudRepository<NetParticipantRecord, String> { 
    public List<NetParticipantRecord> findBynet_callsign(String net_callsign);
    public List<NetParticipantRecord> findByparticipant_callsign(String participant_callsign);

    NetParticipantRecord find(@NonNull String net_callsign, @NonNull String participant_callsign);
}