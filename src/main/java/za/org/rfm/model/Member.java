package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import za.org.rfm.model.json.J_Member;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.persistence.*;
import java.util.ArrayList;
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

    private static final long serialVersionUID = -3952121875186843417L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    /*@XmlElement(name = "firstName", required = true, nillable = false)*/
    public String firstName;
    /*@XmlElement(name = "lastName", required = true, nillable = false)*/
    public String lastName,gender,phone,email,homeAddress,status,type;
    public Date dateCreated;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assembly")
    private Assembly assembly;
    @JsonIgnore
    @OneToOne( mappedBy = "member")
    @Cascade({CascadeType.SAVE_UPDATE,CascadeType.DELETE})
    private Account account;
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<SMSLog> smsLogs;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "member")
    private Set<MemberGroup> memberGroupList;
    private transient String assemblyId;

    public transient List<Transaction> transactionList = new ArrayList<Transaction>();





    public Member(String firstName, String lastName){
           this.firstName = firstName;
           this.lastName = lastName;
    }

    public  Member(J_Member j_member){
        this.firstName = j_member.getFirstName();
        this.lastName = j_member.getLastName();
        this.gender = j_member.getGender();
        this.email = j_member.getEmail();
        this.phone = j_member.getPhone();
        this.homeAddress = j_member.getHomeAddress();
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
     public String getAssemblyName(){
        return getAssembly().getName();
     }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (firstName != null ? !firstName.equals(member.firstName) : member.firstName != null) return false;
        if (gender != null ? !gender.equals(member.gender) : member.gender != null) return false;
        if (lastName != null ? !lastName.equals(member.lastName) : member.lastName != null) return false;
        if (phone != null ? !phone.equals(member.phone) : member.phone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
