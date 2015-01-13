package za.org.rfm.triggers;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;
import za.org.rfm.jobs.ApostolicMonthlyReport;
import za.org.rfm.jobs.ApostolicWeeklyReport;
import za.org.rfm.jobs.MemberInActivityChecker;
import za.org.rfm.jobs.ReportReminder;

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
          JobDetail memberInActivityChecker = JobBuilder.newJob(MemberInActivityChecker.class)
                    .withIdentity("memberInActivityChecker")
                    .build();
            JobDetail reportReminder = JobBuilder.newJob(ReportReminder.class)
                    .withIdentity("reportReminder")
                    .build();
            JobKey jobKeyA = new JobKey("ApostolicWeeklyReport", "apostolic");
            JobDetail apostolicWeekly = JobBuilder.newJob(ApostolicWeeklyReport.class)
                    .withIdentity(jobKeyA).build();

            JobKey jobKeyB = new JobKey("ApostolicMonthlyReport", "apostolic");
            JobDetail apostolicMonthly = JobBuilder.newJob(ApostolicMonthlyReport.class)
                    .withIdentity(jobKeyB).build();



            Trigger everyDayMidnight = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyDayMidnight")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 0 0 1/1 * ? *"))
                    .build();

            Trigger everyFirstDayOfMonth = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyFirstDayOfMonth")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 0 0 1 1/1 ? *"))
                    .build();

            Trigger everyTuesdayMidnight = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyTuesdayMidnight")
                    .withSchedule(
                            //CronScheduleBuilder.cronSchedule("0 0 0 ? * WED *"))
                            CronScheduleBuilder.cronSchedule("0 0 9 ? * TUE *"))  //TODO:need to revert to midnight
                                    .build();
            Trigger everyMondayMidnight = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyMondayMidnight")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 59 23 ? * MON *"))
                    .build();

            SchedulerFactory schFactory = new StdSchedulerFactory();
            Scheduler sch = schFactory.getScheduler();
            sch.start();

            // Tell quartz to schedule the job using the trigger
            sch.scheduleJob(memberInActivityChecker, everyDayMidnight);
            sch.scheduleJob(apostolicWeekly,everyTuesdayMidnight);
            sch.scheduleJob(apostolicMonthly,everyFirstDayOfMonth);
            sch.scheduleJob(reportReminder,everyMondayMidnight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
