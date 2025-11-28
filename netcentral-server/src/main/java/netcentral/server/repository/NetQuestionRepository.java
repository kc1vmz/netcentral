package netcentral.server.repository;

import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.NetQuestionRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface NetQuestionRepository extends CrudRepository<NetQuestionRecord, String> { 
        public List<NetQuestionRecord> findBycompleted_net_id(String completed_net_id);
}