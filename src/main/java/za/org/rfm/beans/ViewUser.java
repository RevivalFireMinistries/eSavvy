package za.org.rfm.beans;

import za.org.rfm.model.User;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/07
 * Time: 3:16 PM
 */
@ViewScoped
@ManagedBean(name = "viewUserBean")
public class ViewUser {
    @ManagedProperty(value="#{UserService}")
    UserService userService;
    User user;
    String[] roles = Constants.roles;

    public List<String> getUserStates() {
        return userStates;
    }

    public void setUserStates(List<String> userStates) {
        this.userStates = userStates;
    }

    private List<String> userStates = Utils.getStates();

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PostConstruct
    public void init() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String username = facesContext.getExternalContext().getRequestParameterMap().get("username");
            if(username == null){
                facesContext.getExternalContext().responseSendError(401,"Invalid username specified");
            }
            user = userService.getUser(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveUser(){
        userService.saveUser(this.user);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Changes have been saved"));
    }
    public void resetUserPassword(){
        user.setPassword(Utils.generateRandomPassword(Constants.DEFAULT_USER_PASSWORD_SIZE));
        userService.saveUser(user);
        //TODO: Add logic to email this user his new password
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Password has been reset & emailed to user"));
    }
    public void blockUser(){
        user.setStatus(Constants.STATUS_IN_ACTIVE);
        userService.saveUser(user);
        //TODO: Add logic to email this user that his account is blocked
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "User blocking successful"));
    }

    public void unBlockUser(){
        user.setStatus(Constants.STATUS_ACTIVE);
        userService.saveUser(user);
        //TODO: Add logic to email this user that his account is unblocked
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "User unblocking successful"));
    }
}
