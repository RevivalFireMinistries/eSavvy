package za.org.rfm.beans;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/08
 * Time: 10:42 AM
 */

import org.joda.time.DateTime;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import za.org.rfm.beans.services.LazyEventDataModel;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Event;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EventService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@ViewScoped
@ManagedBean(name = "tableBean")
public class TableBean {
    private CartesianChartModel categoryModel;
    private int avgAttendance;
    private String currentMonth;
    private int lastAttendance;
    private double percentageOfAttendance;
    private int icu;
    private double totalIncome;
    private Assembly assembly;

    public Assembly getAssembly() {
        return assembly;
    }

    public void setAssembly(Assembly assembly) {
        this.assembly = assembly;
    }

    public String getTotalIncome() {
        return Utils.moneyFormatter(totalIncome);
    }

    public void setTotalIncome(List<Event> events) {
        double sum = 0.0;
        for(Event event1: events){
          sum += event1.getTotalIncome();
        }
        this.totalIncome = sum;
    }

    public int getIcu() {
        return icu;
    }

    public void setIcu(int icu) {
        this.icu = icu;
    }

    public double getPercentageOfTithers() {
        return percentageOfTithers;
    }

    public void setPercentageOfTithers(double percentageOfTithers) {
        this.percentageOfTithers = percentageOfTithers;
    }

    public double getPercentageOfAttendance() {
        System.out.println("----avg att: "+getAvgAttendance()+" registered :"+getAssembly().getMembersRegistered());
        return getAvgAttendance()/getAssembly().getMembersRegistered();
    }

    public void setPercentageOfAttendance(double percentageOfAttendance) {
        this.percentageOfAttendance = percentageOfAttendance;
    }

    public int getLastAttendance() {
        return lastAttendance;
    }

    public void setLastAttendance(int lastAttendance) {
        this.lastAttendance = lastAttendance;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public double getAvgAttendance() {
        return avgAttendance;
    }

    public void setAvgAttendance(List<Event> events) {
        double data[] = new double[events.size()];
        for(int i=0;i<events.size();i++){
              data[i] = events.get(i).getAttendance();
        }
        this.avgAttendance = (int)Utils.mean(data);
    }

    private double percentageOfTithers;

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

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;
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
         setAssembly(assemblyService.getAssemblyById(WebUtil.getUserAssemblyId()));
        DateRange dateRange = initializeDateRange();
        System.out.println("daterange : start "+dateRange.getStartDate()+"  end date : "+dateRange.getEndDate());
        events = eventService.getEventsByAssemblyAndTypeAndDate(WebUtil.getUserAssemblyId(),Constants.SERVICE_TYPE_SUNDAY,dateRange);
        System.out.println("---num of events retrieved---"+events.size());
        //setEvents(events);
        lazyModel = new LazyEventDataModel(events);
        createCategoryModel();
       setCurrentMonth(new SimpleDateFormat("MMMM").format(new Date()));
       setAvgAttendance(getEvents());
        setTotalIncome(getEvents());


    }



    private void createCategoryModel() {
        categoryModel = new CartesianChartModel();
        //List<Event> sundayList = new ArrayList<Event>();
        //List<Event> midweekList = new ArrayList<Event>();
        ChartSeries sundayServices = new ChartSeries();
        ChartSeries midweekServices = new ChartSeries();


        for(Event event1: getEvents()){
           sundayServices.set(event1.getEventDateFormatted(),event1.getAttendance());
        }
        sundayServices.setLabel("Sunday Services");

        categoryModel.addSeries(sundayServices);
        //categoryModel.addSeries(midweekServices);
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

    private DateRange initializeDateRange() {

        DateTime firstDayOfMonth = new DateTime().dayOfMonth().withMinimumValue();
        Date today = new Date(System.currentTimeMillis());
        DateRange dateRange = new DateRange(firstDayOfMonth.toDate(),today);
        return dateRange;
    }
}
