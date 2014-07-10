package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/10
 * Time: 12:21 PM
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rfm_systemvars")
public class SystemVar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    public String name,value;
}
