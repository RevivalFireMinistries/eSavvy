package za.org.rfm.dao;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.Bulletin;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/15/14
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class BulletinDAO {
    Logger logger = Logger.getLogger(BulletinDAO.class);
    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveBulletin(Bulletin bulletin){
        sessionFactory.getCurrentSession().saveOrUpdate(bulletin);
    }

    public List<Bulletin> getActiveBulletins(Date date,String status){
        Query query = getSessionFactory().getCurrentSession().createQuery("from Bulletin b where b.endDate > :date and status =:status");
        query.setParameter("date",date);
        query.setString("status",status);
        List<Bulletin> bulletins = (List<Bulletin>)query.list();

        return bulletins;
    }
}
