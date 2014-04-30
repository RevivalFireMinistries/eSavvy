package za.org.rfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.EventDAO;
import za.org.rfm.model.AssemblyFollowUp;
import za.org.rfm.model.Event;
import za.org.rfm.model.EventLog;
import za.org.rfm.utils.DateRange;

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
    public void saveEvent(Event event){
        eventDAO.saveEvent(event);
    }

    public List<Event> getEventsByAssembly(long assemblyid) {
        return eventDAO.getEventsByAssembly(assemblyid);
    }
    public List<Event> getEventsByAssemblyAndTypeAndDate(long assemblyid,String type,DateRange dateRange) {
        return eventDAO.getEventsByAssemblyAndTypeAndDate(assemblyid, type,dateRange);
    }
    public List<Event> getEventsByDateAndType(String type, Date date){
        return  eventDAO.getEventsByDateAndType(type,date);
    }
    public Event getEventById(long eventid) {
        return eventDAO.getEventById(eventid);
    }
    @Transactional(readOnly = false)
    public void saveEventLog(EventLog eventLog) {
         eventDAO.saveEventLog(eventLog);
    }
    @Transactional(readOnly = false)
    public void saveAssemblyFollowUp(AssemblyFollowUp assemblyFollowUp) {
        eventDAO.saveAssemblyFollowUp(assemblyFollowUp);
     }
    public void generateFollowUpReport(Event event){
      //first get each member's attribute
    }


}
