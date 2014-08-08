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
        List<Member> backSlidenMembers;
        List<Member> rejuvenatedMembers;

        //check inactivity for each member in the entire system
        //TODO:Might need to be for a particular assembly s this can prove to be a large task the bigger the church
        logger.info("Now executing inactivity checker job...");
        List<Assembly> assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
        for(Assembly assembly : assemblyList){
            backSlidenMembers = new ArrayList<Member>();
            rejuvenatedMembers = new ArrayList<Member>();
            assembly.setMembers(memberService.getMembersByAssembly(assembly.getAssemblyid()));
            for(Member member : assembly.getMembers()){
                if(memberService.isInActive(member) && member.getStatus().equalsIgnoreCase(Constants.STATUS_ACTIVE)){
                    logger.debug("got a backslider!!! : "+member.getFullName());
                    //change his status
                    member.setStatus(Constants.STATUS_IN_ACTIVE); //TODO: Consider sending a motivational sms
                    memberService.saveMember(member);
                    backSlidenMembers.add(member);
                }
                else if(!memberService.isInActive(member) && member.getStatus().equalsIgnoreCase(Constants.STATUS_IN_ACTIVE)){
                    logger.debug("got a member who used to be inactive but now active : "+member.getFullName());
                    //change his status
                    member.setStatus(Constants.STATUS_ACTIVE);  //TODO: Consider welcoming them back to church
                    memberService.saveMember(member);
                    rejuvenatedMembers.add(member);
                }
                else if(memberService.isInActive(member) && member.getStatus().equalsIgnoreCase(Constants.STATUS_IN_ACTIVE)){
                    //They have not come to church for a long time now //TODO: Pastor needs to do something here!!
                } else{
                    //This member has been consistent for a long time - give that man a bells!!!
                    //TODO: might consider sending a Thank you for being committed sms

                }
            }
            //now send emails to the pastors
            if(!backSlidenMembers.isEmpty())
                emailService.memberActivityReport(backSlidenMembers,assembly,Constants.MEMBERS_INACTIVE);
            if(!rejuvenatedMembers.isEmpty())
            emailService.memberActivityReport(rejuvenatedMembers,assembly,Constants.MEMBERS_ACTIVE_AGAIN);

        }
    }
}
