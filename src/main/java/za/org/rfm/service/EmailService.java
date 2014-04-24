package za.org.rfm.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import za.org.rfm.model.Event;
import za.org.rfm.model.Member;
import za.org.rfm.utils.Utils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/17
 * Time: 4:03 PM
 */
@Service("mailService")
public class EmailService {
      @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SimpleMailMessage alertMailMessage;
    @Autowired
    private TemplateEngine templateEngine;

    public void sendMail(String from, String to, String subject, String body,List<Member> members) {

        try {

            final Context ctx = new Context(Locale.ENGLISH);
            ctx.setVariable("name", "Russel");
            ctx.setVariable("members",members);
            ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
            // ctx.setVariable("imageResourceName", imageResourceName); // so that we can reference it from HTML


            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
            message.setSubject("Welcome to eSavvy");
            message.setFrom(from);
            message.setTo(to);

            // Create the HTML body using Thymeleaf
            final String htmlContent = this.templateEngine.process("welcome", ctx);
            message.setText(htmlContent, true); // true = isHtml


            // Send mail
            this.mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
    public void sendWeeklyEventsSummaryEmail(List<Event> events) {

        try {

            final Context ctx = new Context(Locale.ENGLISH);
            ctx.setVariable("totalAttendance", getTotalAttendance(events));
            ctx.setVariable("totalIncome", Utils.moneyFormatter(getTotalIncome(events)));
            ctx.setVariable("totalOfferings",Utils.moneyFormatter(getTotalOffering(events)));
            ctx.setVariable("totalTithes",Utils.moneyFormatter(getTotalTithes(events)));
            ctx.setVariable("totalApostolic",Utils.moneyFormatter(getTotalApostolic(events)));
            ctx.setVariable("events",events);


            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8"); // true = multipart
            message.setSubject("eSavvy Sunday Service Weekly Report");
            message.setFrom("churchmanager@rfm.org.za");
            message.setTo("russel@rfm.org.za");

            // Create the HTML body using Thymeleaf
            final String htmlContent = this.templateEngine.process("weeklyReport", ctx);
            message.setText(htmlContent, true); // true = isHtml


            // Send mail
            this.mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
    private int getTotalAttendance(List<Event> events){
        int total = 0;
        for(Event e : events){
            total += e.getAttendance();
        }
        return total;
    }
    private double getTotalIncome(List<Event> events){
        double total = 0;
        for(Event e : events){
            total += e.getTotalIncome();
        }
        return total;
    }
    private double getTotalApostolic(List<Event> events){
        double total = 0;
        for(Event e : events){
            total += e.getApostolic();
        }
        return total;
    }
    private double getTotalTithes(List<Event> events){
        double total = 0;
        for(Event e : events){
            total += e.getTithes();
        }
        return total;
    }
    private double getTotalOffering(List<Event> events){
        double total = 0;
        for(Event e : events){
            total += e.getOfferings();
        }
        return total;
    }
    public void sendAlertMail(String alert) {

        SimpleMailMessage mailMessage = new SimpleMailMessage(alertMailMessage);
        mailMessage.setText(alert);
        mailSender.send(mailMessage);

    }
}
