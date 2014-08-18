package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.User;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.UserService;
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
 * Date: 2014/06/19
 * Time: 11:18 AM
 */
@ManagedBean(name = "viewAssemblyBean")
@ViewScoped
public class ViewAssembly {
    Logger logger = Logger.getLogger(ViewAssembly.class);
    Assembly assembly;
    List<User> userList;
    User selectedUser;
    List<User> filteredUsers;

    public List<User> getFilteredUsers() {
        return filteredUsers;
    }

    public void setFilteredUsers(List<User> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;
    @ManagedProperty(value="#{UserService}")
    UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    public Assembly getAssembly() {
        return assembly;
    }

    public void setAssembly(Assembly assembly) {
        this.assembly = assembly;
    }

    @PostConstruct
    public void init() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String assemblyId = facesContext.getExternalContext().getRequestParameterMap().get("assemblyid");
            if(assemblyId == null){
                facesContext.getExternalContext().responseSendError(401,"Invalid assembly id specified");
            }
            //load this assembly's users as well
            userList = userService.getUsersByAssembly(Long.parseLong(assemblyId));
            System.out.println("user list..."+userList.size());
            logger.info("Assembly Loaded : " + assemblyService.getAssemblyById(Long.parseLong(assemblyId)).getName());
            setAssembly(assemblyService.getAssemblyById(Long.parseLong(assemblyId)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveAssembly(){
        assemblyService.saveAssembly(this.assembly);
        Utils.addFacesMessage("Changes have been saved", FacesMessage.SEVERITY_INFO);
    }
    private List<String> assemblyStates = Utils.getStates();

    public List<String> getAssemblyStates() {
        return assemblyStates;
    }
    public void newUser(){
        try {
            String url = "users/newUser.faces";
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Error opening new user form :" + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    public void onRowSelect(SelectEvent event){
        try {
            User selectedUser = (User)event.getObject();
            String url = "/users/viewUser.faces?username="+selectedUser.getUsername();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Error opening user view :" + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

}
