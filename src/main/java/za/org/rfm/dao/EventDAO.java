package za.org.rfm.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.Event;
import za.org.rfm.model.EventLog;

import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/04
 * Time: 1:50 PM
 */
@Repository
public class EventDAO {
    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveEvent(Event event){
        sessionFactory.getCurrentSession().saveOrUpdate(event);
    }

    public List<Event> getEventsByDateAndType(String type,Date date){
        Query query = sessionFactory.getCurrentSession().createQuery("from Event where eventdate =:eventdate and eventtype =:eventtype");
        query.setDate("eventdate",date);
        query.setString("eventtype",type);
        List<Event> eventList = (List<Event>)query.list();
        return eventList;
    }


    public List<Event> getEventsByAssembly(long assemblyid) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Event order by eventdate asc");
       // query.setLong("assemblyid",assemblyid);
        List<Event> eventList = (List<Event>)query.list();
        return eventList;
    }

    public List<Event> getEventsByAssemblyAndType(long assemblyid,String eventtype) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Event where assembly = :assemblyid and eventtype = :eventtype order by eventdate asc");
        query.setLong("assemblyid",assemblyid);
        query.setString("eventtype",eventtype);
        List<Event> eventList = (List<Event>)query.list();
        return eventList;
    }




    public Event getEventById(long eventid) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Event where id =:eventid");
        query.setLong("eventid",eventid);
        Event event = (Event)query.list().get(0);
        return event;
    }

    public void saveEventLog(EventLog eventLog) {
        getSessionFactory().getCurrentSession().saveOrUpdate(eventLog);
    }
}
