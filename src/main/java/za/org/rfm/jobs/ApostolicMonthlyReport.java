package za.org.rfm.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.EventService;

import javax.annotation.PostConstruct;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/18/14
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ApostolicMonthlyReport implements Job{
    private static Logger logger = Logger.getLogger(ApostolicWeeklyReport.class);

    private static EmailService emailService;
    private static EventService eventService;
    private static AssemblyService assemblyService;
    @PostConstruct
    public void init() {
        eventService = tmpEventService;
        emailService = tmpEmailService;
        assemblyService = tmpAssemblyService;
    }
    @Autowired
    private EventService tmpEventService;
    @Autowired
    private EmailService tmpEmailService;
    @Autowired
    private AssemblyService tmpAssemblyService;
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        logger.info("Now running job : generate sunday service monthly report");
        //execute the job here

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        emailService.apostolicSundayMonthlyReport(--currentMonth);

    }
}
