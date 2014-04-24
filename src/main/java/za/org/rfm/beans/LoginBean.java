package za.org.rfm.beans;

import org.apache.log4j.Logger;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;
import za.org.rfm.model.User;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 10:47 AM
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    static Logger logger = Logger.getLogger(LoginBean.class);
    private static final long serialVersionUID = 1L;
    private String password;
    private String message, uname;
    private User user;
    private Assembly assembly;

    public Assembly getAssembly() {
        return assembly;
    }

    public void setAssembly(Assembly assembly) {
        this.assembly = assembly;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManagedProperty(value="#{UserService}")
    UserService userService;
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    @ManagedProperty(value="#{mailService}")
    EmailService emailService;

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }



    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public String loginProject() {

        try {
            boolean result = getUserService().login(uname, password);
            //boolean result = true;
            if (result) {
                // get Http Session and store username
                HttpSession session = WebUtil.getSession();

                session.setAttribute("username", uname);
                User user = getUserService().getUser(uname);
                setUser(user);
                setAssembly(user.getMember().getAssembly());
                session.setAttribute("assembyid",user.getAssembly());
                List<Member> members = memberService.getMembersByAssembly(WebUtil.getUserAssemblyId());
                //sendEmail(members);
                logger.info("Login successful for user : "+user.getFullname());
                return "home";
            } else {
                Utils.addFacesMessage("Invalid login details!",FacesMessage.SEVERITY_ERROR);
                logger.error("Failed login attempt : "+uname);
                return "login";
            }
        } catch (Exception e) {
            logger.error("Login error : "+e.getMessage());
            e.printStackTrace();
            Utils.addFacesMessage("System currently unavailable, Please try again later",FacesMessage.SEVERITY_ERROR);
        }
        return "login";
    }

    public String logout() {
        HttpSession session = WebUtil.getSession();
        String user = WebUtil.getUserName();
        session.invalidate();
        Utils.addFacesMessage("Logout successful ",FacesMessage.SEVERITY_INFO);
        logger.info("Logout user : "+user);
        return "/login.xhtml?faces-redirect=true";
    }
    public void sendEmail(List<Member> members){
        System.out.println("Now sending an email....");
        emailService.sendMail("russzw@gmail.com", "russel@rfm.org.za", "Testing123", "Testing only \n\n Hello Spring Email Sender",members);

        emailService.sendAlertMail("Exception occurred");
        System.out.println("Email sent!");
    }
}
