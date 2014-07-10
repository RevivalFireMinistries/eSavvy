package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/08
 * Time: 7:37 PM
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_member_group")
public class MemberGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member")
    private Member member;
    private String status,groupName;
    private Date dateCreated;
}
