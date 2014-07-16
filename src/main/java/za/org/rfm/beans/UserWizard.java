package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.*;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 9:37 PM
 */
@ManagedBean(name = "userWizard")
@ViewScoped
public class UserWizard {

    private User user;
    private Member selectedMember;
    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;
    @ManagedProperty(value="#{UserService}")
    UserService userService;
    List<Member> memberList;
    Map<Long,String> assemblyMap;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }


    private List<Role> roles;
    private Role[] selectedRoles;


    private boolean skip;
    String assembly;
    Assembly tempAssembly;

    public Role[] getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(Role[] selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    public Assembly getTempAssembly() {
        return tempAssembly;
    }

    public void setTempAssembly(Assembly tempAssembly) {
        this.tempAssembly = tempAssembly;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        tempAssembly = assemblyService.getAssemblyById(Long.valueOf(assembly));
        this.assembly = assembly;

    }

    private static Logger logger = Logger.getLogger(UserWizard.class.getName());

    @PostConstruct
    public void init() {
        try {
            //preload assemblies  and roles
            List<Assembly> assemblies = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
            if(assemblies.isEmpty()){
                FacesContext facesContext = FacesContext.getCurrentInstance();
                Flash flash = facesContext.getExternalContext().getFlash();
                flash.setKeepMessages(true);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Please add assemblies first before users", "Please add assemblies first before users"));
                String url = "../assembly/newAssembly.faces";
                facesContext.getExternalContext().redirect(url);
                logger.warn("No active assemblies found in the system db...please load or reactivate ");
                return;
            }
            setAssemblyMap(Utils.getAssembliesAsMap(assemblies));
            user = new User();
            roles = userService.getRoles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public AssemblyService getAssemblyService() {
      return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
     this.assemblyService = assemblyService;
    }


    public Map<Long, String> getAssemblyMap() {
        return assemblyMap;
    }

    public void setAssemblyMap(Map<Long, String> assemblyMap) {
        this.assemblyMap = assemblyMap;
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



    public void save(ActionEvent actionEvent) {
        try {
            User newUser = getUser();
            //Persist user
            newUser.setDateCreated(new Date());
            int count =0; //this is the initial trial
            while(userService.checkUserNameExists(user)){
                if(count>0){
                    logger.debug("Username rejected we are retrying..."+user.getUsername());
                    logger.debug("No of attempts : "+count);
                }
                Utils.generateUserName(newUser, count);
                count++;
            }
            logger.debug("Username creation and validation done! : "+newUser.getUsername());
            getUser().setStatus(Constants.STATUS_ACTIVE);
            getUser().setPassword(Utils.generateRandomPassword(Constants.DEFAULT_USER_PASSWORD_SIZE));
            user.setAssembly(tempAssembly);
            Utils.capitaliseUser(user);
            //now process roles
            UserRole userRole;
            List<UserRole> userRoleList = new ArrayList<UserRole>();
            for(Role role : getSelectedRoles()){
                 userRole = new UserRole();
                userRole.setUser(this.user);
                userRole.setRole(role);
                userRole.setStatus(Constants.STATUS_ACTIVE);
                userRoleList.add(userRole);
            }
            logger.debug("Found "+userRoleList.size()+" user roles ready for persistence");
            user.setUserRoles(userRoleList);
            userService.saveUser(getUser());
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Flash flash = facesContext.getExternalContext().getFlash();
            flash.setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "User :" + user.getFullname()+" created successfully"));
            String url = "viewUser.faces?username="+this.getUser().getUsername();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Error opening user view :" + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String onFlowProcess(FlowEvent event) {
        logger.info("Current wizard step:" + event.getOldStep());
        logger.info("Next step:" + event.getNewStep());

        if(skip) {
            skip = false;	//reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }

}
