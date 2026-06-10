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
import netcentral.server.record.report.ShelterOperationalFoodReportRecord;

@Singleton
public class ShelterOperationalFoodReportRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.report.ShelterOperationalFoodReportRepository shelterOperationalFoodReportRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.report.ShelterOperationalFoodReportRepository shelterOperationalFoodReportRepositoryH2;

    public ShelterOperationalFoodReportRecord save(ShelterOperationalFoodReportRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return shelterOperationalFoodReportRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return shelterOperationalFoodReportRepositoryH2.save(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            shelterOperationalFoodReportRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            shelterOperationalFoodReportRepositoryH2.deleteAll();
        }
    }
    public List<ShelterOperationalFoodReportRecord> findAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return shelterOperationalFoodReportRepositoryMySQL.findAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return shelterOperationalFoodReportRepositoryH2.findAll();
        }
        return new ArrayList<>();
    }
    public List<ShelterOperationalFoodReportRecord> findBycallsign(String callsign){
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return shelterOperationalFoodReportRepositoryMySQL.findBycallsign(callsign);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return shelterOperationalFoodReportRepositoryH2.findBycallsign(callsign);
        }
        return new ArrayList<>();
    }
    public void deleteByReported_date(ZonedDateTime heard_time) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            shelterOperationalFoodReportRepositoryMySQL.deleteByReported_date(heard_time);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            shelterOperationalFoodReportRepositoryH2.deleteByReported_date(heard_time);
        }
    }
    public void delete(ShelterOperationalFoodReportRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            shelterOperationalFoodReportRepositoryMySQL.delete(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            shelterOperationalFoodReportRepositoryH2.delete(record);
        }
    }
}
