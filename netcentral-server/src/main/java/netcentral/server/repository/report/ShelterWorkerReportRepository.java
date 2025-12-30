package netcentral.server.repository.report;

import java.time.ZonedDateTime;
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

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import netcentral.server.record.report.ShelterWorkerReportRecord;

@JdbcRepository(dialect = Dialect.MYSQL) 
public interface ShelterWorkerReportRepository extends CrudRepository<ShelterWorkerReportRecord, String> { 
    public List<ShelterWorkerReportRecord> findBycallsign(String callsign);
    public void deleteByReported_date(ZonedDateTime reported_date);
}