package netcentral.server.repository;

import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.NetQuestionAnswerRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface NetQuestionAnswerRepository extends CrudRepository<NetQuestionAnswerRecord, String> { 
        public List<NetQuestionAnswerRecord> findBynet_question_id(String net_question_id);
}