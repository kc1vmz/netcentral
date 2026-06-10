package netcentral.server.repository;

import java.util.ArrayList;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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
import netcentral.server.record.TrackedStationTypeRuleRecord;

@Singleton
public class TrackedStationTypeRuleRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.TrackedStationTypeRuleRepository trackedStationTypeRuleRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.TrackedStationTypeRuleRepository trackedStationTypeRuleRepositoryH2;

    public TrackedStationTypeRuleRecord save(TrackedStationTypeRuleRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return trackedStationTypeRuleRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return trackedStationTypeRuleRepositoryH2.save(record);
        }
        return null;
    }
    public TrackedStationTypeRuleRecord update(TrackedStationTypeRuleRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return trackedStationTypeRuleRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return trackedStationTypeRuleRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            trackedStationTypeRuleRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            trackedStationTypeRuleRepositoryH2.deleteAll();
        }
    }
    public List<TrackedStationTypeRuleRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return trackedStationTypeRuleRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return trackedStationTypeRuleRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(TrackedStationTypeRuleRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            trackedStationTypeRuleRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
           trackedStationTypeRuleRepositoryH2.delete(record);
        }
    }
    public Optional<TrackedStationTypeRuleRecord> findById(String id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return trackedStationTypeRuleRepositoryMySQL.findById(id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return trackedStationTypeRuleRepositoryH2.findById(id);
        }
        return Optional.empty();
    }
}