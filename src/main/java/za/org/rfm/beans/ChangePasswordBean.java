package za.org.rfm.beans;

import za.org.rfm.model.User;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/08
 * Time: 1:18 AM
 */
@ViewScoped
@ManagedBean(name = "changePasswordBean")
public class ChangePasswordBean {
    User user;
    @ManagedProperty(value="#{UserService}")
    UserService userService;
    String oldPwd,password1,password2;

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
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

    public void saveNewPassword(){
       //first check if old password is correct
        if(!user.getPassword().equals(this.getOldPwd())){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Incorrect old password",
                    "The old password you entered is not valid.");
            facesContext.addMessage(null,message);
            return;
        }
        //now change the password on the user coz is ok.
       user.setPassword(this.password1);
        userService.saveUser(user);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_INFO, "Success",
                "Password has been changed successfully.");
        facesContext.addMessage(null,message);
    }
    @PostConstruct
    public void init() {
        try {
            user = userService.getUser(WebUtil.getUserName());
            if(user == null){
                FacesContext.getCurrentInstance().getExternalContext().responseSendError(401,"Invalid user specified");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
