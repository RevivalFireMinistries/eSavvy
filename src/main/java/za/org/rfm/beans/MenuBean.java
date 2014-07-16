package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import za.org.rfm.model.User;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Role;
import za.org.rfm.utils.Utils;
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
 * Date: 2014/03/20
 * Time: 2:33 PM
 */
@ManagedBean(name = "menuBean")
@ViewScoped
public class MenuBean {
    Logger logger = Logger.getLogger(getClass());
    private MenuModel model;
    @ManagedProperty(value="#{UserService}")
    UserService userService;
    User currentUser;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @PostConstruct
    public void init(){
        currentUser = userService.getUser(WebUtil.getUserName());
        if(currentUser == null){
            try {
                //no user in context - go out!
                logger.error("No user found - failed to load menu!");
                String url = "login.faces";
                FacesContext.getCurrentInstance().getExternalContext().redirect(url);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        model = new DefaultMenuModel();
        DefaultSubMenu home = new DefaultSubMenu("Home");
        DefaultMenuItem itemhome = new DefaultMenuItem("Dashboard");
        itemhome.setUrl("/home.faces");
        itemhome.setIcon("ui-icon-home");
        home.addElement(itemhome);
        model.addElement(home);
        //////////////////////////////////////////
        if(Utils.isAuthorised(currentUser, Role.Pastor) || Utils.isAuthorised(currentUser,Role.Administrator)){     //super admin not allowed
        //members mgt
        DefaultSubMenu firstSubmenu = new DefaultSubMenu("Members Management");
        DefaultMenuItem item = new DefaultMenuItem("Add New Member");
        item.setUrl("/members/newMember.faces");
        item.setIcon("ui-icon-home");
        firstSubmenu.addElement(item);
        DefaultMenuItem item2 = new DefaultMenuItem("View Members");
        item2.setUrl("/members/viewMembers.faces");
        item2.setIcon("ui-icon-home");
        firstSubmenu.addElement(item2);
        model.addElement(firstSubmenu);
        ////////////////////////////////////////
        }
        if(Utils.isAuthorised(currentUser, Role.SuperAdmin)){  //only super admin is allowed
        //Assembly mgt
        DefaultSubMenu assembly = new DefaultSubMenu("Assembly Management");
        DefaultMenuItem a1 = new DefaultMenuItem("Add New Assembly");
        a1.setUrl("/assembly/newAssembly.faces");
        a1.setIcon("ui-icon-home");
        assembly.addElement(a1);
        DefaultMenuItem a2 = new DefaultMenuItem("View Assemblies");
        a2.setUrl("/assembly/viewAssemblies.faces");
        a2.setIcon("ui-icon-home");
        assembly.addElement(a2);
        model.addElement(assembly);
        }
        DefaultSubMenu assembly = new DefaultSubMenu("Assembly Management");
        DefaultMenuItem a1 = new DefaultMenuItem("My Assembly");
        a1.setUrl("/assembly/viewAssembly.faces?assemblyid="+WebUtil.getUserAssemblyId());
        a1.setIcon("ui-icon-home");
        assembly.addElement(a1);
        model.addElement(assembly);
        ///////////////////////////////////////
        //user mgt
        DefaultSubMenu users = new DefaultSubMenu("User Management");
        if(Utils.isAuthorised(currentUser,Role.SuperAdmin)){  //only for super admins
        DefaultMenuItem newUser = new DefaultMenuItem("New User");
        newUser.setUrl("/users/newUser.faces");
        newUser.setIcon("ui-icon-home");
        users.addElement(newUser);
        DefaultMenuItem view = new DefaultMenuItem("View Users");
        view.setUrl("/users/viewUsers.faces");
        view.setIcon("ui-icon-home");
        users.addElement(view);
        }
        DefaultMenuItem pwd = new DefaultMenuItem("Change Password");      //everyone should be able to to do this
        pwd.setUrl("/users/changePassword.faces");
        pwd.setIcon("ui-icon-home");
        users.addElement(pwd);

        model.addElement(users);
        /////////////////////////////////////////////////////


        //services mgt
        DefaultSubMenu services = new DefaultSubMenu("Services");
        if (Utils.isAuthorised(currentUser,Role.Administrator)) {
            DefaultMenuItem newreport = new DefaultMenuItem("New Report");
            newreport.setUrl("/services/newReport.faces");
            newreport.setIcon("ui-icon-home");
            services.addElement(newreport);
        }
        if (Utils.isAuthorised(currentUser,Role.Pastor)) {
            DefaultMenuItem viewreports = new DefaultMenuItem("View Reports");
            viewreports.setUrl("/services/viewReports.faces");
            viewreports.setIcon("ui-icon-home");
            services.addElement(viewreports);
            model.addElement(services);
        }
        if(Utils.isAuthorised(currentUser,Role.Administrator) || Utils.isAuthorised(currentUser,Role.Pastor)){
        //sms mgt
        DefaultSubMenu smsSubmenu = new DefaultSubMenu("SMS");
        DefaultMenuItem  item3 = new DefaultMenuItem("Send New");
        item3.setUrl("/sms/sendSMS.faces");
        item3.setIcon("ui-icon-home");
        smsSubmenu.addElement(item3);
        model.addElement(smsSubmenu);
        ////////////////////////////////////////////////////
        }
        if(Utils.isAuthorised(currentUser, Role.Administrator) ){
        //Tithes mgt
        DefaultSubMenu secondSubmenu = new DefaultSubMenu("Tithes");
        DefaultMenuItem item = new DefaultMenuItem();
        item = new DefaultMenuItem("Enter New");
        item.setUrl("/tithes/enterTithe.faces");
        item.setIcon("ui-icon-home");
        secondSubmenu.addElement(item);
        model.addElement(secondSubmenu);
        /////////////////////////////////////
        }

        if(Utils.isAuthorised(currentUser, Role.SuperAdmin) ){
            //System mgt
            DefaultSubMenu secondSubmenu = new DefaultSubMenu("System Config");
            DefaultMenuItem item = new DefaultMenuItem();
            item = new DefaultMenuItem("Manage");
            item.setUrl("/system/viewSystemConfig.faces");
            item.setIcon("ui-icon-home");
            secondSubmenu.addElement(item);
            model.addElement(secondSubmenu);

            item = new DefaultMenuItem();
            item = new DefaultMenuItem("Roles");
            item.setUrl("/system/roles.faces");
            item.setIcon("ui-icon-home");
            secondSubmenu.addElement(item);
            model.addElement(secondSubmenu);
            /////////////////////////////////////
        }

    }
    public MenuBean() {

    }

    public MenuModel getModel() {
        return model;
    }

    public void save() {
        addMessage("Data saved");
    }

    public void update() {
        addMessage("Data updated");
    }

    public void delete() {
        addMessage("Data deleted");
    }

    public String redirect() {
        return "home?faces-redirect=true";
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
