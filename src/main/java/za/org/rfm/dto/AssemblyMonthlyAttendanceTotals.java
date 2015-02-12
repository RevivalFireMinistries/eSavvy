package za.org.rfm.dto;

import lombok.Getter;
import lombok.Setter;
import za.org.rfm.model.Event;
import za.org.rfm.utils.Utils;

import java.util.List;

/**
 * Created by Russel on 2015-02-12.
 */
@Getter
@Setter
public class AssemblyMonthlyAttendanceTotals {
    int[] weeks = new int[5];
    int month,average;
    String monthName;

    public AssemblyMonthlyAttendanceTotals(List<Event> events,int month) {
        for(int i=0;i<events.size();i++ ){
            weeks[i] = events.get(i).getAttendance();
        }
        this.month = month;
        this.monthName = Utils.getMonthName(month);
        setAverage(events);
    }
    public int getWeek1(){
        return weeks[0];
    }
    public int getWeek2(){
        return weeks[1];
    }
    public int getWeek3(){
        return weeks[2];
    }
    public int getWeek4(){
        return weeks[3];
    }
    public int getWeek5(){
        return weeks[4];
    }

    public void setAverage(List<Event> events){
        if(events.isEmpty()){
            return;
        }
        int total = 0;
        for(Event event : events){
            total += event.getAttendance();
        }
        average = total/events.size();
    }
}
