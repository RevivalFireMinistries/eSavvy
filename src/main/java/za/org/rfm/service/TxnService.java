package za.org.rfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.beans.Tithe;
import za.org.rfm.dao.TxnDAO;
import za.org.rfm.model.Member;
import za.org.rfm.model.Transaction;
import za.org.rfm.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/10
 * Time: 8:00 AM
 */
@Service("TxnService")
@Transactional(readOnly = true)
public class TxnService {
    @Autowired
    TxnDAO txnDAO;

    public TxnDAO getTxnDAO() {
        return txnDAO;
    }

    public void setTxnDAO(TxnDAO txnDAO) {
        this.txnDAO = txnDAO;
    }

    @Transactional(readOnly = false)
    public void saveTxn(Transaction txn){
       getTxnDAO().saveTxn(txn);
        System.out.println("txn saved..."+txn.getTransactionid());
    }

    public List<Transaction> getTithesByMemberAndDateRange(Member member,Date startDate, Date endDate){
        return getTxnDAO().getTithesByMemberAndDateRange(member,startDate,endDate);
    }

    public void processTithe(Tithe tithe){
        System.out.println("---now processing tithe txn...");
        //Now create and send an sms
        String sms = "RFM: Hi "+tithe.getMember().getFirstName()+", Your tithe payment of "+ Utils.moneyFormatter(tithe.getAmount())+" made on "+ Utils.dateFormatter(tithe.getTxnDate())+" has been received.Thank you & Stay Blessed!For more info visit www.rfm.org.za.";
        System.out.println("...sending sms..."+sms);
        tithe.getMember().sendSMS(sms,true);
    }

}
