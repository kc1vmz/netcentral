package netcentral.server.repository.aprs;

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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.DatabaseConfiguration;
import netcentral.server.record.aprs.APRSMessageRecord;

@Singleton
public class APRSMessageRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.aprs.APRSMessageRepository aprsMessageRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.aprs.APRSMessageRepository aprsMessageRepositoryH2;

    public APRSMessageRecord save(APRSMessageRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return aprsMessageRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return aprsMessageRepositoryH2.save(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            aprsMessageRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            aprsMessageRepositoryH2.deleteAll();
        }
    }
    public List<APRSMessageRecord> findBycallsign_from(String callsign_from) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return aprsMessageRepositoryMySQL.findBycallsign_from(callsign_from);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return aprsMessageRepositoryH2.findBycallsign_from(callsign_from);
        }
        return new ArrayList<>();
    }
    public List<APRSMessageRecord> findBycallsign_to(String callsign_to) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return aprsMessageRepositoryMySQL.findBycallsign_to(callsign_to);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return aprsMessageRepositoryH2.findBycallsign_to(callsign_to);
        }
        return new ArrayList<>();
    }
    public List<APRSMessageRecord> findBycompleted_net_id(String completed_net_id) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return aprsMessageRepositoryMySQL.findBycompleted_net_id(completed_net_id);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return aprsMessageRepositoryH2.findBycompleted_net_id(completed_net_id);
        }
        return new ArrayList<>();
    }
    public void deleteByHeard_timeBefore(ZonedDateTime heard_time) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            aprsMessageRepositoryMySQL.deleteByHeard_timeBefore(heard_time);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            aprsMessageRepositoryH2.deleteByHeard_timeBefore(heard_time);
        }
    }
}

