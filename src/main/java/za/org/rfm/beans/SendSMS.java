package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.*;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.SMSService;
import za.org.rfm.service.SystemVarService;
import za.org.rfm.utils.*;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;

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
    @ManagedProperty(value="#{SystemVarService}")
    SystemVarService systemVarService;
    Map<Long,String> systemVars;
    SystemVar selectedSMSTemplate;
    Long selectedTmpl;

    public Long getSelectedTmpl() {
        return selectedTmpl;
    }

    public void setSelectedTmpl(Long selectedTmpl) {
        this.selectedTmpl = selectedTmpl;
    }

    public SystemVar getSelectedSMSTemplate() {
        return selectedSMSTemplate;
    }

    public void setSelectedSMSTemplate(SystemVar selectedSMSTemplate) {
        this.selectedSMSTemplate = selectedSMSTemplate;
    }

    public Map<Long, String> getSystemVars() {
        return systemVars;
    }

    public void setSystemVars(Map<Long, String> systemVars) {
        this.systemVars = systemVars;
    }

    public SystemVarService getSystemVarService() {
        return systemVarService;
    }

    public void setSystemVarService(SystemVarService systemVarService) {
        this.systemVarService = systemVarService;
    }

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
        if(event.getOldStep().equalsIgnoreCase("finalList") && !event.getNewStep().equalsIgnoreCase("groups")){
            if(!validateMemberList()){
                logger.debug("Selected members empty - return!");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select at least one member ",null));
                return event.getOldStep();
            }
        }
        if(event.getOldStep().equalsIgnoreCase("sms") && event.getNewStep().equals("confirm")){
            //make sure if the user changed the default you then use the new changes
            logger.debug("the final sms - "+getSms());
        }
        if(skip) {
            skip = false;	//reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }

    private boolean validateMemberList() {
        if(getSelectedMembers() == null || getSelectedMembers().isEmpty()){
            return false;
        }
        return true;
    }

    private void loadMembersDataList(){
        Set<Member> tempSet= new LinkedHashSet<Member>();
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
                tempSet.add(member);
            }
        }
        List<Member> tempList = new ArrayList<Member>();
        tempList.addAll(tempSet);
        memberDataModel = new MemberDataModel(tempList);
        setSelectedMembers(tempList); //make it select all by default

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
       setAssembly(assemblyService.getAssemblyById(WebUtil.getUserAssemblyId()));
        if(WebUtil.getCurrentUserRoles().contains(za.org.rfm.utils.Role.Apostle)) {
            setAssemblyList(assemblyService.getAssemblyList(Constants.STATUS_ACTIVE));
        }else{
            List<Assembly> assemblies = new ArrayList<Assembly>();
            assemblies.add(getAssembly());
            setAssemblyList(assemblies);
        }

        setGroupList(Utils.getGroupsAsList());
        memberList = new ArrayList<Member>();
        systemVars = Utils.getVarsAsMap(systemVarService.getSystemVarByName("sms-template"));
        logger.debug(systemVars.size()+" sms templates loaded");
    }
    Integer progress;

    public Integer getProgress() {
        logger.debug("Progress ...."+progress+"%");
       return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String send(){
       //now send to the finalised group
        logger.debug("Now sending sms...");
        setProgress(0);
        int size = getSelectedMembers().size();
        int divisor = 100/size;
        SystemVar val = systemVarService.getSystemVarByNameUnique(Constants.SMS_ENABLED);
        Boolean sendSMS = false;
        if(val != null){
            sendSMS = Boolean.valueOf(val.getValue());
        }
        for(Member member: getSelectedMembers()){
          smsService.saveSMSLog(member.sendSMS(getSms(),sendSMS));
            this.progress = this.progress + divisor;
        }
        logger.debug("SMS sending completed!");
        setProgress(100);   //done!

        return "sendSMS";

    }

    public void cancel(){
         this.progress = null;
    }
    public void onComplete(){
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,getSelectedMembers().size()+" smses were sent!",null));
    }
    public void onTemplateChange(AjaxBehaviorEvent ajaxBehaviorEvent){
        System.out.println("drop down changed!!!"+ajaxBehaviorEvent.toString());
        if(getSelectedTmpl() != null){
            logger.debug("New sms template selected..."+getSelectedTmpl());
            setSms(systemVarService.getSystemVarById(getSelectedTmpl()).getValue());
        }else{
            logger.debug("No template was selected...continue");
            setSms("");
        }

    }
}
