package za.org.rfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.AssemblyDAO;
import za.org.rfm.model.Assembly;

import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 10:55 PM
 */
@Service("AssemblyService")
@Transactional(readOnly = true)
public class AssemblyService {

    @Autowired
    AssemblyDAO assemblyDAO;

    public List<Assembly> getAssemblyList(){
        return assemblyDAO.getAssemblyList();
    }

    public Assembly getAssemblyById(long l) {
        return assemblyDAO.getAssemblyById(l);
    }
    @Transactional(readOnly = false)
    public void saveAssembly(Assembly assembly){
        assemblyDAO.saveMember(assembly);
    }

}
