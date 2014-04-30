package za.org.rfm.utils;

import java.util.Date;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/30
 * Time: 1:03 PM
 */
public class DateRange {
    Date startDate, endDate;

    public DateRange(Date startdate, Date enddate) {
        this.startDate = startdate;
        this.endDate = enddate;
    }

    @Override
    public String toString() {
        return "DateRange{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}
