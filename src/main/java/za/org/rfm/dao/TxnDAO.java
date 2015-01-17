package za.org.rfm.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.beans.Tithe;
import za.org.rfm.model.Account;
import za.org.rfm.model.Member;
import za.org.rfm.model.Transaction;
import za.org.rfm.utils.DateRange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/10
 * Time: 8:01 AM
 */
@Repository
public class TxnDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    public void saveTxn(Transaction txn){
        sessionFactory.getCurrentSession().saveOrUpdate(txn);
    }

    public List<Transaction> getTithesByMemberAndDateRange(Member member, DateRange dateRange) {
        Account account = member.getAccount();
        if(account != null){
        Query query;
        if(dateRange == null){
            query   = sessionFactory.getCurrentSession().createQuery("from Transaction where account = :account order by txndate desc");
        }
        else if(dateRange.getStartDate() == null && dateRange.getEndDate() == null){
            query   = sessionFactory.getCurrentSession().createQuery("from Transaction where account = :account order by txndate desc");
        } else {
         query   = sessionFactory.getCurrentSession().createQuery("from Transaction where account = :account and txndate between :startDate and :endDate order by txndate desc");
            query.setDate("startDate",dateRange.getStartDate());
            query.setDate("endDate",dateRange.getEndDate());
        }

        query.setLong("account",account.getAccountnumber());

        List<Transaction> txns = (List<Transaction>)query.list();

        return txns;
        }
        return new ArrayList<Transaction>();
    }
}
