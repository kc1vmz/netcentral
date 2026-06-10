package netcentral.server.repository.report;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.DatabaseConfiguration;
import netcentral.server.record.report.EOCContactReportRecord;

@Singleton
public class EOCContactReportRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.report.EOCContactReportRepository eocContactReportRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.report.EOCContactReportRepository eocContactReportRepositoryH2;

    public EOCContactReportRecord save(EOCContactReportRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return eocContactReportRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return eocContactReportRepositoryH2.save(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            eocContactReportRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            eocContactReportRepositoryH2.deleteAll();
        }
    }
    public List<EOCContactReportRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return eocContactReportRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return eocContactReportRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public List<EOCContactReportRecord> findBycallsign(String callsign){
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return eocContactReportRepositoryMySQL.findBycallsign(callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return eocContactReportRepositoryH2.findBycallsign(callsign);
        }
        return new ArrayList<>();
    }
    public void deleteByReported_date(ZonedDateTime heard_time) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            eocContactReportRepositoryMySQL.deleteByReported_date(heard_time);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            eocContactReportRepositoryH2.deleteByReported_date(heard_time);
        }
    }
    public void delete(EOCContactReportRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            eocContactReportRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            eocContactReportRepositoryH2.delete(record);
        }
    }
}
