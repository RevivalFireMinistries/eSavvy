package za.org.rfm.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import za.org.rfm.utils.Constants;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
    private String firstName,lastName,phone,email,password,status;
    private Date dateCreated;
    private Timestamp lastLoginDate;
    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assembly")
    private Assembly assembly;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    private List<UserRole> userRoles;
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<AuditTrail> auditTrailList;

    public User(String username){
        this.username = username;
    }

    public String getFullname() {
       return getFirstName()+" "+getLastName();
    }

    public boolean isBlocked(){
        return Constants.STATUS_IN_ACTIVE.equalsIgnoreCase(this.getStatus())?true:false;
    }

 /*   public void setUserRoles(List<UserRole> userRoleList){
         for(UserRole userRole: userRoleList){
             if(!this.userRoles.contains(userRole)){
                 this.userRoles.add(userRole);
             }
         }
    }*/

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.username;
    }

    public String getRoleToString(){
        String role = "";
        for(UserRole userRole: this.getUserRoles()){
            role = role + userRole.getRole().getName()+", ";
        }
        if(role.equalsIgnoreCase(""))
            role = "No Roles set";
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!username.equals(user.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
