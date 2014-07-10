package za.org.rfm.beans;

import org.apache.log4j.Logger;
import za.org.rfm.utils.WebUtil;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * User: Russel.Mupfumira
 * Date: 2014/05/23
 * Time: 10:40 AM
 */
@ManagedBean
@SessionScoped
public class MenuView {
    Logger logger = Logger.getLogger(MenuView.class);
    public void logout() {
        try {
            HttpSession session = WebUtil.getSession();
            String user = WebUtil.getUserName();
            session.invalidate();
          /*  FacesContext facesContext = FacesContext.getCurrentInstance();
            Flash flash = facesContext.getExternalContext().getFlash();
            flash.setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Logout successful", "Logout successful"));
            */
            String url = "login.faces";
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            logger.info("Logout user : "+user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        addMessage("Success", "Data updated");
    }

    public void delete() {
        addMessage("Success", "Data deleted");
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}
