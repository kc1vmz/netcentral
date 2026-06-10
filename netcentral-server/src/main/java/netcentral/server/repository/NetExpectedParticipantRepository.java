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
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.DatabaseConfiguration;
import netcentral.server.record.ExpectedParticipantRecord;

@Singleton
public class NetExpectedParticipantRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.NetExpectedParticipantRepository netExpectedParticipantRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.NetExpectedParticipantRepository netExpectedParticipantRepositoryH2;

    public ExpectedParticipantRecord save(ExpectedParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netExpectedParticipantRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netExpectedParticipantRepositoryH2.save(record);
        }
        return null;
    }
    public ExpectedParticipantRecord update(ExpectedParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netExpectedParticipantRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netExpectedParticipantRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netExpectedParticipantRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netExpectedParticipantRepositoryH2.deleteAll();
        }
    }
    public List<ExpectedParticipantRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netExpectedParticipantRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netExpectedParticipantRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(ExpectedParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netExpectedParticipantRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netExpectedParticipantRepositoryH2.delete(record);
        }
    }
    public List<ExpectedParticipantRecord> findBynet_callsign(String net_callsign) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netExpectedParticipantRepositoryMySQL.findBynet_callsign(net_callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netExpectedParticipantRepositoryH2.findBynet_callsign(net_callsign);
        }
        return new ArrayList<>();
    }
    public List<ExpectedParticipantRecord> findBycallsign(String callsign) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netExpectedParticipantRepositoryMySQL.findBycallsign(callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netExpectedParticipantRepositoryH2.findBycallsign(callsign);
        }
        return new ArrayList<>();
    }
    public List<ExpectedParticipantRecord> findBycompleted_net_id(String completed_net_id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netExpectedParticipantRepositoryMySQL.findBycompleted_net_id(completed_net_id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netExpectedParticipantRepositoryH2.findBycompleted_net_id(completed_net_id);
        }
        return new ArrayList<>();
    }
    public ExpectedParticipantRecord find(String net_callsign, String callsign) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netExpectedParticipantRepositoryMySQL.find(net_callsign, callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netExpectedParticipantRepositoryH2.find(net_callsign, callsign);
        }
        return null;
    }
}