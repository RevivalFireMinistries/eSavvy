package za.org.rfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.EventDAO;
import za.org.rfm.dto.AssemblyMonthlyAttendanceTotals;
import za.org.rfm.model.EventFollowUp;
import za.org.rfm.model.Event;
import za.org.rfm.model.EventLog;
import za.org.rfm.model.Member;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;
import za.org.rfm.utils.Utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/04
 * Time: 2:23 PM
 */
@Service("EventService")
@Transactional(readOnly = true)
public class EventService {
    @Autowired
    EventDAO eventDAO;

    @Transactional(readOnly = false)
    public void saveEvent(Event event) {
        eventDAO.saveEvent(event);
    }

    public List<Event> getEventsByAssembly(long assemblyid) {
        return eventDAO.getEventsByAssembly(assemblyid);
    }

    public List<Event> getEventsByAssemblyAndTypeAndDateRange(long assemblyid, String type, DateRange dateRange) {
        return eventDAO.getEventsByAssemblyAndTypeAndDateRange(assemblyid, type, dateRange);
    }

    public List<Event> getEventsByTypeAndDateRange(String type, DateRange dateRange) {
        return eventDAO.getEventsByTypeAndDateRange(type, dateRange);
    }

    public List<Event> getEventsByAssemblyAndTypeAndDate(long assemblyid, String type, Timestamp date) {
        return eventDAO.getEventsByAssemblyAndTypeAndDate(assemblyid, type, date);
    }

    public List<Event> getEventsByDateAndType(String type, Date date) {
        return eventDAO.getEventsByDateAndType(type, date);
    }

    public Event getEventById(long eventid) {
        return eventDAO.getEventById(eventid);
    }

    @Transactional(readOnly = false)
    public void saveEventLog(EventLog eventLog) {
        eventDAO.saveEventLog(eventLog);
    }

    @Transactional(readOnly = false)
    public void saveAssemblyFollowUp(EventFollowUp eventFollowUp) {
        eventDAO.saveAssemblyFollowUp(eventFollowUp);
    }

    public List<Event> getEventsByAssemblyAndType(String type, long assemblyid, int limit) {
        return eventDAO.getEventsByAssemblyAndType(type, assemblyid, limit);
    }

    public void generateFollowUpReport(Event event) {
        //first get each member's attribute
    }

    public List<EventFollowUp> getEventFollowUpList(Event event) {
        return eventDAO.getEventFollowUpList(event);
    }

    public List<EventLog> getEventLogsByEventId(Long eventId) {
        return eventDAO.getEventLogsByEventId(eventId);
    }

    public List<EventLog> getEventLogsByMemberandDateRange(Member member, DateRange dateRange) {
        return eventDAO.getEventLogsByMemberandDateRange(member, dateRange);
    }

    public List<AssemblyMonthlyAttendanceTotals> getAssemblyMonthlyAttendance(long id){
        List<AssemblyMonthlyAttendanceTotals> aMTlist = new ArrayList<AssemblyMonthlyAttendanceTotals>();
        for(int i=1; i<=12;i++){
            DateRange dateRange = Utils.getMonthDateRange(i-1);
            List<Event> eventList = eventDAO.getEventsByAssemblyAndTypeAndDateRange(id, Constants.SERVICE_TYPE_SUNDAY,dateRange);
            aMTlist.add(new AssemblyMonthlyAttendanceTotals(eventList,i));
        }
        return aMTlist;
    }

}
