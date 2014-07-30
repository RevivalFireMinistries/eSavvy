package za.org.rfm.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;
import za.org.rfm.model.MemberGroup;
import za.org.rfm.utils.Group;
import za.org.rfm.utils.Style;
import za.org.rfm.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/29
 * Time: 1:45 PM
 */
@Service("pdfService")
public class PDFService {
    private static Logger logger = Logger.getLogger(PDFService.class);
       public ByteArrayOutputStream createLogSheet(Assembly assembly,String serviceType,Date date){
           try {
               ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
               PdfWriter docWriter = null;
               Document doc = new Document(PageSize.A4);
               docWriter = PdfWriter.getInstance(doc, baosPDF);
               logger.debug("Now building pdf...");
               doc.open();
               PdfPTable table = createHeaderTable(assembly,serviceType,date);
               doc.add(table);
               table = createBodyTable(assembly.getMembers());
               doc.add(table);
               doc.close();
               logger.debug("PDF creation done...closing");
               return baosPDF;
           } catch (DocumentException e) {
               e.printStackTrace();
           }
           return null;
       }
    private  PdfPTable createHeaderTable(Assembly assembly,String serviceType,Date date)
            throws BadElementException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(95);

        Font font = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
        PdfPCell c1 = new PdfPCell(new Phrase(assembly.getName()+" - "+serviceType+" - LogSheet",font));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setColspan(4);
        Style.headerCellStyle(c1);
        table.addCell(c1);
        table.setHeaderRows(1);

        table.addCell(createLabelCell("Date :"));
        table.addCell(createValueCell(Utils.dateFormatter(date)));
        table.addCell(createLabelCell("Service Type :"));
        table.addCell(createValueCell(serviceType));
        table.addCell(createLabelCell("Target Attendance :"));
        table.addCell(createValueCell(""));
        table.addCell(createLabelCell("Actual Attendance :"));
        table.addCell(createValueCell(""));
        return table;

    }
    private PdfPTable createBodyTable(java.util.List<Member> memberList)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(95);

        PdfPCell c1 = new PdfPCell(new Phrase("  "));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setColspan(3);
        table.addCell(c1);
        table.setHeaderRows(1);

        table.addCell(createLabelCell("Name :"));
        table.addCell(createLabelCell("Time In"));
        table.addCell(createLabelCell("Signature :"));

        List<Member> elders = new ArrayList<Member>();
        List<Member> deacons = new ArrayList<Member>();
        List<Member> music = new ArrayList<Member>();
        List<Member> general = new ArrayList<Member>();

       for(Member member : memberList){     //need to split into groups
            //Pastors
            for(MemberGroup memberGroup : member.getMemberGroupList()){
                if(Group.ELDERS.equals(Group.valueOf(memberGroup.getGroupName()))){
                    elders.add(member);
                    break;
                }
                else if(Group.DEACONS.equals(Group.valueOf(memberGroup.getGroupName()))){
                    deacons.add(member);
                    break;
                }
                else if(Group.MUSIC.equals(Group.valueOf(memberGroup.getGroupName()))){
                    music.add(member);
                    break;
                }
                else{
                    general.add(member);
                    break;
                }
            }
       }
        addToTable(table,elders,Group.ELDERS);
        addToTable(table,deacons,Group.DEACONS);
        addToTable(table,music,Group.MUSIC);
        addToTable(table,general,null);

        return table;

    }
    private void addToTable(PdfPTable table, java.util.List<Member> members,Group group){
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.LIGHT_GRAY);
        String name = "General";
        if(group != null){
          name =  group.name();
        }

        PdfPCell c1 = new PdfPCell(new Phrase(name,font));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setColspan(3);
        table.addCell(c1);
        for(Member member : members){
            table.addCell(member.getFullName());
            table.addCell("");
            table.addCell("");
        }

    }
    // create cells
    private static PdfPCell createLabelCell(String text){
        // font
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.DARK_GRAY);

        // create cell
        PdfPCell cell = new PdfPCell(new Phrase(text,font));

        // set style
        Style.labelCellStyle(cell);
        return cell;
    }

    // create cells
    private static PdfPCell createValueCell(String text){
        // font
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

        // create cell
        PdfPCell cell = new PdfPCell(new Phrase(text,font));

        // set style
        Style.valueCellStyle(cell);
        return cell;
    }
}
