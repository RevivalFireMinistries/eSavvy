package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/17
 * Time: 11:07 AM
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_event_follow_up_action")
public class EventFollowUpAction extends ChurchManagerEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventFollowUp")
    private EventFollowUp eventFollowUp;
    private String name;
    private String status;
    private Date dateOpened,dateClosed;

}
