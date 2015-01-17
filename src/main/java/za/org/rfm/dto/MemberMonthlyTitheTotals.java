package za.org.rfm.dto;

import lombok.Getter;
import lombok.Setter;
import za.org.rfm.model.Member;

/**
 * Created by Russel on 2015-01-17.
 */
@Getter
@Setter
public class MemberMonthlyTitheTotals {
    public Member member;
    public double[] totals = new double[12];

    public double getJanuary(){
        return totals[0];
    }
    public double getFebruary(){
        return totals[1];
    }
    public double getMarch(){
        return totals[2];
    }
    public double getApril(){
        return totals[3];
    }
    public double getMay(){
        return totals[4];
    }
    public double getJune(){
        return totals[5];
    }
    public double getJuly(){
        return totals[6];
    }
    public double getAugust(){
        return totals[7];
    }
    public double getSeptember(){
        return totals[8];
    }
    public double getOctober(){
        return totals[9];
    }
    public double getNovember(){
        return totals[10];
    }
    public double getDecember(){
        return totals[11];
    }
}
