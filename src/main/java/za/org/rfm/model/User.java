package za.org.rfm.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
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
    private String firstName,lastName,phone,email,password,status,role;
    private Date dateCreated;
    private Timestamp lastLoginDate;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assembly")
    private Assembly assembly;

    public User(String username){
        this.username = username;
    }

    public String getFullname() {
       return this.firstName+" "+getLastName();
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
