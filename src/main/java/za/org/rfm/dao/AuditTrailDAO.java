package za.org.rfm.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.AuditTrail;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/18/14
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class AuditTrailDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveTrail(AuditTrail auditTrail){
        getSessionFactory().getCurrentSession().saveOrUpdate(auditTrail);
    }

}
