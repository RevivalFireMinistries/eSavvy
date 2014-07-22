package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/24
 * Time: 12:04 PM
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_event_follow_up")
public class EventFollowUp extends ChurchManagerEntity{

    private static final long serialVersionUID = 2439054867964628170L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member")
    private Member member;
    @ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "event")
    private Event event;
    private String comment;
    private Date dateCreated;
    private int absenteesmDuration;
  /*  @OneToMany(fetch = FetchType.EAGER, mappedBy = "eventFollowUp")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    private List<EventFollowUpAction> eventFollowUpActionList;*/
    private transient List<String> followUpActions = new ArrayList<String>();
    private String action;
}
