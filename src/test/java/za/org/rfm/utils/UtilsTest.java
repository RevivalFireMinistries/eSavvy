package za.org.rfm.utils;

import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/22
 * Time: 10:09 AM
 */
public class UtilsTest {
    @Test
    public void testCalcLastSunday() throws Exception {
        String startDate = "01/08/2015";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date testDate = df.parse(startDate);
        Assert.assertEquals(df.parse("01/04/2015"),Utils.calcLastSunday(testDate));
    }
    @Test
    public void testCalcLastMonthDateRange() {
        try {
            String startDate = "06/01/2014";
            String endDate = "06/30/2014";
            String myDate = "07/01/2014";

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            Date testDate = df.parse(startDate);
            DateRange expected = new DateRange(df.parse(startDate),df.parse(endDate)) ;
            Assert.assertEquals(expected,Utils.calcLastMonthDateRange(df.parse(myDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetMonthDateRange(){
        DateRange dateRange = Utils.getMonthDateRange(3);
        Calendar cal = Calendar.getInstance();

        System.out.println(cal.get(Calendar.MONTH));
        System.out.println("start : "+dateRange.getStartDate());
        System.out.println("end : "+dateRange.getEndDate());
    }
}
