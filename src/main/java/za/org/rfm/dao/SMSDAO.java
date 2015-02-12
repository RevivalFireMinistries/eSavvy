package za.org.rfm.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.SMSLog;
import za.org.rfm.utils.DateRange;

import java.util.List;

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

    public List<SMSLog> getSMSLogByAssemblyAndDate(long assemblyId, DateRange dateRange) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(SMSLog.class);
        criteria.add(Restrictions.between("dateCreated",dateRange.getStartDate(),dateRange.getEndDate()));
        criteria.createAlias("member","m");
        criteria.createAlias("m.assembly","a");
        criteria.add(Restrictions.eq("a.assemblyid",assemblyId));
        return (List<SMSLog>)criteria.list();

    }
}
