package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.SystemVar;
import za.org.rfm.model.User;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.SystemVarService;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.WebUtil;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
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
    private String churchName;

    public String getChurchName() {
        return churchName;
    }

    public void setChurchName(String churchName) {
        this.churchName = churchName;
    }

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
    @ManagedProperty(value="#{SystemVarService}")
    private SystemVarService systemVarService;

    public SystemVarService getSystemVarService() {
        return systemVarService;
    }

    public void setSystemVarService(SystemVarService systemVarService) {
        this.systemVarService = systemVarService;
    }

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
    public void changePassword(){
        try {
            String url = "/users/changePassword.faces";
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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

    public void loginProject() {

        try {
            boolean result = getUserService().login(uname, password);
            User temp = new User();
            temp.setUsername(uname);
            System.out.println("exists   :"+userService.checkUserNameExists(temp));
            List<User> users  = userService.getUsersByAssembly(1L);
            System.out.println("size--------"+users.size());
            for(User user1 : users){
                System.out.println("username : "+user1.getUsername());
            }
            //boolean result = true;
            if (result) {
                // get Http Session and store username
                HttpSession session = WebUtil.getSession();

                session.setAttribute("username", uname);
                User user = getUserService().getUser(uname);
                if(user.getStatus().equalsIgnoreCase(Constants.STATUS_DELETED) || user.getStatus().equalsIgnoreCase(Constants.STATUS_IN_ACTIVE)){
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Login failed : User blocked/deleted",null));
                    logger.error("Failed login attempt - user blocked/deleted : "+uname);
                    return;
                }
                setUser(user);
                setAssembly(user.getAssembly());
                session.setAttribute("assembly", user.getAssembly());
                logger.info("User's Assembly : "+user.getAssembly().getName());
                //sendEmail(members);
                logger.info("Login successful for user : "+user.getFullname());
                String url = "home.faces";
                //now set the lastlogin to now
                user.setLastLoginDate(new Timestamp(System.currentTimeMillis()));
                userService.saveUser(user);
                SystemVar var = (systemVarService.getSystemVarByNameUnique(Constants.CHURCH_NAME));
                if(var != null && !StringUtils.isEmpty(var.getValue())){
                    setChurchName(var.getValue());
                }
                else{
                    setChurchName("eSavvy");
                    logger.error("Error : variable church name not set!");
                }
                FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Invalid login details!",null));
                logger.error("Failed login attempt : " + uname);
            }
        } catch (Exception e) {
            logger.error("Login error : "+e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"System currently unavailable, Please try again later!",null));

        }
       //return "login";
    }

    public String logout() {
        try {
            HttpSession session = WebUtil.getSession();
            String user = WebUtil.getUserName();
            session.invalidate();
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Flash flash = facesContext.getExternalContext().getFlash();
            flash.setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Logout successful", "Logout successful"));
            String url = "login.faces";
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            logger.info("Logout user : "+user);
            return "login";
        } catch (IOException e) {
            e.printStackTrace();
        }
       return "";
    }

    public void resetPassword(){

    }
    public void sendEmail(User user){
        System.out.println("Now sending an email....");
        emailService.sendWelcomeEmail(user);

        //emailService.sendAlertMail("Exception occurred");
        System.out.println("Email sent!");
    }
}
