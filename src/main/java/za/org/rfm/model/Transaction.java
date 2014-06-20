package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.org.rfm.beans.Tithe;
import za.org.rfm.utils.Utils;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * User: Russel.Mupfumira
 * Date: 2014/02/21
 * Time: 9:43 AM
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_transaction")
public class Transaction extends ChurchManagerEntity {
    @Id
    @Column(name = "transactionid", unique = true, nullable = false)
    public String transactionid;
    public double amount;
    public Timestamp txndate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account")
    public Account account;
    public Transaction(Tithe tithe){
        this.setAccount(tithe.getMember().getAccount());
        this.setTxndate(new Timestamp(tithe.getTxnDate().getTime()));
        this.setAmount(tithe.getAmount());
        this.setTransactionid(Utils.generateID());
    }
}
