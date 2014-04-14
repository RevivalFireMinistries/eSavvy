package za.org.rfm.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Russel.Mupfumira
 * Date: 2014/01/08
 * Time: 4:01 PM
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_user")
public final class User extends ChurchManagerEntity {

    @Id
    private String username;
    // The user's name
    @OneToOne(fetch = FetchType.EAGER)
    private Member member;
    private String fullname,password,status,role;
    private Date dateCreated;

    public User(String username){
        this.username = username;
    }

    public String getFullname() {
       return this.member.getFullName();
        //return "Chacharak";
    }

    public Long getAssembly(){
        return getMember().getAssembly().getAssemblyid();
    }



    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.username;
    }



}
