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

    public void apostolicReport(String frequency) {
        DateRange dateRange = Utils.calcLastMonthDateRange(new Date());
        try {
            List<Event> events = null;
            if (Constants.REPORT_FREQUENCY_WEEKLY.equalsIgnoreCase(frequency)) {
                //we need to get all ss events for the previous sunday
                events = eventService.getEventsByDateAndType(Constants.SERVICE_TYPE_SUNDAY, Utils.calcLastSunday(new Date()));

            } else if (Constants.REPORT_FREQUENCY_MONTHLY.equalsIgnoreCase(frequency)) {

                events = eventService.getEventsByTypeAndDateRange(Constants.SERVICE_TYPE_SUNDAY, dateRange);
            }
            if (events != null && !events.isEmpty()) {
                //we have something to report on - @ least
                Event totals = new Event();
                for (Event event : events) {
                    totals.setAttendance(totals.getAttendance() + event.getAttendance());
                    totals.setOfferings(totals.getOfferings() + event.getOfferings());
                    totals.setTithes(totals.getTithes() + event.getTithes());
                }
                String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();
                String apostolicEmail = (systemVarService.getSystemVarByNameUnique(Constants.APOSTOLIC_EMAIL)).getValue();
                logger.info("Got the apostolic email : " + apostolicEmail);
                if (StringUtils.isEmpty(apostolicEmail)) { //try looking for this in a user with apostolic role
                    User apostle = userService.getApostle();
                    if (apostle == null) {
                        logger.error("Failed to send apostolic email --- Please set up a user with Apostolic ROLE!");
                        return;
                    }
                    apostolicEmail = apostle.getEmail();
                }

                final Context ctx = new Context(Locale.ENGLISH);

                ctx.setVariable("eSavvyLink", eSavvyLink);
                ctx.setVariable("churchName", churchName);
                ctx.setVariable("events", events);
                ctx.setVariable("totals", totals);
                if (Constants.REPORT_FREQUENCY_WEEKLY.equalsIgnoreCase(frequency)) {
                    ctx.setVariable("header", getResource("email.subject.apostolic.report", frequency));
                } else {
                    ctx.setVariable("header", getResource("email.subject.apostolic.report", frequency, dateRange));
                }


                final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                message.setSubject(getResource("email.subject.apostolic.report", frequency, dateRange));
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

    public void memberActivityReport(List<Member> members, Assembly assembly, String inActivityStatus) {

        try {
            assembly.setUsers(assemblyService.getAssemblyUsers(assembly.getAssemblyid()));
            List<User> users = assembly.getUsers();
            for(User user : users){

               if (user != null && user.getEmail() != null) {

                    if (members != null && !members.isEmpty()) {
                        //we have something to report on - @ least
                        String subjectLine = "";
                        if (Constants.MEMBERS_INACTIVE.equalsIgnoreCase(inActivityStatus)) {
                            subjectLine = getResource("email.subject.activity.report.backslide", assembly.getName(), Utils.dateFormatter(new Date()));
                        } else {
                            subjectLine = getResource("email.subject.activity.report.rejuvenated", assembly.getName(), Utils.dateFormatter(new Date()));
                        }
                        String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                        String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();

                        final Context ctx = new Context(Locale.ENGLISH);

                        ctx.setVariable("eSavvyLink", eSavvyLink);
                        ctx.setVariable("churchName", churchName);
                        ctx.setVariable("name", user.getFullname());
                        ctx.setVariable("people", members);
                        ctx.setVariable("header", subjectLine);


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

}
