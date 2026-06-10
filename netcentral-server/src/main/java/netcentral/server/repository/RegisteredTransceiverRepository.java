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
import netcentral.server.record.RegisteredTransceiverRecord;

@Singleton
public class RegisteredTransceiverRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.RegisteredTransceiverRepository registeredTransceiverRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.RegisteredTransceiverRepository registeredTransceiverRepositoryH2;

    public RegisteredTransceiverRecord save(RegisteredTransceiverRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return registeredTransceiverRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return registeredTransceiverRepositoryH2.save(record);
        }
        return null;
    }
    public RegisteredTransceiverRecord update(RegisteredTransceiverRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return registeredTransceiverRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return registeredTransceiverRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            registeredTransceiverRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            registeredTransceiverRepositoryH2.deleteAll();
        }
    }
    public List<RegisteredTransceiverRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return registeredTransceiverRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return registeredTransceiverRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(RegisteredTransceiverRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            registeredTransceiverRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            registeredTransceiverRepositoryH2.delete(record);
        }
    }
    public Optional<RegisteredTransceiverRecord> findById(String id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return registeredTransceiverRepositoryMySQL.findById(id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return registeredTransceiverRepositoryH2.findById(id);
        }
        return Optional.empty();
    }
    public List<RegisteredTransceiverRecord> findByfqd_name(String fqd_name) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return registeredTransceiverRepositoryMySQL.findByfqd_name(fqd_name);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return registeredTransceiverRepositoryH2.findByfqd_name(fqd_name);
        }
        return new ArrayList<>();
    }
    public List<RegisteredTransceiverRecord> findByname(String name) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return registeredTransceiverRepositoryMySQL.findByname(name);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return registeredTransceiverRepositoryH2.findByname(name);
        }
        return new ArrayList<>();
    }
}