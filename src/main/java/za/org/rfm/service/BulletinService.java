package za.org.rfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.BulletinDAO;
import za.org.rfm.model.Bulletin;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/15/14
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("BulletinService")
@Transactional(readOnly = true)
public class BulletinService {

    @Autowired
    BulletinDAO bulletinDAO;

    @Transactional(readOnly = false)
    public void saveBulletin(Bulletin bulletin) {
        bulletinDAO.saveBulletin(bulletin);
    }

    public List<Bulletin> getActiveBulletins(Date endDate, String status) {
        return bulletinDAO.getActiveBulletins(endDate, status);
    }
}
