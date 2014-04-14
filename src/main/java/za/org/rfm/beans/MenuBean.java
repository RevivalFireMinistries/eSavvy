package za.org.rfm.beans;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 2:33 PM
 */
@SessionScoped
@ManagedBean(name = "menuBean")
public class MenuBean {
    private MenuModel model;

    public MenuBean() {
        model = new DefaultMenuModel();
        DefaultSubMenu home = new DefaultSubMenu("Home");
        DefaultMenuItem itemhome = new DefaultMenuItem("Savvy Dashboard");
        itemhome.setUrl("/home.faces");
        itemhome.setIcon("ui-icon-home");
        home.addElement(itemhome);
        model.addElement(home);
        //First submenu
        DefaultSubMenu firstSubmenu = new DefaultSubMenu("Members Management");

        DefaultMenuItem item = new DefaultMenuItem("Add New Member");
        item.setUrl("/members/newMember.faces");
        item.setIcon("ui-icon-home");
        firstSubmenu.addElement(item);
        DefaultMenuItem item2 = new DefaultMenuItem("View Members");
        item2.setUrl("/members/viewMembers.faces");
        item2.setIcon("ui-icon-home");
        firstSubmenu.addElement(item2);

        /*DefaultMenuItem item3 = new DefaultMenuItem("Add Attendance Info");
        item3.setUrl("/members/addAttendance.faces");
        item3.setIcon("ui-icon-home");
        firstSubmenu.addElement(item3);*/

        model.addElement(firstSubmenu);

        //services
        DefaultSubMenu services = new DefaultSubMenu("Services");
        DefaultMenuItem newreport = new DefaultMenuItem("New Report");
        newreport.setUrl("/services/newReport.faces");
        newreport.setIcon("ui-icon-home");
        services.addElement(newreport);
        DefaultMenuItem viewreports = new DefaultMenuItem("View Reports");
        viewreports.setUrl("/services/viewReports.faces");
        viewreports.setIcon("ui-icon-home");
        services.addElement(viewreports);
        model.addElement(services);
        // submenu
        DefaultSubMenu smsSubmenu = new DefaultSubMenu("SMS");
        DefaultMenuItem  item3 = new DefaultMenuItem("Send New");
        item3.setUrl("/sms/sendSMS.faces");
        item3.setIcon("ui-icon-home");
        smsSubmenu.addElement(item3);
       /* DefaultMenuItem item4 = new DefaultMenuItem("View Sent");
        item4.setUrl("/members/viewMembers.faces");
        item4.setIcon("ui-icon-home");
        smsSubmenu.addElement(item4);*/
        model.addElement(smsSubmenu);
        //Second submenu
        DefaultSubMenu secondSubmenu = new DefaultSubMenu("Tithes");

        item = new DefaultMenuItem("Enter New");
        item.setUrl("/tithes/enterTithe.faces");
        item.setIcon("ui-icon-home");
        secondSubmenu.addElement(item);

        item = new DefaultMenuItem("Report");
        item.setIcon("ui-icon-close");
        item.setCommand("#{menuBean.delete}");
        item.setAjax(false);
        secondSubmenu.addElement(item);

        item = new DefaultMenuItem("Redirect");
        item.setIcon("ui-icon-search");
        item.setCommand("#{menuBean.redirect}");
        secondSubmenu.addElement(item);

        model.addElement(secondSubmenu);


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
