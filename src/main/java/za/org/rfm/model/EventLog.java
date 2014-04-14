package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/10
 * Time: 2:04 PM
 */
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "rfm_eventlog")
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event")
    public Event event;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member")
    public Member member;

    public EventLog(Member member,Event event){
        this.member = member;
        this.event = event;
    }
}
