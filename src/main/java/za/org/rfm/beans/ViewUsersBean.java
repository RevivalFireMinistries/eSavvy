package za.org.rfm.beans;

import org.primefaces.event.SelectEvent;
import za.org.rfm.model.User;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/07
 * Time: 4:26 PM
 */
@ViewScoped
@ManagedBean(name = "viewUsersBean")
public class ViewUsersBean {
    @ManagedProperty(value="#{UserService}")
    UserService userService;
    User selectedUser;
    User[] selectedUsers;
    private List<User> filteredUsers;
    List<User> userList;
    @PostConstruct
    public void init() {
        userList = new ArrayList<User>();
        //Get assembly of the current user
        HttpSession session = WebUtil.getSession();
        Long id = WebUtil.getUserAssemblyId();
        userList = userService.getUsersByAssembly(id);
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public User[] getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(User[] selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public List<User> getFilteredUsers() {
        return filteredUsers;
    }

    public void setFilteredUsers(List<User> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
    public void onRowSelect(SelectEvent event){
        try {
            String url = "viewUser.faces?username="+event.getObject();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Error opening user view :" + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
}
