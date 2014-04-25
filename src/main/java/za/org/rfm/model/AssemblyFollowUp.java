package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/24
 * Time: 12:04 PM
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_assemblyfollowup")
public class AssemblyFollowUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member")
    private Member member;
    private String status;
    private Date dateCreated;
    private int absenteesmDuration;

    public AssemblyFollowUp(Member member,String status,Date dateCreated){
         this.member = member;
         this.status = status;
        this.dateCreated = dateCreated;
    }
}
