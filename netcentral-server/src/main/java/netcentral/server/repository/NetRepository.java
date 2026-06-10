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
import netcentral.server.record.NetRecord;

@Singleton
public class NetRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.NetRepository netRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.NetRepository netRepositoryH2;

    public NetRecord save(NetRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netRepositoryH2.save(record);
        }
        return null;
    }
    public NetRecord update(NetRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netRepositoryH2.deleteAll();
        }
    }
    public List<NetRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(NetRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netRepositoryH2.delete(record);
        }
    }
    public Optional<NetRecord> findById(String id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netRepositoryMySQL.findById(id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netRepositoryH2.findById(id);
        }
        return Optional.empty();
    }
}