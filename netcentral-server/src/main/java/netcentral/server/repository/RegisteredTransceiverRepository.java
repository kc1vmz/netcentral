package netcentral.server.repository;

import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.RegisteredTransceiverRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface RegisteredTransceiverRepository extends CrudRepository<RegisteredTransceiverRecord, String> { 
            public List<RegisteredTransceiverRecord> findByfqd_name(String fqd_name);
            public List<RegisteredTransceiverRecord> findByname(String name);
}