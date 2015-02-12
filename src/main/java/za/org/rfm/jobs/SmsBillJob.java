package za.org.rfm.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.dto.Bill;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.SMSLog;
import za.org.rfm.model.SystemVar;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.SMSService;
import za.org.rfm.service.SystemVarService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Russel on 2015-02-12.
 */
@Component
public class SmsBillJob implements Job {
   Logger logger = Logger.getLogger(SmsBillJob.class);

    private static AssemblyService assemblyService;
    private static SMSService smsService;
    private static SystemVarService systemVarService;
    private static EmailService emailService;

    @PostConstruct
    public void init() {
            assemblyService = tmpAssemblyService;
            smsService = tmpsmsService;
            systemVarService = tmpsystemVarService;
            emailService = tmpemailService;
    }

    @Autowired
    private AssemblyService tmpAssemblyService;
    @Autowired
    private SMSService tmpsmsService;
    @Autowired
    private SystemVarService tmpsystemVarService;
    @Autowired
    private EmailService tmpemailService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Now executing inactivity checker job...");
        List<Assembly> assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
        DateRange dateRange = Utils.calcLastMonthDateRange(new Date());
        SystemVar unitPrice = systemVarService.getSystemVarByNameUnique("sms-unit-price");
        List<Bill> bills = new ArrayList<Bill>();
        double smsUnitPrice = 0.0;
        if(unitPrice != null){
             smsUnitPrice = Double.parseDouble(unitPrice.getValue());
        }
        if(smsUnitPrice == 0.0){
            smsUnitPrice = Constants.DEFAULT_SMS_UNIT_PRICE;
            logger.warn("Failed to load SMS Unit price from db...using system default "+smsUnitPrice);
        }

        for(Assembly assembly : assemblyList){
            List<SMSLog> smsLogs =  smsService.getSMSLogByAssemblyAndDate(assembly.getAssemblyid(),dateRange);
            if(smsLogs.isEmpty()){
                logger.info("Assembly : "+assembly+" did not use any smses during the period "+dateRange);
            }else{
                int totalSmses = smsLogs.size();
                Bill assemblyBill = new Bill(assembly+" "+dateRange,totalSmses,smsUnitPrice,dateRange);
                emailService.sendOutBill(assembly,assemblyBill);
                bills.add(assemblyBill);
            }
        }
       emailService.sendOutBillTotal(bills,dateRange);

    }
}
