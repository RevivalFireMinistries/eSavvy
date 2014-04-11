package za.org.rfm.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.log4j.Logger;
import za.org.rfm.model.Member;
import za.org.rfm.model.SMSLog;
import za.org.rfm.model.Transaction;
import za.org.rfm.utils.Utils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: Russel.Mupfumira
 * Date: 2014/02/21
 * Time: 9:47 AM
 */
@Getter
@Setter
@NoArgsConstructor
public class Tithe {
    public static Logger logger = Logger.getLogger(Tithe.class);
      public Long id;
      public String fullName,phoneNumber;
      public Date txnDate, previousTitheDate, previousAttendanceDate;
      public double amount,previousAmount;
     public Member member;

    public Tithe(Member member1,double amount,Date txnDate){
        this.id = member1.getId();
        this.fullName = member1.getFullName();
        this.phoneNumber = member1.getPhone();
        this.member = member1;
        this.amount = amount;
        this.txnDate = txnDate;

    }
    public void update(Member m){
        this.id = m.getId();
        this.fullName = m.getFullName();
        System.out.println("---the full name---"+this.getFullName());
        this.phoneNumber = m.getPhone();
        this.member = m;
        System.out.println("---model refreshed---");
    }

    public SMSLog sendSMS(){

        //Now create and send an sms
        String sms = "Revival Fire Ministries : Your tithe payment of "+ Utils.moneyFormatter(this.amount)+" made on "+ Utils.dateFormatter(this.getTxnDate())+" has been received.Thank you & Stay Blessed!For more info visit www.rfm.org.za.";
        return getMember().sendSMS(sms,true);
    }

    public boolean save(){
        Transaction txn = null;
        try {
            //save to the txn to db
            txn = new Transaction();
            txn.setAccount(getMember().getAccount());
            txn.setTxndate(new Timestamp(getTxnDate().getTime()));
            txn.setAmount(getAmount());
            txn.setTransactionid(Utils.generateID());
            //TransactionFacade.save(txn);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
           return false;
        }

    }
    public String getFormattedDate() {
        return Utils.dateFormatter(this.getTxnDate());
    }
    public String getFormattedAmount() {
        return Utils.moneyFormatter(this.amount);
    }
}
