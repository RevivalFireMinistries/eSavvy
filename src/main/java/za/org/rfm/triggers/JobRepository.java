package za.org.rfm.triggers;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.stereotype.Component;
import za.org.rfm.jobs.ApostolicWeeklyReport;

import javax.annotation.PostConstruct;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/23
 * Time: 1:05 PM
 */
@Component
public class JobRepository {
    @PostConstruct
    public void init() {

        try {
          JobDetail job = JobBuilder.newJob(ApostolicWeeklyReport.class)
                    .withIdentity("apostolicWeeklyEmail")
                    .build();
            //Trigger the job to run on the next round minute
           /* Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(Integer.parseInt(Utils.getResource("report.frequency.apostolic")))
                                    .repeatForever())
                    .build();*/
           /* Trigger triggerCron = TriggerBuilder
                    .newTrigger()
                    .withIdentity("apostolicWeeklyEmail")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("20 0 * * * ?"))
                    .build();

            SchedulerFactory schFactory = new StdSchedulerFactory();
            Scheduler sch = schFactory.getScheduler();
            sch.start();

            // Tell quartz to schedule the job using the trigger
            sch.scheduleJob(job, triggerCron);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
