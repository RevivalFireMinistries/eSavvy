package za.org.rfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.EventDAO;
import za.org.rfm.model.Event;
import za.org.rfm.model.EventLog;

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
    public List<Event> getEventsByAssemblyAndType(long assemblyid,String type) {
        return eventDAO.getEventsByAssemblyAndType(assemblyid,type);
    }
    public Event getEventById(long eventid) {
        return eventDAO.getEventById(eventid);
    }
    @Transactional(readOnly = false)
    public void saveEventLog(EventLog eventLog) {
         eventDAO.saveEventLog(eventLog);
    }
}
