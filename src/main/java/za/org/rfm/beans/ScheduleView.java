package za.org.rfm.beans;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/8/14
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.faces.application.FacesMessage;



import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import za.org.rfm.model.RFMScheduleEvent;
import za.org.rfm.service.ScheduleService;
import za.org.rfm.utils.ScheduleLevel;
import za.org.rfm.utils.WebUtil;

@ManagedBean
@ViewScoped
public class ScheduleView implements Serializable {
    @ManagedProperty(value="#{ScheduleService}")
    ScheduleService scheduleService;


    private ScheduleModel eventModel;

    private ScheduleModel lazyEventModel;

    private ScheduleEvent event = new DefaultScheduleEvent();
    private RFMScheduleEvent rfmScheduleEvent;

    public ScheduleService getScheduleService() {
        return scheduleService;
    }

    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        List<RFMScheduleEvent> rfmScheduleEventList = scheduleService.getEventsByAssembly(WebUtil.getUserAssemblyId().toString());
        for(RFMScheduleEvent r : rfmScheduleEventList){
            eventModel.addEvent(new DefaultScheduleEvent(r.getTitle(), r.getStartDate(), r.getEndDate()));
        }
    }





    public ScheduleModel getEventModel() {
        return eventModel;
    }






    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void addEvent(ActionEvent actionEvent) {
        if(event.getId() == null){
            System.out.println("now saving event....");
            eventModel.addEvent(event);
            rfmScheduleEvent = new RFMScheduleEvent(event);
            rfmScheduleEvent.setOwnerId(WebUtil.getUserAssemblyId().toString());
            rfmScheduleEvent.setLevel(ScheduleLevel.ASSEMBLY_LEVEL.ordinal());
            scheduleService.saveEvent(rfmScheduleEvent);
        }

        else{
            eventModel.updateEvent(event);
        }


        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}