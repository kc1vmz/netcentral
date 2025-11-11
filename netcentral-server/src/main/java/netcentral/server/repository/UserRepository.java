package netcentral.server.repository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.UserRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface UserRepository extends CrudRepository<UserRecord, String> { 
    UserRecord find(@NonNull String username);
}