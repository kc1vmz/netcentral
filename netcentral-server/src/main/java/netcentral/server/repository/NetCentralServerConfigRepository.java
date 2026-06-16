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
import netcentral.server.record.NetCentralServerConfigRecord;

@Singleton
public class NetCentralServerConfigRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.NetCentralServerConfigRepository netCentralServerConfigRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.NetCentralServerConfigRepository netCentralServerConfigRepositoryH2;

    public NetCentralServerConfigRecord save(NetCentralServerConfigRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netCentralServerConfigRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netCentralServerConfigRepositoryH2.save(record);
        }
        return null;
    }
    public NetCentralServerConfigRecord update(NetCentralServerConfigRecord record) {
        try {
            if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
                return netCentralServerConfigRepositoryMySQL.update(record);
            } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
                return netCentralServerConfigRepositoryH2.update(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netCentralServerConfigRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netCentralServerConfigRepositoryH2.deleteAll();
        }
    }
    public List<NetCentralServerConfigRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netCentralServerConfigRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netCentralServerConfigRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(NetCentralServerConfigRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netCentralServerConfigRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
           netCentralServerConfigRepositoryH2.delete(record);
        }
    }
    public Optional<NetCentralServerConfigRecord> findById(Integer config_set) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netCentralServerConfigRepositoryMySQL.findById(config_set);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netCentralServerConfigRepositoryH2.findById(config_set);
        }
        return Optional.empty();
    }
}