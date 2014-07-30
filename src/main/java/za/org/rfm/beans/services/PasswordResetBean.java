package za.org.rfm.beans.services;

import org.apache.log4j.Logger;
import za.org.rfm.model.User;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/29
 * Time: 11:00 AM
 */
@ManagedBean(name = "passwordResetBean")
@ViewScoped
public class PasswordResetBean {
    private static Logger logger = Logger.getLogger(PasswordResetBean.class);
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.messages_en_ZA", Locale.getDefault());
    User user;
    @ManagedProperty(value="#{UserService}")
    UserService userService;
    @ManagedProperty(value="#{mailService}")
    EmailService emailService;

    @PostConstruct
    public void init(){
        user = new User();
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    public void resetPassword(){
        final User user1;
        final String  subject,main,username,password;
        if(userService.checkUserNameExists(user)){
             logger.debug("Username : "+user.getUsername()+" exists on the system...now reset");
              user1 = userService.getUser(user.getUsername());
            //now reset password
            user1.setPassword(Utils.generateRandomPassword(Constants.DEFAULT_USER_PASSWORD_SIZE));
            userService.saveUser(user1);
            logger.info("Password reset for "+user1.getUsername()+" successful...now sending email");
            subject = resourceBundle.getString("email.subject.password.reset");
            main = resourceBundle.getString("email.body.password.reset.message.main");
            username = (MessageFormat.format(resourceBundle.getString("email.body.password.reset.message.username"), user1.getUsername()));
            password = (MessageFormat.format(resourceBundle.getString("email.body.password.reset.message.password"),user1.getPassword()));
            Thread thread = new Thread(){
                @Override
                public void run() {
                    emailService.sendNotification(user1, subject, main, username, password);
                }
            };
             thread.start();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password has been reset & emailed to user", null));
        } else{
            logger.debug("Unknown username! "+user.getUsername());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Password reset failed : User does not exist",null));
        }
    }
}
