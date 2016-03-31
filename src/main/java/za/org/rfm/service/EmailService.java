package za.org.rfm.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import za.org.rfm.beans.Tithe;
import za.org.rfm.dto.Bill;
import za.org.rfm.dto.EventTotals;
import za.org.rfm.model.*;
import za.org.rfm.utils.*;
import za.org.rfm.utils.Role;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/17
 * Time: 4:03 PM
 */
@Service("mailService")
public class EmailService {
    public static final Date LAST_SUNDAY = Utils.calcLastSunday(new Date());
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.messages_en_ZA", Locale.getDefault());
    Logger logger = Logger.getLogger(getClass());
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SimpleMailMessage alertMailMessage;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private SystemVarService systemVarService;
    @Autowired
    private UserService userService;
    @Autowired
    private AssemblyService assemblyService;
    @Autowired
    private EventService eventService;

    public void sendWelcomeEmail(User user) {

        try {
            if (user != null && user.getEmail() != null) {
                //Now validate email first
                EmailFormatValidator emailFormatValidator = new EmailFormatValidator();
                if (emailFormatValidator.validate(user.getEmail())) {
                    logger.debug("Email address validation...OK...process");
                    //now we can proceeed
                    String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                    String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();
                    final Context ctx = new Context(Locale.ENGLISH);
                    ctx.setVariable("name", user.getFullname());
                    ctx.setVariable("username", user.getUsername());
                    ctx.setVariable("password", user.getPassword());
                    ctx.setVariable("roles", user.getRoleToString());
                    ctx.setVariable("assembly", user.getAssembly().getName());
                    ctx.setVariable("eSavvyLink", eSavvyLink);
                    ctx.setVariable("churchName", churchName);


                    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                    message.setSubject(getResource("email.subject.account.welcome"));
                    message.setFrom(getResource("email.system.from"));
                    message.setTo(user.getEmail());

                    // Create the HTML body using Thymeleaf
                    final String htmlContent = this.templateEngine.process("../email/welcome", ctx);
                    message.setText(htmlContent, true); // true = isHtml

                    logger.debug("Email setup complete...now send!");
                    // Send mail
                    this.mailSender.send(mimeMessage);
                    logger.debug("Email sent!");
                }
            } else {
                logger.error("Email address validation failed or insufficient user info...abort!");
            }

        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Email sending error: " + e.getMessage());
        }

    }
    public void sendNotification(String emailAddress, String subject, String... messages) {

        try {
            if (!StringUtils.isEmpty(emailAddress)) {
                //Now validate email first
                EmailFormatValidator emailFormatValidator = new EmailFormatValidator();
                if (emailFormatValidator.validate(emailAddress)) {
                    logger.debug("Email address validation...OK...process");
                    //now we can proceeed
                    String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                    String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();
                    final Context ctx = new Context(Locale.ENGLISH);
                    ctx.setVariable("name","");
                    ctx.setVariable("messages", messages);
                    ctx.setVariable("eSavvyLink", eSavvyLink);
                    ctx.setVariable("churchName", churchName);


                    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                    message.setSubject(subject);
                    message.setFrom(getResource("email.system.from"));
                    message.setTo(emailAddress);

                    // Create the HTML body using Thymeleaf
                    final String htmlContent = this.templateEngine.process("../email/notification", ctx);
                    message.setText(htmlContent, true); // true = isHtml

                    logger.debug("Email setup complete...now send!");
                    // Send mail
                    this.mailSender.send(mimeMessage);
                    logger.info("Email sent!");
                }
            } else {
                logger.error("Email address validation failed or insufficient user info...abort!");
            }

        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Email sending error: " + e.getMessage());
        }

    }

    public void sendNotification(User user, String subject, String... messages) {

        try {
            if (user != null && user.getEmail() != null) {
                //Now validate email first
                EmailFormatValidator emailFormatValidator = new EmailFormatValidator();
                if (emailFormatValidator.validate(user.getEmail())) {
                    logger.debug("Email address validation...OK...process");
                    //now we can proceeed
                    String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                    String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();
                    final Context ctx = new Context(Locale.ENGLISH);
                    ctx.setVariable("name", user.getFullname());
                    ctx.setVariable("messages", messages);
                    ctx.setVariable("eSavvyLink", eSavvyLink);
                    ctx.setVariable("churchName", churchName);


                    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                    message.setSubject(subject);
                    message.setFrom(getResource("email.system.from"));
                    message.setTo(user.getEmail());

                    // Create the HTML body using Thymeleaf
                    final String htmlContent = this.templateEngine.process("../email/notification", ctx);
                    message.setText(htmlContent, true); // true = isHtml

                    logger.debug("Email setup complete...now send!");
                    // Send mail
                    this.mailSender.send(mimeMessage);
                    logger.info("Email sent!");
                }
            } else {
                logger.error("Email address validation failed or insufficient user info...abort!");
            }

        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Email sending error: " + e.getMessage());
        }

    }

    public void eventReport(Event event) {
        try {

            event.getAssembly().setUsers(assemblyService.getAssemblyUsers(event.getAssembly().getAssemblyid()));
            List<User> userList = event.getAssembly().getUsers(); //send an email to every user under this assembly
            if (userList.isEmpty()) {
                logger.error("No users set for this assembly...can't send email " + event.getAssembly().getName());
                return;
            }

            for (User user : userList) {
                if (user.getEmail() != null) {
                    logger.info("Found the user for " + event.getAssembly().getName() + " : " + user.getFullname() + " email : " + user.getEmail());
                    //Now validate email first
                    EmailFormatValidator emailFormatValidator = new EmailFormatValidator();
                    if (emailFormatValidator.validate(user.getEmail())) {
                        logger.debug("Email address validation...OK...process");
                        //now we can proceeed
                        String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                        String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();
                        Map<String, String> items = new LinkedHashMap<String, String>();
                        items.put("Assembly", event.getAssembly().getName());
                        items.put("Date", event.getEventDateFormatted());
                        items.put("Type", event.getEventType());
                        items.put("Attendance", event.getAttendance() + "");
                        items.put("Tithes", event.getTitheFormatted());
                        items.put("Offering", event.getOfferingFormatted());
                        items.put("Total Income", event.getTotalIncomeFormatted());
                        items.put("Comments", event.getComment());
                        String reportIntro = getResource("email.report.assembly.service.introduction", event.getEventType());
                        List<String> messages = new ArrayList<String>();
                        messages.add(reportIntro);
                        final Context ctx = new Context(Locale.ENGLISH);
                        ctx.setVariable("name", user.getFullname());
                        ctx.setVariable("messages", messages);
                        ctx.setVariable("items", items);
                        ctx.setVariable("assembly", event.getAssembly().getName());
                        ctx.setVariable("eSavvyLink", eSavvyLink);
                        ctx.setVariable("churchName", churchName);


                        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                        message.setSubject(getResource("email.subject.assembly.report", event.getAssembly().getName(), event.getEventType(), event.getEventDateFormatted()));
                        message.setFrom(getResource("email.system.from"));
                        message.setTo(user.getEmail());

                        // Create the HTML body using Thymeleaf
                        final String htmlContent = this.templateEngine.process("../email/bean-report", ctx);
                        message.setText(htmlContent, true); // true = isHtml

                        logger.debug("Email setup complete...now send!");
                        // Send mail
                        this.mailSender.send(mimeMessage);
                        logger.debug("Email sent!");
                    }
                }
            }


        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Email sending error: " + e.getMessage());
        }
    }
    public void sendOutBillTotal(List<Bill> bills,DateRange dateRange) {
        try {
            //Now prepare the email
            GlobarEmailVars globarEmailVars = new GlobarEmailVars().invoke();
            String eSavvyLink = globarEmailVars.geteSavvyLink();
            String churchName = globarEmailVars.getChurchName();

            final Context ctx = new Context(Locale.ENGLISH);

            String reportIntro = "Monthly SMS Bill Breakdown "+dateRange;

            ctx.setVariable("eSavvyLink", eSavvyLink);
            ctx.setVariable("churchName", churchName);
            ctx.setVariable("header", reportIntro);


            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
            List<String> messages = new ArrayList<String>();
            messages.add(reportIntro);
            Map<String, String> items = new LinkedHashMap<String, String>();
            double totalUnits =0;
            double totalAmount =0;

            for(Bill bill : bills){
                items.put(bill.getNarration(),bill.getUnits()+" - "+Utils.moneyFormatter(bill.getTotal(),Locale.getDefault()));
                totalAmount += bill.getTotal();
                totalUnits += bill.getUnits();
            }
            items.put("Total Units ",""+totalUnits);
            items.put("Total Amount ",Utils.moneyFormatter(totalAmount,Locale.getDefault()));


            ctx.setVariable("items", items);
            ctx.setVariable("messages",messages);

            message.setSubject(reportIntro);
            message.setFrom(getResource("email.system.from"));

            User user = userService.getSuperUser();
            if(user == null){
                logger.error("Failed to get super user...exit email sending!");
                return;
            }
            message.setTo(user.getEmail());

            // Create the HTML body using Thymeleaf
            final String htmlContent = this.templateEngine.process("../email/bean-report", ctx);
            message.setText(htmlContent, true); // true = isHtml

            logger.debug("Email setup complete...now send!");
            // Send mail
            this.mailSender.send(mimeMessage);

            logger.debug("Email sent!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendOutBill(Assembly assembly,Bill bill){

        try {
            //Now prepare the email
            GlobarEmailVars globarEmailVars = new GlobarEmailVars().invoke();
            String eSavvyLink = globarEmailVars.geteSavvyLink();
            String churchName = globarEmailVars.getChurchName();

            final Context ctx = new Context(Locale.ENGLISH);

            ctx.setVariable("eSavvyLink", eSavvyLink);
            ctx.setVariable("churchName", churchName);
            ctx.setVariable("bill", bill);
            ctx.setVariable("header", bill.getNarration());

            String reportIntro = bill.getNarration();

            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
            List<String> messages = new ArrayList<String>();
            messages.add(reportIntro);
            Map<String, String> items = new LinkedHashMap<String, String>();
            items.put("Narration", bill.getNarration());
            items.put("Date Range", bill.getDateRange().toString());
            items.put("Total units", ""+bill.getUnits());
            items.put("Price Per Unit",Utils.moneyFormatter(bill.getPrice(),assembly.getLocaleObject()));
            items.put("Total Cost", Utils.moneyFormatter(bill.getTotal(),assembly.getLocaleObject()));
            ctx.setVariable("items", items);
            ctx.setVariable("assembly",assembly.getName());
            ctx.setVariable("messages",messages);

            message.setSubject(bill.getNarration());
            message.setFrom(getResource("email.system.from"));

            List<User> userList = assemblyService.getAssemblyUsers(assembly.getAssemblyid());

            for(User user : userList){
                message.setTo(user.getEmail());

                // Create the HTML body using Thymeleaf
                final String htmlContent = this.templateEngine.process("../email/bean-report", ctx);
                message.setText(htmlContent, true); // true = isHtml

                logger.debug("Email setup complete...now send!");
                // Send mail
                this.mailSender.send(mimeMessage);
            }

            logger.debug("Email sent!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void apostolicSundayMonthlyReport(int month) {
        try {
            //DateRange dateRange = Utils.calcLastMonthDateRange(new Date());
            DateRange dateRange = Utils.getMonthDateRange(month);
            List<Assembly> assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
            Map<Assembly,List<Event>> assemblyListMap = new HashMap<Assembly, List<Event>>();
            List<EventTotals> eventTotalsList = new ArrayList<EventTotals>();

            for(Assembly assembly : assemblyList){
                List<Event> eventList = eventService.getEventsByAssemblyAndTypeAndDateRange(assembly.getAssemblyid(), Constants.STATUS_ALL, dateRange);
                assemblyListMap.put(assembly,eventList);
            }

            //now calculate the totals
            EventTotals eventTotals;
            for(Map.Entry<Assembly,List<Event>> entry : assemblyListMap.entrySet()){
                Assembly assembly = entry.getKey();
                eventTotals = new EventTotals(assembly,entry.getValue());
                eventTotalsList.add(eventTotals);
            }

            //Now calculate total of totals
            EventTotals totalOfTotals = new EventTotals(eventTotalsList);

            //Now prepare the email
            GlobarEmailVars globarEmailVars = new GlobarEmailVars().invoke();
            String apostolicEmail = globarEmailVars.getApostolicEmail();
            String eSavvyLink = globarEmailVars.geteSavvyLink();
            String churchName = globarEmailVars.getChurchName();

            CheckIfApostleNotNull checkIfApostleNotNull = new CheckIfApostleNotNull(apostolicEmail).invoke();
            if (checkIfApostleNotNull.is()) return;
            apostolicEmail = checkIfApostleNotNull.getApostolicEmail();

            final Context ctx = new Context(Locale.ENGLISH);

            ctx.setVariable("eSavvyLink", eSavvyLink);
            ctx.setVariable("churchName", churchName);
            ctx.setVariable("assemblyEventTotals", eventTotalsList);
            ctx.setVariable("totalOfTotals", totalOfTotals);
            ctx.setVariable("header", getResource("email.subject.apostolic.report", Constants.REPORT_FREQUENCY_MONTHLY, dateRange));

            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
            message.setSubject(getResource("email.subject.apostolic.report", Constants.REPORT_FREQUENCY_MONTHLY, dateRange));
            message.setFrom(getResource("email.system.from"));
            message.setTo(apostolicEmail);

            // Create the HTML body using Thymeleaf
            final String htmlContent = this.templateEngine.process("../email/apostolic-monthly-report", ctx);
            message.setText(htmlContent, true); // true = isHtml

            logger.debug("Email setup complete...now send!");
            // Send mail
            this.mailSender.send(mimeMessage);
            logger.debug("Email sent!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }

    public void apostolicSundayWeeklyReport(List<Event> events) {

        try {


            if (events != null && !events.isEmpty()) {
                //we have something to report on - @ least
                Event totals = new Event();
                for (Event event : events) {
                    totals.setAttendance(totals.getAttendance() + event.getAttendance());
                    totals.setOfferings(totals.getOfferings() + event.getOfferings());
                    totals.setTithes(totals.getTithes() + event.getTithes());
                }
                GlobarEmailVars globarEmailVars = new GlobarEmailVars().invoke();
                String apostolicEmail = globarEmailVars.getApostolicEmail();
                String eSavvyLink = globarEmailVars.geteSavvyLink();
                String churchName = globarEmailVars.getChurchName();
                logger.info("Got the apostolic email : " + apostolicEmail);
                CheckIfApostleNotNull checkIfApostleNotNull = new CheckIfApostleNotNull(apostolicEmail).invoke();
                if (checkIfApostleNotNull.is()) return;
                apostolicEmail = checkIfApostleNotNull.getApostolicEmail();

                final Context ctx = new Context(Locale.ENGLISH);

                ctx.setVariable("eSavvyLink", eSavvyLink);
                ctx.setVariable("churchName", churchName);
                ctx.setVariable("events", events);
                ctx.setVariable("totals", totals);
                ctx.setVariable("header", getResource("email.subject.apostolic.report", Constants.REPORT_FREQUENCY_WEEKLY, LAST_SUNDAY));



                final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                message.setSubject(getResource("email.subject.apostolic.report", Constants.REPORT_FREQUENCY_WEEKLY, LAST_SUNDAY));
                message.setFrom(getResource("email.system.from"));
                message.setTo(apostolicEmail);

                // Create the HTML body using Thymeleaf
                final String htmlContent = this.templateEngine.process("../email/apostolic-report", ctx);
                message.setText(htmlContent, true); // true = isHtml

                logger.debug("Email setup complete...now send!");
                // Send mail
                this.mailSender.send(mimeMessage);
                logger.debug("Email sent!");

            } else {
                logger.error("No events found for the specified criteria...abort!");
            }

        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Email sending error: " + e.getMessage());
        }
    }

    public void followUpReport(List<EventFollowUp> eventFollowUpList, Date date, Event event) {

        try {
            event.getAssembly().setUsers(assemblyService.getAssemblyUsers(event.getAssembly().getAssemblyid()));
            List<User> userList = event.getAssembly().getUsers(); //send an email to every user under this assembly
            if (userList.isEmpty()) {
                logger.error("No users set for this assembly...can't send email " + event.getAssembly().getName());
                return;
            }

            for (User user : userList) {

                if(user.getEmail() == null || user.getEmail().isEmpty()){
                    logger.error("Invalid/non existent email address for user : "+user.getFullname());
                    continue;
                }

                if (eventFollowUpList != null && !eventFollowUpList.isEmpty()) {
                    //we have something to report on - @ least

                    String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                    String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();

                    final Context ctx = new Context(Locale.ENGLISH);

                    ctx.setVariable("eSavvyLink", eSavvyLink);
                    ctx.setVariable("churchName", churchName);
                    ctx.setVariable("name", user.getFullname());
                    ctx.setVariable("people", eventFollowUpList);
                    ctx.setVariable("header", getResource("email.subject.follow.up.report", event.getAssembly().getName(), date));


                    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                    ctx.setVariable("header", getResource("email.subject.follow.up.report", event.getAssembly().getName(), date));
                    message.setSubject(getResource("email.subject.follow.up.report", event.getAssembly().getName(), date));
                    message.setFrom(getResource("email.system.from"));
                    message.setTo(user.getEmail());

                    // Create the HTML body using Thymeleaf
                    final String htmlContent = this.templateEngine.process("../email/follow-up-report", ctx);
                    message.setText(htmlContent, true); // true = isHtml

                    logger.debug("Email setup complete...now send!");
                    // Send mail
                    this.mailSender.send(mimeMessage);
                    logger.debug("Email sent!");

                } else {
                    logger.error("No events found for the specified criteria...abort!");
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Email sending error: " + e.getMessage());
        }
    }

    public void titheReport(List<Tithe> tithes,User myUser,Date date) {

        try {
            myUser.getAssembly().setUsers(assemblyService.getAssemblyUsers(myUser.getAssembly().getAssemblyid()));
            List<User> userList = myUser.getAssembly().getUsers(); //send an email to every user under this assembly
            if (userList.isEmpty()) {
                logger.error("No users set for this assembly...can't send email " + myUser.getAssembly().getName());
                return;
            }
          Assembly assembly = myUser.getAssembly();
            for (User user : userList) {

                if(user.getEmail() == null || user.getEmail().isEmpty()){
                    logger.error("Invalid/non existent email address for user : "+user.getFullname());
                    continue;
                }

                if (tithes != null && !tithes.isEmpty()) {
                    //we have something to report on - @ least
                    float total = 0;
                    for(Tithe tithe : tithes){
                        total += tithe.getAmount();
                    }

                    String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                    String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();

                    final Context ctx = new Context(Locale.ENGLISH);

                    ctx.setVariable("eSavvyLink", eSavvyLink);
                    ctx.setVariable("churchName", churchName);
                    ctx.setVariable("name", user.getFullname());
                    ctx.setVariable("tithes", tithes);
                    ctx.setVariable("total", Utils.moneyFormatter(total,assembly.getLocaleObject()));
                    ctx.setVariable("header", "Tithe report for : "+date);


                    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                    ctx.setVariable("header", "Tithe report for : "+date);
                    message.setSubject("Tithe report for : "+date);
                    message.setFrom(getResource("email.system.from"));
                    message.setTo(user.getEmail());

                    // Create the HTML body using Thymeleaf
                    final String htmlContent = this.templateEngine.process("../email/tithe-report", ctx);
                    message.setText(htmlContent, true); // true = isHtml

                    logger.debug("Email setup complete...now send!");
                    // Send mail
                    this.mailSender.send(mimeMessage);
                    logger.debug("Email sent!");

                } else {
                    logger.error("No events found for the specified criteria...abort!");
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Email sending error: " + e.getMessage());
        }
    }


    public void memberActivityReport(List<Member> members, Assembly assembly, String inActivityStatus) {

        try {
            assembly.setUsers(assemblyService.getAssemblyUsers(assembly.getAssemblyid()));
            List<User> users = assembly.getUsers();
            for(User user : users){

               if (user != null && user.getEmail() != null) {

                    if (members != null && !members.isEmpty()) {
                        //we have something to report on - @ least
                        String subjectLine = getResource("email.subject.activity.report.nonetithers", assembly.getName(), Utils.dateFormatter(new Date()));
                        String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                        String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();

                        final Context ctx = new Context(Locale.ENGLISH);

                        ctx.setVariable("eSavvyLink", eSavvyLink);
                        ctx.setVariable("churchName", churchName);
                        ctx.setVariable("name", user.getFullname());
                        ctx.setVariable("people", members);
                        ctx.setVariable("header", subjectLine);
                        ctx.setVariable("message", "The following is a list of None-Tithers for the past 4 weeks");


                        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                        ctx.setVariable("header", subjectLine);
                        message.setFrom(getResource("email.system.from"));
                        message.setTo(user.getEmail());
                        message.setSubject(subjectLine);
                        // Create the HTML body using Thymeleaf
                        final String htmlContent = this.templateEngine.process("../email/activity-report", ctx);
                        message.setText(htmlContent, true); // true = isHtml

                        logger.debug("Email setup complete...now send!");
                        // Send mail
                        this.mailSender.send(mimeMessage);
                        logger.debug("Email sent!");

                    } else {
                        logger.error("No events found for the specified criteria...abort!");
                    }
                } else {
                    logger.error("No Pastor found for this assembly or email is invalid...abort!");
                }
            }

        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Email sending error: " + e.getMessage());
        }
    }
    public void pastoralFollowUpReport(List<EventFollowUp> eventFollowUpList,Event event) {

        try {           User pastor = event.getAssembly().getUserWithRole(Role.Pastor);

              if (pastor != null && pastor.getEmail() != null) {

                    if (eventFollowUpList != null && !eventFollowUpList.isEmpty()) {
                        //we have something to report on - @ least
                        String subjectLine = "Members referred to you for your attention";
                        String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                        String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();

                        final Context ctx = new Context(Locale.ENGLISH);

                        ctx.setVariable("eSavvyLink", eSavvyLink);
                        ctx.setVariable("churchName", churchName);
                        ctx.setVariable("name", pastor.getFullname());
                        ctx.setVariable("people", eventFollowUpList);
                        ctx.setVariable("header", subjectLine);


                        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                        ctx.setVariable("header", subjectLine);
                        message.setFrom(getResource("email.system.from"));
                        message.setTo(pastor.getEmail());
                        message.setSubject(subjectLine);
                        // Create the HTML body using Thymeleaf
                        final String htmlContent = this.templateEngine.process("../email/pastor-follow-up-report", ctx);
                        message.setText(htmlContent, true); // true = isHtml

                        logger.debug("Email setup complete...now send!");
                        // Send mail
                        this.mailSender.send(mimeMessage);
                        logger.debug("Email sent!");

                    } else {
                        logger.error("No events found for the specified criteria...abort!");
                    }
                } else {
                    logger.error("No Pastor found for this assembly or email is invalid...abort!");
                }
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Email sending error: " + e.getMessage());
        }
    }

    private String getResource(String key, Object... args) {
        return (new NonStaticMessageFormat(resourceBundle.getString(key), args).getMsg());
    }



    private class GlobarEmailVars {
        private String eSavvyLink;
        private String churchName;
        private String apostolicEmail;

        public String geteSavvyLink() {
            return eSavvyLink;
        }

        public String getChurchName() {
            return churchName;
        }

        public String getApostolicEmail() {
            return apostolicEmail;
        }

        public GlobarEmailVars invoke() {
            eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
            churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();
            apostolicEmail = (systemVarService.getSystemVarByNameUnique(Constants.APOSTOLIC_EMAIL)).getValue();
            return this;
        }
    }

    private class CheckIfApostleNotNull {
        private boolean myResult;
        private String apostolicEmail;

        public CheckIfApostleNotNull(String apostolicEmail) {
            this.apostolicEmail = apostolicEmail;
        }

        boolean is() {
            return myResult;
        }

        public String getApostolicEmail() {
            return apostolicEmail;
        }

        public CheckIfApostleNotNull invoke() {
            if (StringUtils.isEmpty(apostolicEmail)) { //try looking for this in a user with apostolic role
                User apostle = userService.getApostle();
                if (apostle == null) {
                    logger.error("Failed to send apostolic email --- Please set up a user with Apostolic ROLE!");
                    myResult = true;
                    return this;
                }
                apostolicEmail = apostle.getEmail();
            }
            myResult = false;
            return this;
        }
    }
}
