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
import netcentral.server.record.CompletedExpectedParticipantRecord;

@Singleton
public class CompletedExpectedParticipantRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.CompletedExpectedParticipantRepository completedExpectedParticipantRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.CompletedExpectedParticipantRepository completedExpectedParticipantRepositoryH2;

    public CompletedExpectedParticipantRecord save(CompletedExpectedParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedExpectedParticipantRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedExpectedParticipantRepositoryH2.save(record);
        }
        return null;
    }
    public CompletedExpectedParticipantRecord update(CompletedExpectedParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedExpectedParticipantRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedExpectedParticipantRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            completedExpectedParticipantRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            completedExpectedParticipantRepositoryH2.deleteAll();
        }
    }
    public List<CompletedExpectedParticipantRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedExpectedParticipantRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedExpectedParticipantRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(CompletedExpectedParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            completedExpectedParticipantRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            completedExpectedParticipantRepositoryH2.delete(record);
        }
    }
    public List<CompletedExpectedParticipantRecord> findBycallsign(String callsign) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedExpectedParticipantRepositoryMySQL.findBycallsign(callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedExpectedParticipantRepositoryH2.findBycallsign(callsign);
        }
        return new ArrayList<>();
    }
    public List<CompletedExpectedParticipantRecord> findBycompleted_net_id(String completed_net_id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedExpectedParticipantRepositoryMySQL.findBycompleted_net_id(completed_net_id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedExpectedParticipantRepositoryH2.findBycompleted_net_id(completed_net_id);
        }
        return new ArrayList<>();
    }
    public Optional<CompletedExpectedParticipantRecord> findById(String id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedExpectedParticipantRepositoryMySQL.findById(id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedExpectedParticipantRepositoryH2.findById(id);
        }
        return Optional.empty();
    }
}
