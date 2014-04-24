package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.persistence.*;
import java.sql.Timestamp;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assembly")
    private Assembly assembly;
    private Timestamp eventDate;
    private String eventType;
    private int attendance;
    private String comment;
    private double tithes;
    private double offerings;
    private int guests,totalRegistered;
    private Timestamp timeStart,timeEnd;
    private int converts;
    private boolean register;
    private int targetAttendance;
    private transient double totalIncome,percentageOfAttendance,percentageOfAbsent;


   public String getEventDateFormatted(){
       return Utils.dateFormatter(getEventDate());
   }

   public String getTitheFormatted(){
       return Utils.moneyFormatter(getTithes());
   }

    public String getOfferingFormatted(){
        return Utils.moneyFormatter(getOfferings());
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
        return Utils.moneyFormatter(this.getTotalIncome());
    }
    public String getApostolicFormatted(){
        return Utils.moneyFormatter(this.getApostolic());
    }

    public double getPercentageAbsent(){
        return  ((totalRegistered- attendance)/totalRegistered)*100;
    }

    public double getPercentagePresent(){
        return   (attendance/totalRegistered)*100;
    }


}
