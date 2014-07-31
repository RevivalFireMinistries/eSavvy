package za.org.rfm.beans.services;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import za.org.rfm.model.Assembly;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.PDFService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/29
 * Time: 1:16 PM
 */
@ManagedBean
@ViewScoped
public class DownloadsBean {
    private static Logger logger = Logger.getLogger(DownloadsBean.class);
    @ManagedProperty(value="#{pdfService}")
    PDFService pdfService;
    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;

    public String selected;
    public Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public PDFService getPdfService() {
        return pdfService;
    }

    public void setPdfService(PDFService pdfService) {
        this.pdfService = pdfService;
    }

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    private StreamedContent file;

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }
    private List<SelectItem>  items;

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }

    @PostConstruct
    public void init(){
        //initialise list
        //cars
        SelectItemGroup sheets = new SelectItemGroup("Log Sheets");
        sheets.setSelectItems(new SelectItem[] {new SelectItem(Constants.SERVICE_TYPE_SUNDAY,Constants.SERVICE_TYPE_SUNDAY), new SelectItem(Constants.SERVICE_TYPE_MIDWEEK, Constants.SERVICE_TYPE_MIDWEEK)});

        SelectItemGroup templates = new SelectItemGroup("Report Templates");
        templates.setSelectItems(new SelectItem[] {new SelectItem(Constants.REPORT_TEMPLATE_FINANCE, Constants.REPORT_TEMPLATE_FINANCE), new SelectItem(Constants.REPORT_TEMPLATE_SERVICE, Constants.REPORT_TEMPLATE_SERVICE)});

        items = new ArrayList<SelectItem>();
        items.add(sheets);
        items.add(templates);

    }
    public void createLogSheet(){
        ByteArrayOutputStream byteArrayOutputStream = null;
        Assembly assembly = assemblyService.getAssemblyById(WebUtil.getUserAssemblyId());
        if(assembly != null){
            assembly.setMembers(memberService.getMembersByAssembly(assembly.getAssemblyid()));
        }
        logger.debug("Now creating logsheet for assembly "+assembly.getName());
        if(Constants.SERVICE_TYPE_SUNDAY.equals(getSelected()) || Constants.SERVICE_TYPE_MIDWEEK.equals(getSelected())){
          byteArrayOutputStream =  pdfService.createLogSheet(assembly,getSelected(),getDate());
            setFile(new DefaultStreamedContent(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),"application/pdf",getSelected()+"-LogSheet.pdf"));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Log Sheet created successfully and ready for download",""));
        }
        else if(Constants.REPORT_TEMPLATE_FINANCE.equals(getSelected())){
            byteArrayOutputStream =  pdfService.createFinanceTemplate(assembly,getSelected(),getDate());
            setFile(new DefaultStreamedContent(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),"application/pdf",getSelected()+".pdf"));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Template created successfully and ready for download",""));
        }

    }


}
