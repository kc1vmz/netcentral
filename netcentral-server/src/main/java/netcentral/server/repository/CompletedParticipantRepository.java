package netcentral.server.repository;

import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.CompletedParticipantRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface CompletedParticipantRepository extends CrudRepository<CompletedParticipantRecord, String> { 
        public List<CompletedParticipantRecord> findBycallsign(String callsign);
        public List<CompletedParticipantRecord> findBycompleted_net_id(String completed_net_id);

}