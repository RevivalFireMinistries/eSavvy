package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/02/21
 * Time: 9:41 AM
 */
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "rfm_account")
public class Account extends ChurchManagerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountnumber", unique = true, nullable = false)
    public Long accountnumber;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
    private List<Transaction> transactions;
    @OneToOne
    private Member member;
}
