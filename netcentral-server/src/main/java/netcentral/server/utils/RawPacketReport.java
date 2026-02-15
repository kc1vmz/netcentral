package netcentral.server.utils;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSRaw;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.accessor.NetCentralServerConfigAccessor;

@Singleton
public class RawPacketReport {
    @Inject
    private NetCentralServerConfigAccessor netCentralServerConfigAccessor;

    private static final String NEW_LINE_SEPARATOR = "\n";

    private static final Logger logger = LogManager.getLogger(RawPacketReport.class);

    public String createReport(List<APRSRaw> rawPackets) throws FileNotFoundException {
        String filename = getUniqueFileName(netCentralServerConfigAccessor.getTempReportDir(), "txt");

        try (FileWriter fileWriter = new FileWriter(filename)) {
            // write the packets
            for (APRSRaw rawPacket : rawPackets) {
                fileWriter.append(rawPacket.getPrettyHeardTime()+" "+new String(rawPacket.getData())+NEW_LINE_SEPARATOR);
            }
            return filename.substring(netCentralServerConfigAccessor.getTempReportDir().length());
        } catch (Exception e) {
            logger.error("Exception caught creating raw packet report", e);
        }

        return null;
    }

    public String getUniqueFileName(String directory, String extension) {
        String fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), extension.trim());
        return Paths.get(directory, fileName).toString();
    }

}
