package za.org.rfm.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.SMSLog;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/02
 * Time: 2:43 PM
 */
@Repository
public class SMSDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    public void saveSMSLog(SMSLog smsLog){
        sessionFactory.getCurrentSession().save(smsLog);
    }
}
