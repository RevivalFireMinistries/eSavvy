package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/18/14
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rfm_audit_trail")
public class AuditTrail extends ChurchManagerEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    public String action;
    public Timestamp dateOfExecution;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    public User user;
}
