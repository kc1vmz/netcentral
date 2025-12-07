package netcentral.server.repository;

import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.CompletedExpectedParticipantRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface CompletedExpectedParticipantRepository extends CrudRepository<CompletedExpectedParticipantRecord, String> { 
        public List<CompletedExpectedParticipantRecord> findBycallsign(String callsign);
        public List<CompletedExpectedParticipantRecord> findBycompleted_net_id(String completed_net_id);
}