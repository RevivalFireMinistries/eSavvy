package za.org.rfm.beans.services;

import za.org.rfm.model.*;
import za.org.rfm.service.EventService;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/10
 * Time: 12:42 PM
 */
@ManagedBean(name = "attendance")
@ViewScoped
public class AttendanceBean {
    @ManagedProperty(value="#{EventService}")
    EventService eventService;
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    Event event;
    List<AssemblyFollowUp> assemblyFollowUpList;

    public List<AssemblyFollowUp> getAssemblyFollowUpList() {
        return assemblyFollowUpList;
    }

    public void setAssemblyFollowUpList(List<AssemblyFollowUp> assemblyFollowUpList) {
        this.assemblyFollowUpList = assemblyFollowUpList;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    private List<Member> memberList;
    private MemberDataModel memberDataModel;

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

    private List<Member> selectedMembers;

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @PostConstruct
    public void init(){
        //load all the members of this church
        memberList = memberService.getMembersByAssembly(WebUtil.getUserAssemblyId());
        memberDataModel = new MemberDataModel(memberList);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String eventid = facesContext.getExternalContext().getRequestParameterMap().get("eventid");
        event = eventService.getEventById(Long.parseLong(eventid));
    }

    public void processRegister(){
        EventLog eventLog;
        for(Member member :selectedMembers){
            eventLog = new EventLog(member,event);
            eventService.saveEventLog(eventLog);
        }
        event.setAttendance(selectedMembers.size());
        event.setRegister(true);
        eventService.saveEvent(event);
        generateFollowUpReport(selectedMembers,event);
        Utils.addFacesMessage("Event attendance captured successfully", FacesMessage.SEVERITY_INFO);
    }

    private void generateFollowUpReport(List<Member> presentMembers,Event event){
        //first lets get all members for this assembly
        List<Member> members = memberService.getMembersByAssembly(event.getAssembly().getAssemblyid());
        List<Member> absentList = new ArrayList<Member>();
        if(!members.isEmpty()){
            //now check who didn't come
            for(Member member : members){
                if(!presentMembers.contains(member)){
                    absentList.add(member);
                }
            }
        }
        if(!absentList.isEmpty()){
         List<AssemblyFollowUp> followUps = new ArrayList<AssemblyFollowUp>();
        //now we know who didn't come - so lets create followup objects
            AssemblyFollowUp assemblyFollowUp = null;
        for(Member member: absentList){
             assemblyFollowUp = new AssemblyFollowUp(member, Constants.STATUS_ACTIVE,new Date());
            followUps.add(assemblyFollowUp);
            eventService.saveAssemblyFollowUp(assemblyFollowUp);
        }
            setAssemblyFollowUpList(followUps);
        }//else simply means everyone came...glory! :-)

    }
}
