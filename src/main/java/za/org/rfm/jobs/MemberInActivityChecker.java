package za.org.rfm.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.Constants;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/7/14
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MemberInActivityChecker implements Job{
    Logger logger = Logger.getLogger(MemberInActivityChecker.class);
    private static AssemblyService assemblyService;
    private static MemberService memberService;
    private static EmailService emailService;
    @Autowired
    private AssemblyService tmpAssemblyService;
    @Autowired
    private MemberService tmpMemberService;
    @Autowired
    private EmailService tmpEmailService;

    @PostConstruct
    public void init() {
        assemblyService  = tmpAssemblyService;
        memberService = tmpMemberService;
        emailService = tmpEmailService;
    }
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<Member> nonTithersList;

        //check inactivity for each member in the entire system
        //TODO:Might need to be for a particular assembly s this can prove to be a large task the bigger the church
        logger.info("Now executing inactivity checker job...");
        List<Assembly> assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
        for(Assembly assembly : assemblyList){
            nonTithersList = new ArrayList<Member>();
            assembly.setMembers(memberService.getMembersByAssembly(assembly.getAssemblyid()));
            for(Member member : assembly.getMembers()){
                if(memberService.isNotTithing(member)){
                    logger.debug("got a non-tither!!! : "+member.getFullName());
                    //change his status
                    member.setStatus(Constants.STATUS_NEEDS_FOLLOW_UP);
                    memberService.saveMember(member);
                    nonTithersList.add(member);
                }
            }
            //now send emails to the pastors
            if(!nonTithersList.isEmpty())
                emailService.memberActivityReport(nonTithersList,assembly,Constants.MEMBERS_INACTIVE);

        }
    }
}
