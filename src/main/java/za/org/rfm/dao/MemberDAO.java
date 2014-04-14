package za.org.rfm.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.Member;

import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 9:59 PM
 */
@Repository
public class MemberDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveMember(Member member){
        sessionFactory.getCurrentSession().saveOrUpdate(member);
    }




    public List<Member> getMembersByAssembly(long assemblyid) {
        System.out.println("---assembly id "+assemblyid);
        Query query = sessionFactory.getCurrentSession().createQuery("from Member where assembly =:assemblyid");
        query.setLong("assemblyid",assemblyid);
        List<Member> memberList = (List<Member>)query.list();
        return memberList;
    }




    public Member getMemberById(long memberid) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Member where id =:memberid");
        query.setLong("memberid",memberid);
        Member member = (Member)query.list().get(0);
        return member;
    }
}