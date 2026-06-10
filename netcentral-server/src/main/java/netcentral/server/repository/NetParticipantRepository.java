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
import netcentral.server.record.NetParticipantRecord;

@Singleton
public class NetParticipantRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.NetParticipantRepository netParticipantRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.NetParticipantRepository netParticipantRepositoryH2;

    public NetParticipantRecord save(NetParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netParticipantRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netParticipantRepositoryH2.save(record);
        }
        return null;
    }
    public NetParticipantRecord update(NetParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netParticipantRepositoryMySQL.update(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netParticipantRepositoryH2.update(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netParticipantRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netParticipantRepositoryH2.deleteAll();
        }
    }
    public List<NetParticipantRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netParticipantRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netParticipantRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public void delete(NetParticipantRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            netParticipantRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            netParticipantRepositoryH2.delete(record);
        }
    }
    public List<NetParticipantRecord> findBynet_callsign(String net_callsign) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netParticipantRepositoryMySQL.findBynet_callsign(net_callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netParticipantRepositoryH2.findBynet_callsign(net_callsign);
        }
        return new ArrayList<>();
    }
    public List<NetParticipantRecord> findByparticipant_callsign(String participant_callsign) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netParticipantRepositoryMySQL.findByparticipant_callsign(participant_callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netParticipantRepositoryH2.findByparticipant_callsign(participant_callsign);
        }
        return new ArrayList<>();
    }
    public Optional<NetParticipantRecord> findById(String id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netParticipantRepositoryMySQL.findById(id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netParticipantRepositoryH2.findById(id);
        }
        return Optional.empty();
    }
    public NetParticipantRecord find(String net_callsign, String participant_callsign) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return netParticipantRepositoryMySQL.find(net_callsign, participant_callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return netParticipantRepositoryH2.find(net_callsign, participant_callsign);
        }
        return null;
    }
}