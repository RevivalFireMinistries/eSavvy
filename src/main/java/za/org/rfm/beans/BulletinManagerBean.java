package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import za.org.rfm.model.Bulletin;
import za.org.rfm.service.BulletinService;
import za.org.rfm.utils.Constants;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/15/14
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean(name = "manageBulletins")
public class BulletinManagerBean {
     Logger logger = Logger.getLogger(BulletinManagerBean.class);
    @ManagedProperty(value="#{BulletinService}")
    BulletinService bulletinService;
    List<Bulletin> bulletinList;
    Bulletin bulletin;
    Date today = new Date();

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    public BulletinService getBulletinService() {
        return bulletinService;
    }

    public void setBulletinService(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }

    public List<Bulletin> getBulletinList() {
        return bulletinList;
    }

    public void setBulletinList(List<Bulletin> bulletinList) {
        this.bulletinList = bulletinList;
    }

    public Bulletin getBulletin() {
        return bulletin;
    }

    public void setBulletin(Bulletin bulletin) {
        this.bulletin = bulletin;
    }
    @PostConstruct
    public void init() {
        bulletinList = bulletinService.getActiveBulletins(new Date(),Constants.STATUS_ACTIVE);
        bulletin = new Bulletin();
    }
    public void save(){
       bulletin.setType(Constants.REPORT_TYPE_APOSTOLIC);
        bulletin.setStatus(Constants.STATUS_ACTIVE);
       bulletinService.saveBulletin(bulletin);
       logger.debug("Bulletin saved");
       FacesMessage msg = new FacesMessage("Bulletin Created Successfully :" );
       FacesContext.getCurrentInstance().addMessage(null, msg);
        bulletinList = bulletinService.getActiveBulletins(new Date(),Constants.STATUS_ACTIVE);
        bulletin = new Bulletin(); //reset model
    }
    public void onRowEdit(RowEditEvent event) {
        Bulletin var = (Bulletin) event.getObject();
        bulletinService.saveBulletin(var);
        FacesMessage msg = new FacesMessage("Bulletin Edited :"+var.getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled : ", ((Bulletin) event.getObject()).getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
