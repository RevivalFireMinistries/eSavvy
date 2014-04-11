package za.org.rfm.beans;

import lombok.Getter;
import lombok.Setter;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;

import java.sql.Timestamp;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/04
 * Time: 1:42 PM
 */
@Getter
@Setter
public class SMS {
    private String message;
    private Timestamp sendingDate;
    private String status;
    private List<Member> recipients;
    private String phone;
    private Assembly assembly;

}
