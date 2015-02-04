package za.org.rfm.dto;

import lombok.Getter;
import lombok.Setter;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Event;
import za.org.rfm.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Russel on 2015-02-02.
 */
@Getter
@Setter
public class EventTotals {
    String assemblyName;
    double totalTithe,totalOffering,totalIncome,totalApostolic;
    double averageAttendance;

    public EventTotals(Assembly assembly, List<Event> eventList) {
        List<Integer> attendanceList = new ArrayList<Integer>();
        this.assemblyName = assembly.getName();
        for(Event event : eventList){
            totalTithe += event.getTithes();
            totalOffering += event.getOfferings();
            totalIncome += event.getTotalIncome();
            totalApostolic += event.getApostolic();
            attendanceList.add(event.getAttendance());
        }
        //calc avg attendance
       averageAttendance = Math.round(Utils.calculateAverage(attendanceList));
    }

    public EventTotals(List<EventTotals> eventTotalsList) {
        for(EventTotals eventTotals : eventTotalsList){
            this.totalTithe += eventTotals.getTotalTithe();
            this.totalOffering += eventTotals.getTotalOffering();
            this.totalIncome += eventTotals.getTotalIncome();
            this.totalApostolic += eventTotals.getTotalApostolic();
            this.setAssemblyName("Totals");
        }
    }
}
