package za.org.rfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.SMSDAO;
import za.org.rfm.model.SMSLog;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/02
 * Time: 2:42 PM
 */
@Service("SMSService")
@Transactional(readOnly = true)
public class SMSService {
    @Autowired
    SMSDAO smsDAO;

    public SMSDAO getSmsDAO() {
        return smsDAO;
    }

    public void setSmsDAO(SMSDAO smsDAO) {
        this.smsDAO = smsDAO;
    }

    @Transactional(readOnly = false)
    public void saveSMSLog(SMSLog smsLog) {
        getSmsDAO().saveSMSLog(smsLog);
    }
}
