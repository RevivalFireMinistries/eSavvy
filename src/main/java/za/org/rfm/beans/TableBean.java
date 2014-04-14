package za.org.rfm.beans;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/08
 * Time: 10:42 AM
 */

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import za.org.rfm.beans.services.LazyEventDataModel;
import za.org.rfm.model.Event;
import za.org.rfm.service.EventService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;
@ViewScoped
@ManagedBean(name = "tableBean")
public class TableBean {
    private CartesianChartModel categoryModel;

    public CartesianChartModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CartesianChartModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @ManagedProperty(value="#{EventService}")
    EventService eventService;
    private LazyDataModel<Event> lazyModel;

    private List<Event> filteredEvents;

    public List<Event> getFilteredEvents() {
        return filteredEvents;
    }

    public void setFilteredEvents(List<Event> filteredEvents) {
        this.filteredEvents = filteredEvents;
    }

    private Event selectedEvent;

    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @PostConstruct
    public void init(){
        events = eventService.getEventsByAssembly(WebUtil.getUserAssemblyId());
        //setEvents(events);
        lazyModel = new LazyEventDataModel(events);
        createCategoryModel();
    }

    private void createCategoryModel() {
        categoryModel = new CartesianChartModel();
        //List<Event> sundayList = new ArrayList<Event>();
        //List<Event> midweekList = new ArrayList<Event>();
        ChartSeries sundayServices = new ChartSeries();
        ChartSeries midweekServices = new ChartSeries();
        List<Event> sundayList = eventService.getEventsByAssemblyAndType(WebUtil.getUserAssemblyId(),Constants.SERVICE_TYPE_SUNDAY);
        List<Event> midweekList = eventService.getEventsByAssemblyAndType(WebUtil.getUserAssemblyId(),Constants.SERVICE_TYPE_MIDWEEK);
        int count = 0;
        for(Event event1: sundayList){
            count++;
           sundayServices.set(event1.getEventDateFormatted(),event1.getAttendance());
            if (count == 4){
                count = 0;
                break;
            }
        }
        sundayServices.setLabel("Sunday Services");
        for(Event event1: midweekList){
            count++;
            midweekServices.set(event1.getEventDateFormatted(),event1.getAttendance());
            if (count == 4){
                count = 0;
                break;
            }
        }
        midweekServices.setLabel("Midweek Services");
        categoryModel.addSeries(sundayServices);
        categoryModel.addSeries(midweekServices);
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public LazyDataModel<Event> getLazyModel() {
        return lazyModel;
    }
    public void addRegister(Event event){
        try {
            String url = "/members/addRegister.faces?eventid="+event.getId();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            Utils.addFacesMessage("Error : Failed to open requested page", FacesMessage.SEVERITY_ERROR);
        }
    }


}
