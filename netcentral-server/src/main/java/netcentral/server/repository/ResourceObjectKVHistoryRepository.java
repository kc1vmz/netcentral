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
import netcentral.server.record.ResourceObjectKVHistoryRecord;

@Singleton
public class ResourceObjectKVHistoryRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.ResourceObjectKVHistoryRepository resourceObjectKVHistoryRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.ResourceObjectKVHistoryRepository resourceObjectKVHistoryRepositoryH2;

    public ResourceObjectKVHistoryRecord save(ResourceObjectKVHistoryRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return resourceObjectKVHistoryRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return resourceObjectKVHistoryRepositoryH2.save(record);
        }
        return null;
    }
    public ResourceObjectKVHistoryRecord update(ResourceObjectKVHistoryRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return resourceObjectKVHistoryRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return resourceObjectKVHistoryRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            resourceObjectKVHistoryRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            resourceObjectKVHistoryRepositoryH2.deleteAll();
        }
    }
    public List<ResourceObjectKVHistoryRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return resourceObjectKVHistoryRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return resourceObjectKVHistoryRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(ResourceObjectKVHistoryRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            resourceObjectKVHistoryRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            resourceObjectKVHistoryRepositoryH2.delete(record);
        }
    }
    public Optional<ResourceObjectKVHistoryRecord> findById(String id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return resourceObjectKVHistoryRepositoryMySQL.findById(id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return resourceObjectKVHistoryRepositoryH2.findById(id);
        }
        return Optional.empty();
    }
    public List<ResourceObjectKVHistoryRecord> findByaprs_object_id(String aprs_object_id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return resourceObjectKVHistoryRepositoryMySQL.findByaprs_object_id(aprs_object_id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return resourceObjectKVHistoryRepositoryH2.findByaprs_object_id(aprs_object_id);
        }
        return new ArrayList<>();
    }
}