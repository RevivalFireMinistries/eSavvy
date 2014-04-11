package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.User;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 9:37 PM
 */
@ManagedBean(name = "userWizard")
@SessionScoped
public class UserWizard {

    private User user = new User();

    private boolean skip;

    private static Logger logger = Logger.getLogger(UserWizard.class.getName());

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void save(ActionEvent actionEvent) {
        //Persist user

        FacesMessage msg = new FacesMessage("Successful", "Welcome :" + user.getFullname());
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
