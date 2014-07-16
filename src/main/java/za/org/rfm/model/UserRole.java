package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/15
 * Time: 2:40 PM
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role")
    Role role;
    String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole)) return false;

        UserRole userRole = (UserRole) o;

        if (!role.equals(userRole.role)) return false;
        if (!user.equals(userRole.user)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + role.hashCode();
        return result;
    }
}
