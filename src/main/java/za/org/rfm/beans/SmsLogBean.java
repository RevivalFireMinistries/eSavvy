package za.org.rfm.beans;

import org.apache.log4j.Logger;
import za.org.rfm.model.SMSLog;
import za.org.rfm.service.SMSService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/04
 * Time: 11:21 AM
 */
@ViewScoped
@ManagedBean(name = "smsLogBean")
public class SmsLogBean {

    private static Logger logger = Logger.getLogger(SendSMS.class.getName());
    @ManagedProperty(value="#{SMSService}")
    SMSService smsService;
    List<SMSLog> smsLogs;

    public List<SMSLog> getSmsLogs() {
        return smsLogs;
    }

    public void setSmsLogs(List<SMSLog> smsLogs) {
        this.smsLogs = smsLogs;
    }

    public SMSService getSmsService() {
        return smsService;
    }

    public void setSmsService(SMSService smsService) {
        this.smsService = smsService;
    }
}
