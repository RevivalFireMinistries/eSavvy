package za.org.rfm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.AuditTrailDAO;
import za.org.rfm.model.AuditTrail;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/18/14
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional(readOnly = true)
public class AuditTrailService {

    Logger logger = Logger.getLogger(MemberService.class);
    @Autowired
    AuditTrailDAO auditTrailDAO;

    @Transactional(readOnly = false)
    public void saveTrail(AuditTrail auditTrail) {
        auditTrailDAO.saveTrail(auditTrail);
    }
}
