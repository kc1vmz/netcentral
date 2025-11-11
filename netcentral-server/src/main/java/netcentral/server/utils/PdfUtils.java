package netcentral.server.utils;

import java.io.IOException;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;

public class PdfUtils {
    /**
     * make text in cell bold
     * 
     * @param cell cell to act on
     */
    public static void makeBold(Cell cell) {
        PdfFont code = null;
        Style style = null;
        try {
            code = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            style = new Style()
            .setFont(code)
            .setFontSize(12)
            .setFontColor(ColorConstants.BLACK)
            .setBold()
            .setBackgroundColor(ColorConstants.WHITE);
            } catch (IOException e) {
        }

        if (style != null) {
            cell.addStyle(style);
        }
    }

    /**
     * fix up for to be correct size for report
     *
     * @param cell cell to act on
     */
    public static void fixupFont(Cell cell) {
        PdfFont code = null;
        Style style = null;
        try {
            code = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            style = new Style()
            .setFont(code)
            .setFontSize(10)
            .setFontColor(ColorConstants.BLACK)
            .setBold()
            .setBackgroundColor(ColorConstants.WHITE);
            } catch (IOException e) {
        }

        if (style != null) {
            cell.addStyle(style);
        }
    }

    /**
     * fix up for to be correct size and be bold for report
     * 
     * @param cell cell to act on
     */
    public static void fixupFontBold(Cell cell) {
        PdfFont code = null;
        Style style = null;
        try {
            code = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            style = new Style()
            .setFont(code)
            .setBold()
            .setFontSize(10)
            .setFontColor(ColorConstants.BLACK)
            .setBold()
            .setBackgroundColor(ColorConstants.WHITE);
            } catch (IOException e) {
        }

        if (style != null) {
            cell.addStyle(style);
        }
    }

    /**
     * make the background dark
     *
     * @param cell cell to act on
     */
    public static void makeDarkBackground(Cell cell) {
        PdfFont code = null;
        Style style = null;
        try {
            code = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            style = new Style()
            .setFont(code)
            .setFontSize(10)
            .setBold()
            .setFontColor(ColorConstants.BLACK)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            } catch (IOException e) {
        }

        if (style != null) {
            cell.addStyle(style);
        }
    }

}
