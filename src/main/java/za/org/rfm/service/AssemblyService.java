package za.org.rfm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.AssemblyDAO;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.User;

import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 10:55 PM
 */
@Service("AssemblyService")
@Transactional(readOnly = true)
public class AssemblyService {
    Logger logger = Logger.getLogger(AssemblyService.class);
    @Autowired
    AssemblyDAO assemblyDAO;

    public List<Assembly> getAssemblyList(String status){
        logger.debug("Getting assemblies by status : "+status);
        List<Assembly> assemblyList = assemblyDAO.getAssemblyList(status);
        logger.debug("Assemblies retrieved : "+assemblyList.size());
        return assemblyList;
    }

    public Assembly getAssemblyById(long l)
    {
        return assemblyDAO.getAssemblyById(l);
    }
    @Transactional(readOnly = false)
    public void saveAssembly(Assembly assembly){
        assemblyDAO.saveMember(assembly);
    }
    public List<User> getAssemblyUsers(Long id){
        return assemblyDAO.getAssemblyUsers(id);
    }
}
