package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.SMSService;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/04
 * Time: 8:05 AM
 */
@ViewScoped
@ManagedBean(name = "smsBean")
public class SendSMS {
    private static Logger logger = Logger.getLogger(SendSMS.class.getName());
    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    @ManagedProperty(value="#{SMSService}")
    SMSService smsService;
    boolean skip;
    String sms;
    private Assembly selectedAssembly;

    public Assembly getSelectedAssembly() {
        return selectedAssembly;
    }

    public void setSelectedAssembly(Assembly selectedAssembly) {
        this.selectedAssembly = selectedAssembly;
    }

    public String onFlowProcess(FlowEvent event) {
        logger.info("Current wizard step:" + event.getOldStep());
        logger.info("Next step:" + event.getNewStep());

        if(skip) {
            skip = false;	//reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    List<Assembly> assemblyList;
    Assembly assembly;

    public Assembly getAssembly() {
        return assembly;
    }

    public void setAssembly(Assembly assembly) {
        this.assembly = assembly;
    }

    public List<Assembly> getAssemblyList() {
        return assemblyList;
    }

    public void setAssemblyList(List<Assembly> assemblyList) {
        this.assemblyList = assemblyList;
    }

    public SMSService getSmsService() {
        return smsService;
    }

    public void setSmsService(SMSService smsService) {
        this.smsService = smsService;
    }

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    @PostConstruct

    public void init() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        HttpSession session = WebUtil.getSession();
        Long id = (Long)session.getAttribute("assembyid");
        setAssembly(assemblyService.getAssemblyById(id));
        setAssemblyList(assemblyService.getAssemblyList());

    }

    public void send(){
             List<Member> memberList = memberService.getMembersByAssembly(getAssembly().getAssemblyid());

        for(Member member: memberList){
          smsService.saveSMSLog(member.sendSMS(getSms(),true));
        }
        Utils.addFacesMessage(memberList.size()+" smses were sent!");
    }
}
