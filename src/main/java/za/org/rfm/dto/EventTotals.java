package za.org.rfm.dto;

import lombok.Getter;
import lombok.Setter;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Event;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Russel on 2015-02-02.
 */
@Getter
@Setter
public class EventTotals {
    Assembly assembly;
    String assemblyName;
    double totalTithe,totalOffering,totalIncome,totalApostolic;
    double averageAttendance;

    public EventTotals(Assembly assembly, List<Event> eventList) {
        List<Integer> attendanceList = new ArrayList<Integer>();
        this.assemblyName = assembly.getName();
        this.assembly = assembly;
        for(Event event : eventList){
            totalTithe += event.getTithes();
            totalOffering += event.getOfferings();
            totalIncome += event.getTotalIncome();
            attendanceList.add(event.getAttendance());
        }
        //calc avg attendance
       totalApostolic =  totalIncome * Constants.APOSTOLIC_CONTRIBUTION_PERCENTAGE;
       averageAttendance = (int)Math.round(Utils.calculateAverage(attendanceList));
    }

    public EventTotals(List<EventTotals> eventTotalsList) {
        for(EventTotals eventTotals : eventTotalsList){
            this.totalTithe += eventTotals.totalTithe;
            this.totalOffering += eventTotals.totalOffering;
            this.totalIncome += eventTotals.totalIncome;
            this.setAssemblyName("Totals ");
        }
        this.totalApostolic = this.totalIncome * Constants.APOSTOLIC_CONTRIBUTION_PERCENTAGE;
    }

    public String getTotalTitheFormatted(){
        if(assembly == null){
            return Utils.moneyFormatter(totalTithe, Locale.getDefault());
        }
        return Utils.moneyFormatter(totalTithe,assembly.getLocaleObject());
    }

    public String getTotalOfferingFormatted(){
        if(assembly == null){
            return Utils.moneyFormatter(totalOffering, Locale.getDefault());
        }
        return Utils.moneyFormatter(totalOffering,assembly.getLocaleObject());
    }

    public String getTotalIncomeFormatted(){
        if(assembly == null){
            return Utils.moneyFormatter(totalIncome, Locale.getDefault());
        }
        return Utils.moneyFormatter(totalIncome,assembly.getLocaleObject());
    }

    public String getTotalApostolicFormatted(){
        if(assembly == null){
            return Utils.moneyFormatter(totalApostolic, Locale.getDefault());
        }
        return Utils.moneyFormatter(totalApostolic,assembly.getLocaleObject());
    }




}
