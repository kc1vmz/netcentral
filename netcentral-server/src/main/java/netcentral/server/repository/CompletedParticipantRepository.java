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
import netcentral.server.record.CompletedParticipantRecord;

@Singleton
public class CompletedParticipantRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.CompletedParticipantRepository completedParticipantRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.CompletedParticipantRepository completedParticipantRepositoryH2;

    public CompletedParticipantRecord save(CompletedParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedParticipantRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedParticipantRepositoryH2.save(record);
        }
        return null;
    }
    public CompletedParticipantRecord update(CompletedParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedParticipantRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedParticipantRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            completedParticipantRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            completedParticipantRepositoryH2.deleteAll();
        }
    }
    public List<CompletedParticipantRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedParticipantRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedParticipantRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(CompletedParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            completedParticipantRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            completedParticipantRepositoryH2.delete(record);
        }
    }
    public Optional<CompletedParticipantRecord> findById(String id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedParticipantRepositoryMySQL.findById(id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedParticipantRepositoryH2.findById(id);
        }
        return Optional.empty();
    }
    public List<CompletedParticipantRecord> findBycallsign(String callsign) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedParticipantRepositoryMySQL.findBycallsign(callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedParticipantRepositoryH2.findBycallsign(callsign);
        }
        return new ArrayList<>();
    }
    public List<CompletedParticipantRecord> findBycompleted_net_id(String completed_net_id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return completedParticipantRepositoryMySQL.findBycompleted_net_id(completed_net_id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return completedParticipantRepositoryH2.findBycompleted_net_id(completed_net_id);
        }
        return new ArrayList<>();
    }
}
