package netcentral.server.accessor;

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
import netcentral.server.object.Net;
import netcentral.server.object.User;

@Singleton
public class NetReportAccessor {
    private boolean stop = false;

    @Inject
    private NetAccessor netAccessor;
    @Inject
    private FederatedObjectReporterAccessor federatedObjectReporterAccessor;

    public void sendReports() {
        if (!stayRunning()) {
            return;
        }
        User user = new User();

        // send net announcement bulletins and reports
        List <Net> nets = netAccessor.getAll(user, null);
        if (!nets.isEmpty()) {
            for (Net net : nets) {
                federatedObjectReporterAccessor.announce(user, net);
                federatedObjectReporterAccessor.announceStart(user, net);
            }
        }
    }

    public boolean stayRunning() {
        return !stop;
    }
    public void shutdown() {
        stop = true;
    }
}
