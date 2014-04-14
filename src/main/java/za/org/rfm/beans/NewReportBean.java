package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Event;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EventService;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.sql.Timestamp;
import java.util.Date;

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

        System.out.println("---the asbly id------"+WebUtil.getUserAssemblyId());
        Assembly assembly =  assemblyService.getAssemblyById(WebUtil.getUserAssemblyId());
        getEvent().setAssembly(assembly);
       eventService.saveEvent(getEvent());
        Utils.addFacesMessage("Event captured successfully", FacesMessage.SEVERITY_INFO);
    }
}
