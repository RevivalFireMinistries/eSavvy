package za.org.rfm.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.primefaces.component.schedule.Schedule;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/8/14
 * Time: 5:55 PM
 * To change this template use File | Settings | File Templates.
 */
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "rfm_schedule")
public class RFMScheduleEvent extends ChurchManagerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    int level;
    String ownerId;
    String status,title;
    Date startDate,endDate;

    public RFMScheduleEvent(ScheduleEvent event) {
        DefaultScheduleEvent event1 = (DefaultScheduleEvent)event;
        this.title = event1.getTitle();
        this.startDate = event1.getStartDate();
        this.endDate = event1.getEndDate();
    }
}
