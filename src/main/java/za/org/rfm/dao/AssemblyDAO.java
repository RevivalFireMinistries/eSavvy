package za.org.rfm.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.dto.MemberMonthlyTitheTotals;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;
import za.org.rfm.model.Transaction;
import za.org.rfm.model.User;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.TxnService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 10:56 PM
 */
@Repository
public class AssemblyDAO {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private MemberService memberService;
    @Autowired
    private TxnService txnService;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Assembly> getAssemblyList(String status){
        Query query;
        if(Constants.STATUS_ALL.equalsIgnoreCase(status)){
           query=  sessionFactory.getCurrentSession().createQuery("from Assembly ");
        }else{
          query =  sessionFactory.getCurrentSession().createQuery("from Assembly where status=:status");
          query.setString("status",status);
        }

        return (List<Assembly>)query.list();
    }


    public Assembly getAssemblyById(long l) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Assembly where id=:id");
        query.setLong("id",l);
        Assembly assembly = (Assembly)query.list().get(0);
        System.out.println("----got an assembly ---"+assembly.name);
        assembly.setMembersRegistered(assembly.getTotalRegistered());
        return assembly;
    }

    public void saveMember(Assembly assembly) {
        getSessionFactory().getCurrentSession().saveOrUpdate(assembly);
    }

    public List<User> getAssemblyUsers(Long id) {
       Query query = sessionFactory.getCurrentSession().createQuery("from User where assembly =:id");
        query.setLong("id",id);
        return (List<User>)query.list();
    }

    public List<MemberMonthlyTitheTotals> getMemberMonthlyTitheTotals(Long assemblyId) {
        List<MemberMonthlyTitheTotals> memberMonthlyTitheTotalsList = new ArrayList<MemberMonthlyTitheTotals>();
        List<Member> memberList = memberService.getMembersByAssembly(assemblyId);
        MemberMonthlyTitheTotals mmt;
        for(Member member : memberList){
            mmt = new MemberMonthlyTitheTotals();
            mmt.setMember(member);
            for(int i=0;i<12;i++){
                List<Transaction> transactions = txnService.getTithesByMemberAndDateRange(member, Utils.getMonthDateRange(i));
                mmt.getTotals()[i] = Utils.getTxnTotal(transactions);
            }
           memberMonthlyTitheTotalsList.add(mmt);
        }

        return memberMonthlyTitheTotalsList;
    }
}

