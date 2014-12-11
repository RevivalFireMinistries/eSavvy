package za.org.rfm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import za.org.rfm.dao.MemberDAO;
import za.org.rfm.model.*;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;
import za.org.rfm.utils.Group;
import za.org.rfm.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 9:57 PM
 */
@Service("MemberService")
@Transactional(readOnly = true)
public class MemberService {
    Logger logger = Logger.getLogger(MemberService.class);
    @Autowired
    MemberDAO memberDAO;
    @Autowired
    private SystemVarService systemVarService;
    @Autowired
    private EventService eventService;
    @Autowired
    private TxnService txnService;

    @Transactional(readOnly = false)
    public void saveMember(Member member){
        memberDAO.saveMember(member);
    }

    public List<Member> getMembersByAssembly(long assemblyid) {
        return memberDAO.getMembersByAssembly(assemblyid);
    }

    public Member getMemberById(long memberId) {
        return memberDAO.getMemberById(memberId);
    }
    public List<MemberGroup> getMemberGroups(long memberId){
        return  memberDAO.getMemberGroups(memberId);
    }
    @Transactional(readOnly = false)
    public void saveMemberGroup(MemberGroup memberGroup){
        memberDAO.saveMemberGroup(memberGroup);
        logger.debug("Group saved successfully : "+memberGroup.getMember().getFullName()+" - "+memberGroup.getGroupName());
    }
    public boolean memberExists(Member member){
        return memberDAO.memberExists(member);
    }
    public boolean isInActive(Member member){
        //get the number of services to look at sunday services
        int weekCounter = 4; //the default
        SystemVar var = (systemVarService.getSystemVarByNameUnique(Constants.NUMBER_OF_WEEKS_TO_RENDER_INACTIVE));
        if(var != null && !StringUtils.isEmpty(var.getValue())){
            weekCounter = Integer.parseInt(var.getValue());
           logger.debug("using configured inactivity week counter : "+weekCounter);
        }
         DateRange dateRange = Utils.goBackXWeeks(new Date(),weekCounter);
        //now check if this member has any event logs for him during this period
         List<EventLog> logs = eventService.getEventLogsByMemberandDateRange(member,dateRange);
        logger.debug("found "+logs.size()+" event logs for "+member.getFullName());
        //Now look for financial transactions for the specified period
        List<Transaction> transactions = txnService.getTithesByMemberAndDateRange(member,dateRange);

        if(logs.isEmpty() && transactions.isEmpty())
            return true;
        return false;

    }
    public java.util.List<za.org.rfm.model.Member> getALLMembers() {
        return memberDAO.getALLMembers();
    }

    @Transactional(readOnly = false)
    public void addToEveryone(Member member) {
        //Add him to the general group - for everyone
        MemberGroup memberGroup = new MemberGroup();
        memberGroup.setStatus(Constants.STATUS_ACTIVE);
        memberGroup.setDateCreated(new Date());
        memberGroup.setGroupName(Group.EVERYONE.name());//default group
        memberGroup.setMember(member);
        saveMemberGroup(memberGroup);
    }
}
