package netcentral.server.repository;

import java.util.ArrayList;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.DatabaseConfiguration;
import netcentral.server.record.NetQuestionAnswerRecord;

@Singleton
public class NetQuestionAnswerRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.NetQuestionAnswerRepository netQuestionAnswerRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.NetQuestionAnswerRepository netQuestionAnswerRepositoryH2;

    public NetQuestionAnswerRecord save(NetQuestionAnswerRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netQuestionAnswerRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netQuestionAnswerRepositoryH2.save(record);
        }
        return null;
    }
    public NetQuestionAnswerRecord update(NetQuestionAnswerRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netQuestionAnswerRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netQuestionAnswerRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netQuestionAnswerRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netQuestionAnswerRepositoryH2.deleteAll();
        }
    }
    public List<NetQuestionAnswerRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netQuestionAnswerRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netQuestionAnswerRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(NetQuestionAnswerRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netQuestionAnswerRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netQuestionAnswerRepositoryH2.delete(record);
        }
    }
    public Optional<NetQuestionAnswerRecord> findById(String id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netQuestionAnswerRepositoryMySQL.findById(id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netQuestionAnswerRepositoryH2.findById(id);
        }
        return Optional.empty();
    }
    public List<NetQuestionAnswerRecord> findBynet_question_id(String net_question_id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netQuestionAnswerRepositoryMySQL.findBynet_question_id(net_question_id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netQuestionAnswerRepositoryH2.findBynet_question_id(net_question_id);
        }
        return new ArrayList<>();
   }
}