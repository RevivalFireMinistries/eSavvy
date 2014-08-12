package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.Account;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 9:45 PM
 */
@ManagedBean(name = "memberBean")
@ViewScoped
public class MemberBean implements Serializable{
    private static Logger logger = Logger.getLogger(MemberBean.class.getName());
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;
    private Assembly assembly;
    private boolean skip;
    private List<Assembly> assemblyList;

    public Assembly getAssembly() {
        return assembly;
    }

    public void setAssembly(Assembly assembly) {
        this.assembly = assembly;
    }

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }



    public List<Assembly> getAssemblyList() {
        if(getAssemblyList() == null || getAssemblyList().isEmpty()){
            setAssemblyList(assemblyService.getAssemblyList(Constants.STATUS_ALL));
        } else{
        }
        return getAssemblyList();
    }

    public void setAssemblyList(List<Assembly> assemblyList) {
        this.assemblyList = assemblyList;
    }

    private Member member = new Member();
    private String[] memberTypes = Utils.getMemberTypes();

    public String[] getMemberTypes() {
        return memberTypes;
    }

    public void setMemberTypes(String[] memberTypes) {
        this.memberTypes = memberTypes;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void save(ActionEvent actionEvent){
        getMember().setDateCreated(new Date(System.currentTimeMillis()));
        getMember().setStatus(Constants.STATUS_ACTIVE);
        Long id  = WebUtil.getUserAssemblyId();
        Assembly assembly1 = assemblyService.getAssemblyById(id);
        getMember().setAssembly(assembly1);
        Account account = new Account();
        account.setMember(getMember());
        getMember().setAccount(account);
        Utils.capitaliseMember(getMember());
        getMemberService().saveMember(getMember());
        Utils.addFacesMessage("Member :"+getMember().getFullName()+" has been saved", FacesMessage.SEVERITY_INFO);
    }
    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
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
}
