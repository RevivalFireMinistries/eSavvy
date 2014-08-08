package za.org.rfm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import za.org.rfm.dao.MemberDAO;
import za.org.rfm.model.EventLog;
import za.org.rfm.model.Member;
import za.org.rfm.model.MemberGroup;
import za.org.rfm.model.SystemVar;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;
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
        if(logs.isEmpty())
            return true;
        return false;

    }
}
