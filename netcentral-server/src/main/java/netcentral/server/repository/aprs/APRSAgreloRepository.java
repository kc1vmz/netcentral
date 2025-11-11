package netcentral.server.repository.aprs;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.aprs.APRSAgreloRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface APRSAgreloRepository extends CrudRepository<APRSAgreloRecord, String> { 
}