package za.org.rfm.beans;

import za.org.rfm.model.User;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 10:47 AM
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String password;
    private String message, uname;

    @ManagedProperty(value="#{UserService}")
    UserService userService;
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;

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

    public String loginProject() {

        try {
            boolean result = getUserService().login(uname, password);
            //boolean result = true;
            if (result) {
                // get Http Session and store username
                HttpSession session = WebUtil.getSession();
                session.setAttribute("username", uname);
                User user = getUserService().getUser(uname);
                System.out.println("---User assembly---"+user.getAssembly());
                session.setAttribute("assembyid",user.getAssembly());
                return "home";
            } else {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Invalid Login!",
                                "Please Try Again!"));

                // invalidate session, and redirect to other pages

                //message = "Invalid Login. Please Try Again!";
                return "login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.addFacesMessage("System currently unavailable, Please try again later");
        }
        return "login";
    }

    public String logout() {
        HttpSession session = WebUtil.getSession();
        session.invalidate();
        return "login";
    }
}
