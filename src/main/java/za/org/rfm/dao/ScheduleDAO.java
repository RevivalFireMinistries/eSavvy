package za.org.rfm.dao;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.primefaces.model.DefaultScheduleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.RFMScheduleEvent;
import za.org.rfm.utils.ScheduleLevel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/8/14
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ScheduleDAO {
    Logger logger = Logger.getLogger(ScheduleDAO.class);
    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    public void saveEvent(RFMScheduleEvent event){
        logger.debug("Now about to save...");
        sessionFactory.getCurrentSession().saveOrUpdate(event);
        logger.debug("Event saved successfully :" + event.getTitle());
    }
    public List<RFMScheduleEvent> getEventsByAssembly(String assemblyid) {
        Query query = sessionFactory.getCurrentSession().createQuery("from RFMScheduleEvent rfm where rfm.level =:level and rfm.ownerId =:id ");
        query.setInteger("level", ScheduleLevel.ASSEMBLY_LEVEL.ordinal());
        query.setString("id",assemblyid);
        List<RFMScheduleEvent> rfmScheduleEvents = (List<RFMScheduleEvent>)query.list();
        return rfmScheduleEvents;
    }

}
