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
import netcentral.server.record.report.ShelterStatusReportRecord;

@Singleton
public class ShelterStatusReportRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.report.ShelterStatusReportRepository shelterStatusReportRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.report.ShelterStatusReportRepository shelterStatusReportRepositoryH2;

    public ShelterStatusReportRecord save(ShelterStatusReportRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return shelterStatusReportRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return shelterStatusReportRepositoryH2.save(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            shelterStatusReportRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            shelterStatusReportRepositoryH2.deleteAll();
        }
    }
    public List<ShelterStatusReportRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return shelterStatusReportRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return shelterStatusReportRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public List<ShelterStatusReportRecord> findBycallsign(String callsign){
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return shelterStatusReportRepositoryMySQL.findBycallsign(callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return shelterStatusReportRepositoryH2.findBycallsign(callsign);
        }
        return new ArrayList<>();
    }
    public void deleteByReported_date(ZonedDateTime heard_time) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            shelterStatusReportRepositoryMySQL.deleteByReported_date(heard_time);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            shelterStatusReportRepositoryH2.deleteByReported_date(heard_time);
        }
    }
    public void delete(ShelterStatusReportRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            shelterStatusReportRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            shelterStatusReportRepositoryH2.delete(record);
        }
    }
}
