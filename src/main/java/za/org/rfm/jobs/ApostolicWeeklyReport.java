package za.org.rfm.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Event;
import za.org.rfm.model.User;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.EventService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/23
 * Time: 11:47 AM
 */
@Component
public class ApostolicWeeklyReport implements Job {
    private static Logger logger = Logger.getLogger(ApostolicWeeklyReport.class);
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.messages_en_ZA", Locale.getDefault());

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

      emailService.apostolicSundayWeeklyReport(Constants.REPORT_FREQUENCY_WEEKLY);
      //now update assembly latest event info

       List<Assembly> assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
        for(Assembly a : assemblyList){
             List<Event> events = eventService.getEventsByAssemblyAndTypeAndDate(a.getAssemblyid(), Constants.SERVICE_TYPE_SUNDAY, new Timestamp(Utils.calcLastSunday(new Date()).getTime()));
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
                //now send the missing report alert!
                a.setUsers(assemblyService.getAssemblyUsers(a.getAssemblyid()));
                for(User user : a.getUsers()){
                    emailService.sendNotification(user,resourceBundle.getString("email.subject.missing.report"),resourceBundle.getString("email.body.missing.report"));
                }
                logger.info("Missing report for assembly : "+a.getName()+"  - alerts have been sent!");
            }
            assemblyService.saveAssembly(a);
        }
    }
}
