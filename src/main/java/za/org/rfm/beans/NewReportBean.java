package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.*;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.EventService;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.IOException;
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
    @ManagedProperty(value="#{mailService}")
    EmailService emailService;
    @ManagedProperty(value="#{EventService}")
    EventService eventService;
    String[] serviceTypes = Utils.getServiceTypes();
    boolean skip;
    private Event event;
    private Date maxDate = new Date(System.currentTimeMillis());
    private List<Member> memberList;
    private MemberDataModel memberDataModel;
    private List<Member> selectedMembers;

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

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
        if("register".equalsIgnoreCase(event.getOldStep()) && event.getNewStep().equalsIgnoreCase("confirm")){
            logger.debug("now processing the followUp...");
            //first check if user selected at least one person
            if(getSelectedMembers().isEmpty()){
                logger.debug("The followUp is empty...exit(0)");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please mark the followUp!",null));
                return event.getOldStep();
            }
            if(getSelectedMembers().size() != this.getEvent().getAttendance()+ this.getEvent().getGuests()){
                logger.debug("The attendance figures do not correspond to total in ticked in the register!");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Total entered in attendance "+getEvent().getAttendance()+" doesn't correspond to total captured in registered "+getSelectedMembers().size(),null));
                return event.getNewStep();
            }
        }
        if(skip) {
            skip = false;	//reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }

    public void save(){
        try {
            getEvent().setEventDate(new Timestamp(getEventDate().getTime()));
            Assembly assembly =  assemblyService.getAssemblyById(WebUtil.getUserAssemblyId());
            getEvent().setAssembly(assembly);
            getEvent().setTotalRegistered(assembly.getTotalRegistered());
            //Now create the event log-persist followUp info
            EventLog eventLog;
            List<EventLog> eventLogs = new ArrayList<EventLog>();
            for(Member member: getSelectedMembers()){
                eventLog = new EventLog(member,getEvent());
                eventLogs.add(eventLog);
            }
            logger.debug(eventLogs.size()+" Event Log objs created...");
            getEvent().setEventLogList(eventLogs);
            //Now prepare follow up report
            createFollowUpReport();

            eventService.saveEvent(getEvent());
            //Now send an email to pastoral! - but spawn a new thread to separate execution
            updateMemberLastServiceAttended();
            Thread emailThread = new Thread(){
                @Override
                public void run() {
                    emailService.eventReport(getEvent());
                }
            };
            emailThread.start();
            String url = "viewReports.faces";
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Flash flash = facesContext.getExternalContext().getFlash();
            flash.setKeepMessages(true);
            facesContext.getExternalContext().redirect(url);
            Utils.addFacesMessage("Event captured successfully", FacesMessage.SEVERITY_INFO);
            logger.info("Event captured succesfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMemberLastServiceAttended() {
        for(Member member : getSelectedMembers()){
            member.setDateLastAtChurch(getEventDate());
            memberService.saveMember(member);
        }
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    public void createFollowUpReport(){
        List<EventFollowUp> eventFollowUpList;
        //load absent members for this event
        List<Member> allMembers = memberService.getMembersByAssembly(event.getAssembly().getAssemblyid());
        List<Member> presentMembers = getSelectedMembers();
        if(allMembers.removeAll(presentMembers) && !event.isFollowUp()){
            //it means they are some absent members
            EventFollowUp eventFollowUp;
            eventFollowUpList = new ArrayList<EventFollowUp>();
            for(Member member : allMembers){
                eventFollowUp = new EventFollowUp();
                eventFollowUp.setMember(member);
                eventFollowUp.setEvent(event);
                eventFollowUpList.add(eventFollowUp);
            }
            event.setEventFollowUpList(eventFollowUpList);
        }else{
            return;
        }
        logger.debug("New follow up report has been created");

    }

    private List<Member> getMembersFromEventLogs(Event event){
        List<Member> presentMembers = new ArrayList<Member>();
        List<EventLog> eventLogs = eventService.getEventLogsByEventId(event.getId());
        logger.debug("Found "+eventLogs.size()+" event logs for this event "+event.getId());
        for(EventLog eventLog: eventLogs){
            presentMembers.add(eventLog.getMember());
        }
        return presentMembers;
    }

}
