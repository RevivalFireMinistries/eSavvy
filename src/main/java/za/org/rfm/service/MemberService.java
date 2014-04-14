package za.org.rfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.MemberDAO;
import za.org.rfm.model.Member;

import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 9:57 PM
 */
@Service("MemberService")
@Transactional(readOnly = true)
public class MemberService {
    @Autowired
    MemberDAO memberDAO;

    @Transactional(readOnly = false)
    public void saveMember(Member member){
        memberDAO.saveMember(member);
    }

    public List<Member> getMembersByAssembly(long assemblyid) {
        return memberDAO.getMembersByAssembly(assemblyid);
    }

    public Member getMemberById(long memberid) {
        return memberDAO.getMemberById(memberid);
    }
}
