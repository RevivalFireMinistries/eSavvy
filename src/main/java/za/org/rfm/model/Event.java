package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import za.org.rfm.model.json.J_Event;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/01/14
 * Time: 2:05 PM
 */
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "rfm_event")
public class Event extends ChurchManagerEntity {

    private static final long serialVersionUID = 5860576192855907797L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assembly")
    private Assembly assembly;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    private List<EventLog> eventLogList;
    @JsonIgnore
    private Timestamp eventDate;
    private String eventDateString;
    public String eventType;
    public int attendance;
    public String comment;
    public transient String assemblyId;
    public double tithes;
    public double offerings;
    public int guests,totalRegistered = 100;
    public Timestamp timeStart,timeEnd;
    public int converts;
    public boolean followUp;
    public int targetAttendance;
    private transient double totalIncome,percentageOfAttendance,percentageOfAbsent;
    @JsonIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    private List<EventFollowUp> eventFollowUpList;

    public Event(J_Event j_event){
        this.eventType = j_event.getEventType();
        this.eventDate = Utils.getTimeStampFromString("yyyy-MM-dd",j_event.getEventDateString());
        this.tithes = Double.parseDouble(j_event.getTithes());
        this.offerings = Double.parseDouble(j_event.getOfferings());
        this.attendance = j_event.getAttendance();
        this.comment = j_event.getComment();
    }

   public String getEventDateFormatted(){
       return Utils.dateFormatter(getEventDate());
   }

   public String getTitheFormatted(){
       return Utils.moneyFormatter(getTithes(),getAssembly().getLocaleObject());
   }

    public String getOfferingFormatted(){
        return Utils.moneyFormatter(getOfferings(),getAssembly().getLocaleObject());
    }

    public void setAssembly(Assembly assembly){
        this.assembly = assembly;
        this.targetAttendance = assembly.getTargetAttendance();
        //this.totalRegistered = assembly.getMembers().size();
    }

    public double getTotalIncome(){
        return this.tithes + this.offerings;
    }
    public double getApostolic(){
        return getTotalIncome()* Constants.APOSTOLIC_CONTRIBUTION_PERCENTAGE;
    }
    public String getTotalIncomeFormatted(){
        return Utils.moneyFormatter(this.getTotalIncome(),getAssembly().getLocaleObject());
    }
    public String getApostolicFormatted(){
        return Utils.moneyFormatter(this.getApostolic(),getAssembly().getLocaleObject());
    }

    public double getPercentageAbsent(){
        return  ((totalRegistered- attendance)/totalRegistered)*100;
    }

    public double getPercentagePresent(){
        return   (attendance/totalRegistered)*100;
    }


}
