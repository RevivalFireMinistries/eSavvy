package za.org.rfm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.AssemblyDAO;
import za.org.rfm.dto.MemberMonthlyTitheTotals;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Event;
import za.org.rfm.model.User;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

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
    @Autowired
    EventService eventService;

    public List<Assembly> getAssemblyList(String status) {
        logger.debug("Getting assemblies by status : " + status);
        List<Assembly> assemblyList = assemblyDAO.getAssemblyList(status);
        logger.debug("Assemblies retrieved : " + assemblyList.size());
        return assemblyList;
    }

    public Assembly getAssemblyById(long l) {
        return assemblyDAO.getAssemblyById(l);
    }

    @Transactional(readOnly = false)
    public void saveAssembly(Assembly assembly) {
        assemblyDAO.saveMember(assembly);
    }

    public List<User> getAssemblyUsers(Long id) {
        return assemblyDAO.getAssemblyUsers(id);
    }

    public List<MemberMonthlyTitheTotals> getMemberMonthlyTitheTotals(Long assemblyId){
        return assemblyDAO.getMemberMonthlyTitheTotals(assemblyId);
    }

    public double getMonthlyOffering(long id, int month){
        double total = 0.0;
        List<Event> eventList = eventService.getEventsByAssemblyAndTypeAndDateRange(id, Constants.SERVICE_TYPE_SUNDAY, Utils.getMonthDateRange(month));

        for(Event event : eventList){
              total += event.getOfferings();
        }

        return total;
    }
}
