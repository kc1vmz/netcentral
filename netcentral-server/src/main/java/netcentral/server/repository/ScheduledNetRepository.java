package netcentral.server.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.ScheduledNetRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface ScheduledNetRepository extends CrudRepository<ScheduledNetRecord, String> { 
}