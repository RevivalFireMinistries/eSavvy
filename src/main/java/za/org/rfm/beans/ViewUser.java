package za.org.rfm.beans;

import za.org.rfm.model.Role;
import za.org.rfm.model.User;
import za.org.rfm.model.UserRole;
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
import java.util.ArrayList;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/07
 * Time: 3:16 PM
 */
@ViewScoped
@ManagedBean(name = "viewUserBean")
public class ViewUser extends SuperBean{

    @ManagedProperty(value="#{UserService}")
    UserService userService;
    User user;

    List<Role> myRoles;
    List<Role> roles;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getMyRoles() {
        return myRoles;
    }

    public void setMyRoles(List<Role> myRoles) {
        this.myRoles = myRoles;
    }

    public List<String> getUserStates() {
        return userStates;
    }

    public void setUserStates(List<String> userStates) {
        this.userStates = userStates;
    }

    private List<String> userStates = Utils.getStates();



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
            //now populate his roles
            logger.debug("found roles for this user : "+user.getUserRoles().size());
            if(!user.getUserRoles().isEmpty()){
                UserRole userRole;
                myRoles = new ArrayList<Role>();
                for(int i=0;i<user.getUserRoles().size();i++){
                     myRoles.add(user.getUserRoles().get(i).getRole());
                }
            }

            roles = userService.getRoles();
            logger.info("Now loading the user profile...");
        } catch (IOException e) {
           logger.error("Encountered error : "+e.getMessage());
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

    public void handleChange(){
       logger.debug("Now saving the changes...");
       //now map selected rows and the user's actual roles in db
        // 1. delete the ones removed
        //2. persist the new ones
        if(getMyRoles().isEmpty()){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select at least one role from the list",null));
            logger.error("0 roles selected...Not allowed!!");
            return;
        }
        UserRole userRole;
        List<UserRole> temp = new ArrayList<UserRole>();
        for(Role role: getMyRoles()){
          userRole =new UserRole();
          userRole.setRole(role);
          userRole.setStatus(Constants.STATUS_ACTIVE);
          userRole.setUser(this.user);
          temp.add(userRole);
        }
        //now establish the removed ones
        if(this.user.getUserRoles().removeAll(temp)){
            //then something has indeed changed  -pliz delete!
           for(UserRole ur : this.user.getUserRoles()){
               userService.deleteUserRole(ur);
               logger.debug("Role "+ur.getRole().getName()+" deleted");
           }
        }
        //now we need to persist the new roles
        if(!temp.isEmpty()){
            //only save if it doesn't exist
            for(UserRole userRole1 : temp){
                userService.saveOrUpdateUserRole(userRole1);
            }
            this.user.setUserRoles(temp);
        }
        logger.debug("Roles updated!");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Changes saved successfully",null));

    }
}

