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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetAnnounceReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetStartReport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.NetCentralServerConfig;
import netcentral.server.object.Net;
import netcentral.server.object.User;

@Singleton
public class NetReportAccessor {
    private static final Logger logger = LogManager.getLogger(NetReportAccessor.class);
    private boolean stop = false;

    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    @Inject
    private NetAccessor netAccessor;
    @Inject
    private NetCentralServerConfig netConfigServerConfig; 

    public void sendReports() {
        if (!stayRunning()) {
            return;
        }
        User user = new User();

        if (netConfigServerConfig.isFederated()) {
            // send net announcement bulletins and reports
            List <Net> nets = netAccessor.getAll(user, null);
            if (!nets.isEmpty()) {
                for (Net net : nets) {
                    if (net.isRemote()) {
                        continue;
                    }

                    try {
                        APRSNetCentralNetAnnounceReport reportAnnounce = new APRSNetCentralNetAnnounceReport(net.getCallsign(), net.getName(), net.getDescription());
                        transceiverMessageAccessor.sendReport(user, reportAnnounce);
                        APRSNetCentralNetStartReport reportStart = new APRSNetCentralNetStartReport(net.getCallsign(), net.getStartTime());
                        transceiverMessageAccessor.sendReport(user, reportStart);
                    } catch (Exception e) {
                        logger.debug("Exception caught reporting nets", e);
                    }
                }
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
