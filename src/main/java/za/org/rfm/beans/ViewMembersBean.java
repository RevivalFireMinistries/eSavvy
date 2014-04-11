package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import za.org.rfm.model.Member;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/26
 * Time: 1:01 PM
 */
@ManagedBean(name = "viewMembersBean")
@ViewScoped
public class ViewMembersBean implements Serializable {
    private static Logger logger = Logger.getLogger(ViewMembersBean.class.getName());
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    private List<Member> filteredMembers;

    private List<Member> members;

    private Member selectedMember;

    private Member[] selectedMembers;

    public void setFilteredMembers(List<Member> filteredMembers) {
        this.filteredMembers = filteredMembers;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public Member getSelectedMember() {
        return selectedMember;
    }

    public Member[] getSelectedMembers() {
        return selectedMembers;
    }

    public void setSelectedMembers(Member[] selectedMembers) {
        this.selectedMembers = selectedMembers;
    }

    @PostConstruct
    public void init() {
        members = new ArrayList<Member>();
        //Get assembly of the current user
        HttpSession session = WebUtil.getSession();
        Long id = (Long)session.getAttribute("assembyid");
        populateMembers(members,id);
    }
    public ViewMembersBean() {

    }


    public void setSelectedMember(Member selectedMember) {
        this.selectedMember = selectedMember;
    }

    private void populateMembers(List<Member> list, long assemblyid) {
         list = getMemberService().getMembersByAssembly(assemblyid);
        setMembers(list);
    }


    public List<Member> getFilteredMembers() {
        return filteredMembers;
    }



    public List<Member> getMembers() {
        return members;
    }

    public void onRowSelect(SelectEvent event){
        try {
            String url = "viewMember.faces?memberid="+this.getSelectedMember().getId();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Error opening member view :"+e.getMessage());
        }
    }
}
