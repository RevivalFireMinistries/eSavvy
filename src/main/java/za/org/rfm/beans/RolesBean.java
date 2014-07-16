package za.org.rfm.beans;

import org.primefaces.event.RowEditEvent;
import za.org.rfm.model.Role;
import za.org.rfm.service.UserService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/16
 * Time: 3:38 PM
 */
@ViewScoped
@ManagedBean
public class RolesBean extends SuperBean {
    @ManagedProperty(value="#{UserService}")
    UserService userService;
    List<Role> roles;
    Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public void save(){
        userService.saveRole(role);
        FacesMessage msg = new FacesMessage("Role Created Successfully :", role.getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        role = new Role(); //reset model
        roles = userService.getRoles();
    }
    @PostConstruct
    public void init() {
        roles = userService.getRoles();
        logger.debug("Roles loaded...."+roles.size());
        role = new Role();
    }
    public void onRowEdit(RowEditEvent event) {
        logger.debug("now editing a variable....");
        Role role1 = (Role) event.getObject();
        userService.saveRole(role1);
        FacesMessage msg = new FacesMessage("Role Edited :", role1.getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled : ", ((Role) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
