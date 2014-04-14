package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * User: Russel.Mupfumira
 * Date: 2014/02/24
 * Time: 2:20 PM
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_smslog")
public class SMSLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    public String message;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member")
    private Member member;
    private String status;
    private Timestamp dateCreated;

    public SMSLog(String message,String status,Member member){
         this.message = message;
        this.status = status;
        this.member = member;
        this.dateCreated = new Timestamp(System.currentTimeMillis());
    }
}
