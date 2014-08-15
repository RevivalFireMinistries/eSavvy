package za.org.rfm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.metamodel.domain.Entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/15/14
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Getter
@Setter
@NoArgsConstructor
@javax.persistence.Entity
@Table(name = "rfm_bulletin")
public class Bulletin extends ChurchManagerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long id;
    private String message,title;
    private Date startDate,endDate;
    private String type,status;
}
