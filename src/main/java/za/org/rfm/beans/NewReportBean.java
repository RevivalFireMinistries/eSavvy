package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.*;
import za.org.rfm.service.AssemblyService;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/04
 * Time: 12:52 PM
 */
@ViewScoped
@ManagedBean(name = "newReportBean")
public class NewReportBean {
    private static Logger logger = Logger.getLogger(NewReportBean.class.getName());
    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
    private Date eventDate;

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    @ManagedProperty(value="#{EventService}")
    EventService eventService;
    String[] serviceTypes = Utils.getServiceTypes();
    boolean skip;
    private Event event;
    private Date maxDate = new Date(System.currentTimeMillis());
    private List<Member> memberList;
    private MemberDataModel memberDataModel;
    private List<Member> selectedMembers;

    public List<Member> getSelectedMembers() {
        return selectedMembers;
    }

    public void setSelectedMembers(List<Member> selectedMembers) {
        this.selectedMembers = selectedMembers;
    }

    public MemberDataModel getMemberDataModel() {
        return memberDataModel;
    }

    public void setMemberDataModel(MemberDataModel memberDataModel) {
        this.memberDataModel = memberDataModel;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public boolean isSkip() {
        return skip;
    }
    @PostConstruct
    public void init(){

        setEvent(new Event());
        //load all the members of this church
        memberList = memberService.getMembersByAssembly(WebUtil.getUserAssemblyId());
        memberDataModel = new MemberDataModel(memberList);
    }
    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String[] getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(String[] serviceTypes) {
        this.serviceTypes = serviceTypes;
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

    public void save(){
        getEvent().setEventDate(new Timestamp(getEventDate().getTime()));
        Assembly assembly =  assemblyService.getAssemblyById(WebUtil.getUserAssemblyId());
        getEvent().setAssembly(assembly);
        getEvent().setTotalRegistered(assembly.getTotalRegistered());
       eventService.saveEvent(getEvent());
        Utils.addFacesMessage("Event captured successfully", FacesMessage.SEVERITY_INFO);
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
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
    public void setAssemblyFollowUpList(List<AssemblyFollowUp> assemblyFollowUpList) {
        this.assemblyFollowUpList = assemblyFollowUpList;
    }
    List<AssemblyFollowUp> assemblyFollowUpList;

    public List<AssemblyFollowUp> getAssemblyFollowUpList() {
        return assemblyFollowUpList;
    }
}
