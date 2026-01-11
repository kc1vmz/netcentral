package netcentral.server.utils;

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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.NetCentralServerConfig;
import netcentral.server.object.CompletedExpectedParticipant;
import netcentral.server.object.CompletedNet;
import netcentral.server.object.CompletedParticipant;
import netcentral.server.object.Report309Entry;
import netcentral.server.object.SoftwareIdentity;

@Singleton
public class NetParticipantReport {
    @Inject
    private NetCentralServerConfig netConfigServerConfig;

    private static final Logger logger = LogManager.getLogger(NetParticipantReport.class);

    private final static int MAX_ROWS = 25;

    public String createReport(CompletedNet net, List<CompletedParticipant> participants,  List<CompletedExpectedParticipant> expectedParticipants) throws FileNotFoundException {
        String filename = getUniqueFileName(netConfigServerConfig.getTempReportDir(), "pdf");
        try {
            List<Report309Entry> entries = buildReportEntries(participants, expectedParticipants);

            PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));
            Document document = new Document(pdf);
            document.setMargins(30, 32, 30, 32);

            int pageCount = 1;
            if ((participants != null) && (!participants.isEmpty())) {
                // calculate number of pages
                pageCount = (participants.size() / MAX_ROWS) + 1;
            }

            for (int page = 1; page <= pageCount; page++) {
                document.add(addDocumentHeader(""));
                document.add(addInfoHeader(net, net.getName()));
                document.add(addOperatorHeader(net));
                document.add(addCommunicationsLogBanner());

                Table communicationsLogHeader = addCommunicationsLogHeader();
                addCommunicationLogRows(communicationsLogHeader, entries, page);
                document.add(communicationsLogHeader);

                document.add(addFooter(page, pageCount));
                document.add(addGenerator());
                if (page != pageCount) {
                    // do not add to last page
                    document.add(new AreaBreak());
                }
            }
            document.close();
            return filename.substring(netConfigServerConfig.getTempReportDir().length());
        } catch (Exception e) {
            logger.error("Exception caught creating Net Participant Report - ", e);
        }
        return null;
    }

    private List<Report309Entry> buildReportEntries(List<CompletedParticipant> participants, List<CompletedExpectedParticipant> expectedParticipants) {
        List<Report309Entry> ret = new ArrayList<>();

        if (participants != null) {
            for (CompletedParticipant participant : participants) {
                ret.add(new Report309Entry(participant));
            }
        }

        if (expectedParticipants != null) {
            for (CompletedExpectedParticipant expectedParticipant : expectedParticipants) {
                if (participants != null) {
                    // make sure they didnt show up before adding to list
                    boolean found = false;
                    for (CompletedParticipant participant : participants) {
                        if (expectedParticipant.getCallsign().equals(participant.getCallsign().split("-")[0])) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ret.add(new Report309Entry(expectedParticipant));
                    }
                } else {
                    ret.add(new Report309Entry(expectedParticipant));
                }
            }
        }

        Collections.sort(ret, new Comparator<Report309Entry>() {
            @Override
            public int compare(Report309Entry p1, Report309Entry p2) {
                return p1.getFrom().compareTo(p2.getFrom());
            }
        });

        return ret;
    }

    public String getUniqueFileName(String directory, String extension) {
        String fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), extension.trim());
        return Paths.get(directory, fileName).toString();
    }

    /**
     * add the document header
     *
     * @param table table to create
     * @param ctx application context
     */
    private Table addDocumentHeader(String taskId) {
        Table table = new Table(3);
        table.useAllAvailableWidth();

        Cell cell1 = new Cell().add(new Paragraph("COMMUNICATIONS LOG")).setBorder(new SolidBorder(1)).setVerticalAlignment(VerticalAlignment.MIDDLE);
        PdfUtils.makeBold(cell1);
        table.addCell(cell1);
        Cell cell2 = new Cell().add(new Paragraph("TASK # " + taskId)).setBorder(new SolidBorder(1)).setVerticalAlignment(VerticalAlignment.MIDDLE);
        PdfUtils.makeBold(cell2);
        table.addCell(cell2);

        LocalDateTime now = LocalDateTime.now();
        String dateStr = DateStrUtils.getDateStr(now);
        String timeStr = DateStrUtils.getTimeStr(now);
        Cell cell3 = new Cell().add(new Paragraph("DATE PREPARED:" + dateStr + " \n" +
                                    "TIME PREPARED: " + timeStr + "\n")).setBorder(new SolidBorder(1));
        PdfUtils.fixupFont(cell3);
        table.addCell(cell3);
        return table;
    }

    /**
     * add communications log banner
     * 
     * @return table
     */
    private Table addCommunicationsLogBanner() {
        Table table = new Table(1);

        table.useAllAvailableWidth();
        Cell cell = new Cell().add(new Paragraph("LOG")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER);
        PdfUtils.makeDarkBackground(cell);
        table.addCell(cell);
        return table;
    }

    /**
     * add communications log header
     *
     * @return table
     */
    private Table addCommunicationsLogHeader() {
        Table table = new Table(4);
        table.useAllAvailableWidth();
        Cell cell1 = new Cell().add(new Paragraph("CALLSIGN")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER);
        PdfUtils.fixupFontBold(cell1);
        table.addCell(cell1);

        Cell cell2 = new Cell().add(new Paragraph("START")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER);
        PdfUtils.fixupFontBold(cell2);
        table.addCell(cell2);

        Cell cell3 = new Cell().add(new Paragraph("END")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER);
        PdfUtils.fixupFontBold(cell3);
        table.addCell(cell3);

        Cell cell4 = new Cell().add(new Paragraph("DETAILS")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER);
        PdfUtils.fixupFontBold(cell4);
        table.addCell(cell4);

        return table;
    }

    /**
     * add info header
     *
     * @param ctx application context
     * @return table
     */
    private Table addInfoHeader(CompletedNet net, String taskName) {
        Table table = new Table(2);
        table.useAllAvailableWidth();

        String dateStr = DateStrUtils.getDateStr(net.getStartTime());
        String startTimeStr = DateStrUtils.getTimeStr(net.getStartTime());
        String endTimeStr = DateStrUtils.getTimeStr(net.getEndTime());

        String operationalPeriod = String.format("OPERATIONAL PERIOD %s from %s to %s", dateStr, startTimeStr, endTimeStr);

        Cell cell1 = new Cell().add(new Paragraph(operationalPeriod)).setBorder(new SolidBorder(1));
        PdfUtils.fixupFont(cell1);
        table.addCell(cell1);
        Cell cell2 = new Cell().add(new Paragraph("TASK NAME: " + taskName)).setBorder(new SolidBorder(1));
        PdfUtils.fixupFont(cell2);
        table.addCell(cell2);
        return table;
    }

    /**
     * add operator header
     *
     * @param ctx application context
     * @return table
     */
    private Table addOperatorHeader(CompletedNet net) {
        Table table = new Table(2);

        table.useAllAvailableWidth();
        Cell cell1 = new Cell().add(new Paragraph("RADIO OPERATOR NAME: "+net.getCreatorName())).setBorder(new SolidBorder(1));
        PdfUtils.fixupFont(cell1);
        table.addCell(cell1);
        Cell cell2 = new Cell().add(new Paragraph("STATION I.D. " + net.getCallsign())).setBorder(new SolidBorder(1));
        PdfUtils.fixupFont(cell2);
        table.addCell(cell2);
        return table;
    }

    /**
     * add communication log data rows
     *
     * @param table table to add rows to
     * @param messages list of messages to log
     * @param page current page number
     */
    private void addCommunicationLogRows(Table table, List<Report309Entry> entries, int page) {
        table.useAllAvailableWidth();

        if ((entries == null) || (entries.isEmpty()) ) {
            return;
            // need to make this paged
        }

        // skip over
        int skipCount = (page - 1) * MAX_ROWS;
        int itemCount = 0;

        for (Report309Entry entry : entries) {
            // skip to corret page of messages
            if (skipCount != 0) {
                skipCount--;
                continue;
            }

            Cell cellStartTime = new Cell().add(new Paragraph(entry.getStartTime()));
            Cell cellEndTime = new Cell().add(new Paragraph(entry.getEndTime()));
            Cell cellFrom = new Cell().add(new Paragraph(entry.getFrom()));
            Cell cellMsg = new Cell().add(new Paragraph(entry.getMessage()));
            table.addCell(cellFrom);
            table.addCell(cellStartTime);
            table.addCell(cellEndTime);
            table.addCell(cellMsg);

            itemCount++;
            if (itemCount == MAX_ROWS) {
                // limit page to MAX_ROWS rows
                break;
            }
        }

        // build blank rows
        if (itemCount < MAX_ROWS) {
            for (; itemCount < MAX_ROWS; itemCount++) {
                // add row of 4 cells
                for (int i = 0; i < 4; i++) {
                    Cell cell = new Cell();
                    cell.setHeight(16);
                    table.addCell(cell);
                }
            }
        }

    }

    /**
     * add footer
     *
     * @param ctx application context
     * @param pageCurrent current page number
     * @param pageCount total number of pages
     * @return table
     */
    private Table addFooter(int pageCurrent, int pageCount) {
        Table table = new Table(2);
        table.useAllAvailableWidth();
        table.addCell(new Cell().add(new Paragraph("Page " + pageCurrent + " of " + pageCount)).setBorder(new SolidBorder(1)));
        Cell cell = new Cell().add(new Paragraph("309 Form")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT);
        PdfUtils.makeBold(cell);
        table.addCell(cell);
        return table;
    }

    /**
     * add generator row
     *
     * @return table
     */
    private Table addGenerator() {
        Table table = new Table(1);
        table.useAllAvailableWidth();
        String text = String.format("309 communications log generated by %s %s", SoftwareIdentity.NAME, SoftwareIdentity.VERSION);
        table.addCell(new Cell().add(new Paragraph(text)).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));
        return table;
    }
}
