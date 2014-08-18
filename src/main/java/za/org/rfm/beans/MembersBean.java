package za.org.rfm.beans;

import za.org.rfm.model.Member;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/27
 * Time: 10:25 AM
 */
@ManagedBean(name = "membersBean")
@ViewScoped
public class MembersBean implements Serializable {
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;

    private List<Member> filteredMembers;

    private List<Member> members;

    private Member selectedMember;

    private Member[] selectedMembers;

    public MembersBean() {
        members = new ArrayList<Member>();

        populateMembers(members, 50);
    }


    public void setSelectedMember(Member selectedMember) {
        this.selectedMember = selectedMember;
    }

    private void populateMembers(List<Member> list, long assemblyid) {
        list = memberService.getMembersByAssembly(assemblyid);
    }


    public List<Member> getFilteredMembers() {
        return filteredMembers;
    }



    public List<Member> getMembers() {
        return members;
    }
    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

}
