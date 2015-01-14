package za.org.rfm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.beans.Tithe;
import za.org.rfm.dao.TxnDAO;
import za.org.rfm.model.Member;
import za.org.rfm.model.SystemVar;
import za.org.rfm.model.Transaction;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;
import za.org.rfm.utils.Utils;

import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/10
 * Time: 8:00 AM
 */
@Service("TxnService")
@Transactional(readOnly = true)
public class TxnService {
    Logger logger = Logger.getLogger(TxnService.class);
    @Autowired
    SMSService smsService;
    @Autowired
    TxnDAO txnDAO;
    @Autowired
    private SystemVarService systemVarService;

    public TxnDAO getTxnDAO() {
        return txnDAO;
    }

    public void setTxnDAO(TxnDAO txnDAO) {
        this.txnDAO = txnDAO;
    }

    @Transactional(readOnly = false)
    public void saveTxn(Transaction txn) {
        getTxnDAO().saveTxn(txn);
        logger.debug("txn saved..." + txn.getTransactionid());
    }

    public List<Transaction> getTithesByMemberAndDateRange(Member member, DateRange dateRange) {
        return getTxnDAO().getTithesByMemberAndDateRange(member, dateRange);
    }

    @Transactional(readOnly = false)
    public void processTithe(Tithe tithe) {
      logger.debug("---now processing tithe txn...");
        //txn to db first
        saveTxn(new Transaction(tithe));
        //Now create and send an sms
        String sms = "RFM: Hi " + tithe.getMember().getFirstName() + ", Your tithe payment of " + Utils.moneyFormatter(tithe.getAmount(), tithe.getMember().getAssembly().getLocaleObject()) + " made on " + Utils.dateFormatter(tithe.getTxnDate()) + " has been received.Thank you and Stay Blessed!For more info visit www.rfm.org.za.";
        logger.info("...sending sms..." + sms);
        SystemVar val = systemVarService.getSystemVarByNameUnique(Constants.SMS_ENABLED);
        Boolean sendSMS = false;
        if(val != null){
            sendSMS = Boolean.valueOf(val.getValue());
        }
        logger.info("Sending SMS enabled : "+sendSMS);
        smsService.saveSMSLog(tithe.getMember().sendSMS(sms, sendSMS));
    }

}
