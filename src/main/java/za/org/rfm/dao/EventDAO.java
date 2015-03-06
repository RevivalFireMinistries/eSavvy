package za.org.rfm.dao;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.EventFollowUp;
import za.org.rfm.model.Event;
import za.org.rfm.model.EventLog;
import za.org.rfm.model.Member;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/04
 * Time: 1:50 PM
 */
@Repository
public class EventDAO {
    Logger logger = Logger.getLogger(EventDAO.class);
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
        String hql = "";
        if(Constants.STATUS_ALL.equalsIgnoreCase(type)){
             hql =  "from Event where eventdate =:eventdate";
        }else{
           hql = "from Event where eventdate =:eventdate and eventtype =:eventtype";
        }
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setDate("eventdate",date);
        if(!Constants.STATUS_ALL.equalsIgnoreCase(type))
        query.setString("eventtype",type);
        List<Event> eventList = (List<Event>)query.list();
        return eventList;
    }
    public List<Event> getEventsByAssemblyAndType(String type,long assemblyid,int limit){
        String hql = "";
        if(limit == 0){
           hql = "from Event where assembly =:assemblyid and eventtype =:eventtype order by eventdate desc";
        }   else{
          hql = "from Event where assembly =:assemblyid and eventtype =:eventtype order by eventdate desc limit "+limit;
        }
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setLong("assemblyid", assemblyid);
        query.setString("eventtype",type);
        query.setMaxResults(limit);
        List<Event> eventList = (List<Event>)query.list();
        return eventList;
    }


    public List<Event> getEventsByAssembly(long assemblyid) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Event order by eventdate asc");
       // query.setLong("assemblyid",assemblyid);
        List<Event> eventList = (List<Event>)query.list();
        return eventList;
    }

    public List<Event> getEventsByAssemblyAndTypeAndDateRange(long assemblyid, String eventtype, DateRange dateRange) {
        String hql = "";
        if(Constants.STATUS_ALL.equalsIgnoreCase(eventtype)){
            hql =  "from Event where assembly = :assemblyid and eventdate between :from and :to order by eventdate asc";
        }else{
            hql = "from Event where assembly = :assemblyid and eventtype = :eventtype and eventdate between :from and :to order by eventdate asc";
        }
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setLong("assemblyid",assemblyid);
        if(!Constants.STATUS_ALL.equalsIgnoreCase(eventtype))
        query.setString("eventtype",eventtype);
        query.setDate("from",dateRange.getStartDate());
        query.setDate("to",dateRange.getEndDate());
        List<Event> eventList = (List<Event>)query.list();
        return eventList;
    }

    public List<Event> getEventsByAssemblyAndTypeAndDate(long assemblyid, String eventtype, Timestamp date) {
        logger.info("Getting events for assembly : "+assemblyid+" for the date "+date);
        String hql = "";
        if(Constants.STATUS_ALL.equalsIgnoreCase(eventtype)){
            hql =  "from Event where assembly = :assemblyid and eventdate = :date";
        }else{
            hql = "from Event where assembly = :assemblyid and eventtype = :eventtype and eventdate = :date ";
        }
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setLong("assemblyid",assemblyid);
        if(!Constants.STATUS_ALL.equalsIgnoreCase(eventtype))
            query.setString("eventtype",eventtype);
        query.setDate("date",date);
        List<Event> eventList = (List<Event>)query.list();
        return eventList;
    }

public List<Event> getEventsByTypeAndDateRange(String eventtype, DateRange dateRange) {
        String hql = "";
        if(Constants.STATUS_ALL.equalsIgnoreCase(eventtype)){
            hql =  "from Event where  eventdate between :from and :to order by eventdate asc";
        }else{
            hql = "from Event where  eventtype = :eventtype and eventdate between :from and :to order by eventdate asc";
        }
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        if(!Constants.STATUS_ALL.equalsIgnoreCase(eventtype))
        query.setString("eventtype",eventtype);
        query.setDate("from",dateRange.getStartDate());
        query.setDate("to",dateRange.getEndDate());
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
    public void saveAssemblyFollowUp(EventFollowUp eventFollowUp) {
          getSessionFactory().getCurrentSession().saveOrUpdate(eventFollowUp);
    }
    public List<EventFollowUp> getEventFollowUpList(Event event) {
        Query query = sessionFactory.getCurrentSession().createQuery("from EventFollowUp where event =:eventid");
        query.setLong("eventid",event.getId());
        return (List<EventFollowUp>)query.list();
    }
    public List<EventLog> getEventLogsByEventId(Long eventId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from EventLog where event =:eventid");
        query.setLong("eventid",eventId);
       return (List<EventLog>)query.list();

    }

    public List<EventLog> getEventLogsByMemberandDateRange(Member member,DateRange dateRange){
        List<EventLog> eventLogs = new ArrayList<EventLog>();
        Query query = sessionFactory.getCurrentSession().createQuery("from EventLog el where el.member =:memberid and el.event.eventDate between :from and :to");
        query.setLong("memberid",member.getId());
        query.setDate("from",dateRange.getStartDate());
        query.setDate("to",dateRange.getEndDate());
        List<EventLog> logs = (List<EventLog>)query.list();
       logger.debug("got events between "+dateRange.getStartDate()+" and "+dateRange.getEndDate()+" size = "+logs.size());
        for(EventLog eventLog : logs){
            if (eventLog.getEvent().getEventDate().after(dateRange.getStartDate())){
                eventLogs.add(eventLog);
            }
        }
        return eventLogs;
    }
}
