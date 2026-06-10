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
import netcentral.server.record.aprs.APRSWeatherReportRecord;

@Singleton
public class APRSWeatherReportRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.aprs.APRSWeatherReportRepository aprsWeatherReportRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.aprs.APRSWeatherReportRepository aprsWeatherReportRepositoryH2;

    public APRSWeatherReportRecord save(APRSWeatherReportRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return aprsWeatherReportRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return aprsWeatherReportRepositoryH2.save(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            aprsWeatherReportRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            aprsWeatherReportRepositoryH2.deleteAll();
        }
    }
    public List<APRSWeatherReportRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return aprsWeatherReportRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return aprsWeatherReportRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public List<APRSWeatherReportRecord> findBycallsign_from(String callsign_from) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return aprsWeatherReportRepositoryMySQL.findBycallsign_from(callsign_from);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return aprsWeatherReportRepositoryH2.findBycallsign_from(callsign_from);
        }
        return new ArrayList<>();
    }
    public void deleteByHeard_timeBefore(ZonedDateTime heard_time) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            aprsWeatherReportRepositoryMySQL.deleteByHeard_timeBefore(heard_time);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            aprsWeatherReportRepositoryH2.deleteByHeard_timeBefore(heard_time);
        }
    }
    public void delete(APRSWeatherReportRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            aprsWeatherReportRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            aprsWeatherReportRepositoryH2.delete(record);
        }
    }
}