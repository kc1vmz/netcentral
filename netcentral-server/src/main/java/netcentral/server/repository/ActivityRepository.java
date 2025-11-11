package netcentral.server.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.ActivityRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface ActivityRepository extends CrudRepository<ActivityRecord, String> { 
}