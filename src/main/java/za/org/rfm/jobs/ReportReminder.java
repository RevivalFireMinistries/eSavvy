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
 * Created by Russel on 2015-01-08.
 */
@Component
public class ReportReminder implements Job {
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

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
                 //first get all assemblies
        List<Assembly> assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
        for(Assembly assembly : assemblyList){
            List<Event> events = eventService.getEventsByAssemblyAndTypeAndDate(assembly.getAssemblyid(),Constants.SERVICE_TYPE_SUNDAY, new Timestamp(Utils.calcLastSunday(new Date()).getTime()));
            if(events.isEmpty()){
                logger.info("Found assembly which hasn't sent report :  notify! "+assembly.getName());
                //please send a reminder
                assembly.setUsers(assemblyService.getAssemblyUsers(assembly.getAssemblyid()));
                for(User user : assembly.getUsers()){
                    logger.info("send email to "+user.getEmail());
                    emailService.sendNotification(user,resourceBundle.getString("email.subject.report.reminder"),resourceBundle.getString("email.body.report.reminder"));
                }

            }
        }
    }
}
