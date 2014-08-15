package za.org.rfm.beans;

import za.org.rfm.model.Bulletin;
import za.org.rfm.service.BulletinService;
import za.org.rfm.utils.Constants;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/15/14
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean(name = "bulletinScroller")
public class BulletinScroller {

    @ManagedProperty(value="#{BulletinService}")
    BulletinService bulletinService;
    List<Bulletin> bulletinList;

    public List<Bulletin> getBulletinList() {
        return bulletinList;
    }

    public void setBulletinList(List<Bulletin> bulletinList) {
        this.bulletinList = bulletinList;
    }

    public BulletinService getBulletinService() {
        return bulletinService;
    }

    public void setBulletinService(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }
    @PostConstruct
    public void init(){
         setBulletinList(bulletinService.getActiveBulletins(new Date(), Constants.STATUS_ACTIVE));
    }
}
