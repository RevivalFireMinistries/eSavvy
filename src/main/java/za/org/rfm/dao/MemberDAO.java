package za.org.rfm.dao;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.Member;
import za.org.rfm.model.MemberGroup;
import za.org.rfm.utils.Constants;

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
        //Query query = sessionFactory.getCurrentSession().createQuery("from Member where assembly =:assemblyid and status !=:status");
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("SELECT * from rfm_member where assembly = :assemblyId");
        query.addEntity(Member.class);
        query.setParameter("assemblyId", assemblyid);
        //query.setString("status",Constants.STATUS_DELETED);
        List<Member> memberList = (List<Member>)query.list();
        return memberList;
    }

    public List<MemberGroup> getMemberGroups(Long memberId){
        Query query = sessionFactory.getCurrentSession().createQuery("from MemberGroup mg where mg.member.id =:memberId");
        query.setLong("memberId",memberId);
        List<MemberGroup> memberGroupList = (List<MemberGroup>)query.list();
        return memberGroupList;
    }
     public void saveMemberGroup(MemberGroup memberGroup){
         if(memberGroup.getStatus().equalsIgnoreCase(Constants.STATUS_DELETED)){
             sessionFactory.getCurrentSession().delete(memberGroup);
         }else{
             sessionFactory.getCurrentSession().saveOrUpdate(memberGroup);
         }
     }

    public Member getMemberById(long memberid) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Member where id =:memberid");
        query.setLong("memberid",memberid);
        Member member = (Member)query.list().get(0);
        return member;
    }
    public boolean memberExists(Member member) {
        Query qry = sessionFactory.getCurrentSession().createQuery("From Member  where firstName  like ? and lastName like ?");
        qry.setString(0, "%"+member.getFirstName()+"%");
        qry.setString(1, "%"+member.getLastName()+"%");
        List<Member> members = (List<Member>)qry.list();

        for(Member member1 : members){
            if(member.equals(member1)){
                return true;
            }
        }
        return false;
    }
    public List<Member> getALLMembers() {
        Query query = sessionFactory.getCurrentSession().createQuery("from Member");

        List<Member> memberList = (List<Member>)query.list();
        return memberList;
    }

}