package za.org.rfm.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import za.org.rfm.model.Event;
import za.org.rfm.model.User;
import za.org.rfm.utils.*;

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
    private AssemblyService assemblyService;
    @Autowired
    private EventService eventService;

    public void sendWelcomeEmail(User user) {

        try {
              if(user != null && user.getEmail()!= null){
                  //Now validate email first
                  EmailFormatValidator emailFormatValidator = new EmailFormatValidator();
                  if(emailFormatValidator.validate(user.getEmail())){
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
                      ctx.setVariable("churchName",churchName);


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
            logger.error("Email sending error: "+e.getMessage());
        }

    }
    public void sendNotification(User user,String subject,String...messages) {

        try {
            if(user != null && user.getEmail()!= null){
                //Now validate email first
                EmailFormatValidator emailFormatValidator = new EmailFormatValidator();
                if(emailFormatValidator.validate(user.getEmail())){
                    logger.debug("Email address validation...OK...process");
                    //now we can proceeed
                    String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                    String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();
                    final Context ctx = new Context(Locale.ENGLISH);
                    ctx.setVariable("name", user.getFullname());
                    ctx.setVariable("messages", messages);
                    ctx.setVariable("eSavvyLink", eSavvyLink);
                    ctx.setVariable("churchName",churchName);


                    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                    message.setSubject(subject);
                    message.setFrom(Constants.SYSTEM_DEFAULT_EMAIL);
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
            logger.error("Email sending error: "+e.getMessage());
        }

    }

    public void eventReport(Event event){
        try {
            //first get pastor's email
            event.getAssembly().setUsers(assemblyService.getAssemblyUsers(event.getAssembly().getAssemblyid()));
            User pastor = event.getAssembly().getUserWithRole(Role.Pastor);
             if(pastor != null && pastor.getEmail()!= null){
                 logger.info("Found the pastor for "+event.getAssembly().getName()+" : "+pastor.getFullname()+" email : "+pastor.getEmail());
                //Now validate email first
                EmailFormatValidator emailFormatValidator = new EmailFormatValidator();
                if(emailFormatValidator.validate(pastor.getEmail())){
                    logger.debug("Email address validation...OK...process");
                    //now we can proceeed
                    String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                    String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();
                    Map<String,String> items = new LinkedHashMap<String, String>();
                    items.put("Assembly",event.getAssembly().getName());
                    items.put("Date",event.getEventDateFormatted());
                    items.put("Type",event.getEventType());
                    items.put("Attendance",event.getAttendance()+"");
                    items.put("Tithes",event.getTitheFormatted());
                    items.put("Offering",event.getOfferingFormatted());
                    items.put("Total Income",event.getTotalIncomeFormatted());
                    items.put("Comments",event.getComment());
                    String reportIntro = getResource("email.report.assembly.service.introduction",event.getEventType());
                     List<String> messages = new ArrayList<String>();
                    messages.add(reportIntro);
                    final Context ctx = new Context(Locale.ENGLISH);
                    ctx.setVariable("name", pastor.getFullname());
                    ctx.setVariable("messages",messages);
                    ctx.setVariable("items", items);
                    ctx.setVariable("assembly", event.getAssembly().getName());
                    ctx.setVariable("eSavvyLink", eSavvyLink);
                    ctx.setVariable("churchName",churchName);


                    final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                    message.setSubject(getResource("email.subject.assembly.report",event.getAssembly().getName(),event.getEventType(),event.getEventDateFormatted()));
                    message.setFrom(getResource("email.system.from"));
                    message.setTo(pastor.getEmail());

                    // Create the HTML body using Thymeleaf
                    final String htmlContent = this.templateEngine.process("../email/bean-report", ctx);
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
            logger.error("Email sending error: "+e.getMessage());
        }
    }
    public void apostolicReport(String frequency){
        DateRange dateRange = Utils.calcLastMonthDateRange(new Date());
        try {
            List<Event> events = null;
            if(Constants.REPORT_FREQUENCY_WEEKLY.equalsIgnoreCase(frequency)){
               //we need to get all ss events for the previous sunday
                events = eventService.getEventsByDateAndType(Constants.SERVICE_TYPE_SUNDAY,Utils.calcLastSunday(new Date()));

            }else if(Constants.REPORT_FREQUENCY_MONTHLY.equalsIgnoreCase(frequency)){

                events =  eventService.getEventsByTypeAndDateRange(Constants.SERVICE_TYPE_SUNDAY,dateRange);
            }
            if(events != null && !events.isEmpty()){
               //we have something to report on - @ least
                Event totals = new Event();
                for(Event event : events){
                    totals.setAttendance(totals.getAttendance()+event.getAttendance());
                    totals.setOfferings(totals.getOfferings()+event.getOfferings());
                    totals.setTithes(totals.getTithes()+event.getTithes());
                }
                String eSavvyLink = (systemVarService.getSystemVarByNameUnique(Constants.ESAVVY_LINK)).getValue();
                String churchName = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME)).getValue();

                final Context ctx = new Context(Locale.ENGLISH);

                ctx.setVariable("eSavvyLink", eSavvyLink);
                ctx.setVariable("churchName",churchName);
                ctx.setVariable("events",events);
                ctx.setVariable("totals",totals);
                ctx.setVariable("header",getResource("email.subject.apostolic.report",frequency,dateRange));


                final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
                message.setSubject(getResource("email.subject.apostolic.report",frequency,dateRange));
                message.setFrom(getResource("email.system.from"));
                message.setTo(getResource("email.apostolic"));

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
            logger.error("Email sending error: "+e.getMessage());
        }
    }

    private String getResource(String key,Object...args){
       return (new NonStaticMessageFormat(resourceBundle.getString(key),args).getMsg());
    }

}
