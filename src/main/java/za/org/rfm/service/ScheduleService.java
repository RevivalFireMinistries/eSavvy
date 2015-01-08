package za.org.rfm.service;

import org.primefaces.model.DefaultScheduleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.ScheduleDAO;
import za.org.rfm.model.RFMScheduleEvent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/8/14
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("ScheduleService")
@Transactional(readOnly = true)
public class ScheduleService {

    @Autowired
    ScheduleDAO scheduleDAO;

    @Transactional(readOnly = false)
    public void saveEvent(RFMScheduleEvent event) {
        scheduleDAO.saveEvent(event);
    }

    public List<RFMScheduleEvent> getEventsByAssembly(String assemblyid) {
        return scheduleDAO.getEventsByAssembly(assemblyid);
    }

}
