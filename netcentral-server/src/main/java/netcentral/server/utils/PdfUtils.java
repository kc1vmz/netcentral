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
