package za.org.rfm.utils;

import java.util.Date;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/30
 * Time: 1:03 PM
 */
public class DateRange {
    Date startDate, endDate;

    public DateRange() {
    }

    public DateRange(Date startdate, Date enddate) {
        this.startDate = startdate;
        this.endDate = enddate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return  "Start Date=" + Utils.dateFormatter(startDate) +
                ", End Date=" + Utils.dateFormatter(endDate);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateRange)) return false;

        DateRange dateRange = (DateRange) o;

        if (endDate != null ? !endDate.equals(dateRange.endDate) : dateRange.endDate != null) return false;
        if (startDate != null ? !startDate.equals(dateRange.startDate) : dateRange.startDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startDate != null ? startDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }
}
