package za.org.rfm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.org.rfm.utils.DateRange;

import java.util.Date;

/**
 * Created by Russel on 2015-02-12.
 */
@Getter
@Setter
@NoArgsConstructor
public class Bill {
    String narration, status;
    int units;
    double price, total;
    DateRange dateRange;
    Date created;

    public Bill(String narration,int units,double price,DateRange dateRange) {
        this.narration = narration;
        this.units = units;
        this.price = price;
        this.dateRange = dateRange;
    }

    public double getTotal(){
        return price*units;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "narration='" + narration + '\'' +
                ", total=" + getTotal() +
                ", dateRange=" + dateRange +
                '}';
    }
}


