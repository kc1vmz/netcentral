package netcentral.server.repository.report;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.report.ShelterOperationalFoodReportRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface ShelterOperationalFoodReportRepository extends CrudRepository<ShelterOperationalFoodReportRecord, String> { 
}