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


import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.DatabaseConfiguration;
import netcentral.server.record.aprs.APRSMaidenheadLocatorBeaconRecord;

@Singleton
public class APRSMaidenheadLocatorBeaconRepository {
    @Inject
    private DatabaseConfiguration databaseConfiguration;
    @Inject
    private netcentral.server.repository.mysql.aprs.APRSMaidenheadLocatorBeaconRepository aprsMaidenheadLocatorBeaconRepositoryMySQL;
    @Inject
    private netcentral.server.repository.h2.aprs.APRSMaidenheadLocatorBeaconRepository aprsMaidenheadLocatorBeaconRepositoryH2;

    public APRSMaidenheadLocatorBeaconRecord save(APRSMaidenheadLocatorBeaconRecord record) {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            return aprsMaidenheadLocatorBeaconRepositoryMySQL.save(record);
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            return aprsMaidenheadLocatorBeaconRepositoryH2.save(record);
        }
        return null;
    }
    public void deleteAll() {
        if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_MYSQL)) {
            aprsMaidenheadLocatorBeaconRepositoryMySQL.deleteAll();
        } else if (databaseConfiguration.getDialect().equals(DatabaseConfiguration.DIALECT_H2)) {
            aprsMaidenheadLocatorBeaconRepositoryH2.deleteAll();
        }
    }
}
