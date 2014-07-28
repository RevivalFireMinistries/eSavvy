package za.org.rfm.beans;

import za.org.rfm.model.*;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.EventService;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.SystemVarService;
import za.org.rfm.utils.Constants;

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
 * Date: 2014/07/17
 * Time: 9:00 AM
 */
@ManagedBean
@ViewScoped
public class FollowUpBean extends SuperBean{
    @ManagedProperty(value="#{EventService}")
    EventService eventService;
    @ManagedProperty(value="#{SystemVarService}")
    SystemVarService systemVarService;
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    @ManagedProperty(value="#{mailService}")
    EmailService emailService;
    List<Member> absentMembers;
    List<Member> fliteredMembers;
    String[] followUpStates = Constants.followUpStates;
    String[] followUpActions = Constants.followUpActions;
    List<EventFollowUp> eventFollowUpList;
    Event event;
    List<Member> smsList = new ArrayList<Member>();
    List<Member> referredToPastor = new ArrayList<Member>();

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public SystemVarService getSystemVarService() {
        return systemVarService;
    }

    public void setSystemVarService(SystemVarService systemVarService) {
        this.systemVarService = systemVarService;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String[] getFollowUpActions() {
        return followUpActions;
    }

    public void setFollowUpActions(String[] followUpActions) {
        this.followUpActions = followUpActions;
    }

    public List<EventFollowUp> getEventFollowUpList() {
        return eventFollowUpList;
    }

    public void setEventFollowUpList(List<EventFollowUp> eventFollowUpList) {
        this.eventFollowUpList = eventFollowUpList;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public List<Member> getAbsentMembers() {
        return absentMembers;
    }

    public void setAbsentMembers(List<Member> absentMembers) {
        this.absentMembers = absentMembers;
    }

    public List<Member> getFliteredMembers() {
        return fliteredMembers;
    }

    public void setFliteredMembers(List<Member> fliteredMembers) {
        this.fliteredMembers = fliteredMembers;
    }

    public String[] getFollowUpStates() {
        return followUpStates;
    }

    public void setFollowUpStates(String[] followUpStates) {
        this.followUpStates = followUpStates;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostConstruct
    public void init(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String eventid = facesContext.getExternalContext().getRequestParameterMap().get("eventid");
        setEvent(eventService.getEventById(Long.parseLong(eventid)));
        if(event.isFollowUp()){
            //follow up was done already
            setEventFollowUpList(eventService.getEventFollowUpList(event));
        }
    }
    public void handleChange(){
          logger.debug("Now refreshing actions...");
    }
    public void createFollowUpReport(Event event){
        //load absent members for this event
       setEvent(eventService.getEventById(event.getId()));
       List<Member> allMembers = memberService.getMembersByAssembly(event.getAssembly().getAssemblyid());
       List<Member> presentMembers = getMembersFromEventLogs(event);
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
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Follow up report already exists for this event",""));
            return;
        }
        logger.debug("New follow up report has been created");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"A new follow up report has been generated!",""));
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
    public void saveReport(){
       //mark the event- followup done!
        this.event.setFollowUp(true);
        this.event.setEventFollowUpList(getEventFollowUpList());
        eventService.saveEvent(this.event);
        logger.debug("follow up report saved...now executing actions");
        for(EventFollowUp eventFollowUp : getEventFollowUpList()) {
            String action = eventFollowUp.getAction();
            if (Constants.ACTION_SEND_MOTIVATIONAL_SMS.equalsIgnoreCase(action)) {
                smsList.add(eventFollowUp.getMember());
            } else if (Constants.ACTION_REFER_TO_PASTOR.equalsIgnoreCase(action)) {
                referredToPastor.add(eventFollowUp.getMember());
            } else {
            }
        }     //now send Pastor Report on what happened to the pastor and a list pple that need his attention

        Thread email = new Thread(){
            @Override
            public void run() {
                emailService.followUpReport(getEventFollowUpList(),new Date(),getEvent());
            }
        };   email.start();
        logger.debug("Found "+smsList.size()+" members for sending smses");
        logger.debug("Found "+referredToPastor.size()+" members referred to the pastor");
        List<SystemVar> systemVars = systemVarService.getSystemVarByName(Constants.MOTIVATIONAL_SMS);
//        Utils.sendBulkSMS(smsList,systemVars.get(0).getValue()); //TODO:sort the sms sending here!

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Your follow up report has been saved!",""));
    }

}
