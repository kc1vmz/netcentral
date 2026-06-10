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
import netcentral.server.record.NetMessageRecord;

@Singleton
public class NetMessageRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.NetMessageRepository netMessageRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.NetMessageRepository netMessageRepositoryH2;

    public NetMessageRecord save(NetMessageRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netMessageRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netMessageRepositoryH2.save(record);
        }
        return null;
    }
    public NetMessageRecord update(NetMessageRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netMessageRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netMessageRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netMessageRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netMessageRepositoryH2.deleteAll();
        }
    }
    public List<NetMessageRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netMessageRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netMessageRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(NetMessageRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netMessageRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netMessageRepositoryH2.delete(record);
        }
    }
    public List<NetMessageRecord> findBycompleted_net_id(String completed_net_id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netMessageRepositoryMySQL.findBycompleted_net_id(completed_net_id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netMessageRepositoryH2.findBycompleted_net_id(completed_net_id);
        }
        return new ArrayList<>();
    }
    public Optional<NetMessageRecord> findById(String id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netMessageRepositoryMySQL.findById(id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netMessageRepositoryH2.findById(id);
        }
        return Optional.empty();
    }
}