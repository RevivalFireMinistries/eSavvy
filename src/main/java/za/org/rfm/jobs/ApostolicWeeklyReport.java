package za.org.rfm.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.model.Event;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.EventService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

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
        //first calculate the date of the last sunday
        Date lastSunday = Utils.calcLastSunday(new Date(System.currentTimeMillis()));
        //now get all events for this day
        List<Event> eventList = eventService.getEventsByDateAndType(Constants.SERVICE_TYPE_SUNDAY,lastSunday);
     //now generate email with results
        if(eventList.isEmpty()){
           logger.info("---REPORT GENERATION SKIPPED : NO EVENTS");
        } else{
            emailService.sendWeeklyEventsSummaryEmail(eventList);
        }

    }
}