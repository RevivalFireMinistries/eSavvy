package za.org.rfm.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;
import za.org.rfm.model.Transaction;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.TxnService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/14/14
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class AssemblyMonthlyTitheReport implements Job {
    private static AssemblyService assemblyService;
    private static TxnService txnService;

    @PostConstruct
    public void init() {
    assemblyService = tmpAssemblyService;
        txnService = tmpTxnservice;
    }
    @Autowired
    private AssemblyService tmpAssemblyService;
    @Autowired
    private TxnService tmpTxnservice;
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //this job has to run on the 1st day of every month
        //for each assembly in the system generate a tithe report for the last month
        List<Assembly> assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
        for(Assembly assembly : assemblyList){
            DateRange dateRange = Utils.calcLastMonthDateRange(new Date());
            //now go and get tithe transactions for each member and write them to a pdf
            for(Member member : assembly.getMembers()){
               member.setTransactionList(txnService.getTithesByMemberAndDateRange(member,dateRange));
            }
            //now write to file and email the pastor!

        }

    }
}
