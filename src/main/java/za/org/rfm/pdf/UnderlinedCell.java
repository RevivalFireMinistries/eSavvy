package za.org.rfm.pdf;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/30
 * Time: 12:39 PM
 */
public class UnderlinedCell implements PdfPCellEvent {

    public void cellLayout(PdfPCell cell, Rectangle position,
                           PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
        canvas.setLineWidth(0.5f);
        canvas.setLineDash(3f, 3f);
        canvas.moveTo(position.getLeft(), position.getBottom());
        canvas.lineTo(position.getRight(), position.getBottom());

        canvas.stroke();
    }
}