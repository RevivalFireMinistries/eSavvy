package za.org.rfm.triggers;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;
import za.org.rfm.jobs.*;

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

            JobKey jobKeyC = new JobKey("SmSBillMonthlyReport", "apostolic");
            JobDetail smsBillJob = JobBuilder.newJob(SmsBillJob.class)
                    .withIdentity(jobKeyC).build();



            Trigger everyDayMidnight = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyDayMidnight")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 0 0 1/1 * ? *"))
                    .build();

            Trigger everyFiveMinutes = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyFiveMinutes")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 0/5 * 1/1 * ? *"))
                    .build();
            Trigger everyTwoMinutes = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyTwoMinutes")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 0/2 * 1/1 * ? *"))
                    .build();

            Trigger everyFirstDayOfMonth = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyFirstDayOfMonth")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 0 0 1 1/1 ? *"))
                    .build();

            Trigger everyFirstDayOfMonth2 = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyFirstDayOfMonth1")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 0 1 1 1/1 ? *"))
                    .build();

            Trigger everyTuesdayMidnight = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyTuesdayMidnight")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 5 0 ? * WED *"))
                    .build();
            Trigger everyMondayMidnight = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyMondayMidnight")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 59 23 ? * MON *"))
                    .build();
            Trigger everyFridayMidnight = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyFridayMidnight")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 59 23 ? * FRI *"))
                    .build();

            SchedulerFactory schFactory = new StdSchedulerFactory();
            Scheduler sch = schFactory.getScheduler();
            sch.start();

            // Tell quartz to schedule the job using the trigger
            sch.scheduleJob(memberInActivityChecker, everyFirstDayOfMonth2);
            sch.scheduleJob(apostolicWeekly,everyTuesdayMidnight);
            sch.scheduleJob(apostolicMonthly,everyFirstDayOfMonth);
            sch.scheduleJob(reportReminder,everyMondayMidnight);
            sch.scheduleJob(smsBillJob,everyFirstDayOfMonth2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
