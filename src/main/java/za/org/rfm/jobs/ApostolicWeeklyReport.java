package za.org.rfm.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Event;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.EventService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;
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
       logger.info("Now running job : generate sunday service weekly report");
      //execute the job here

      emailService.apostolicReport(Constants.REPORT_FREQUENCY_WEEKLY);
      //now update assembly latest event info

       List<Assembly> assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
        for(Assembly a : assemblyList){
             List<Event> events = eventService.getEventsByAssemblyAndTypeAndDateRange(a.getAssemblyid(),Constants.SERVICE_TYPE_SUNDAY,new DateRange(Utils.calcLastSunday(new Date()),Utils.calcLastSunday(new Date())));
            if(events != null && !events.isEmpty()){
                Event event = events.get(0);
                a.setLatestAttendance(event.getAttendance());
                a.setLatestOffering(event.getOfferings());
                a.setLatestTithe(event.getTithes());
            }
            else{      //no event report so set all to zero
                a.setLatestTithe(0);
                a.setLatestAttendance(0);
                a.setLatestOffering(0);
            }
            assemblyService.saveAssembly(a);
        }
    }
}
