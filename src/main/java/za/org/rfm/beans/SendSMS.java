package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;
import za.org.rfm.model.MemberDataModel;
import za.org.rfm.model.MemberGroup;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.SMSService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Group;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
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
    Assembly[] selectedAssemblies;
    Group[] selectedGroups;
    List<Group> groupList;
    private MemberDataModel memberDataModel;
    private List<Member> memberList;

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    private List<Member> selectedMembers;

    public MemberDataModel getMemberDataModel() {
        return memberDataModel;
    }

    public void setMemberDataModel(MemberDataModel memberDataModel) {
        this.memberDataModel = memberDataModel;
    }

    public List<Member> getSelectedMembers() {
        return selectedMembers;
    }

    public void setSelectedMembers(List<Member> selectedMembers) {
        this.selectedMembers = selectedMembers;
    }

    public Group[] getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(Group[] selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public Assembly[] getSelectedAssemblies() {
        return selectedAssemblies;
    }

    public void setSelectedAssemblies(Assembly[] selectedAssemblies) {
        this.selectedAssemblies = selectedAssemblies;
    }

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
        if(event.getOldStep().equalsIgnoreCase("groups")) {
           //prepare final members list from the select assembly and group
            loadMembersDataList();
        }
        if(skip) {
            skip = false;	//reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }
    private void loadMembersDataList(){
        List<Member> tempList = new ArrayList<Member>();
        //first add all members from the selected assemblies
        for(Assembly assembly1 : getSelectedAssemblies()){
            List<Member> assemblyMembers = memberService.getMembersByAssembly(assembly1.getAssemblyid());
            if( assemblyMembers != null){
                logger.debug("got "+assemblyMembers.size()+" for assembly :"+assembly1.name);
                memberList.addAll(assemblyMembers);
            }

        }
        //now for these members filter by selected groups
        for(Member member : memberList){
            if(isAllowed(member,selectedGroups)){
                tempList.add(member);
            }
        }
        memberDataModel = new MemberDataModel(tempList);

    }
    private boolean isAllowed(Member member,Group[] selectedGroups){
        for(MemberGroup memberGroup : member.getMemberGroupList()){
            for(Group group : selectedGroups){
                if(group.equals(Group.valueOf(memberGroup.getGroupName()))){
                    logger.debug("Member added to final list : "+member.getFullName());
                    //there is a match so return with true
                    return true;
                }
            }
        }
        return false;

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

        setAssembly(assemblyService.getAssemblyById(WebUtil.getUserAssemblyId()));
        setAssemblyList(assemblyService.getAssemblyList(Constants.STATUS_ACTIVE));
        setGroupList(Utils.getGroupsAsList());
        memberList = new ArrayList<Member>();
    }

    public void send(){
             List<Member> memberList = memberService.getMembersByAssembly(getAssembly().getAssemblyid());

        for(Member member: memberList){
          smsService.saveSMSLog(member.sendSMS(getSms(),true));
        }
        Utils.addFacesMessage(memberList.size()+" smses were sent!", FacesMessage.SEVERITY_INFO);
    }
}
