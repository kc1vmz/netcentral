package netcentral.server.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
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
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.NetConfigServerConfig;
import netcentral.server.object.CompletedNet;
import netcentral.server.object.NetMessage;
import netcentral.server.object.SoftwareIdentity;

@Singleton
public class NetMessageReport {
    @Inject
    private NetConfigServerConfig netConfigServerConfig;

    private static final Logger logger = LogManager.getLogger(NetMessageReport.class);

    private final static int MAX_ROWS = 25;

    public String createReport(CompletedNet net, List<NetMessage> messages) throws FileNotFoundException {
        String filename = getUniqueFileName(netConfigServerConfig.getTempReportDir(), "pdf");
        try {
            PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream(filename)));
            Document document = new Document(pdf);
            document.setMargins(30, 32, 30, 32);

            int pageCount = 1;
            if ((messages != null) && (!messages.isEmpty())) {
                // calculate number of pages
                pageCount = (messages.size() / MAX_ROWS) + 1;
            }

            for (int page = 1; page <= pageCount; page++) {
                document.add(addDocumentHeader(""));
                document.add(addInfoHeader(net, net.getName()));
                document.add(addOperatorHeader(net));
                document.add(addCommunicationsLogBanner());

                Table communicationsLogHeader = addCommunicationsLogHeader();
                addCommunicationLogRows(communicationsLogHeader, messages, page);
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
            logger.error("Exception caught creating Net Message Report - ", e);
        }
        return null;
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

        Cell cell1 = new Cell().add(new Paragraph("MESSAGE LOG")).setBorder(new SolidBorder(1)).setVerticalAlignment(VerticalAlignment.MIDDLE);
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
        UnitValue width = table.getWidth();
        float value = width.getValue() / 100;
        UnitValue tiny = new UnitValue(2, value*10);
        UnitValue small = new UnitValue(2, value*15);
        UnitValue large = new UnitValue(2, value*60);
        Cell cell1 = new Cell().add(new Paragraph("TIME")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER).setWidth(tiny);
        PdfUtils.fixupFontBold(cell1);
        table.addCell(cell1);

        Cell cell2 = new Cell().add(new Paragraph("FROM")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER).setWidth(small);
        PdfUtils.fixupFontBold(cell2);
        table.addCell(cell2);

        Cell cell3 = new Cell().add(new Paragraph("TO")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER).setWidth(small);
        PdfUtils.fixupFontBold(cell3);
        table.addCell(cell3);

        Cell cell4 = new Cell().add(new Paragraph("MESSAGE")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.CENTER).setWidth(large);
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
    private void addCommunicationLogRows(Table table, List<NetMessage> messages, int page) {
        table.useAllAvailableWidth();

        if ((messages == null) || (messages.isEmpty()) ) {
            return;
            // need to make this paged
        }

        // skip over
        int skipCount = (page - 1) * MAX_ROWS;
        int itemCount = 0;

        for (NetMessage message : messages) {
            // skip to corret page of messages
            if (skipCount != 0) {
                skipCount--;
                continue;
            }

            String timeStartStr = DateStrUtils.getTimeStr(message.getReceivedTime());

            Cell cellStartTime = new Cell().add(new Paragraph(timeStartStr));
            Cell cellFrom = new Cell().add(new Paragraph((String.format("%s", message.getCallsignFrom()))));
            Cell cellTo = new Cell().add(new Paragraph((String.format("%s", message.getRecipient()))));
            Cell cellMsg = new Cell().add(new Paragraph((String.format("%s", message.getMessage()))));
            table.addCell(cellStartTime);
            table.addCell(cellFrom);
            table.addCell(cellTo);
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
        Cell cell = new Cell().add(new Paragraph("Net Message Report")).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT);
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
        String text = String.format("Net Message Report generated by %s %s", SoftwareIdentity.NAME, SoftwareIdentity.VERSION);
        table.addCell(new Cell().add(new Paragraph(text)).setBorder(new SolidBorder(1)).setTextAlignment(TextAlignment.RIGHT));
        return table;
    }
}
