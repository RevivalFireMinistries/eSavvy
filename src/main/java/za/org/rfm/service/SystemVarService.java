package za.org.rfm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.SystemVarDAO;
import za.org.rfm.model.SystemVar;

import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/10
 * Time: 12:27 PM
 */
@Service("SystemVarService")
@Transactional(readOnly = true)
public class SystemVarService {
    Logger logger = Logger.getLogger(SystemVarService.class);
    @Autowired
    SystemVarDAO systemVarDAO;

    public SystemVarDAO getSystemVarDAO() {
        return systemVarDAO;
    }

    public void setSystemVarDAO(SystemVarDAO systemVarDAO) {
        this.systemVarDAO = systemVarDAO;
    }
    @Transactional(readOnly = false)
    public void saveSystemVar(SystemVar vars){
        systemVarDAO.saveSystemVar(vars);
    }
    public List<SystemVar> getSystemVarByName(String name) {
        return systemVarDAO.getSystemVarByName(name);
    }
    public List<SystemVar> getAllVars() {
        return systemVarDAO.getAllVars();
    }
    public SystemVar getSystemVarById(Long id) {
        return systemVarDAO.getSystemVarById(id);
    }
}
