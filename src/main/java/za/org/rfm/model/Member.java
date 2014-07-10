package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User: Russel.Mupfumira
 * Date: 2013/12/13
 * Time: 11:06 AM
 */

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_member")
public class Member extends ChurchManagerEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    public String firstName,lastName,gender,phone,email,homeAddress,status,type;
    public Date dateCreated;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assembly")
    private Assembly assembly;
    @OneToOne( mappedBy = "member")
    @Cascade({CascadeType.SAVE_UPDATE,CascadeType.DELETE})
    private Account account;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<SMSLog> smsLogs;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "member")
    private Set<MemberGroup> memberGroupList;




    public Member(String firstName, String lastName){
           this.firstName = firstName;
           this.lastName = lastName;
    }
    public String getFullName(){
        return this.getFirstName()+" "+this.getLastName();
    }

    public SMSLog sendSMS(String message,boolean test){
        boolean sent = false;
        SMSLog log = null;
        if(this.getPhone() != null && (message != null && message != "")){
             sent = Utils.sendSMS(this.getPhone(),message,test);
            if(sent){
             log =   logSMS(message, Constants.STATUS_SENT);
            }else{
             log =   logSMS(message,Constants.STATUS_FAILED);
            }
        }
        return log;
    }

    public SMSLog logSMS(String messsage, String status){
          SMSLog smsLog =new SMSLog(messsage,status,this);
          return smsLog;

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (id != null ? !id.equals(member.id) : member.id != null) return false;
        if (firstName != null ? !firstName.equals(member.firstName) : member.firstName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (this.firstName != null ? this.firstName.hashCode() : 0);
        return result;
    }


}
