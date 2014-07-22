package za.org.rfm.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.EventService;
import za.org.rfm.utils.Constants;

import javax.annotation.PostConstruct;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/23
 * Time: 11:47 AM
 */
@Component
public class ApostolicWeeklyReport implements Job {
    private static Logger logger = Logger.getLogger(ApostolicWeeklyReport.class);

    private static EmailService emailService;
    private static EventService eventService;
    @PostConstruct
    public void init() {
        eventService = tmpEventService;
        emailService = tmpEmailService;
    }
    @Autowired
    private EventService tmpEventService;
    @Autowired
    private EmailService tmpEmailService;
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
       logger.info("Now running job : generate sunday service weekly report");
      //execute the job here

      emailService.apostolicReport(Constants.REPORT_FREQUENCY_MONTHLY);

    }
}
