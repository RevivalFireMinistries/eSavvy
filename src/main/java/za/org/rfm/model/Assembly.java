package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2013/12/11
 * Time: 4:05 PM
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rfm_assembly")
public class Assembly extends ChurchManagerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assemblyid", unique = true, nullable = false)
    public Long assemblyid;
    public String name,physicalAddress,email,status,country;
    public String phone;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assembly")
    private List<Member> members;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assembly")
    private List<Event> events;
    private int targetAttendance;
    private int membersRegistered;
    @Override
    public boolean equals(Object o) {
        System.out.println("---now in equals----");
        if (this == o) return true;
        if (!(o instanceof Assembly)) return false;

        Assembly assembly = (Assembly) o;
        if(assembly != null && assemblyid != null) {
            if (!assemblyid.equals(assembly.assemblyid)) return false;
        }  else{
            return false;
        }


        return true;
    }

    @Override
    public int hashCode() {
        return assemblyid.hashCode();
    }

   public int getTotalRegistered(){
       return  members.size();
   }
}
